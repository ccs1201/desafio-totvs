package br.com.ccs.contaspagar.domain.service.impl;

import br.com.ccs.contaspagar.domain.repository.UserRepository;
import br.com.ccs.contaspagar.domain.service.AuthenticationService;
import br.com.ccs.contaspagar.infra.config.security.JwtService;
import br.com.ccs.contaspagar.infra.exception.ContasPagarAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final String AUTH_ERRO_MSG = "Usuário ou senha incorretos";

    @Override
    public String authenticate(String login, String password) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ContasPagarAuthenticationException(AUTH_ERRO_MSG));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ContasPagarAuthenticationException(AUTH_ERRO_MSG);
        }

        return jwtService.generateToken(user.getUsername());
    }
}
