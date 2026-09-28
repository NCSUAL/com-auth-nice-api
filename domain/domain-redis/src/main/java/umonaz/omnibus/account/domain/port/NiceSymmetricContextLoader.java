package umonaz.omnibus.account.domain.port;

import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;

public interface NiceSymmetricContextLoader {

    public NiceSymmetricContext loadById(final String transactionId);
}
