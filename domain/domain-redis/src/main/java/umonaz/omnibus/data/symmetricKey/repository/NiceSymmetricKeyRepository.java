package umonaz.omnibus.data.symmetricKey.repository;

import org.springframework.data.repository.CrudRepository;
import umonaz.omnibus.account.domain.model.symmetric.NiceSymmetricKey;

public interface NiceSymmetricKeyRepository extends CrudRepository<NiceSymmetricKey, String> {}
