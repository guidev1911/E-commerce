package com.guidev1911.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.CategoriaDTO;
import com.guidev1911.ecommerce.exception.CategoriaNaoEncontradaException;
import com.guidev1911.ecommerce.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class CategoriaControllerTest {

    @InjectMocks
    private CategoriaController categoriaController;

    @Mock
    private CategoriaService categoriaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1L);
        categoriaDTO.setNome("Informática");
        categoriaDTO.setDescricao("Eletrônicos e computadores");
    }
    @Test
    void deveCriarUmaCategoriaSingle() throws Exception {
        when(categoriaService.criarVarias(any())).thenReturn(List.of(categoriaDTO));

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Informática"));

        verify(categoriaService).criarVarias(any());
    }

    @Test
    void deveCriarVariasCategorias() throws Exception {
        CategoriaDTO c2 = new CategoriaDTO();
        c2.setId(2L);
        c2.setNome("Escritório");
        c2.setDescricao("Materiais de escritório");

        when(categoriaService.criarVarias(any())).thenReturn(List.of(categoriaDTO, c2));

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(categoriaDTO, c2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(categoriaService).criarVarias(any());
    }

    @Test
    void deveListarCategoriasPaginadas() throws Exception {
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1L);
        categoriaDTO.setNome("Eletrônicos");
        categoriaDTO.setDescricao("Categoria de eletrônicos");

        Page<CategoriaDTO> page = new PageImpl<>(
                List.of(categoriaDTO),
                PageRequest.of(0, 10),
                1
        );

        when(categoriaService.listarTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/categorias")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nome").value("Eletrônicos"))
                .andExpect(jsonPath("$.content[0].descricao").value("Categoria de eletrônicos"));

        verify(categoriaService).listarTodos(any(Pageable.class));
    }

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(categoriaDTO);

        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Informática"));

        verify(categoriaService).buscarPorId(1L);
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        when(categoriaService.atualizar(eq(1L), any(CategoriaDTO.class))).thenReturn(categoriaDTO);

        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Informática"));

        verify(categoriaService).atualizar(eq(1L), any(CategoriaDTO.class));
    }

    @Test
    void deveDeletarCategoria() throws Exception {
        doNothing().when(categoriaService).deletar(1L);

        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService).deletar(1L);
    }
}