
package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventarioTest {

    @Test
    void agregarProductoCorrectamente() {
        Inventario inventario = new Inventario();
        Producto producto = new Producto(1, "Arroz", 3500, 20);

        assertTrue(inventario.agregarProducto(producto));
        assertTrue(inventario.existeProducto("Arroz"));
        assertEquals(20, inventario.buscarStock("Arroz"));
    }

    @Test
    void noPermitirProductosDuplicados() {
        Inventario inventario = new Inventario();

        Producto primero = new Producto(1, "Arroz", 3500, 20);
        Producto segundo = new Producto(2, "Arroz", 4000, 15);

        assertTrue(inventario.agregarProducto(primero));
        assertFalse(inventario.agregarProducto(segundo));
    }

    @Test
    void noPermitirPrecioOStockInvalidos() {
        Inventario inventario = new Inventario();

        Producto precioInvalido = new Producto(1, "Leche", 0, 10);
        Producto stockInvalido = new Producto(2, "Pan", 2000, -5);

        assertFalse(inventario.agregarProducto(precioInvalido));
        assertFalse(inventario.agregarProducto(stockInvalido));
    }

    @Test
    void actualizarStockCorrectamente() {
        Inventario inventario = new Inventario();
        Producto producto = new Producto(1, "Arroz", 3500, 20);

        inventario.agregarProducto(producto);

        assertTrue(inventario.actualizarStock("Arroz", 12));
        assertEquals(12, inventario.buscarStock("Arroz"));
        assertEquals(12, producto.getStock());
    }

    @Test
    void noPermitirStockNegativoNiProductoInexistente() {
        Inventario inventario = new Inventario();
        Producto producto = new Producto(1, "Arroz", 3500, 20);

        inventario.agregarProducto(producto);

        assertFalse(inventario.actualizarStock("Arroz", -1));
        assertFalse(inventario.actualizarStock("Leche", 10));
    }
}
