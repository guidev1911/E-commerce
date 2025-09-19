package com.guidev1911.ecommerce.dto;


import com.guidev1911.ecommerce.model.MetodoPagamento;
import com.guidev1911.ecommerce.model.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoDTO {

    private Long id;
    private StatusPagamento status;
    private MetodoPagamento metodo;
    private BigDecimal valor;
    private LocalDateTime confirmadoEm;

    public PagamentoDTO() {
    }

    public PagamentoDTO(Long id, StatusPagamento status, MetodoPagamento metodo, BigDecimal valor, LocalDateTime confirmadoEm) {
        this.id = id;
        this.status = status;
        this.metodo = metodo;
        this.valor = valor;
        this.confirmadoEm = confirmadoEm;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public BigDecimal getValor() {
        return valor;
    }
}