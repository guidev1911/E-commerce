package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@Testcontainers
class CategoriaRepositoryTest {

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
    private CategoriaRepository categoriaRepository;

    private Categoria novaCategoria;

    @BeforeEach
    void setup() {
        novaCategoria = new Categoria();
        novaCategoria.setNome("Informática");
        novaCategoria.setDescricao("Categoria de informática e tecnologia");
    }

    @Test
    @DisplayName("Deve salvar categoria com sucesso")
    void deveSalvarCategoria() {
        Categoria salvo = categoriaRepository.save(novaCategoria);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Informática");
    }

    @Test
    @DisplayName("Deve buscar categoria por ID")
    void deveBuscarCategoriaPorId() {
        Categoria salvo = categoriaRepository.save(novaCategoria);

        Optional<Categoria> encontrada = categoriaRepository.findById(salvo.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNome()).isEqualTo("Informática");
    }

    @Test
    @DisplayName("Deve atualizar categoria")
    void deveAtualizarCategoria() {
        Categoria salvo = categoriaRepository.save(novaCategoria);

        salvo.setNome("Eletrônicos");
        salvo.setDescricao("Categoria de produtos eletrônicos");

        Categoria atualizado = categoriaRepository.save(salvo);

        assertThat(atualizado.getNome()).isEqualTo("Eletrônicos");
        assertThat(atualizado.getDescricao()).isEqualTo("Categoria de produtos eletrônicos");
    }

    @Test
    @DisplayName("Deve deletar categoria")
    void deveDeletarCategoria() {
        Categoria salvo = categoriaRepository.save(novaCategoria);

        categoriaRepository.delete(salvo);

        Optional<Categoria> encontrada = categoriaRepository.findById(salvo.getId());
        assertThat(encontrada).isEmpty();
    }
}