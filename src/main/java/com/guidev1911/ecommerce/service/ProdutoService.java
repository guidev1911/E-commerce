package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.mapper.ProdutoMapper;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    public List<ProdutoDTO> listarTodos() {
        return produtoMapper.toDTOList(produtoRepository.findAll());
    }

    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        return produtoMapper.toDTO(produto);
    }

    public ProdutoDTO criar(ProdutoDTO produtoDTO) {
        Produto produto = produtoMapper.toEntity(produtoDTO);
        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toDTO(salvo);
    }

    public ProdutoDTO atualizar(Long id, ProdutoDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setEstoque(produtoDTO.getEstoque());

        Produto atualizado = produtoRepository.save(produto);
        return produtoMapper.toDTO(atualizado);
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        produtoRepository.delete(produto);
    }
}