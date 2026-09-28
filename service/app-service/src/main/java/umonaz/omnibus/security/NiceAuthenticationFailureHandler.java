package umonaz.omnibus.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import umonaz.omnibus.exception.BusinessException;
import umonaz.omnibus.response.ApiEntity;
import umonaz.omnibus.response.ErrorDto;

@Component
public class NiceAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        if (exception.getCause() instanceof BusinessException ex) {
            response.setStatus(ex.getErrorCode().httpStatus().value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter()
                    .write(objectMapper.writeValueAsString(ApiEntity.fail(
                            ErrorDto.of(ex.getMessage(), ex.getErrorCode().code()))));
            return;
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(
                        ApiEntity.fail(ErrorDto.of("본인인증 요청값이 올바르지 않습니다.", "INVALID_NICE_AUTH_REQUEST"))));
    }
}
