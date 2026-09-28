package umonaz.omnibus.service.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.stereotype.Service;
import umonaz.omnibus.account.domain.exception.AccountEncryptionFailedException;
import umonaz.omnibus.account.domain.port.in.HasingService;

@Service(value = "nice")
public class NiceSha256HasingService implements HasingService {

    private final MessageDigest messageDigest;

    public NiceSha256HasingService() {
        try {
            this.messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new AccountEncryptionFailedException();
        }
    }

    @Override
    public String hash(String plainText) {
        messageDigest.update(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(messageDigest.digest());
    }
}
