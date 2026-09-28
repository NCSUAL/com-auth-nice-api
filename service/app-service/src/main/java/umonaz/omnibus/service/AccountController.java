package umonaz.omnibus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import umonaz.omnibus.account.domain.service.NiceAccountService;
import umonaz.omnibus.account.domain.service.NiceCredentialIssuer;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/account")
public class AccountController implements AccountControllerSwagger {

    private final NiceCredentialIssuer niceCredentialIssuer;
    private final NiceAccountService niceAccountService;

    @RequestMapping(value = "/sign-up/nice", method = RequestMethod.GET)
    public RedirectView processNiceSignUp() {
        return new RedirectView(niceCredentialIssuer.issue().authUrl());
    }
}
