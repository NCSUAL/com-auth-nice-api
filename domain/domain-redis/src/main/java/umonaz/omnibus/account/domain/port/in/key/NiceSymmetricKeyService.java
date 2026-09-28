package umonaz.omnibus.account.domain.port.in.key;

import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface NiceSymmetricKeyService {

    public void createSymmetricKey(NiceSymmetricKey niceSymmetricKey);
}
