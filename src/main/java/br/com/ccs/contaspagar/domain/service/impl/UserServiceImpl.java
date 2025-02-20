package br.com.ccs.contaspagar.domain.service.impl;

import br.com.ccs.contaspagar.domain.entity.Usuario;
import br.com.ccs.contaspagar.domain.repository.UserRepository;
import br.com.ccs.contaspagar.domain.service.UserService;
import br.com.ccs.contaspagar.infra.exception.ContasPagarException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void save(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        try {
            repository.save(usuario);
        } catch (Exception e) {
            throw new ContasPagarException(HttpStatus.BAD_REQUEST, "Erro ao cadastrar usuário.", e);
        }
    }
}
