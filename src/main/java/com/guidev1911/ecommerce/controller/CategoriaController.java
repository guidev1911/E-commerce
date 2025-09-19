package com.guidev1911.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.service.CategoriaService;
import com.guidev1911.ecommerce.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody Object body) {
        List<CategoriaDTO> dtos;

        if (body instanceof List<?>) {
            dtos = ((List<?>) body).stream()
                    .map(o -> new ObjectMapper().convertValue(o, CategoriaDTO.class))
                    .peek(this::validarCategoria)
                    .toList();
        } else {
            CategoriaDTO dto = new ObjectMapper().convertValue(body, CategoriaDTO.class);
            validarCategoria(dto);
            dtos = List.of(dto);
        }

        List<CategoriaDTO> criadas = categoriaService.criarVarias(dtos);
        return ResponseUtil.singleOrList(criadas);
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(categoriaService.listarTodos(pageable));
    }

    private void validarCategoria(CategoriaDTO dto) {
        List<String> erros = new ArrayList<>();

        if (dto.getNome() == null || dto.getNome().isBlank()) {
            erros.add("O nome da categoria é obrigatório.");
        }

        if (!erros.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", erros));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        return ResponseEntity.ok(categoriaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}