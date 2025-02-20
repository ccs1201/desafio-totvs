package br.com.ccs.contaspagar.domain.service.impl;

import br.com.ccs.contaspagar.domain.entity.Usuario;
import br.com.ccs.contaspagar.domain.repository.UserRepository;
import br.com.ccs.contaspagar.domain.service.UserService;
import lombok.RequiredArgsConstructor;
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
        repository.save(usuario);
    }
}
