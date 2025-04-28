package br.com.ccs.contaspagar.api.v1.controller;

import br.com.ccs.contaspagar.api.v1.model.input.AutenticacaoInput;
import br.com.ccs.contaspagar.api.v1.model.input.CadastroUsuarioInput;
import br.com.ccs.contaspagar.domain.service.AutenticacaoService;
import br.com.ccs.contaspagar.domain.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AutenticacaoControllerTest {

    @InjectMocks
    private AutenticacaoController autenticacaoController;

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    void testCadastrarSuccessful() {
        var input = new CadastroUsuarioInput("", "");
        autenticacaoController.cadastrar(input);
        verify(usuarioService, times(1)).cadastrar(input.toUser());
    }

    @Test
    void testLogin() {
        var input = new AutenticacaoInput("", "");
        autenticacaoController.login(input);
        verify(autenticacaoService, times(1)).autenticar(input.login(), input.password());
    }

}
