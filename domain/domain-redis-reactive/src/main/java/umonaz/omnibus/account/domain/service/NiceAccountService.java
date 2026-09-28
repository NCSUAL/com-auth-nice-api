package umonaz.omnibus.account.domain.service;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.model.command.NiceCallbackCommand;

public interface NiceAccountService {

    // 회원가입 및 인증 처리
    public Mono<Void> authenticate(final NiceCallbackCommand niceCallbackCommand);
}
