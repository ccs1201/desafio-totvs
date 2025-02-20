package br.com.ccs.contaspagar.api.v1.model.input;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CsvInput(
        @NotNull(message = "Nenhum arquivo enviado, selecione um arquivo para importar.") MultipartFile file) {

}
