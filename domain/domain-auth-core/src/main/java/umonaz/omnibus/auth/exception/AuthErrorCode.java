package umonaz.omnibus.auth.exception;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umonaz.omnibus.error.ErrorCode;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum AuthErrorCode implements ErrorCode {
    AUTH_TOKEN_NOT_FOUND("AUTH-001", "해당 인증 토큰을 찾을 수 없어요.", HttpStatus.BAD_REQUEST),
    AUTH_SESSION_EXPIRED("AUTH-002", "인증 세션이 만료되었어요.", HttpStatus.BAD_REQUEST);

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
