package com.guidev1911.ecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodo;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal valor;

    private LocalDateTime criadoEm;
    private LocalDateTime confirmadoEm;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    public Pagamento() {
    }

    public Pagamento(Long id, StatusPagamento status, MetodoPagamento metodo, BigDecimal valor, LocalDateTime criadoEm, LocalDateTime confirmadoEm, Pedido pedido) {
        this.id = id;
        this.status = status;
        this.metodo = metodo;
        this.valor = valor;
        this.criadoEm = criadoEm;
        this.confirmadoEm = confirmadoEm;
        this.pedido = pedido;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getConfirmadoEm() {
        return confirmadoEm;
    }

    public void setConfirmadoEm(LocalDateTime confirmadoEm) {
        this.confirmadoEm = confirmadoEm;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}