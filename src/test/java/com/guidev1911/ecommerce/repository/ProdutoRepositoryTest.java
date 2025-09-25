package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProdutoRepositoryTest {

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
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoria;

    @BeforeEach
    void setup() {
        categoria = new Categoria();
        categoria.setNome("Eletrônicos");
        categoria.setDescricao("Categoria de eletrônicos");
        categoriaRepository.save(categoria);
    }

    private Produto novoProduto() {
        Produto produto = new Produto();
        produto.setNome("Smartphone");
        produto.setDescricao("Smartphone Android topo de linha");
        produto.setPreco(BigDecimal.valueOf(2999.90));
        produto.setEstoque(50);
        produto.setCategoria(categoria);
        produto.setTamanho(TamanhoProduto.MEDIO);
        produto.setPeso(PesoProduto.MEDIO);
        produto.setFragilidade(FragilidadeProduto.MEDIA);
        return produto;
    }

    @Test
    void deveSalvarProduto() {
        Produto produto = novoProduto();

        Produto salvo = produtoRepository.save(produto);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Smartphone");
    }

    @Test
    void deveBuscarProdutoPorId() {
        Produto produto = produtoRepository.save(novoProduto());

        Optional<Produto> encontrado = produtoRepository.findById(produto.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Smartphone");
    }

    @Test
    void deveAtualizarProduto() {
        Produto produto = produtoRepository.save(novoProduto());

        produto.setNome("Notebook Gamer");
        produto.setPreco(BigDecimal.valueOf(5999.99));

        Produto atualizado = produtoRepository.save(produto);

        assertThat(atualizado.getNome()).isEqualTo("Notebook Gamer");
        assertThat(atualizado.getPreco()).isEqualTo(BigDecimal.valueOf(5999.99));
    }

    @Test
    void deveDeletarProduto() {
        Produto produto = produtoRepository.save(novoProduto());

        produtoRepository.delete(produto);

        Optional<Produto> encontrado = produtoRepository.findById(produto.getId());
        assertThat(encontrado).isEmpty();
    }
}