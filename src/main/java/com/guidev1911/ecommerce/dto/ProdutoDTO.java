package com.guidev1911.ecommerce.dto;

import com.guidev1911.ecommerce.model.FragilidadeProduto;
import com.guidev1911.ecommerce.model.PesoProduto;
import com.guidev1911.ecommerce.model.TamanhoProduto;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ProdutoDTO {
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "O estoque é obrigatório")
    @Positive(message = "O estoque deve ser maior que zero")
    private Integer estoque;

    @NotNull(message = "Categoria de tamanho é obrigatório")
    private TamanhoProduto tamanho;

    @NotNull(message = "Categoria de peso é obrigatório")
    private PesoProduto peso;

    @NotNull(message = "Categoria de fragilidade é obrigatório")
    private FragilidadeProduto fragilidade;

    @NotNull
    private Long categoriaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public TamanhoProduto getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoProduto tamanho) {
        this.tamanho = tamanho;
    }

    public PesoProduto getPeso() {
        return peso;
    }

    public void setPeso(PesoProduto peso) {
        this.peso = peso;
    }

    public FragilidadeProduto getFragilidade() {
        return fragilidade;
    }

    public void setFragilidade(FragilidadeProduto fragilidade) {
        this.fragilidade = fragilidade;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
