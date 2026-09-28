package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public class AccountDecryptionFailedException extends BusinessException {

    public AccountDecryptionFailedException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountDecryptionFailedException() {
        super(AccountErrorCode.ACCOUNT_DECRYPTION_FAILED);
    }
}
