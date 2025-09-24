package com.guidev1911.ecommerce.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.guidev1911.ecommerce.dto.PagamentoDTO;
import com.guidev1911.ecommerce.exception.PedidoNaoEncontradoException;
import com.guidev1911.ecommerce.exception.global.GlobalExceptionHandler;
import com.guidev1911.ecommerce.model.MetodoPagamento;
import com.guidev1911.ecommerce.model.StatusPagamento;
import com.guidev1911.ecommerce.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

    @InjectMocks
    private PagamentoController pagamentoController;

    @Mock
    private PagamentoService pagamentoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(pagamentoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void deveIniciarPagamentoComSucesso() throws Exception {

        PagamentoDTO pagamentoMock = new PagamentoDTO();
        pagamentoMock.setId(1L);
        pagamentoMock.setMetodo(MetodoPagamento.CARTAO);
        pagamentoMock.setStatus(StatusPagamento.PENDENTE);
        pagamentoMock.setValor(BigDecimal.valueOf(4231.50));
        pagamentoMock.setConfirmadoEm(LocalDateTime.now());

        when(pagamentoService.iniciarPagamento(anyLong(), any(MetodoPagamento.class)))
                .thenReturn(pagamentoMock);

        mockMvc.perform(post("/pagamentos/iniciar/4")
                        .param("metodo", "CARTAO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.metodo").value("CARTAO"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.valor").value(4231.50));

        verify(pagamentoService).iniciarPagamento(eq(4L), eq(MetodoPagamento.CARTAO));
    }

    @Test
    void deveRetornar404QuandoPedidoNaoEncontrado() throws Exception {
        when(pagamentoService.iniciarPagamento(eq(99L), any(MetodoPagamento.class)))
                .thenThrow(new PedidoNaoEncontradoException("Pedido não encontrado"));

        mockMvc.perform(post("/pagamentos/iniciar/99")
                        .param("metodo", "PIX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pedido não encontrado"));
    }

    @Test
    void deveProcessarCallbackAprovado() throws Exception {
        doNothing().when(pagamentoService).processarCallback(eq(1L), eq(true));

        mockMvc.perform(post("/pagamentos/callback/1")
                        .param("aprovado", "true"))
                .andExpect(status().isOk());

        verify(pagamentoService).processarCallback(eq(1L), eq(true));
    }

    @Test
    void deveProcessarCallbackRecusado() throws Exception {
        doNothing().when(pagamentoService).processarCallback(eq(2L), eq(false));

        mockMvc.perform(post("/pagamentos/callback/2")
                        .param("aprovado", "false"))
                .andExpect(status().isOk());

        verify(pagamentoService).processarCallback(eq(2L), eq(false));
    }
}