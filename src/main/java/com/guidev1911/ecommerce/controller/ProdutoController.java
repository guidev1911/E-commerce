package com.guidev1911.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.service.ProdutoService;

import com.guidev1911.ecommerce.util.ResponseUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/produtos")
@Validated
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoDTO>> listarTodos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) String nome,
            Pageable pageable) {

        Page<ProdutoDTO> produtos = produtoService.listarFiltrado(categoriaId, precoMin, precoMax, nome, pageable);
        return ResponseEntity.ok(produtos);
    }

    @PostMapping
    public ResponseEntity<Object> criar(@Valid @RequestBody Object body) {
        List<ProdutoDTO> produtosDTO;

        if (body instanceof List<?>) {
            produtosDTO = ((List<?>) body).stream()
                    .map(o -> new ObjectMapper().convertValue(o, ProdutoDTO.class))
                    .toList();
        } else {
            produtosDTO = List.of(new ObjectMapper().convertValue(body, ProdutoDTO.class));
        }

        List<ProdutoDTO> criados = produtoService.criarVarios(produtosDTO);
        return ResponseUtil.singleOrList(criados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(
            @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO atualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(
            @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}