package br.com.ccs.contaspagar.domain.service.impl;

import br.com.ccs.contaspagar.domain.entity.Usuario;
import br.com.ccs.contaspagar.domain.repository.UsuarioRepository;
import br.com.ccs.contaspagar.domain.service.UsuarioService;
import br.com.ccs.contaspagar.infra.exception.ContasPagarException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void cadastrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        try {
            repository.save(usuario);
        } catch (Exception e) {
            throw new ContasPagarException(HttpStatus.BAD_REQUEST, "Erro ao cadastrar usuário.", e);
        }
    }
}
