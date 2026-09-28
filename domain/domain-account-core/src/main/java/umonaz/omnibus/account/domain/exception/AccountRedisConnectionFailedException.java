package umonaz.omnibus.account.domain.exception;

import umonaz.omnibus.error.ErrorCode;
import umonaz.omnibus.exception.BusinessException;

public final class AccountRedisConnectionFailedException extends BusinessException {

    public AccountRedisConnectionFailedException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public AccountRedisConnectionFailedException() {
        super(AccountErrorCode.ACCOUNT_REDIS_CONNECTION_FAILED);
    }
}
