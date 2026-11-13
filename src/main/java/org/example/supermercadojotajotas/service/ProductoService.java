
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

        //extrae todos los productos de la base de datos y los carga en el inventario
        inventario.cargarProductos(new LinkedList<>(productoRepository.findAll()));
        //historial de entregas
        registros.cargarRegistros(registroEntregaRepository.findAll());
        //mete en la cola de prioridad solo los pedidos que esten en el estado "espera"
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


    public boolean existeProducto(String nombre) {
        return inventario.existeProducto(nombre);
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
    public boolean confirmarCompra(String cliente, boolean cuponRapidez) {

        if (carrito.estaVacio() ||
                cliente == null ||
                cliente.trim().isEmpty()) {
            return false;
        }
        //crea un nuevo hashmap para reunir a los productos unico de la compra
        Map<String, Producto> productosCompra = new HashMap<>();

        for (String nombre : carrito.getProductos().keySet()) {
            //verifica la cantidad que quiere el cliente
            int cantidad = carrito.getProductos().get(nombre);
            //
            Producto producto = productoRepository.findByNombre(nombre);
            //verifica que la cantidad que quiere el cliente no supere el stock
            if (!inventario.existeProducto(nombre) ||
                    producto == null ||
                    cantidad <= 0 ||
                    cantidad > producto.getStock()) {
                return false;
            }

            productosCompra.put(nombre, producto);
        }
        //se crea el pedido
        Pedido pedido = new Pedido(cliente, cuponRapidez);
        Map<String, Integer> nuevosStocks = new HashMap<>();
        //se desencola el hasmap anterior
        for (String nombre : carrito.getProductos().keySet()) {
            //se obtiene la cantidad que quiere de ese producto
            int cantidad = carrito.getProductos().get(nombre);
            //se verifica cual es el producto
            Producto producto = productosCompra.get(nombre);
            //lo agrega al pedido
            pedido.agregarProducto(nombre, cantidad);

            //hace la resta en el stock del producto seleccionado y a su vez se actualiza la base de datos
            int nuevoStock = producto.getStock() - cantidad;

            producto.setStock(nuevoStock);
            productoRepository.save(producto);

            nuevosStocks.put(nombre, nuevoStock);
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        //este bloque solo funciona si la etiqueta @transaccional no reporto ningun error
        // y hace que se sincronice el resto del sistema con los cambios realizados
        ejecutarDespuesDeConfirmar(() -> {

            for (String nombre : nuevosStocks.keySet()) {
                inventario.actualizarStock(nombre, nuevosStocks.get(nombre));
            }

            colaEntregas.agregarPedido(pedidoGuardado);
            carrito.vaciar();
        });

        return true;
    }



    public List<Pedido> listarPedidosEnEspera() {
        return colaEntregas.listarPedidos();
    }

    @Transactional
    public Pedido procesarSiguientePedido() {

        Pedido pedido = colaEntregas.verSiguiente();
        //si depues de el no hay nadie quiere decir que esta de primero
        if (pedido == null) {
            return null;
        }
        //se le cambia el estado porque ya se atendio y se cambia a en "camino"
        pedido.setEstado("EN CAMINO");

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        LocalDateTime fechaCambio = LocalDateTime.now();
        //crea un nuevo registro con la informacion de este pedido
        RegistroEntrega registro = new RegistroEntrega(
                pedidoGuardado.getId(),
                new HashMap<>(pedidoGuardado.getProductos()),
                fechaCambio,
                pedidoGuardado.getEstado()
        );

        RegistroEntrega registroGuardado =
                registroEntregaRepository.save(registro);
    //se limpia de la cola, se agrega el registro, y se registra el log
        ejecutarDespuesDeConfirmar(() -> {

            colaEntregas.eliminarPedido(pedidoGuardado.getId());

            registros.agregarRegistro(registroGuardado);

            logEntrega.registrar(pedidoGuardado, fechaCambio);
        });

        return pedidoGuardado;
    }


    public RegistroEntregas getRegistros() {
        return registros;
    }


    private void ejecutarDespuesDeConfirmar(Runnable accion) {
        // verifica si hay una transaccion activa con la base de datos
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // pone en espera la accion hasta que la base de datos termine
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        // se activa solo cuando los datos ya se guardaron en el disco
                        public void afterCommit() {
                            // ejecuta los cambios en las estructuras de memoria
                            accion.run();
                        }
                    }
            );
        } else {
            // si no hay transaccion activa ejecuta los cambios de inmediato
            accion.run();
        }
    }
}
