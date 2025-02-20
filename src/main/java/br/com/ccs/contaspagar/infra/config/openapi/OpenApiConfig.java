package br.com.ccs.contaspagar.infra.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customiza as configurações da Home do Swagger.
 *
 * @author Cleber.Souza
 * @version 1.0
 * @since 12/2022
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        var contato = new Contact();
        contato.email("ccs1201@gmail.com")
                .name("Cleber Souza")
                .url("https://www.linkedin.com/in/ccs1201/");


        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Desafio Técnico TOTVS | Contas Pagar").description(
                                """
                                        Neste desafio você deverá implementar uma API REST para um sistema simples de
                                        contas a pagar. O sistema permitirá realizar o CRUD de uma conta a pagar, alterar a
                                        situação dela quando for efetuado pagamento, obter informações sobre as contas
                                        cadastradas no banco de dados, e importar um lote de contas de um arquivo CSV, conforme
                                        descrito abaixo.
                                        """)
                        .contact(contato));
    }
}