package umonaz.omnibus.account.domain.port.in;

import lombok.Builder;

public interface HmacIntegrityService<Salt> {

    @Builder
    public record VerifyHmac(String target, String plainText, String salt) {
        public static VerifyHmac of(final String target, final String plainText, final String salt) {
            return VerifyHmac.builder()
                    .target(target)
                    .plainText(plainText)
                    .salt(salt)
                    .build();
        }
    }

    public String generateHmac(final String plainText, final Salt salt);

    public boolean verifyHmac(final String target, final String plainText, final String hmac);

    public boolean verifyHmac(final VerifyHmac vertifyHmac);
}
