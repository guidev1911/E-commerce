package com.guidev1911.ecommerce.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.EnderecoDTO;
import com.guidev1911.ecommerce.exception.EnderecoNaoEncontradoException;
import com.guidev1911.ecommerce.exception.EnderecoNaoPertenceAoUsuarioException;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.exception.global.GlobalExceptionHandler;
import com.guidev1911.ecommerce.service.EnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnderecoControllerTest {

    private EnderecoService enderecoService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        enderecoService = Mockito.mock(EnderecoService.class);
        EnderecoController enderecoController = new EnderecoController(enderecoService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(enderecoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        enderecoDTO = new EnderecoDTO();
        enderecoDTO.setLogradouro("Rua X");
        enderecoDTO.setNumero("123");
        enderecoDTO.setBairro("Centro");
        enderecoDTO.setCidade("Cidade Y");
        enderecoDTO.setEstado("Estado Z");
        enderecoDTO.setCep("00000-000");
        enderecoDTO.setPais("Brasil");
        enderecoDTO.setPrincipal(true);
    }

    @Test
    void deveAdicionarEndereco() throws Exception {
        when(enderecoService.adicionarEndereco(eq(1L), any())).thenReturn(enderecoDTO);

        mockMvc.perform(post("/usuarios/1/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua X"))
                .andExpect(jsonPath("$.numero").value("123"));
    }

    @Test
    void deveListarEnderecosDoUsuario() throws Exception {
        when(enderecoService.listarEnderecos(1L)).thenReturn(List.of(enderecoDTO));

        mockMvc.perform(get("/usuarios/1/enderecos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].logradouro").value("Rua X"))
                .andExpect(jsonPath("$[0].numero").value("123"));
    }

    @Test
    void deveAtualizarEndereco() throws Exception {
        when(enderecoService.atualizarEndereco(eq(1L), eq(2L), any())).thenReturn(enderecoDTO);

        mockMvc.perform(put("/usuarios/1/enderecos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua X"))
                .andExpect(jsonPath("$.numero").value("123"));
    }

    @Test
    void deveDeletarEndereco() throws Exception {
        doNothing().when(enderecoService).deletarEndereco(1L, 2L);

        mockMvc.perform(delete("/usuarios/1/enderecos/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroQuandoUsuarioNaoEncontrado() throws Exception {
        when(enderecoService.adicionarEndereco(eq(999L), any()))
                .thenThrow(new UsuarioNaoEncontradoException(999L));

        mockMvc.perform(post("/usuarios/999/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com id: 999"));
    }

    @Test
    void deveRetornarErroQuandoEnderecoNaoPertenceAoUsuario() throws Exception {
        when(enderecoService.atualizarEndereco(eq(1L), eq(2L), any()))
                .thenThrow(new EnderecoNaoPertenceAoUsuarioException(2L, 1L));

        mockMvc.perform(put("/usuarios/1/enderecos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Endereço com id 2 não pertence ao usuário com id 1"));
    }

    @Test
    void deveRetornarErroQuandoEnderecoNaoEncontrado() throws Exception {
        doThrow(new EnderecoNaoEncontradoException(999L))
                .when(enderecoService).deletarEndereco(1L, 999L);

        mockMvc.perform(delete("/usuarios/1/enderecos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Endereço não encontrado: 999"));
    }
}