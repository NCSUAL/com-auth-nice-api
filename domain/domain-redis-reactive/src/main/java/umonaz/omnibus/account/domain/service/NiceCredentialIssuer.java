package umonaz.omnibus.account.domain.service;

import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.credential.NiceCredential;

public interface NiceCredentialIssuer {

    Mono<NiceCredential> issue();
}
