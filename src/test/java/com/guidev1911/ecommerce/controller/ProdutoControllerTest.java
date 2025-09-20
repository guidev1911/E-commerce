package com.guidev1911.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.ProdutoDTO;
import com.guidev1911.ecommerce.exception.global.GlobalExceptionHandler;
import com.guidev1911.ecommerce.model.FragilidadeProduto;
import com.guidev1911.ecommerce.model.PesoProduto;
import com.guidev1911.ecommerce.model.TamanhoProduto;
import com.guidev1911.ecommerce.service.ProdutoService;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;



@ExtendWith(SpringExtension.class)
class ProdutoControllerTest {

    @InjectMocks
    private ProdutoController produtoController;

    @Mock
    private ProdutoService produtoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        produtoController = new ProdutoController(produtoService, validator);

        mockMvc = MockMvcBuilders.standaloneSetup(produtoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);
        produtoDTO.setNome("Notebook");
        produtoDTO.setDescricao("Notebook gamer");
        produtoDTO.setPreco(BigDecimal.valueOf(5000));
        produtoDTO.setEstoque(10);
        produtoDTO.setCategoriaId(1L);
        produtoDTO.setTamanho(TamanhoProduto.PEQUENO);
        produtoDTO.setPeso(PesoProduto.LEVE);
        produtoDTO.setFragilidade(FragilidadeProduto.BAIXA);
    }

    @Test
    void deveCriarProdutoUnico() throws Exception {
        when(produtoService.criar(any())).thenReturn(produtoDTO);

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook"))
                .andExpect(jsonPath("$.descricao").value("Notebook gamer"));

        verify(produtoService).criar(any());
    }

    @Test
    void deveCriarListaProdutos() throws Exception {
        ProdutoDTO produto2 = new ProdutoDTO();
        produto2.setId(2L);
        produto2.setNome("Mouse");
        produto2.setDescricao("Mouse gamer");
        produto2.setPreco(BigDecimal.valueOf(250));
        produto2.setEstoque(30);
        produto2.setTamanho(TamanhoProduto.PEQUENO);
        produto2.setPeso(PesoProduto.LEVE);
        produto2.setFragilidade(FragilidadeProduto.MEDIA);
        produto2.setCategoriaId(1L);

        when(produtoService.criarVarios(any())).thenReturn(List.of(produtoDTO, produto2));

        mockMvc.perform(post("/api/v1/produtos/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(produtoDTO, produto2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].nome").value("Notebook"))
                .andExpect(jsonPath("$[1].nome").value("Mouse"));

        verify(produtoService).criarVarios(any());
    }

    @Test
    void deveRetornar400QuandoProdutoUnicoInvalido() throws Exception {
        ProdutoDTO dtoInvalido = new ProdutoDTO();

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deveRetornar400QuandoListaProdutosContemInvalido() throws Exception {
        ProdutoDTO dtoValido = produtoDTO;
        ProdutoDTO dtoInvalido = new ProdutoDTO(); // sem campos obrigatórios

        mockMvc.perform(post("/api/v1/produtos/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(dtoValido, dtoInvalido))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deveRetornar400QuandoNaoEnviarBody() throws Exception {
        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request body é obrigatório"));
    }

    @Test
    void deveListarProdutosComFiltrosEPaginacao() throws Exception {
        ProdutoDTO produto2 = new ProdutoDTO();
        produto2.setId(2L);
        produto2.setNome("Mouse");
        produto2.setPreco(BigDecimal.valueOf(250));
        produto2.setEstoque(20);
        produto2.setTamanho(TamanhoProduto.PEQUENO);
        produto2.setPeso(PesoProduto.LEVE);
        produto2.setFragilidade(FragilidadeProduto.MEDIA);
        produto2.setCategoriaId(1L);

        Page<ProdutoDTO> page = new PageImpl<>(List.of(produtoDTO, produto2), PageRequest.of(0, 10), 2);
        when(produtoService.listarFiltrado(any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/produtos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("nome", "Notebook")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(produtoService).listarFiltrado(any(), any(), any(), any(), any());
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        when(produtoService.buscarPorId(1L)).thenReturn(produtoDTO);

        mockMvc.perform(get("/api/v1/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook"));

        verify(produtoService).buscarPorId(1L);
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        ProdutoDTO atualizado = produtoDTO;
        atualizado.setNome("Notebook Atualizado");

        when(produtoService.atualizar(eq(1L), any())).thenReturn(atualizado);

        mockMvc.perform(put("/api/v1/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Notebook Atualizado"));

        verify(produtoService).atualizar(eq(1L), any());
    }

    @Test
    void deveDeletarProduto() throws Exception {
        doNothing().when(produtoService).deletar(1L);

        mockMvc.perform(delete("/api/v1/produtos/1"))
                .andExpect(status().isNoContent());

        verify(produtoService).deletar(1L);
    }

    @Test
    void deveRetornarClassificacaoProdutos() throws Exception {
        mockMvc.perform(get("/api/v1/produtos/classificacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.TamanhoProduto").exists())
                .andExpect(jsonPath("$.PesoProduto").exists())
                .andExpect(jsonPath("$.FragilidadeProduto").exists());
    }
}