package umonaz.omnibus.account.domain.service;

import umonaz.omnibus.account.domain.model.command.NiceCallbackCommand;
import umonaz.omnibus.auth.domain.nice.NiceVerifiedUser;

public interface NiceAccountService {

    // 회원가입 및 인증 처리
    NiceVerifiedUser authenticate(final NiceCallbackCommand niceCallbackCommand);
}
