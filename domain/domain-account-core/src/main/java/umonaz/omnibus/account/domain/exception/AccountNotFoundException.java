package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public final class AccountNotFoundException extends BusinessException {

    public AccountNotFoundException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountNotFoundException() {
        super(AccountErrorCode.ACCOUNT_NOT_FOUND);
    }
}
