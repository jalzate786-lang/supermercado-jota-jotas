
package org.example.supermercadojotajotas.service;

import org.example.supermercadojotajotas.model.Pedido;
import org.example.supermercadojotajotas.model.Producto;
import org.example.supermercadojotajotas.repository.PedidoRepository;
import org.example.supermercadojotajotas.repository.ProductoRepository;
import org.example.supermercadojotajotas.repository.RegistroEntregaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    private ProductoRepository productoRepository;
    private PedidoRepository pedidoRepository;
    private RegistroEntregaRepository registroEntregaRepository;
    private ProductoService productoService;

    @BeforeEach
    void configurar() {
        productoRepository = mock(ProductoRepository.class);
        pedidoRepository = mock(PedidoRepository.class);
        registroEntregaRepository = mock(RegistroEntregaRepository.class);

        when(productoRepository.findAll()).thenReturn(List.of());
        when(pedidoRepository.findAll()).thenReturn(List.of());
        when(registroEntregaRepository.findAll()).thenReturn(List.of());

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        productoService = new ProductoService(
                productoRepository,
                pedidoRepository,
                registroEntregaRepository
        );
    }

    @Test
    void registrarProductoCorrectamente() {
        Producto producto = new Producto(1, "Arroz", 3500, 20);

        assertTrue(productoService.registrarProducto(producto));
        assertTrue(productoService.existeProducto("Arroz"));
        assertEquals(20, productoService.buscarStock("Arroz"));

        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void rechazarProductoConDatosInvalidos() {
        Producto sinPrecio = new Producto(1, "Leche", 0, 10);
        Producto stockNegativo = new Producto(2, "Pan", 2000, -5);

        assertFalse(productoService.registrarProducto(sinPrecio));
        assertFalse(productoService.registrarProducto(stockNegativo));

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void rechazarProductoDuplicado() {
        Producto primero = new Producto(1, "Arroz", 3500, 20);
        Producto segundo = new Producto(2, "Arroz", 4000, 15);

        assertTrue(productoService.registrarProducto(primero));
        assertFalse(productoService.registrarProducto(segundo));
    }

    @Test
    void agregarProductosAlCarritoRespetandoStock() {
        Producto producto = new Producto(1, "Arroz", 3500, 10);

        productoService.registrarProducto(producto);

        assertTrue(productoService.agregarAlCarrito("Arroz", 5));
        assertFalse(productoService.agregarAlCarrito("Arroz", 6));
        assertFalse(productoService.agregarAlCarrito("Leche", 2));

        assertEquals(5, productoService.verCarrito()
                .getProductos().get("Arroz"));
    }

    @Test
    void confirmarCompraYDescontarInventario() {
        Producto producto = new Producto(1, "Arroz", 3500, 10);

        productoService.registrarProducto(producto);

        when(productoRepository.findByNombre("Arroz"))
                .thenReturn(producto);

        assertTrue(productoService.agregarAlCarrito("Arroz", 4));
        assertTrue(productoService.confirmarCompra("Juan", false));

        assertEquals(6, productoService.buscarStock("Arroz"));
        assertTrue(productoService.verCarrito().estaVacio());
        assertEquals(1, productoService.cantidadPedidos());

        verify(pedidoRepository).save(any(Pedido.class));
    }
}
