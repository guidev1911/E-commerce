package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarEEncontrarUsuarioPorEmail() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("123456");

        usuarioRepository.save(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("joao@email.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("João");
    }

    @Test
    void deveVerificarSeEmailExiste() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setSenha("abcdef");
        usuarioRepository.save(usuario);

        boolean existe = usuarioRepository.existsByEmail("maria@email.com");
        boolean naoExiste = usuarioRepository.existsByEmail("outro@email.com");

        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }

    @Test
    void deveLancarExcecaoSeEmailDuplicado() {
        Usuario usuario1 = new Usuario();
        usuario1.setNome("Ana");
        usuario1.setEmail("ana@email.com");
        usuario1.setSenha("111");
        usuarioRepository.save(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNome("Ana Clone");
        usuario2.setEmail("ana@email.com");
        usuario2.setSenha("222");

        assertThrows(Exception.class, () -> usuarioRepository.saveAndFlush(usuario2));
    }
}