package com.guidev1911.ecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private LocalDateTime criadoEm = LocalDateTime.now();

    private LocalDateTime expiraEm;

    private LocalDateTime pagoEm;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "endereco_id")
    private Endereco enderecoEntrega;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal frete = BigDecimal.ZERO;

    public Pedido() {
    }

    public Pedido(Long id, Usuario usuario, List<ItemPedido> itens, BigDecimal total, StatusPedido status, LocalDateTime criadoEm, LocalDateTime expiraEm, LocalDateTime pagoEm, Pagamento pagamento, Endereco enderecoEntrega, BigDecimal frete) {
        this.id = id;
        this.usuario = usuario;
        this.itens = itens;
        this.total = total;
        this.status = status;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
        this.pagoEm = pagoEm;
        this.pagamento = pagamento;
        this.enderecoEntrega = enderecoEntrega;
        this.frete = frete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(LocalDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }

    public LocalDateTime getPagoEm() {
        return pagoEm;
    }

    public void setPagoEm(LocalDateTime pagoEm) {
        this.pagoEm = pagoEm;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public BigDecimal getFrete() {
        return frete;
    }

    public void setFrete(BigDecimal frete) {
        this.frete = frete;
    }
}
