package br.com.ccs.contaspagar.api.v1.model.input;

import br.com.ccs.contaspagar.domain.entity.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CadastroUsuarioInput(
        @NotBlank @Length(min = 4, max = 25) String login,
        @NotBlank @Length(min = 4, max = 25) String password) {

    public Usuario toUser() {
        return Usuario.builder()
                .login(this.login)
                .password(this.password)
                .build();
    }
}
