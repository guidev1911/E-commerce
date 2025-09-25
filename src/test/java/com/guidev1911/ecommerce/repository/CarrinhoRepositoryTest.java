package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.Role;
import com.guidev1911.ecommerce.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarrinhoRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("123456");
        usuario.setNome("Usuário Teste");
        usuario.getRoles().add(Role.ROLE_USER);

        usuario = usuarioRepository.save(usuario);
    }

    @Test
    void deveSalvarCarrinho() {
        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);

        Carrinho salvo = carrinhoRepository.save(carrinho);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getUsuario().getEmail()).isEqualTo("teste@teste.com");
    }

    @Test
    void deveBuscarCarrinhoPorUsuario() {
        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinhoRepository.save(carrinho);

        Optional<Carrinho> encontrado = carrinhoRepository.findByUsuario(usuario);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getUsuario().getNome()).isEqualTo("Usuário Teste");
    }

    @Test
    void deveRetornarVazioQuandoNaoExisteCarrinho() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setEmail("outro@teste.com");
        outroUsuario.setSenha("123456");
        outroUsuario.setNome("Outro Usuário");
        outroUsuario.getRoles().add(Role.ROLE_USER);

        outroUsuario = usuarioRepository.save(outroUsuario);

        Optional<Carrinho> resultado = carrinhoRepository.findByUsuario(outroUsuario);

        assertThat(resultado).isEmpty();
    }
}