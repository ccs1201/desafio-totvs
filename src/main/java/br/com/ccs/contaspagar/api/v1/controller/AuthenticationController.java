package br.com.ccs.contaspagar.api.v1.controller;

import br.com.ccs.contaspagar.api.v1.model.input.AuthenticationInput;
import br.com.ccs.contaspagar.api.v1.model.input.CadastroUsuarioInput;
import br.com.ccs.contaspagar.domain.service.AuthenticationService;
import br.com.ccs.contaspagar.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody @Valid AuthenticationInput input) {

        return authenticationService.authenticate(input.login(), input.password());

    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.OK)
    public void register(@RequestBody @Valid CadastroUsuarioInput input) {
        userService.save(input.toUser());
    }
}
