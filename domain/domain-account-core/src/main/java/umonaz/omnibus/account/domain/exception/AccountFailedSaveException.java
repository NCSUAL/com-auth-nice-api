package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public final class AccountFailedSaveException extends BusinessException {

    public AccountFailedSaveException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountFailedSaveException() {
        super(AccountErrorCode.ACCOUNT_FAILED_SAVE);
    }
}
