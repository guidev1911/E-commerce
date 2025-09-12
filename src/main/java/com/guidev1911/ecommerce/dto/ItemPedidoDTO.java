package com.guidev1911.ecommerce.dto;

import com.guidev1911.ecommerce.model.FragilidadeProduto;
import com.guidev1911.ecommerce.model.PesoProduto;
import com.guidev1911.ecommerce.model.TamanhoProduto;

import java.math.BigDecimal;

public class ItemPedidoDTO {
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
    private PesoProduto peso;
    private TamanhoProduto tamanho;
    private FragilidadeProduto fragilidade;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public PesoProduto getPeso() {
        return peso;
    }

    public void setPeso(PesoProduto peso) {
        this.peso = peso;
    }

    public TamanhoProduto getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoProduto tamanho) {
        this.tamanho = tamanho;
    }

    public FragilidadeProduto getFragilidade() {
        return fragilidade;
    }

    public void setFragilidade(FragilidadeProduto fragilidade) {
        this.fragilidade = fragilidade;
    }
}