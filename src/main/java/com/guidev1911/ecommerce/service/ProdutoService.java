package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto criar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto produtoDetalhes) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        produto.setNome(produtoDetalhes.getNome());
        produto.setDescricao(produtoDetalhes.getDescricao());
        produto.setPreco(produtoDetalhes.getPreco());
        produto.setEstoque(produtoDetalhes.getEstoque());

        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        produtoRepository.delete(produto);
    }
}