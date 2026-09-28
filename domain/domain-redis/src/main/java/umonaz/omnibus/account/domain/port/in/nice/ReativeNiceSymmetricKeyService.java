package umonaz.omnibus.account.domain.port.in.nice;

import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface ReativeNiceSymmetricKeyService {

    public void createSymmetricKey(NiceSymmetricKey niceSymmetricKey);
}
