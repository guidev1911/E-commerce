package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.exception.CategoriaNaoEncontradaException;
import com.guidev1911.ecommerce.exception.ProdutoNaoEncontradoException;
import com.guidev1911.ecommerce.mapper.ProdutoMapper;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.CategoriaRepository;
import com.guidev1911.ecommerce.repository.ProdutoRepository;
import com.guidev1911.ecommerce.specification.ProdutoSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.produtoMapper = produtoMapper;
    }

    public Page<ProdutoDTO> listarFiltrado(
            Long categoriaId,
            BigDecimal precoMin,
            BigDecimal precoMax,
            String nome,
            Pageable pageable) {

        Specification<Produto> spec = Specification.allOf(
                ProdutoSpecification.comCategoria(categoriaId),
                ProdutoSpecification.comPrecoEntre(precoMin, precoMax),
                ProdutoSpecification.comNome(nome)
        );

        Page<Produto> page = produtoRepository.findAll(spec, pageable);
        return page.map(produtoMapper::toDTO);
    }

    public ProdutoDTO buscarPorId(Long id) {
        return produtoMapper.toDTO(verificarExistencia(id));
    }

    public ProdutoDTO criar(ProdutoDTO dto) {
        Produto produto = produtoMapper.toEntity(dto);
        produto.setCategoria(verificarCategoria(dto.getCategoriaId()));
        Produto salvo = produtoRepository.save(produto);
        return produtoMapper.toDTO(salvo);
    }

    public List<ProdutoDTO> criarVarios(List<ProdutoDTO> produtosDTO) {
        List<Produto> entidades = produtosDTO.stream()
                .map(dto -> {
                    Produto produto = produtoMapper.toEntity(dto);
                    produto.setCategoria(verificarCategoria(dto.getCategoriaId()));
                    return produto;
                })
                .toList();

        List<Produto> salvos = produtoRepository.saveAll(entidades);

        return salvos.stream()
                .map(produtoMapper::toDTO)
                .toList();
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
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    private Categoria verificarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));
    }
}
