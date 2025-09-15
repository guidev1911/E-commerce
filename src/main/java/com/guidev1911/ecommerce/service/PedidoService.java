package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.*;
import com.guidev1911.ecommerce.exception.CancelamentoNaoPermitidoException;
import com.guidev1911.ecommerce.exception.CarrinhoVazioException;
import com.guidev1911.ecommerce.exception.PedidoNaoEncontradoException;
import com.guidev1911.ecommerce.exception.ProdutoNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.PedidoMapper;
import com.guidev1911.ecommerce.model.*;
import com.guidev1911.ecommerce.repository.PedidoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.guidev1911.ecommerce.util.PedidoUtils.recalcularTotal;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoService carrinhoService;
    private final PedidoMapper pedidoMapper;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         CarrinhoService carrinhoService,
                         PedidoMapper pedidoMapper,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoService = carrinhoService;
        this.pedidoMapper = pedidoMapper;
        this.produtoRepository = produtoRepository;
    }

    public PedidoPreviewDTO simularPedido(Usuario usuario, PedidoCreateDTO dto) {
        CarrinhoDTO carrinho = carrinhoService.listarCarrinho(usuario);

        Pedido pedido = montarPedido(usuario, dto, carrinho);

        BigDecimal subtotal = recalcularTotal(pedido);
        BigDecimal frete = calcularFreteSimulado(pedido);
        BigDecimal total = subtotal.add(frete).setScale(2, RoundingMode.HALF_UP);

        List<ItemPedidoDTO> itensPreview = pedido.getItens().stream()
                .map(pedidoMapper::toItemDTO)
                .toList();

        return new PedidoPreviewDTO(
                itensPreview,
                subtotal.setScale(2, RoundingMode.HALF_UP),
                frete,
                total,
                pedido.getEnderecoEntrega().getId()
        );
    }

    public PedidoDTO criarPedido(Usuario usuario, PedidoCreateDTO dto) {
        CarrinhoDTO carrinho = carrinhoService.listarCarrinho(usuario);

        Pedido pedido = montarPedido(usuario, dto, carrinho);

        BigDecimal frete = calcularFreteSimulado(pedido);
        pedido.setFrete(frete);
        pedido.setTotal(recalcularTotal(pedido).add(frete).setScale(2, RoundingMode.HALF_UP));

        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setCriadoEm(LocalDateTime.now());
        pedido.setExpiraEm(LocalDateTime.now().plusHours(24));

        Pedido salvo = pedidoRepository.save(pedido);

        carrinhoService.limparCarrinho(usuario);

        return pedidoMapper.toDTO(salvo);
    }

    private Pedido montarPedido(Usuario usuario, PedidoCreateDTO dto, CarrinhoDTO carrinho) {
        if (carrinho.getItens().isEmpty()) {
            throw new CarrinhoVazioException("Carrinho vazio, não é possível criar pedido.");
        }

        Endereco endereco = usuario.getEnderecos().stream()
                .filter(e -> e.getId().equals(dto.getEnderecoId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado para este usuário."));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEnderecoEntrega(endereco);

        List<Long> produtoIds = carrinho.getItens().stream()
                .map(ItemCarrinhoDTO::getProdutoId)
                .toList();

        Map<Long, Produto> produtos = produtoRepository.findAllById(produtoIds)
                .stream()
                .collect(Collectors.toMap(Produto::getId, p -> p));

        for (ItemCarrinhoDTO item : carrinho.getItens()) {
            Produto produto = produtos.get(item.getProdutoId());
            if (produto == null) {
                throw new ProdutoNaoEncontradoException(item.getProdutoId());
            }

            ItemPedido ip = new ItemPedido();
            ip.setPedido(pedido);
            ip.setProduto(produto);
            ip.setQuantidade(item.getQuantidade());
            ip.setPrecoUnitario(item.getPrecoUnitario());
            ip.setSubtotal(item.getSubtotal());

            pedido.getItens().add(ip);
        }

        return pedido;
    }

    private BigDecimal calcularFreteSimulado(Pedido pedido) {
        BigDecimal freteTotal = BigDecimal.ZERO;
        BigDecimal fatorEstado = fatorRegiao(pedido.getEnderecoEntrega().getEstado());

        for (ItemPedido item : pedido.getItens()) {
            Produto p = item.getProduto();
            BigDecimal freteItem = BigDecimal.ZERO;

            switch (p.getPeso()) {
                case LEVE -> freteItem = freteItem.add(new BigDecimal("5"));
                case MEDIO -> freteItem = freteItem.add(new BigDecimal("10"));
                case PESADO -> freteItem = freteItem.add(new BigDecimal("20"));
            }

            switch (p.getTamanho()) {
                case PEQUENO -> freteItem = freteItem.add(new BigDecimal("2"));
                case MEDIO -> freteItem = freteItem.add(new BigDecimal("5"));
                case GRANDE -> freteItem = freteItem.add(new BigDecimal("10"));
                case ENORME -> freteItem = freteItem.add(new BigDecimal("20"));
            }

            switch (p.getFragilidade()) {
                case BAIXA -> {}
                case MEDIA -> freteItem = freteItem.multiply(new BigDecimal("1.2"));
                case ALTA -> freteItem = freteItem.multiply(new BigDecimal("1.5"));
            }

            freteItem = freteItem.multiply(BigDecimal.valueOf(item.getQuantidade()))
                    .multiply(fatorEstado);

            freteTotal = freteTotal.add(freteItem);
        }

        return freteTotal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal fatorRegiao(String estadoDestino) {
        estadoDestino = estadoDestino.toUpperCase();

        List<String> nordeste = List.of("AL", "BA", "CE", "MA", "PB", "PE", "PI", "RN", "SE");
        List<String> sudeste = List.of("ES", "MG", "RJ", "SP");
        List<String> sul = List.of("PR", "RS", "SC");
        List<String> centroOeste = List.of("DF", "GO", "MT", "MS");
        List<String> norte = List.of("AC", "AP", "AM", "PA", "RO", "RR", "TO");

        if (estadoDestino.equals("SE")) return new BigDecimal("1.0");

        if (nordeste.contains(estadoDestino)) return new BigDecimal("1.3");
        if (sudeste.contains(estadoDestino)) return new BigDecimal("2.5");
        if (sul.contains(estadoDestino)) return new BigDecimal("3.0");
        if (centroOeste.contains(estadoDestino)) return new BigDecimal("3.5");
        if (norte.contains(estadoDestino)) return new BigDecimal("4.5");

        return new BigDecimal("3.0");
    }

    public List<PedidoDTO> listarPedidos(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario).stream()
                .map(pedidoMapper::toDTO)
                .toList();
    }

    public PedidoDTO buscarPorId(Usuario usuario, Long id) {
        Pedido pedido = pedidoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.PENDENTE &&
                pedido.getExpiraEm() != null &&
                pedido.getExpiraEm().isBefore(LocalDateTime.now())) {
            pedido.setStatus(StatusPedido.EXPIRADO);
            pedidoRepository.save(pedido);
        }

        return pedidoMapper.toDTO(pedido);
    }

    public PedidoDTO cancelarPedido(Usuario usuario, Long id) {
        Pedido pedido = pedidoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.ENVIADO ||
                pedido.getStatus() == StatusPedido.CANCELADO ||
                pedido.getStatus() == StatusPedido.EXPIRADO ||
                pedido.getStatus() == StatusPedido.CONCLUIDO) {
            throw new CancelamentoNaoPermitidoException("Não é possível cancelar este pedido no status atual: " + pedido.getStatus());
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);

        return pedidoMapper.toDTO(pedido);
    }

    @Scheduled(fixedRate = 60_000)
    public void cancelarPedidosExpirados() {
        List<Pedido> pedidosPendentes = pedidoRepository.findByStatus(StatusPedido.PENDENTE);
        for (Pedido pedido : pedidosPendentes) {
            if (pedido.getExpiraEm() != null &&
                    pedido.getExpiraEm().isBefore(LocalDateTime.now())) {
                pedido.setStatus(StatusPedido.EXPIRADO);
                pedidoRepository.save(pedido);
            }
        }
    }
}