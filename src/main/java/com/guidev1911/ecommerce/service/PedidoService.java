package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.dto.ItemCarrinhoDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.guidev1911.ecommerce.service.PedidoUtils.recalcularTotal;

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

    public PedidoDTO criarPedido(Usuario usuario) {
        CarrinhoDTO carrinho = carrinhoService.listarCarrinho(usuario);

        if (carrinho.getItens().isEmpty()) {
            throw new CarrinhoVazioException("Carrinho vazio, não é possível criar pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTotal(recalcularTotal(pedido));
        pedido.setCriadoEm(LocalDateTime.now());
        pedido.setExpiraEm(LocalDateTime.now().plusHours(24));

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

        Pedido salvo = pedidoRepository.save(pedido);

        carrinhoService.limparCarrinho(usuario);

        return pedidoMapper.toDTO(salvo);
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
            pedido.setStatus(StatusPedido.CANCELADO);
            pedidoRepository.save(pedido);
        }

        return pedidoMapper.toDTO(pedido);
    }

    public PedidoDTO cancelarPedido(Usuario usuario, Long id) {
        Pedido pedido = pedidoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.ENVIADO ||
                pedido.getStatus() == StatusPedido.CANCELADO ||
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
                pedido.setStatus(StatusPedido.CANCELADO);
                pedidoRepository.save(pedido);
            }
        }
    }
}