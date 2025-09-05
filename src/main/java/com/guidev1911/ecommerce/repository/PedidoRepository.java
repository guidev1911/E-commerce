package com.guidev1911.ecommerce.repository;

import com.guidev1911.ecommerce.model.Carrinho;
import com.guidev1911.ecommerce.model.Pedido;
import com.guidev1911.ecommerce.model.StatusPedido;
import com.guidev1911.ecommerce.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuario(Usuario usuario);

    Page<Pedido> findByUsuario(Usuario usuario, Pageable pageable);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByUsuarioAndStatus(Usuario usuario, StatusPedido status);

    Optional<Pedido> findByIdAndUsuario(Long id, Usuario usuario);

}
