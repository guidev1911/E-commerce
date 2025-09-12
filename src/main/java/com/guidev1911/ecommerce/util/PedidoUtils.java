package com.guidev1911.ecommerce.util;

import com.guidev1911.ecommerce.model.ItemPedido;
import com.guidev1911.ecommerce.model.Pedido;

import java.math.BigDecimal;

public class PedidoUtils {

    public static BigDecimal recalcularTotal(Pedido pedido) {
        return pedido.getItens().stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
