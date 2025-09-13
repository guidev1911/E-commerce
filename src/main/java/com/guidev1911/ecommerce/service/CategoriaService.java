package com.guidev1911.ecommerce.service;

import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.exception.CategoriaNaoEncontradaException;
import com.guidev1911.ecommerce.mapper.CategoriaMapper;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.model.Produto;
import com.guidev1911.ecommerce.repository.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    public List<CategoriaDTO> criarVarias(List<CategoriaDTO> dtos) {
        List<Categoria> entidades = dtos.stream()
                .map(categoriaMapper::toEntity)
                .toList();

        List<Categoria> salvas = categoriaRepository.saveAll(entidades);

        return salvas.stream()
                .map(categoriaMapper::toDTO)
                .toList();
    }
    public Page<CategoriaDTO> listarTodos(Pageable pageable) {
        return categoriaRepository.findAll(pageable)
                .map(categoriaMapper::toDTO);
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