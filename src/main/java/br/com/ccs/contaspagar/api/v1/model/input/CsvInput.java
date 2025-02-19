package br.com.ccs.contaspagar.api.v1.model.input;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CsvInput(@NotNull MultipartFile multipartFile) {

}
