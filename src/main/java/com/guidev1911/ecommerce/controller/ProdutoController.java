package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.service.ProdutoService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/produtos")
@Validated
public class ProdutoController {

    private final ProdutoService produtoService;

    private final Validator validator;

    public ProdutoController(ProdutoService produtoService, Validator validator) {
        this.produtoService = produtoService;
        this.validator = validator;
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
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoDTO dto) {
        ProdutoDTO criado = produtoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PostMapping("/lote")
    public ResponseEntity<List<ProdutoDTO>> criarLote(@RequestBody List<ProdutoDTO> dtos) {
        dtos.forEach(this::validarProduto);
        List<ProdutoDTO> criados = produtoService.criarVarios(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(criados);
    }

    private void validarProduto(ProdutoDTO dto) {
        Set<ConstraintViolation<ProdutoDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String mensagens = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(mensagens);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(
            @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoDTO atualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(atualizado);
    }
    @GetMapping("/classificacao")
    public Map<String, List<Map<String, String>>> getDimensoesEPesos() {

        List<Map<String, String>> tamanhos = List.of(
                Map.of("categoria", "PEQUENO", "descricao", "Itens pequenos, geralmente até 30cm, exemplo: celular, mouse"),
                Map.of("categoria", "MEDIO", "descricao", "Itens médios, 30-80cm, exemplo: micro-ondas"),
                Map.of("categoria", "GRANDE", "descricao", "Itens grandes, 80-150cm, exemplo: bicicleta"),
                Map.of("categoria", "ENORME", "descricao", "Itens muito grandes, acima de 150cm, exemplo: sofá, geladeira")
        );

        List<Map<String, String>> pesos = List.of(
                Map.of("categoria", "LEVE", "descricao", "Até 5kg, exemplo: livros, roupas"),
                Map.of("categoria", "MEDIO", "descricao", "5kg a 20kg, exemplo: micro-ondas, bagagem média"),
                Map.of("categoria", "PESADO", "descricao", "Acima de 20kg, exemplo: móveis, equipamentos grandes")
        );

        List<Map<String, String>> fragilidade = List.of(
                Map.of("categoria", "BAIXA", "descricao", "Itens resistentes, exemplo: metal, madeira"),
                Map.of("categoria", "MEDIA", "descricao", "Itens delicados, exemplo: cerâmica, plástico fino"),
                Map.of("categoria", "ALTA", "descricao", "Itens muito frágeis, exemplo: vidro, eletrônicos sensíveis")
        );

        return Map.of(
                "TamanhoProduto", tamanhos,
                "PesoProduto", pesos,
                "FragilidadeProduto", fragilidade
        );
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