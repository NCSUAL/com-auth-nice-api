package umonaz.omnibus.auth.domain.nice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 인증 토큰이 권한을 잃지 않는지 검증한다.
 *
 * <p>4-인자 {@code of(...)} 는 {@code authorities} 를 받아 놓고 빌더에 싣지 않아 늘 빈 권한 토큰을 만들었다.
 * 이 경로가 최초 로그인({@code NiceAuthenticationProvider})이라, 권한으로 지키는 엔드포인트는 로그인 직후
 * 무조건 403 이 되고 세션을 한 번 재발급해야 통과하는 상태였다. 이 검증이 깨지면 권한 게이트가 조용히
 * 무력화된다.
 */
@DisplayName("NiceAuthenticationToken — 권한 전달")
class NiceAuthenticationTokenTest {

    private static final List<GrantedAuthority> ADMIN = List.of(new SimpleGrantedAuthority("ADMIN"));

    @Test
    @DisplayName("최초 로그인 토큰이 권한을 그대로 싣는다")
    void carriesAuthoritiesOnLogin() {
        final NiceAuthenticationToken callback = NiceAuthenticationToken.of("tx", "webTx");

        final NiceAuthenticationToken authenticated =
                NiceAuthenticationToken.of(ADMIN, callback, "refresh-token", UUID.randomUUID());

        assertThat(authenticated.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ADMIN");
    }

    @Test
    @DisplayName("최초 로그인 토큰이 본인인증 거래 식별자를 유지한다")
    void keepsTransactionIdentifiersOnLogin() {
        final NiceAuthenticationToken callback = NiceAuthenticationToken.of("tx", "webTx");

        final NiceAuthenticationToken authenticated =
                NiceAuthenticationToken.of(ADMIN, callback, "refresh-token", UUID.randomUUID());

        assertThat(authenticated.getTransactionId()).isEqualTo("tx");
        assertThat(authenticated.getWebTransactionId()).isEqualTo("webTx");
    }

    @Test
    @DisplayName("재발급 토큰도 권한을 그대로 싣는다")
    void carriesAuthoritiesOnReissue() {
        final NiceAuthenticationToken reissued = NiceAuthenticationToken.of(ADMIN, UUID.randomUUID(), "refresh-token");

        assertThat(reissued.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ADMIN");
    }

    @Test
    @DisplayName("콜백 단계 토큰은 아직 인증되지 않은 상태다")
    void callbackTokenIsNotAuthenticated() {
        assertThat(NiceAuthenticationToken.of("tx", "webTx").isAuthenticated()).isFalse();
    }
}
