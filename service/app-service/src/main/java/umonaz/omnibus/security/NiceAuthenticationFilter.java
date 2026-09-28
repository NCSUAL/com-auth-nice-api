package umonaz.omnibus.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import umonaz.omnibus.auth.domain.nice.NiceAuthenticationToken;
import umonaz.omnibus.response.ApiEntity;

public class NiceAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final SecurityContextRepository securityContextRepository;

    public NiceAuthenticationFilter(
            SecurityContextRepository securityContextRepository, AuthenticationManager authenticationManager) {
        super(PathPatternRequestMatcher.withDefaults().matcher("/account/verified/sign-up/nice"));
        this.securityContextRepository = securityContextRepository;
        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(new NiceAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        // return_url에 포함된 우리 세션 키 (Redis 조회용)
        String requestNo = request.getParameter("request_no");
        // NICE가 콜백 시 추가하는 web_transaction_id (인증결과 API 호출용)
        String webTransactionId = request.getParameter("web_transaction_id");

        if (requestNo == null || webTransactionId == null) {
            throw new BadCredentialsException("본인인증 요청값이 올바르지 않습니다.");
        }

        NiceAuthenticationToken token = new NiceAuthenticationToken(requestNo, webTransactionId);

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        NiceAuthenticationToken niceAuthenticationToken = (NiceAuthenticationToken) authResult;

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(niceAuthenticationToken);

        securityContextRepository.saveContext(context, request, response);
        SecurityContextHolder.setContext(context);

        // 삭제: response.sendRedirect("/api/auth/token"); (로그인 토큰 발급, AuthController 제외)
        // 인증·검증까지만 하므로 성공 응답만 내려준다
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(ApiEntity.ok()));
    }
}
