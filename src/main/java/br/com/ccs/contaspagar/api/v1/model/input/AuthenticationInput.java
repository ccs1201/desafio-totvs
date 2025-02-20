package br.com.ccs.contaspagar.api.v1.model.input;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationInput(@NotBlank String login,
                                  @NotBlank String password) {
}
