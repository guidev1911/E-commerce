package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.exception.CarrinhoNotFoundException;
import com.guidev1911.ecommerce.exception.ProdutoNotFoundException;
import com.guidev1911.ecommerce.mapper.CarrinhoMapper;
import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.ItemCarrinho;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.CarrinhoRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CarrinhoMapper carrinhoMapper;

    public CarrinhoDTO criarCarrinho() {
        Carrinho carrinho = new Carrinho();
        carrinho = carrinhoRepository.save(carrinho);
        return carrinhoMapper.toDTO(carrinho);
    }

    public CarrinhoDTO adicionarItem(Long carrinhoId, Long produtoId, Integer quantidade) {
        Carrinho carrinho = buscarCarrinho(carrinhoId);
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNotFoundException(produtoId));

        Optional<ItemCarrinho> existente = carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setQuantidade(existente.get().getQuantidade() + quantidade);
        } else {
            ItemCarrinho item = new ItemCarrinho();
            item.setCarrinho(carrinho);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            carrinho.getItens().add(item);
        }

        carrinho = carrinhoRepository.save(carrinho);
        return carrinhoMapper.toDTO(carrinho);
    }

    public CarrinhoDTO removerItem(Long carrinhoId, Long produtoId) {
        Carrinho carrinho = buscarCarrinho(carrinhoId);
        carrinho.getItens().removeIf(i -> i.getProduto().getId().equals(produtoId));
        carrinho = carrinhoRepository.save(carrinho);
        return carrinhoMapper.toDTO(carrinho);
    }

    public CarrinhoDTO listarCarrinho(Long carrinhoId) {
        Carrinho carrinho = buscarCarrinho(carrinhoId);
        return carrinhoMapper.toDTO(carrinho);
    }

    private Carrinho buscarCarrinho(Long id) {
        return carrinhoRepository.findById(id)
                .orElseThrow(() -> new CarrinhoNotFoundException("Carrinho não encontrado"));
    }
}
