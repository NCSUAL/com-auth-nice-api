package umonaz.omnibus.account.domain.port.in.nice;

import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricContext;

public interface GetNiceSymmetricContextService {

    public NiceSymmetricContext execute(String transactionId);
}
