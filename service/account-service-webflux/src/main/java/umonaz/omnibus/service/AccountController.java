package umonaz.omnibus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;
import umonaz.omnibus.account.domain.service.NiceAccountService;
import umonaz.omnibus.account.domain.service.NiceCredentialIssuer;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/account")
public class AccountController {

    private final NiceCredentialIssuer niceCredentialIssuer;
    private final NiceAccountService niceAccountService;

    @RequestMapping(value = "/sign-up/nice", method = RequestMethod.GET)
    public Mono<String> processNiceSignUp() {
        return niceCredentialIssuer.issue().map(credential -> "redirect:" + credential.authUrl());
    }
}
