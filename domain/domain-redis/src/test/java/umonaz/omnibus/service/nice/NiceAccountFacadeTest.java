package umonaz.omnibus.service.nice;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.exception.AccountAuthenticationValidationFailedException;
import umonaz.omnibus.account.domain.model.command.NiceCallbackCommand;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;
import umonaz.omnibus.account.domain.port.NiceSymmetricContextDeleter;
import umonaz.omnibus.account.domain.port.in.nice.GetNiceSymmetricContextService;
import umonaz.omnibus.account.domain.port.in.nice.NiceVerificationService;
import umonaz.omnibus.client.properties.NiceProperties;
import umonaz.omnibus.service.crypto.NiceAesGcmCryptoService;

/**
 * 본인인증 콜백 처리의 재사용 차단과 결과 코드 검증.
 *
 * <p>{@code request_no} 와 {@code web_transaction_id} 는 콜백 URL 의 쿼리스트링에 실려 오므로 nginx 접근
 * 로그·브라우저 기록·Referer 헤더에 남는다. 컨텍스트를 지우지 않으면 그 URL 한 줄로 TTL(600초) 내내
 * 같은 사용자의 세션을 새로 발급받을 수 있다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NiceAccountFacade — 본인인증 콜백 검증")
class NiceAccountFacadeTest {

    private static final String REQUEST_NO = "req-no-0001";

    @Mock
    private GetNiceSymmetricContextService getNiceSymmetricContextService;

    @Mock
    private NiceSymmetricContextDeleter niceSymmetricContextDeleter;

    @Mock
    private NiceVerificationService niceVerificationService;

    @Mock
    private NiceAesGcmCryptoService niceAesGcmCryptoService;

    @Mock
    private NiceHttpCredentialFacade niceHttpCredentialFacade;

    @Mock
    private NiceProperties niceProperties;

    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private NiceAccountFacade niceAccountFacade;

    private NiceCallbackCommand command;

    @BeforeEach
    void setUp() {
        command = NiceCallbackCommand.builder()
                .transactionId(REQUEST_NO)
                .webTransactionId("web-tx-0001")
                .build();

        lenient()
                .when(getNiceSymmetricContextService.execute(REQUEST_NO))
                .thenReturn(NiceSymmetricContext.of("ticket", 1, REQUEST_NO, "access-token", "nice-tx"));
        lenient()
                .when(niceHttpCredentialFacade.deriveSymmetricSalt(anyString(), anyString(), anyInt()))
                .thenReturn(NiceSymmetricSalt.builder()
                        .key("key")
                        .hmacKey("hmacKey")
                        .build());
        lenient().when(niceProperties.generateBearerAuthHeader(anyString())).thenReturn("Bearer access-token");
        lenient().when(niceProperties.authResultRequestUrl()).thenReturn("https://nice.example/result");
    }

    private void givenResultApiReturns(final String body) {
        given(restTemplate.exchange(
                        anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(body));
    }

    private void givenDecryptedPayload(final String payload) {
        givenResultApiReturns("{\"enc_data\":\"enc\",\"integrity_value\":\"iv\"}");
        given(niceAesGcmCryptoService.decrypt(anyString(), any())).willReturn(payload);
    }

    private static String payloadWithResultCode(final String resultCode) {
        return """
                {"requestno":"req-no-0001","enctime":"20260902120000","sitecode":"S1","responseno":"R1",
                 "authtype":"M","name":"홍길동","utf8_name":"%%ED%%99%%8D","birthdate":"19940317","gender":"1",
                 "national_info":"0","mobile_co":"SKT","mobile_no":"01012345678","ci":"ci-value","di":"di-value",
                 "receivedata":"","resultcode":"%s"}
                """
                .formatted(resultCode);
    }

    @Test
    @DisplayName("컨텍스트를 읽은 즉시 회수해 같은 request_no 로 다시 인증할 수 없게 한다")
    void burnsContextOnSuccess() {
        givenDecryptedPayload(payloadWithResultCode("0000"));

        niceAccountFacade.authenticate(command);

        then(niceSymmetricContextDeleter).should().deleteById(REQUEST_NO);
    }

    /**
     * 실패했을 때도 지워야 한다.
     *
     * <p>성공했을 때만 지우면 실패를 유도해 컨텍스트를 살려 둔 채 재시도할 수 있다. 인증 코드를 한 번만
     * 쓰게 하는 규칙은 결과와 무관해야 한다.
     */
    @Test
    @DisplayName("결과 API 가 실패해도 컨텍스트를 회수한다")
    void burnsContextEvenWhenResultApiFails() {
        given(restTemplate.exchange(
                        anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willThrow(new IllegalStateException("network down"));

        assertThatThrownBy(() -> niceAccountFacade.authenticate(command)).isInstanceOf(RuntimeException.class);

        then(niceSymmetricContextDeleter).should().deleteById(REQUEST_NO);
    }

    /**
     * HMAC 무결성은 「NICE 가 보낸 값이 맞다」만 말한다.
     *
     * <p>실패 결과도 같은 키로 서명되어 오므로 무결성 검증은 그대로 통과한다. 본인확인이 실제로 끝났는지는
     * 페이로드의 {@code resultcode} 가 답한다.
     */
    @Test
    @DisplayName("결과 코드가 성공이 아니면 무결성을 통과해도 거절한다")
    void rejectsNonSuccessResultCode() {
        givenDecryptedPayload(payloadWithResultCode("9999"));

        assertThatThrownBy(() -> niceAccountFacade.authenticate(command))
                .isInstanceOf(AccountAuthenticationValidationFailedException.class);
    }

    @Test
    @DisplayName("결과 코드가 0000 이면 확인된 사용자를 돌려준다")
    void acceptsSuccessResultCode() {
        givenDecryptedPayload(payloadWithResultCode("0000"));

        assertThatCode(() -> niceAccountFacade.authenticate(command)).doesNotThrowAnyException();
    }
}
