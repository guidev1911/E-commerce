package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Endereco;
import com.guidev1911.ecommerce.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@Testcontainers
class EnderecoRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withStartupTimeout(Duration.ofMinutes(5));

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setNome("Teste Usuário");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("123456");
        usuarioRepository.save(usuario);
    }

    private Endereco novoEndereco() {
        Endereco endereco = new Endereco();
        endereco.setUsuario(usuario);
        endereco.setLogradouro("Rua X");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("Cidade Y");
        endereco.setEstado("Estado Z");
        endereco.setCep("00000-000");
        endereco.setPais("Brasil");
        endereco.setPrincipal(true);
        return endereco;
    }

    @Test
    void deveSalvarEndereco() {
        Endereco endereco = novoEndereco();
        Endereco salvo = enderecoRepository.save(endereco);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getLogradouro()).isEqualTo("Rua X");
        assertThat(salvo.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void deveBuscarEnderecoPorId() {
        Endereco endereco = enderecoRepository.save(novoEndereco());

        Optional<Endereco> encontrado = enderecoRepository.findById(endereco.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getLogradouro()).isEqualTo("Rua X");
    }

    @Test
    void deveAtualizarEndereco() {
        Endereco endereco = enderecoRepository.save(novoEndereco());

        endereco.setLogradouro("Avenida Nova");
        endereco.setNumero("456");

        Endereco atualizado = enderecoRepository.save(endereco);

        assertThat(atualizado.getLogradouro()).isEqualTo("Avenida Nova");
        assertThat(atualizado.getNumero()).isEqualTo("456");
    }

    @Test
    void deveDeletarEndereco() {
        Endereco endereco = enderecoRepository.save(novoEndereco());

        enderecoRepository.delete(endereco);

        Optional<Endereco> encontrado = enderecoRepository.findById(endereco.getId());
        assertThat(encontrado).isEmpty();
    }

    @Test
    void deveBuscarEnderecosPorUsuario() {
        Endereco endereco1 = novoEndereco();
        Endereco endereco2 = novoEndereco();
        endereco2.setLogradouro("Rua Y");
        endereco2.setPrincipal(false);

        enderecoRepository.save(endereco1);
        enderecoRepository.save(endereco2);

        List<Endereco> enderecos = enderecoRepository.findByUsuario(usuario);

        assertThat(enderecos).hasSize(2);
        assertThat(enderecos).extracting("logradouro").containsExactlyInAnyOrder("Rua X", "Rua Y");
    }
}