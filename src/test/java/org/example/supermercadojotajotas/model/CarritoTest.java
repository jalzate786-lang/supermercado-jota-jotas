
package org.example.supermercadojotajotas.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    @Test
    void agregarProductoCorrectamente() {
        Carrito carrito = new Carrito();

        assertTrue(carrito.agregarProducto("Arroz", 3, 10));
        assertEquals(3, carrito.getProductos().get("Arroz"));
    }

    @Test
    void noPermitirCantidadCeroONegativa() {
        Carrito carrito = new Carrito();

        assertFalse(carrito.agregarProducto("Arroz", 0, 10));
        assertFalse(carrito.agregarProducto("Arroz", -2, 10));
        assertTrue(carrito.estaVacio());
    }

    @Test
    void noPermitirCantidadMayorAlStock() {
        Carrito carrito = new Carrito();

        assertFalse(carrito.agregarProducto("Arroz", 11, 10));
        assertTrue(carrito.estaVacio());
    }

    @Test
    void acumularProductosSinSuperarStock() {
        Carrito carrito = new Carrito();

        assertTrue(carrito.agregarProducto("Arroz", 3, 10));
        assertTrue(carrito.agregarProducto("Arroz", 4, 10));

        assertEquals(7, carrito.getProductos().get("Arroz"));
        assertFalse(carrito.agregarProducto("Arroz", 4, 10));
        assertEquals(7, carrito.getProductos().get("Arroz"));
    }

    @Test
    void vaciarCarritoCorrectamente() {
        Carrito carrito = new Carrito();

        carrito.agregarProducto("Arroz", 3, 10);
        carrito.vaciar();

        assertTrue(carrito.estaVacio());
        assertEquals(0, carrito.getProductos().size());
    }
}
