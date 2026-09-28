package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public class AccountPayloadResponseFailedException extends BusinessException {

    public AccountPayloadResponseFailedException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountPayloadResponseFailedException() {
        super(AccountErrorCode.ACCOUNT_PAYLOAD_RESPONSE_FAILED);
    }
}
