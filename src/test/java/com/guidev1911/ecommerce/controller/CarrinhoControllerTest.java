package com.guidev1911.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guidev1911.ecommerce.dto.CarrinhoDTO;
import com.guidev1911.ecommerce.dto.ItemCarrinhoRequest;
import com.guidev1911.ecommerce.exception.ProdutoNaoEncontradoException;
import com.guidev1911.ecommerce.exception.UsuarioNaoEncontradoException;
import com.guidev1911.ecommerce.exception.global.GlobalExceptionHandler;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.CarrinhoService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CarrinhoControllerTest {

    @InjectMocks
    private CarrinhoController carrinhoController;

    @Mock
    private CarrinhoService carrinhoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Usuario usuario;
    private CarrinhoDTO carrinhoDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(carrinhoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");

        carrinhoDTO = new CarrinhoDTO();
        carrinhoDTO.setId(1L);
        carrinhoDTO.setUsuarioId(1L);

        when(authentication.getName()).thenReturn(usuario.getEmail());
        when(usuarioService.findByEmail(usuario.getEmail())).thenReturn(usuario);
    }

    @Test
    void deveAdicionarItemAoCarrinho() throws Exception {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest();
        request.setProdutoId(10L);
        request.setQuantidade(2);

        when(carrinhoService.alterarItem(usuario, 10L, 2, true)).thenReturn(carrinhoDTO);

        mockMvc.perform(post("/api/v1/carrinho/itens")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carrinhoService).alterarItem(usuario, 10L, 2, true);
    }

    @Test
    void deveAtualizarQuantidadeDoItem() throws Exception {
        ItemCarrinhoRequest request = new ItemCarrinhoRequest();
        request.setQuantidade(5);

        when(carrinhoService.alterarItem(usuario, 10L, 5, false)).thenReturn(carrinhoDTO);

        mockMvc.perform(put("/api/v1/carrinho/itens/10")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carrinhoService).alterarItem(usuario, 10L, 5, false);
    }

    @Test
    void deveRemoverItemDoCarrinho() throws Exception {
        when(carrinhoService.removerItem(usuario, 10L)).thenReturn(carrinhoDTO);

        mockMvc.perform(delete("/api/v1/carrinho/itens/10")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(carrinhoService).removerItem(usuario, 10L);
    }

    @Test
    void deveListarCarrinhoDoUsuario() throws Exception {
        when(carrinhoService.listarCarrinho(usuario)).thenReturn(carrinhoDTO);

        mockMvc.perform(get("/api/v1/carrinho")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1));

        verify(carrinhoService).listarCarrinho(usuario);
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
        when(usuarioService.findByEmail("teste@email.com"))
                .thenThrow(new UsuarioNaoEncontradoException("teste@email.com"));

        ItemCarrinhoRequest request = new ItemCarrinhoRequest();
        request.setProdutoId(10L);
        request.setQuantidade(1);

        mockMvc.perform(post("/api/v1/carrinho/itens")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com e-mail: teste@email.com"));

        verifyNoInteractions(carrinhoService);
    }

    @Test
    void deveRetornar404QuandoProdutoNaoEncontrado() throws Exception {

        ItemCarrinhoRequest request = new ItemCarrinhoRequest();
        request.setProdutoId(999L);
        request.setQuantidade(1);

        when(carrinhoService.alterarItem(eq(usuario), eq(999L), eq(1), eq(true)))
                .thenThrow(new ProdutoNaoEncontradoException(999L));

        mockMvc.perform(post("/api/v1/carrinho/itens")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Produto com ID 999 não encontrado."));

        verify(carrinhoService).alterarItem(eq(usuario), eq(999L), eq(1), eq(true));
    }
}
