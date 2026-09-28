package umonaz.omnibus.account.domain.port.in;

public interface CryptoService<Salt> {

    public String encrypt(String plainText, Salt salt);

    public String decrypt(String cipherText, Salt salt);
}
