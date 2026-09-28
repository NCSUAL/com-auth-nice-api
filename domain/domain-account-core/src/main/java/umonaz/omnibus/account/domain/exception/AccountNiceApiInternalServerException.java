package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public class AccountNiceApiInternalServerException extends BusinessException {

    public AccountNiceApiInternalServerException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountNiceApiInternalServerException() {
        super(AccountErrorCode.ACCOUNT_FAILED_SAVE);
    }
}
