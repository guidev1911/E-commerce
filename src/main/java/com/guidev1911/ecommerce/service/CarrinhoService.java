package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.exception.ProdutoNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.CarrinhoMapper;
import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.ItemCarrinho;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.repository.CarrinhoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final CarrinhoMapper carrinhoMapper;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ProdutoRepository produtoRepository,
                           CarrinhoMapper carrinhoMapper) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.carrinhoMapper = carrinhoMapper;
    }

    public CarrinhoDTO alterarItem(Usuario usuario, Long produtoId, Integer quantidade, boolean incrementar) {
        Carrinho carrinho = buscarOuCriarCarrinho(usuario);
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));

        Optional<ItemCarrinho> itemOpt = carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst();

        if (itemOpt.isPresent()) {
            ItemCarrinho item = itemOpt.get();
            int novaQuantidade = incrementar
                    ? item.getQuantidade() + quantidade
                    : quantidade;

            if (novaQuantidade <= 0) {
                carrinho.getItens().remove(item);
            } else if (novaQuantidade > produto.getEstoque()) {
                throw new RuntimeException("Quantidade solicitada excede o estoque disponível.");
            } else {
                item.setQuantidade(novaQuantidade);
            }
        } else if (quantidade > 0) {
            if (quantidade > produto.getEstoque()) {
                throw new RuntimeException("Quantidade solicitada excede o estoque disponível.");
            }
            ItemCarrinho novoItem = new ItemCarrinho();
            novoItem.setCarrinho(carrinho);
            novoItem.setProduto(produto);
            novoItem.setQuantidade(quantidade);
            carrinho.getItens().add(novoItem);
        }

        carrinho = carrinhoRepository.save(carrinho);
        return carrinhoMapper.toDTO(carrinho);
    }

    public CarrinhoDTO removerItem(Usuario usuario, Long produtoId) {
        Carrinho carrinho = buscarOuCriarCarrinho(usuario);
        carrinho.getItens().removeIf(i -> i.getProduto().getId().equals(produtoId));
        carrinho = carrinhoRepository.save(carrinho);
        return carrinhoMapper.toDTO(carrinho);
    }

    public CarrinhoDTO listarCarrinho(Usuario usuario) {
        Carrinho carrinho = buscarOuCriarCarrinho(usuario);
        return carrinhoMapper.toDTO(carrinho);
    }

    private Carrinho buscarOuCriarCarrinho(Usuario usuario) {
        return carrinhoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrinho novo = new Carrinho();
                    novo.setUsuario(usuario);
                    return carrinhoRepository.save(novo);
                });
    }
}
