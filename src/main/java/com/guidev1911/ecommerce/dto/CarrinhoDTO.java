package com.guidev1911.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarrinhoDTO {
    private Long id;
    private List<ItemCarrinhoDTO> itens;
    private BigDecimal total;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemCarrinhoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinhoDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
