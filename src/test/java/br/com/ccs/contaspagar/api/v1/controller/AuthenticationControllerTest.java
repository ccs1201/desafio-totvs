package br.com.ccs.contaspagar.api.v1.controller;

import br.com.ccs.contaspagar.api.v1.model.input.AuthenticationInput;
import br.com.ccs.contaspagar.api.v1.model.input.CadastroUsuarioInput;
import br.com.ccs.contaspagar.domain.service.AuthenticationService;
import br.com.ccs.contaspagar.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationService authenticationService;

    /**
     * Test case for successful user registration.
     * Verifies that the register method calls the userService.save() method with the correct user data.
     */
    @Test
    void testRegisterSuccessful() {
        var input = new CadastroUsuarioInput("", "");
        authenticationController.register(input);
        verify(userService, times(1)).save(input.toUser());
    }

    @Test
    void testLogin() {
        var input = new AuthenticationInput("", "");
        authenticationController.login(input);
        verify(authenticationService, times(1)).authenticate(input.login(), input.password());
    }

}
