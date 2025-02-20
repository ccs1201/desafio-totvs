package br.com.ccs.contaspagar.domain.repository;

import br.com.ccs.contaspagar.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, String> {
    Optional <UserDetails> findByLogin(String login);
}