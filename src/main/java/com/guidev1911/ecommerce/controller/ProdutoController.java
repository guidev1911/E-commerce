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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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