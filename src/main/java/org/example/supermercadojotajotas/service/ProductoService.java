
package org.example.supermercadojotajotas.service;

import org.example.supermercadojotajotas.model.Carrito;
import org.example.supermercadojotajotas.model.Pedido;
import org.example.supermercadojotajotas.model.Producto;
import org.example.supermercadojotajotas.model.RegistroEntrega;
import org.example.supermercadojotajotas.repository.PedidoRepository;
import org.example.supermercadojotajotas.repository.ProductoRepository;
import org.example.supermercadojotajotas.repository.RegistroEntregaRepository;
import org.example.supermercadojotajotas.structures.ColaEntregas;
import org.example.supermercadojotajotas.structures.Inventario;
import org.example.supermercadojotajotas.structures.RegistroEntregas;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class ProductoService {

    private Inventario inventario;
    private ColaEntregas colaEntregas;
    private Carrito carrito;
    private RegistroEntregas registros;
    private LogEntrega logEntrega;

    private ProductoRepository productoRepository;
    private PedidoRepository pedidoRepository;
    private RegistroEntregaRepository registroEntregaRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            PedidoRepository pedidoRepository,
            RegistroEntregaRepository registroEntregaRepository) {

        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.registroEntregaRepository = registroEntregaRepository;

        inventario = new Inventario();
        colaEntregas = new ColaEntregas();
        carrito = new Carrito();
        registros = new RegistroEntregas();
        logEntrega = new LogEntrega();

        inventario.cargarProductos(productoRepository.findAll());

        registros.cargarRegistros(registroEntregaRepository.findAll());

        for (Pedido pedido : pedidoRepository.findAll()) {

            if ("ESPERA".equals(pedido.getEstado())) {
                colaEntregas.agregarPedido(pedido);
            }
        }
    }

    public boolean registrarProducto(Producto producto) {

        if (producto == null ||
                producto.getNombre() == null ||
                producto.getNombre().trim().isEmpty() ||
                producto.getPrecio() <= 0 ||
                producto.getStock() < 0 ||
                existeProducto(producto.getNombre())) {
            return false;
        }

        Producto nuevoProducto = new Producto(
                0,
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );

        Producto guardado = productoRepository.save(nuevoProducto);

        return inventario.agregarProducto(guardado);
    }

    public Integer buscarStock(String nombre) {
        return inventario.buscarStock(nombre);
    }

    public boolean actualizarStock(String nombre, int nuevoStock) {

        if (!inventario.existeProducto(nombre) || nuevoStock < 0) {
            return false;
        }

        Producto producto = productoRepository.findByNombre(nombre);

        if (producto == null) {
            return false;
        }

        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        return inventario.actualizarStock(nombre, nuevoStock);
    }

    public boolean existeProducto(String nombre) {
        return inventario.existeProducto(nombre);
    }

    public Inventario getInventario() {
        return inventario;
    }

    public Collection<Producto> listarProductos() {
        return inventario.listarProductos();
    }

    public boolean agregarAlCarrito(String nombre, int cantidad) {

        if (!inventario.existeProducto(nombre)) {
            return false;
        }

        Integer stock = inventario.buscarStock(nombre);

        return carrito.agregarProducto(nombre, cantidad, stock);
    }

    public Carrito verCarrito() {
        return carrito;
    }

    public boolean confirmarCompra() {
        return confirmarCompra("Cliente", false);
    }

    public boolean confirmarCompra(String cliente, boolean cuponRapidez) {

        if (carrito.estaVacio() ||
                cliente == null ||
                cliente.trim().isEmpty()) {
            return false;
        }

        for (String nombre : carrito.getProductos().keySet()) {

            int cantidad = carrito.getProductos().get(nombre);
            int stockActual = inventario.buscarStock(nombre);

            if (!inventario.existeProducto(nombre) || cantidad > stockActual) {
                return false;
            }
        }

        Pedido pedido = new Pedido(cliente, cuponRapidez);

        for (String nombre : carrito.getProductos().keySet()) {

            int cantidad = carrito.getProductos().get(nombre);

            pedido.agregarProducto(nombre, cantidad);

            int stockActual = inventario.buscarStock(nombre);

            actualizarStock(nombre, stockActual - cantidad);
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        colaEntregas.agregarPedido(pedidoGuardado);

        carrito.vaciar();

        return true;
    }

    public void agregarPedido(Pedido pedido) {

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        colaEntregas.agregarPedido(pedidoGuardado);
    }

    public Pedido verSiguientePedido() {
        return colaEntregas.verSiguiente();
    }

    public List<Pedido> listarPedidosEnEspera() {
        return colaEntregas.listarPedidos();
    }

    public Pedido procesarSiguientePedido() {

        Pedido pedido = colaEntregas.siguientePedido();

        if (pedido != null) {

            pedido.setEstado("EN CAMINO");

            pedidoRepository.save(pedido);

            LocalDateTime fechaCambio = LocalDateTime.now();

            RegistroEntrega registro = new RegistroEntrega(
                    pedido.getId(),
                    new HashMap<>(pedido.getProductos()),
                    fechaCambio,
                    pedido.getEstado()
            );

            RegistroEntrega registroGuardado =
                    registroEntregaRepository.save(registro);

            registros.agregarRegistro(registroGuardado);

            logEntrega.registrar(pedido, fechaCambio);
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
