package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.mapper.ProdutoMapper;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    public Page<ProdutoDTO> listarTodos(Pageable pageable) {
        Page<Produto> page = produtoRepository.findAll(pageable);
        return page.map(produtoMapper::toDTO);
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

        produtoMapper.updateEntityFromDTO(produtoDTO, produto);

        Produto atualizado = produtoRepository.save(produto);
        return produtoMapper.toDTO(atualizado);
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        produtoRepository.delete(produto);
    }
}
