
package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Pedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColaEntregasTest {

    @Test
    void pedidoConCuponTienePrioridad() {
        ColaEntregas cola = new ColaEntregas();

        Pedido normal = new Pedido("Juan", false);
        Pedido rapido = new Pedido("Pedro", true);

        cola.agregarPedido(normal);
        cola.agregarPedido(rapido);

        assertSame(rapido, cola.verSiguiente());
    }

    @Test
    void procesarPedidosEnOrdenDePrioridad() {
        ColaEntregas cola = new ColaEntregas();

        Pedido normal = new Pedido("Juan", false);
        Pedido rapido = new Pedido("Pedro", true);

        cola.agregarPedido(normal);
        cola.agregarPedido(rapido);

        assertSame(rapido, cola.siguientePedido());
        assertSame(normal, cola.siguientePedido());
        assertTrue(cola.estaVacia());
    }

    @Test
    void contarPedidosCorrectamente() {
        ColaEntregas cola = new ColaEntregas();

        cola.agregarPedido(new Pedido("Juan", false));
        cola.agregarPedido(new Pedido("Pedro", true));

        assertEquals(2, cola.cantidadPedidos());

        cola.siguientePedido();

        assertEquals(1, cola.cantidadPedidos());
    }

    @Test
    void colaVaciaNoTieneSiguientePedido() {
        ColaEntregas cola = new ColaEntregas();

        assertTrue(cola.estaVacia());
        assertNull(cola.verSiguiente());
        assertNull(cola.siguientePedido());
    }
}
