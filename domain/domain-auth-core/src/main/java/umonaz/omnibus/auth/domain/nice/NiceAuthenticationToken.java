package umonaz.omnibus.auth.domain.nice;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import umonaz.omnibus.auth.domain.AuthenticationToken;

@Getter
public final class NiceAuthenticationToken extends AbstractAuthenticationToken
        implements AuthenticationToken<UUID>, Serializable {

    private final UUID userId;

    private final String refreshToken;

    private final String transactionId;

    private final String webTransactionId;

    /**
     * 로그인에 성공한 사용자의 인증 토큰.
     */
    @Builder
    public NiceAuthenticationToken(
            final Collection<? extends GrantedAuthority> authorities,
            UUID userId,
            String refreshToken,
            String transactionId,
            String webTransactionId) {
        super(authorities);
        setAuthenticated(true);
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.transactionId = transactionId;
        this.webTransactionId = webTransactionId;
    }

    public NiceAuthenticationToken(
            final Collection<? extends GrantedAuthority> authorities, UUID userId, String refreshToken) {
        super(authorities);
        setAuthenticated(true);
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.transactionId = null;
        this.webTransactionId = null;
    }

    public NiceAuthenticationToken(String transactionId, String webTransactionId) {
        super(null);
        this.userId = null;
        this.refreshToken = null;
        setAuthenticated(false);
        this.transactionId = transactionId;
        this.webTransactionId = webTransactionId;
    }

    public static NiceAuthenticationToken of(
            final Collection<? extends GrantedAuthority> authorities,
            final NiceAuthenticationToken niceAuthenticationToken,
            final String refreshToken,
            final UUID userId) {
        return NiceAuthenticationToken.builder()
                .authorities(authorities)
                .userId(userId)
                .refreshToken(refreshToken)
                .transactionId(niceAuthenticationToken.getTransactionId())
                .webTransactionId(niceAuthenticationToken.getWebTransactionId())
                .build();
    }

    public static NiceAuthenticationToken of(final String transactionId, final String webTransactionId) {
        return new NiceAuthenticationToken(transactionId, webTransactionId);
    }

    public static NiceAuthenticationToken of(
            final Collection<? extends GrantedAuthority> authorities, UUID userId, final String refreshToken) {
        return new NiceAuthenticationToken(authorities, userId, refreshToken);
    }

    @Override
    public Object getPrincipal() {
        return this.userId.toString();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return this.refreshToken;
    }
}
