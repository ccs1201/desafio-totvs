package br.com.ccs.contaspagar.infra.exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ProblemaDetailResponse {

    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    private OffsetDateTime timeStamp = OffsetDateTime.now();
    private String title;
    private int status;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String detail;
    private String path;
}
