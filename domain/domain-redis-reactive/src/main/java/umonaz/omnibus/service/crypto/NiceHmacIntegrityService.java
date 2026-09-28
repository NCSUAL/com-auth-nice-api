package umonaz.omnibus.service.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import umonaz.omnibus.account.domain.credential.NiceSymmetricSalt;
import umonaz.omnibus.account.domain.exception.AccountAuthenticationValidationFailedException;
import umonaz.omnibus.account.domain.exception.AccountEncryptionFailedException;
import umonaz.omnibus.account.domain.port.in.HmacIntegrityService;

@Service
public class NiceHmacIntegrityService implements HmacIntegrityService<NiceSymmetricSalt> {

    private static final String ENCRYPT_ALGORITHM = "HmacSHA256";

    private static final ThreadLocal<Mac> macTL = ThreadLocal.withInitial(() -> {
        try {
            return Mac.getInstance(ENCRYPT_ALGORITHM);
        } catch (Exception e) {
            throw new AccountEncryptionFailedException();
        }
    });

    @Override
    public String generateHmac(String plainText, NiceSymmetricSalt salt) {
        try {
            Mac mac = macTL.get();
            SecretKey secretKey = new SecretKeySpec(salt.hmacKey().getBytes(StandardCharsets.UTF_8), ENCRYPT_ALGORITHM);
            mac.init(secretKey);
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException e) {
            throw new AccountEncryptionFailedException();
        }
    }

    @Override
    public boolean verifyHmac(String target, String plainText, String salt) {
        return verify(VerifyHmac.of(target, plainText, salt));
    }

    @Override
    public boolean verifyHmac(VerifyHmac verifyHmac) {
        return verify(verifyHmac);
    }

    private boolean verify(final VerifyHmac verifyHmac) {
        try {
            Mac mac = macTL.get();
            SecretKey secretKey =
                    new SecretKeySpec(verifyHmac.salt().getBytes(StandardCharsets.UTF_8), ENCRYPT_ALGORITHM);
            mac.init(secretKey);
            String expected = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(verifyHmac.plainText().getBytes(StandardCharsets.UTF_8)));
            // 타이밍 공격 방지용. String.equals 는 다른 글자 나오면 바로 리턴해서 응답시간으로 유추 가능함
            return MessageDigest.isEqual(
                    verifyHmac.target().getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8));
        } catch (InvalidKeyException e) {
            throw new AccountAuthenticationValidationFailedException();
        }
    }
}
