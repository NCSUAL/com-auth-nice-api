package umonaz.omnibus.auth.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public final class AuthTokenNotFoundException extends BusinessException {

    public AuthTokenNotFoundException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AuthTokenNotFoundException() {
        super(AuthErrorCode.AUTH_TOKEN_NOT_FOUND);
    }
}
