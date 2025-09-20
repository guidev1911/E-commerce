package com.guidev1911.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.model.Categoria;
import com.guidev1911.ecommerce.service.CategoriaService;
import com.guidev1911.ecommerce.util.ResponseUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final Validator validator;

    public CategoriaController(CategoriaService categoriaService, Validator validator) {
        this.categoriaService = categoriaService;
        this.validator = validator;
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> criar(@Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO criada = categoriaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PostMapping("/lote")
    public ResponseEntity<List<CategoriaDTO>> criarLote(@RequestBody List<CategoriaDTO> dtos) {
        dtos.forEach(this::validarCategoria);
        List<CategoriaDTO> criadas = categoriaService.criarVarias(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(criadas);
    }

    private void validarCategoria(CategoriaDTO dto) {
        Set<ConstraintViolation<CategoriaDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String mensagens = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(mensagens);
        }
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(categoriaService.listarTodos(pageable));
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