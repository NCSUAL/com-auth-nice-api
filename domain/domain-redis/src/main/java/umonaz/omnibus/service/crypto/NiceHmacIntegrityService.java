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

    private static final ThreadLocal<Mac> MAC_TL = ThreadLocal.withInitial(() -> {
        try {
            return Mac.getInstance(ENCRYPT_ALGORITHM);
        } catch (Exception e) {
            throw new AccountEncryptionFailedException();
        }
    });

    @Override
    public String generateHmac(String plainText, NiceSymmetricSalt salt) {
        try {
            Mac mac = MAC_TL.get();
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

    private boolean verify(VerifyHmac verifyHmac) {
        try {
            Mac mac = MAC_TL.get();
            SecretKey secretKey =
                    new SecretKeySpec(verifyHmac.salt().getBytes(StandardCharsets.UTF_8), ENCRYPT_ALGORITHM);
            mac.init(secretKey);
            String expected = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(verifyHmac.plainText().getBytes(StandardCharsets.UTF_8)));
            // String.equals 는 첫 불일치 문자에서 즉시 빠져나와 비교 시간이 「앞에서 몇 글자가 맞았는가」에
            // 비례한다. 무결성 값을 바꿔 가며 응답 시간을 재면 한 글자씩 맞춰 갈 수 있는 통로가 된다.
            // MessageDigest.isEqual 은 길이가 같은 동안 끝까지 훑어 그 신호를 없앤다.
            return MessageDigest.isEqual(
                    verifyHmac.target().getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8));
        } catch (InvalidKeyException e) {
            throw new AccountAuthenticationValidationFailedException();
        }
    }
}
