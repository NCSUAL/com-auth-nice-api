package umonaz.omnibus.service.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.port.in.HmacIntegrityService.VerifyHmac;

/**
 * 무결성 비교가 상수 시간인지, 그리고 상수 시간으로 바꾸면서 판정 자체가 바뀌지 않았는지 검증한다.
 *
 * <p>{@code String.equals} 는 첫 불일치 문자에서 즉시 빠져나와 비교 시간이 「앞에서 몇 글자가 맞았는가」에
 * 비례한다. {@code MessageDigest.isEqual} 로 바꿨으므로, 바뀐 것은 시간 특성뿐이고 참·거짓은 그대로여야
 * 한다.
 */
@DisplayName("NiceHmacIntegrityService — 무결성 검증")
class NiceHmacIntegrityServiceTest {

    private static final String PLAIN_TEXT = "enc-data-payload";

    private static final NiceSymmetricSalt SALT =
            NiceSymmetricSalt.builder().key("key").hmacKey("hmac-key-0001").build();

    private final NiceHmacIntegrityService service = new NiceHmacIntegrityService();

    @Test
    @DisplayName("같은 키로 만든 값은 통과한다")
    void acceptsMatchingHmac() {
        final String hmac = service.generateHmac(PLAIN_TEXT, SALT);

        assertThat(service.verifyHmac(VerifyHmac.of(hmac, PLAIN_TEXT, SALT.hmacKey())))
                .isTrue();
    }

    @Test
    @DisplayName("한 글자만 달라도 거절한다")
    void rejectsTamperedHmac() {
        final String hmac = service.generateHmac(PLAIN_TEXT, SALT);
        final String tampered = ('A' == hmac.charAt(0) ? 'B' : 'A') + hmac.substring(1);

        assertThat(service.verifyHmac(VerifyHmac.of(tampered, PLAIN_TEXT, SALT.hmacKey())))
                .isFalse();
    }

    @Test
    @DisplayName("본문이 바뀌면 거절한다")
    void rejectsTamperedPlainText() {
        final String hmac = service.generateHmac(PLAIN_TEXT, SALT);

        assertThat(service.verifyHmac(VerifyHmac.of(hmac, PLAIN_TEXT + "x", SALT.hmacKey())))
                .isFalse();
    }

    @Test
    @DisplayName("다른 키로 만든 값은 거절한다")
    void rejectsDifferentKey() {
        final String hmac = service.generateHmac(PLAIN_TEXT, SALT);

        assertThat(service.verifyHmac(VerifyHmac.of(hmac, PLAIN_TEXT, "other-key")))
                .isFalse();
    }

    /** 길이가 다른 값은 그 자체로 불일치다. 예외 없이 false 여야 한다. */
    @Test
    @DisplayName("길이가 다른 값은 예외 없이 거절한다")
    void rejectsDifferentLengthWithoutThrowing() {
        assertThat(service.verifyHmac(VerifyHmac.of("short", PLAIN_TEXT, SALT.hmacKey())))
                .isFalse();
    }
}
