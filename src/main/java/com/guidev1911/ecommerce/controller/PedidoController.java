package com.guidev1911.ecommerce.controller;


import com.guidev1911.ecommerce.controller.swagger.PedidoControllerDoc;
import com.guidev1911.ecommerce.dto.PedidoCreateDTO;
import com.guidev1911.ecommerce.dto.PedidoDTO;
import com.guidev1911.ecommerce.dto.PedidoPreviewDTO;
import com.guidev1911.ecommerce.model.Usuario;
import com.guidev1911.ecommerce.service.PedidoService;
import com.guidev1911.ecommerce.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class PedidoController implements PedidoControllerDoc {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @Override
    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(Authentication authentication,
                                                 @RequestBody PedidoCreateDTO dto) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        PedidoDTO pedido = pedidoService.criarPedido(usuario, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @Override
    @PostMapping("/preview")
    public ResponseEntity<PedidoPreviewDTO> simularPedido(Authentication authentication,
                                                          @RequestBody PedidoCreateDTO dto) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        PedidoPreviewDTO preview = pedidoService.simularPedido(usuario, dto);
        return ResponseEntity.ok(preview);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos(Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(pedidoService.listarPedidos(usuario));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(pedidoService.buscarPorId(usuario, id));
    }

    @Override
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        PedidoDTO pedidoCancelado = pedidoService.cancelarPedido(usuario, id);
        return ResponseEntity.ok(pedidoCancelado);
    }
}