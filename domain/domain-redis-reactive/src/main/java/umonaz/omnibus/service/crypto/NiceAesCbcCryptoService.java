package umonaz.omnibus.service.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.exception.AccountDecryptionFailedException;
import umonaz.omnibus.account.domain.exception.AccountEncryptionFailedException;
import umonaz.omnibus.account.domain.port.in.CryptoService;

@Service
public class NiceAesCbcCryptoService implements CryptoService<NiceSymmetricSalt> {

    private static final String ENCRYPT_ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_LENGTH = 16;

    private static final ThreadLocal<Cipher> cipherTL = ThreadLocal.withInitial(() -> {
        try {
            return Cipher.getInstance(ENCRYPT_ALGORITHM);
        } catch (Exception e) {
            throw new AccountEncryptionFailedException();
        }
    });

    @Override
    public String decrypt(final String cipherText, NiceSymmetricSalt salt) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cipherText);
            byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
            byte[] cipher = Arrays.copyOfRange(decoded, IV_LENGTH, decoded.length);

            SecretKey secretKey = new SecretKeySpec(salt.key().getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher c = cipherTL.get();
            c.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(c.doFinal(cipher), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new AccountDecryptionFailedException();
        }
    }

    @Override
    public String encrypt(String plainText, NiceSymmetricSalt salt) {
        throw new UnsupportedOperationException("NICE v2 API에서는 서버 측 암호화를 사용하지 않습니다.");
    }
}
