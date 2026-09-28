package umonaz.omnibus.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.view.RedirectView;

@Tag(name = "account", description = "회원가입 API")
public interface AccountControllerSwagger {

    @Operation(description = "나이스 본인인증 진행 URL로 리다이렉트", summary = "나이스 본인인증")
    RedirectView processNiceSignUp();
}
