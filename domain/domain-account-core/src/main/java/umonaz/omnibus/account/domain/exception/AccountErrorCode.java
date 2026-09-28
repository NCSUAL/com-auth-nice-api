package umonaz.omnibus.account.domain.exception;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umonaz.omnibus.error.ErrorCode;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum AccountErrorCode implements ErrorCode {
    ACCOUNT_FAILED_SAVE("ACCOUNT-001", "인증키를 저장하는 중 오류가 발생했어요.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_NOT_FOUND("ACCOUNT-002", "인증 중에 데이터가 손실되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_ENCRYPTION_FAILED("ACCOUNT-003", "인증 중에 암호화에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_DECRYPTION_FAILED("ACCOUNT-004", "인증 중에 복호화에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_PAYLOAD_RESPONSE_FAILED("ACCOUNT-005", "인증 Payload로 반환하는 중에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_AUTH_VALID_FAILED("ACCOUNT-006", "인증 유효성 검사에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_NICE_API_INTERNAL_SERVER("ACCOUNT-007", "NICE API 통신 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_REDIS_CONNECTION_FAILED(
            "ACCOUNT-008", "인증 저장소에 연결할 수 없어요. 잠시 후 다시 시도해주세요.", HttpStatus.SERVICE_UNAVAILABLE);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public HttpStatus httpStatus() {
        return this.httpStatus;
    }
}
