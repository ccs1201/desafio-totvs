package br.com.ccs.contaspagar.domain.service;

import org.springframework.data.domain.PageRequest;

public interface Service {

    default PageRequest checkPageRequest(PageRequest pageRequest) {
        if (pageRequest == null) {
            pageRequest = PageRequest.of(0, 5);
        }
        return pageRequest;
    }
}
