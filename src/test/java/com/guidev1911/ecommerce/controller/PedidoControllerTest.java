package com.guidev1911.ecommerce.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.PedidoCreateDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.dto.PedidoPreviewDTO;
import com.guidev1911.ecommerce.exception.*;
import com.guidev1911.ecommerce.exception.global.GlobalExceptionHandler;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.PedidoService;
import com.guidev1911.ecommerce.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @InjectMocks
    private PedidoController pedidoController;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Usuario usuario;
    private PedidoDTO pedidoDTO;
    private PedidoCreateDTO createDTO;
    private PedidoPreviewDTO previewDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");

        pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(100L);
        pedidoDTO.setUsuarioId(1L);
        pedidoDTO.setTotal(BigDecimal.valueOf(250));
        pedidoDTO.setStatus("CRIADO");
        pedidoDTO.setCriadoEm(LocalDateTime.now());

        createDTO = new PedidoCreateDTO();
        createDTO.setEnderecoId(10L);

        previewDTO = new PedidoPreviewDTO();
        previewDTO.setTotal(BigDecimal.valueOf(250));
        previewDTO.setFrete(BigDecimal.valueOf(20));
        previewDTO.setSubtotal(BigDecimal.valueOf(230));

        when(authentication.getName()).thenReturn(usuario.getEmail());
        when(usuarioService.findByEmail(usuario.getEmail())).thenReturn(usuario);
    }

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        when(pedidoService.criarPedido(eq(usuario), any(PedidoCreateDTO.class)))
                .thenReturn(pedidoDTO);

        mockMvc.perform(post("/api/v1/pedidos")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("CRIADO"));

        verify(pedidoService).criarPedido(eq(usuario), any(PedidoCreateDTO.class));
    }

    @Test
    void deveSimularPedido() throws Exception {
        when(pedidoService.simularPedido(eq(usuario), any(PedidoCreateDTO.class)))
                .thenReturn(previewDTO);

        mockMvc.perform(post("/api/v1/pedidos/preview")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(250));

        verify(pedidoService).simularPedido(eq(usuario), any(PedidoCreateDTO.class));
    }

    @Test
    void deveListarPedidosDoUsuario() throws Exception {
        when(pedidoService.listarPedidos(usuario)).thenReturn(List.of(pedidoDTO));

        mockMvc.perform(get("/api/v1/pedidos").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));

        verify(pedidoService).listarPedidos(usuario);
    }

    @Test
    void deveBuscarPedidoPorId() throws Exception {
        when(pedidoService.buscarPorId(usuario, 100L)).thenReturn(pedidoDTO);

        mockMvc.perform(get("/api/v1/pedidos/100").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));

        verify(pedidoService).buscarPorId(usuario, 100L);
    }

    @Test
    void deveCancelarPedido() throws Exception {
        when(pedidoService.cancelarPedido(usuario, 100L)).thenReturn(pedidoDTO);

        mockMvc.perform(put("/api/v1/pedidos/100/cancelar").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));

        verify(pedidoService).cancelarPedido(usuario, 100L);
    }

    @Test
    void deveRetornar404QuandoPedidoNaoEncontrado() throws Exception {
        when(pedidoService.buscarPorId(usuario, 999L))
                .thenThrow(new PedidoNaoEncontradoException("Pedido não encontrado"));

        mockMvc.perform(get("/api/v1/pedidos/999").principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pedido não encontrado"));
    }

    @Test
    void deveRetornar400QuandoCarrinhoVazioAoCriarPedido() throws Exception {
        when(pedidoService.criarPedido(eq(usuario), any(PedidoCreateDTO.class)))
                .thenThrow(new CarrinhoVazioException("Carrinho vazio, não é possível criar pedido."));

        mockMvc.perform(post("/api/v1/pedidos")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Carrinho vazio, não é possível criar pedido."))
                .andExpect(jsonPath("$.path").value("/api/v1/pedidos"));
    }

    @Test
    void deveRetornar409QuandoCancelamentoNaoPermitido() throws Exception {
        when(pedidoService.cancelarPedido(usuario, 100L))
                .thenThrow(new CancelamentoNaoPermitidoException("Pedido já enviado, não pode cancelar."));

        mockMvc.perform(put("/api/v1/pedidos/100/cancelar").principal(authentication))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Pedido já enviado, não pode cancelar."));
    }

}