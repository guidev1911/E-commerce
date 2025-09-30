package com.guidev1911.ecommerce.repository;


import com.guidev1911.ecommerce.model.RefreshToken;
import com.guidev1911.ecommerce.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@Testcontainers
class RefreshTokenRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withStartupTimeout(Duration.ofMinutes(5));

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarEEncontrarTokenPorToken() {

        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("123456");
        usuarioRepository.save(usuario);

        RefreshToken token = new RefreshToken();
        token.setToken("token123");
        token.setUsuario(usuario);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshTokenRepository.save(token);

        Optional<RefreshToken> encontrado = refreshTokenRepository.findByToken("token123");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getUsuario().getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveDeletarTokenPorUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setSenha("abcdef");
        usuarioRepository.save(usuario);

        RefreshToken token = new RefreshToken();
        token.setToken("token456");
        token.setUsuario(usuario);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshTokenRepository.save(token);

        refreshTokenRepository.deleteByUsuario(usuario);

        Optional<RefreshToken> encontrado = refreshTokenRepository.findByToken("token456");
        assertThat(encontrado).isNotPresent();
    }

    @Test
    void deveDeletarTokensExpirados() {
        Usuario usuario = new Usuario();
        usuario.setNome("Ana");
        usuario.setEmail("ana@email.com");
        usuario.setSenha("111");
        usuarioRepository.save(usuario);

        RefreshToken tokenValido = new RefreshToken();
        tokenValido.setToken("valido");
        tokenValido.setUsuario(usuario);
        tokenValido.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshTokenRepository.save(tokenValido);

        RefreshToken tokenExpirado = new RefreshToken();
        tokenExpirado.setToken("expirado");
        tokenExpirado.setUsuario(usuario);
        tokenExpirado.setExpiryDate(Instant.now().minusSeconds(3600));
        refreshTokenRepository.save(tokenExpirado);

        refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());

        assertThat(refreshTokenRepository.findByToken("valido")).isPresent();
        assertThat(refreshTokenRepository.findByToken("expirado")).isNotPresent();
    }
}