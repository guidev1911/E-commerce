package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.exception.CategoriaNaoEncontradaException;
import com.guidev1911.ecommerce.mapper.CategoriaMapper;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaDTO criar(CategoriaDTO dto) {
        Categoria categoria = categoriaMapper.toEntity(dto);
        Categoria salvo = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(salvo);
    }

    public List<CategoriaDTO> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = buscarOuFalhar(id);
        return categoriaMapper.toDTO(categoria);
    }

    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = buscarOuFalhar(id);
        categoriaMapper.updateFromDto(dto, categoria);
        Categoria atualizado = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(atualizado);
    }

    public void deletar(Long id) {
        Categoria categoria = buscarOuFalhar(id);
        categoriaRepository.delete(categoria);
    }

    private Categoria buscarOuFalhar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(id));
    }
}