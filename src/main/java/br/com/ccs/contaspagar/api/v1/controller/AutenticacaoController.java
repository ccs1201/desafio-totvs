package br.com.ccs.contaspagar.api.v1.controller;

import br.com.ccs.contaspagar.api.v1.model.input.AutenticacaoInput;
import br.com.ccs.contaspagar.api.v1.model.input.CadastroUsuarioInput;
import br.com.ccs.contaspagar.domain.service.AutenticacaoService;
import br.com.ccs.contaspagar.domain.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "API para autenticação de usuários")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody @Valid AutenticacaoInput input) {
        return autenticacaoService.autenticar(input.login(), input.password());
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.OK)
    public void cadastrar(@RequestBody @Valid CadastroUsuarioInput input) {
        usuarioService.cadastrar(input.toUser());
    }
}
