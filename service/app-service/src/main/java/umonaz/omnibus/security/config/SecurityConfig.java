package umonaz.omnibus.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import umonaz.omnibus.security.AccessDeniedHandlerImpl;
import umonaz.omnibus.security.AuthenticationEntryPointHandler;
import umonaz.omnibus.security.NiceAuthenticationFilter;
import umonaz.omnibus.security.NiceAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ACTUATOR_HEALTH = "/actuator/health";

    private static final String ACTUATOR_ALL = "/actuator/**";

    // 삭제: ADMOB_SSV, ADMIN, CATALOG_PATHS, ADMIN_PATHS 상수 (AdMob·관리자·꾸미기 카탈로그, NICE 본인인증과 무관)

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(false)
                .ignoring()
                .requestMatchers(
                        "/images/**",
                        "/css/**",
                        "/js/**",
                        "/favicon.ico",
                        "/swagger-ui.html",
                        "/swagger/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**");
    }

    @Bean
    public ProviderManager authenticationManager(NiceAuthenticationProvider niceAuthenticationProvider) {
        return new ProviderManager(niceAuthenticationProvider);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ProviderManager providerManager, // 직접 호출 대신 주입
            SecurityContextRepository securityContextRepository)
            throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .requestCache(RequestCacheConfigurer::disable)
                .addFilterAt(
                        new NiceAuthenticationFilter(securityContextRepository, providerManager),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new AuthenticationEntryPointHandler())
                        .accessDeniedHandler(new AccessDeniedHandlerImpl()))
                .authorizeHttpRequests(requestMatcher -> requestMatcher
                        .requestMatchers(
                                "/account/sign-up/nice",
                                "/account/verified/sign-up/nice",
                                // 삭제: "/api/auth/reissue/token" (로그인 토큰 재발급)
                                "/error")
                        .permitAll()
                        .requestMatchers(ACTUATOR_HEALTH)
                        .permitAll()
                        .requestMatchers(ACTUATOR_ALL)
                        .denyAll()
                        // 삭제: ADMIN_PATHS·CATALOG_PATHS 인가 규칙 (관리자·꾸미기 카탈로그)
                        .anyRequest()
                        .authenticated())
                .build();
    }
}
