package com.guidev1911.ecommerce.controller;

import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.PedidoService;
import com.guidev1911.ecommerce.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
        public ResponseEntity<PedidoDTO> criarPedido(Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        PedidoDTO pedido = pedidoService.criarPedido(usuario);
        return new ResponseEntity<>(pedido, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos(Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(pedidoService.listarPedidos(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(pedidoService.buscarPorId(usuario, id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        PedidoDTO pedidoCancelado = pedidoService.cancelarPedido(usuario, id);
        return ResponseEntity.ok(pedidoCancelado);
    }
}
