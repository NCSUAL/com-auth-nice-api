package umonaz.omnibus.account.domain.service;

import umonaz.omnibus.account.domain.credential.NiceCredential;

public interface NiceCredentialIssuer {

    NiceCredential issue();
}
