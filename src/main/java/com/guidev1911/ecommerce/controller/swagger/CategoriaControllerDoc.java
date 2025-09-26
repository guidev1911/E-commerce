package com.guidev1911.ecommerce.controller.swagger;

import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.exception.global.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Tag(name = "Categoria", description = "Gerenciamento de categorias de produtos")
@RequestMapping("/api/v1/categorias")
public interface CategoriaControllerDoc {

    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
            content = @Content(schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Validação inválida",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<CategoriaDTO> criar(CategoriaDTO dto);

    @Operation(summary = "Criar várias categorias em lote", description = "Cria múltiplas categorias de uma vez")
    @ApiResponse(responseCode = "201", description = "Categorias criadas com sucesso",
            content = @Content(schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Validação inválida em alguma categoria",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<List<CategoriaDTO>> criarLote(List<CategoriaDTO> dtos);

    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias paginadas")
    @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class)))
    ResponseEntity<Page<CategoriaDTO>> listar(Pageable pageable);

    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada",
            content = @Content(schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<CategoriaDTO> buscarPorId(Long id);

    @Operation(summary = "Atualizar categoria", description = "Atualiza uma categoria existente")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = CategoriaDTO.class)))
    @ApiResponse(responseCode = "400", description = "Validação inválida",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<CategoriaDTO> atualizar(Long id, CategoriaDTO dto);

    @Operation(summary = "Deletar categoria", description = "Remove uma categoria pelo ID")
    @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    ResponseEntity<Void> deletar(Long id);
}