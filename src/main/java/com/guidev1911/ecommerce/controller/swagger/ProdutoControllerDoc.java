package com.guidev1911.ecommerce.controller.swagger;

import com.guidev1911.ecommerce.dto.ProdutoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "Produtos", description = "Permite gerenciar produtos em um e-commerce. Suporta criação de um produto ou múltiplos produtos de uma vez, além de listagem paginada com filtros opcionais.")
@RequestMapping("/api/v1/produtos")
public interface ProdutoControllerDoc {

    @Operation(summary = "Listar produtos", description = "Lista produtos com filtros opcionais e paginação")
    @GetMapping
    ResponseEntity<Page<ProdutoDTO>> listarTodos(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) String nome,
            @ParameterObject Pageable pageable);

    @Operation(summary = "Criar produto", description = "Cria um novo produto. É necessário já existir uma categoria associada")
    @PostMapping
    ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoDTO dto);

    @Operation(summary = "Criar vários produtos em lote", description = "Cria vários produtos de uma só vez")
    @PostMapping("/lote")
    ResponseEntity<List<ProdutoDTO>> criarLote(@RequestBody List<ProdutoDTO> dtos);

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @PutMapping("/{id}")
    ResponseEntity<ProdutoDTO> atualizar(
            @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO);

    @Operation(summary = "Classificação de produtos", description = "Tabela de tamanhos, pesos e fragilidade para cálculo de frete")
    @GetMapping("/classificacao")
    Map<String, List<Map<String, String>>> getDimensoesEPesos();

    @Operation(summary = "Buscar produto por ID", description = "Busca um produto pelo seu ID")
    @GetMapping("/{id}")
    ResponseEntity<ProdutoDTO> buscarPorId(
            @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id);

    @Operation(summary = "Deletar produto", description = "Remove um produto pelo seu ID")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletar(
            @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id);
}