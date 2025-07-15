package br.com.ccs.contaspagar.domain.service.impl;

import br.com.ccs.contaspagar.domain.repository.UsuarioRepository;
import br.com.ccs.contaspagar.domain.service.AutenticacaoService;
import br.com.ccs.contaspagar.infra.config.security.JwtService;
import br.com.ccs.contaspagar.infra.exception.ContasPagarAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final String AUTH_ERRO_MSG = "Usuário ou senha incorretos";

    @Override
    public String autenticar(String login, String password) {
        var user = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new ContasPagarAuthenticationException(AUTH_ERRO_MSG));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ContasPagarAuthenticationException(AUTH_ERRO_MSG);
        }

        return jwtService.generateToken(user.getUsername());
    }
}
