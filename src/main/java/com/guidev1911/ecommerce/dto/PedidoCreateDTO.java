package com.guidev1911.ecommerce.dto;

import jakarta.validation.constraints.NotNull;

public class PedidoCreateDTO {
    @NotNull
    private Long enderecoId;

    public Long getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(Long enderecoId) {
        this.enderecoId = enderecoId;
    }
}
