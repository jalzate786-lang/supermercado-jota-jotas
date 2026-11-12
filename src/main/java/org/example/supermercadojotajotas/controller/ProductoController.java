package org.example.supermercadojotajotas.controller;

import org.example.supermercadojotajotas.model.Carrito;
import org.example.supermercadojotajotas.model.Pedido;
import org.example.supermercadojotajotas.model.Producto;
import org.example.supermercadojotajotas.model.RegistroEntrega;
import org.example.supermercadojotajotas.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public String registrarProducto(@RequestBody Producto producto) {

        boolean registrado = productoService.registrarProducto(producto);

        if (registrado) {
            return "Producto registrado correctamente";
        }

        return "Error: el precio debe ser mayor a 0 y el stock no puede ser negativo";
    }

    @GetMapping("/{nombre}")
    public String buscarStock(@PathVariable String nombre) {

        Integer stock = productoService.buscarStock(nombre);

        if (stock == null) {
            return "Producto no encontrado";
        }

        return "Producto: " + nombre + " | Stock: " + stock;
    }

    @PutMapping("/{nombre}/stock")
    public String actualizarStock(
            @PathVariable String nombre,
            @RequestParam int stock) {

        boolean actualizado =
                productoService.actualizarStock(nombre, stock);

        if (actualizado) {
            return "Stock actualizado correctamente";
        }

        return "Error: producto inexistente o stock inválido";
    }

    @PostMapping("/carrito")
    public String agregarAlCarrito(
            @RequestParam String nombre,
            @RequestParam int cantidad) {

        boolean agregado =
                productoService.agregarAlCarrito(nombre, cantidad);

        if (agregado) {
            return "Producto agregado al carrito";
        }

        return "No se puede agregar: producto inexistente o stock insuficiente";
    }

    @GetMapping("/carrito")
    public Carrito verCarrito() {
        return productoService.verCarrito();
    }

    @PostMapping("/carrito/confirmar")
    public String confirmarCompra() {

        boolean confirmado =
                productoService.confirmarCompra();

        if (confirmado) {
            return "Compra confirmada y stock descontado correctamente";
        }

        return "No se puede confirmar la compra";
    }

    @PostMapping("/pedidos")
    public String agregarPedido(@RequestBody Pedido pedido) {

        productoService.agregarPedido(pedido);

        return "Pedido agregado correctamente";
    }

    @GetMapping("/pedidos/siguiente")
    public Pedido verSiguientePedido() {
        return productoService.verSiguientePedido();
    }

    @PostMapping("/pedidos/procesar")
    public Pedido procesarSiguientePedido() {
        return productoService.procesarSiguientePedido();
    }

    @GetMapping("/pedidos/cantidad")
    public String cantidadPedidos() {
        return "Pedidos en espera: " + productoService.cantidadPedidos();
    }

    @GetMapping("/pedidos/registros")
    public List<RegistroEntrega> verRegistros() {
        return productoService.getRegistros().getRegistros();
    }
}