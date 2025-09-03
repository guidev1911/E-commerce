package com.guidev1911.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "itens_carrinho")
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    @Positive
    private Integer quantidade;

    public BigDecimal getSubtotal() {
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

    public ItemCarrinho() {
    }

    public ItemCarrinho(Long id, Carrinho carrinho, Produto produto, Integer quantidade) {
        this.id = id;
        this.carrinho = carrinho;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCarrinho that = (ItemCarrinho) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCarrinho(), that.getCarrinho()) && Objects.equals(getProduto(), that.getProduto()) && Objects.equals(getQuantidade(), that.getQuantidade());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCarrinho(), getProduto(), getQuantidade());
    }
}