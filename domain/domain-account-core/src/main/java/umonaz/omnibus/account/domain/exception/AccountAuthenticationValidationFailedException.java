package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public class AccountAuthenticationValidationFailedException extends BusinessException {

    public AccountAuthenticationValidationFailedException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountAuthenticationValidationFailedException() {
        super(AccountErrorCode.ACCOUNT_AUTH_VALID_FAILED);
    }
}
