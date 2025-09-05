package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.dto.ItemCarrinhoDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.mapper.PedidoMapper;
import com.guidev1911.ecommerce.model.*;
import com.guidev1911.ecommerce.repository.PedidoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new RuntimeException("Carrinho vazio, não é possível criar pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTotal(carrinho.getTotal());

        List<Long> produtoIds = carrinho.getItens().stream()
                .map(ItemCarrinhoDTO::getProdutoId)
                .toList();

        Map<Long, Produto> produtos = produtoRepository.findAllById(produtoIds)
                .stream()
                .collect(Collectors.toMap(Produto::getId, p -> p));

        for (ItemCarrinhoDTO item : carrinho.getItens()) {
            Produto produto = produtos.get(item.getProdutoId());
            if (produto == null) {
                throw new RuntimeException("Produto não encontrado: " + item.getProdutoId());
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
        return pedidoMapper.toDTO(salvo);
    }

    public List<PedidoDTO> listarPedidos(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario).stream()
                .map(pedidoMapper::toDTO)
                .toList();
    }

    public PedidoDTO buscarPorId(Usuario usuario, Long id) {
        Pedido pedido = pedidoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return pedidoMapper.toDTO(pedido);
    }
}
