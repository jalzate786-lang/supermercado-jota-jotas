
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.*;

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
            //llamo a la clases que heredean de jparepository
            ProductoRepository productoRepository,
            PedidoRepository pedidoRepository,
            RegistroEntregaRepository registroEntregaRepository) {

        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.registroEntregaRepository = registroEntregaRepository;
        //inicializo cada clase
        inventario = new Inventario();
        colaEntregas = new ColaEntregas();
        carrito = new Carrito();
        registros = new RegistroEntregas();
        logEntrega = new LogEntrega();

        inventario.cargarProductos(new LinkedList<>(productoRepository.findAll()));

        registros.cargarRegistros(registroEntregaRepository.findAll());

        for (Pedido pedido : pedidoRepository.findAll()) {

            if ("ESPERA".equals(pedido.getEstado())) {
                colaEntregas.agregarPedido(pedido);
            }
        }
    }

    public boolean registrarProducto(Producto producto) {
        //si existe algun error en el formulario que no permita el ingreso del producto
        if (producto == null ||
                producto.getNombre() == null ||
                producto.getNombre().trim().isEmpty() ||
                producto.getPrecio() <= 0 ||
                producto.getStock() < 0 ||
                existeProducto(producto.getNombre())) {
            return false;
        }

        String nombre = producto.getNombre().trim();

        Producto nuevoProducto = new Producto(
                0,
                nombre,
                producto.getPrecio(),
                producto.getStock()
        );
        //de pasar las pruebas se agrega a la base de datos
        Producto guardado = productoRepository.save(nuevoProducto);

        return inventario.agregarProducto(guardado);
    }

    public Integer buscarStock(String nombre) {
        return inventario.buscarStock(nombre);
    }

    public boolean actualizarStock(String nombre, int nuevoStock) {

        String nombreOficial = inventario.buscarNombre(nombre);

        if (nombreOficial == null || nuevoStock < 0) {
            return false;
        }

        Producto producto = productoRepository.findByNombre(nombreOficial);

        if (producto == null) {
            return false;
        }

        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        return inventario.actualizarStock(nombreOficial, nuevoStock);
    }

    public boolean existeProducto(String nombre) {
        return inventario.existeProducto(nombre);
    }

    public Inventario getInventario() {
        return inventario;
    }

    public LinkedList<Producto> listarProductos() {
        return inventario.listarProductos();
    }

    public boolean agregarAlCarrito(String nombre, int cantidad) {

        String nombreOficial = inventario.buscarNombre(nombre);

        if (nombreOficial == null) {
            return false;
        }

        int stock = inventario.buscarStock(nombreOficial);
        //si el producto esta correcto que se agregue al carrito
        return carrito.agregarProducto(
                nombreOficial,
                cantidad,
                stock
        );
    }

    public Carrito verCarrito() {
        return carrito;
    }

    @Transactional
    public boolean confirmarCompra() {
        return confirmarCompra("Cliente", false);
    }

    @Transactional
    public boolean confirmarCompra(String cliente, boolean cuponRapidez) {

        if (carrito.estaVacio() ||
                cliente == null ||
                cliente.trim().isEmpty()) {
            return false;
        }

        Map<String, Producto> productosCompra = new HashMap<>();

        for (String nombre : carrito.getProductos().keySet()) {

            int cantidad = carrito.getProductos().get(nombre);

            Producto producto = productoRepository.findByNombre(nombre);

            if (!inventario.existeProducto(nombre) ||
                    producto == null ||
                    cantidad <= 0 ||
                    cantidad > producto.getStock()) {
                return false;
            }

            productosCompra.put(nombre, producto);
        }

        Pedido pedido = new Pedido(cliente, cuponRapidez);
        Map<String, Integer> nuevosStocks = new HashMap<>();

        for (String nombre : carrito.getProductos().keySet()) {

            int cantidad = carrito.getProductos().get(nombre);

            Producto producto = productosCompra.get(nombre);

            pedido.agregarProducto(nombre, cantidad);

            int nuevoStock = producto.getStock() - cantidad;

            producto.setStock(nuevoStock);
            productoRepository.save(producto);

            nuevosStocks.put(nombre, nuevoStock);
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        ejecutarDespuesDeConfirmar(() -> {

            for (String nombre : nuevosStocks.keySet()) {
                inventario.actualizarStock(nombre, nuevosStocks.get(nombre));
            }

            colaEntregas.agregarPedido(pedidoGuardado);
            carrito.vaciar();
        });

        return true;
    }

    @Transactional
    public void agregarPedido(Pedido pedido) {

        if (pedido == null) {
            return;
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        ejecutarDespuesDeConfirmar(() ->
                colaEntregas.agregarPedido(pedidoGuardado)
        );
    }

    public Pedido verSiguientePedido() {
        return colaEntregas.verSiguiente();
    }

    public List<Pedido> listarPedidosEnEspera() {
        return colaEntregas.listarPedidos();
    }

    @Transactional
    public Pedido procesarSiguientePedido() {

        Pedido pedido = colaEntregas.verSiguiente();

        if (pedido == null) {
            return null;
        }

        pedido.setEstado("EN CAMINO");

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        LocalDateTime fechaCambio = LocalDateTime.now();

        RegistroEntrega registro = new RegistroEntrega(
                pedidoGuardado.getId(),
                new HashMap<>(pedidoGuardado.getProductos()),
                fechaCambio,
                pedidoGuardado.getEstado()
        );

        RegistroEntrega registroGuardado =
                registroEntregaRepository.save(registro);

        ejecutarDespuesDeConfirmar(() -> {

            colaEntregas.eliminarPedido(pedidoGuardado.getId());

            registros.agregarRegistro(registroGuardado);

            logEntrega.registrar(pedidoGuardado, fechaCambio);
        });

        return pedidoGuardado;
    }

    public int cantidadPedidos() {
        return colaEntregas.cantidadPedidos();
    }

    public RegistroEntregas getRegistros() {
        return registros;
    }

    private void ejecutarDespuesDeConfirmar(Runnable accion) {

        if (TransactionSynchronizationManager.isSynchronizationActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCommit() {
                            accion.run();
                        }
                    }
            );

        } else {
            accion.run();
        }
    }
}
