package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.exception.CategoriaNaoEncontradaException;
import com.guidev1911.ecommerce.exception.ProdutoNotFoundException;
import com.guidev1911.ecommerce.mapper.ProdutoMapper;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.CategoriaRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    public Page<ProdutoDTO> listarTodos(Pageable pageable) {
        Page<Produto> page = produtoRepository.findAll(pageable);
        return page.map(produtoMapper::toDTO);
    }

    public ProdutoDTO buscarPorId(Long id) {
        return produtoMapper.toDTO(verificarExistencia(id));
    }

    public ProdutoDTO criar(ProdutoDTO produtoDTO) {
        Categoria categoria = verificarCategoria(produtoDTO.getCategoriaId());
        Produto produto = produtoMapper.toEntity(produtoDTO);
        produto.setCategoria(categoria);
        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toDTO(salvo);
    }

    public ProdutoDTO atualizar(Long id, ProdutoDTO produtoDTO) {
        Produto produto = verificarExistencia(id);
        Categoria categoria = verificarCategoria(produtoDTO.getCategoriaId());

        produtoMapper.updateEntityFromDTO(produtoDTO, produto);
        produto.setCategoria(categoria);

        Produto atualizado = produtoRepository.save(produto);
        return produtoMapper.toDTO(atualizado);
    }

    public void deletar(Long id) {
        Produto produto = verificarExistencia(id);
        produtoRepository.delete(produto);
    }

    private Produto verificarExistencia(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));
    }

    private Categoria verificarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));
    }
}