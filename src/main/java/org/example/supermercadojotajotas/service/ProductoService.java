package org.example.supermercadojotajotas.service;

import org.example.supermercadojotajotas.model.Carrito;
import org.example.supermercadojotajotas.model.Pedido;
import org.example.supermercadojotajotas.model.Producto;
import org.example.supermercadojotajotas.model.RegistroEntrega;
import org.example.supermercadojotajotas.structures.ColaEntregas;
import org.example.supermercadojotajotas.structures.Inventario;
import org.example.supermercadojotajotas.structures.RegistroEntregas;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class ProductoService {

    private Inventario inventario;
    private ColaEntregas colaEntregas;
    private Carrito carrito;
    private RegistroEntregas registros;
    private LogEntrega logEntrega;

    public ProductoService() {
        inventario = new Inventario();
        colaEntregas = new ColaEntregas();
        carrito = new Carrito();
        registros = new RegistroEntregas();
        logEntrega = new LogEntrega();
    }

    public boolean registrarProducto(Producto producto) {
        return inventario.agregarProducto(producto);
    }

    public Integer buscarStock(String nombre) {
        return inventario.buscarStock(nombre);
    }

    public boolean actualizarStock(String nombre, int nuevoStock) {
        return inventario.actualizarStock(nombre, nuevoStock);
    }

    public boolean existeProducto(String nombre) {
        return inventario.existeProducto(nombre);
    }

    public Inventario getInventario() {
        return inventario;
    }

    public boolean agregarAlCarrito(String nombre, int cantidad) {

        Integer stock = inventario.buscarStock(nombre);

        if (stock == null) {
            return false;
        }

        return carrito.agregarProducto(nombre, cantidad, stock);
    }

    public Carrito verCarrito() {
        return carrito;
    }

    public boolean confirmarCompra() {

        if (carrito.estaVacio()) {
            return false;
        }

        for (String nombre : carrito.getProductos().keySet()) {

            int cantidad = carrito.getProductos().get(nombre);
            int stockActual = inventario.buscarStock(nombre);

            if (cantidad > stockActual) {
                return false;
            }
        }

        for (String nombre : carrito.getProductos().keySet()) {

            int cantidad = carrito.getProductos().get(nombre);
            int stockActual = inventario.buscarStock(nombre);

            inventario.actualizarStock(
                    nombre,
                    stockActual - cantidad
            );
        }

        carrito.vaciar();

        return true;
    }

    public void agregarPedido(Pedido pedido) {
        colaEntregas.agregarPedido(pedido);
    }

    public Pedido verSiguientePedido() {
        return colaEntregas.verSiguiente();
    }

    public Pedido procesarSiguientePedido() {

        Pedido pedido = colaEntregas.siguientePedido();

        if (pedido != null) {

            pedido.setEstado("EN CAMINO");

            HashMap<String, Integer> productos =
                    new HashMap<>(pedido.getProductos());

            RegistroEntrega registro = new RegistroEntrega(
                    pedido.getId(),
                    productos,
                    LocalDateTime.now(),
                    pedido.getEstado()
            );

            registros.agregarRegistro(registro);

            logEntrega.registrar(pedido);
        }

        return pedido;
    }

    public int cantidadPedidos() {
        return colaEntregas.cantidadPedidos();
    }

    public RegistroEntregas getRegistros() {
        return registros;
    }
}