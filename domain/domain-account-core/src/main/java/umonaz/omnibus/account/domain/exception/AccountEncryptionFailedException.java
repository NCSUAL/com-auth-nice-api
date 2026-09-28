package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public class AccountEncryptionFailedException extends BusinessException {

    public AccountEncryptionFailedException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountEncryptionFailedException() {
        super(AccountErrorCode.ACCOUNT_ENCRYPTION_FAILED);
    }
}
