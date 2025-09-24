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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public MetodoPagamento getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPagamento metodo) {
        this.metodo = metodo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getConfirmadoEm() {
        return confirmadoEm;
    }

    public void setConfirmadoEm(LocalDateTime confirmadoEm) {
        this.confirmadoEm = confirmadoEm;
    }
}