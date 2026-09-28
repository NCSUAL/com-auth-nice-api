package umonaz.omnibus.account.domain.port;

/**
 * 본인인증 대칭키 컨텍스트를 회수
 */
public interface NiceSymmetricContextDeleter {

    public void deleteById(final String transactionId);
}
