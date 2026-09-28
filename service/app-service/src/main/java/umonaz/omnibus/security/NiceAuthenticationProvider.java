package umonaz.omnibus.security;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import umonaz.omnibus.account.domain.model.command.NiceCallbackCommand;
import umonaz.omnibus.account.domain.service.NiceAccountService;
import umonaz.omnibus.auth.domain.nice.NiceAuthenticationToken;
import umonaz.omnibus.auth.domain.nice.NiceVerifiedUser;
import umonaz.omnibus.exception.BusinessException;

// 삭제: import umonaz.omnibus.RefreshTokenProvider; (로그인 리프레시 토큰, core-web)
// 삭제: import umonaz.omnibus.auth.service.AuthService; (유저 저장)
// 삭제: import umonaz.omnibus.users.domain.model.entity.Users; (유저 저장, data-rdb)

@Component
@RequiredArgsConstructor
public class NiceAuthenticationProvider implements AuthenticationProvider {

    // 삭제: private final AuthService.NiceAuthService<Users> niceAuthService; (유저 저장)
    private final NiceAccountService niceAccountService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        NiceAuthenticationToken token = (NiceAuthenticationToken) authentication;

        NiceVerifiedUser verifiedUser;
        try {
            verifiedUser = niceAccountService.authenticate(NiceCallbackCommand.builder()
                    .transactionId(token.getTransactionId())
                    .webTransactionId(token.getWebTransactionId())
                    .build());
        } catch (BusinessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

        // 삭제: Users user = niceAuthService.enroll(verifiedUser); (유저 저장)

        // 삭제: return NiceAuthenticationToken.of(
        //          user.getRole().getAuthorities(), token, RefreshTokenProvider.generate(), user.getId());
        // 유저 저장/로그인을 뺐으므로 권한·리프레시 토큰 없음. userId 는 null 이면 getPrincipal() 에서 NPE 라 임시 UUID
        return NiceAuthenticationToken.of(List.of(), token, null, UUID.randomUUID());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return NiceAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
