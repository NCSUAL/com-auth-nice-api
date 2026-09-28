package umonaz.omnibus.account.domain.port;

import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface NiceSymmetricKeySaver {

    public void execute(final NiceSymmetricKey niceSymmetricKey);
}
