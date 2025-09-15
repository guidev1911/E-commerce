package com.guidev1911.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public class PedidoPreviewDTO {
    private List<ItemPedidoDTO> itens;
    private BigDecimal subtotal;
    private BigDecimal frete;
    private BigDecimal total;
    private Long enderecoId;

    public PedidoPreviewDTO(){

    }

    public PedidoPreviewDTO(List<ItemPedidoDTO> itens, BigDecimal subtotal, BigDecimal frete, BigDecimal total, Long enderecoId) {
        this.itens = itens;
        this.subtotal = subtotal;
        this.frete = frete;
        this.total = total;
        this.enderecoId = enderecoId;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getFrete() {
        return frete;
    }

    public void setFrete(BigDecimal frete) {
        this.frete = frete;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(Long enderecoId) {
        this.enderecoId = enderecoId;
    }
}