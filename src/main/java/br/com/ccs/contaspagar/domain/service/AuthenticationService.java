package br.com.ccs.contaspagar.domain.service;

public interface AuthenticationService {
    String authenticate(String login, String password);
}
