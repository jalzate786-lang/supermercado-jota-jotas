
package org.example.supermercadojotajotas.controller;

import org.example.supermercadojotajotas.model.Producto;
import org.example.supermercadojotajotas.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaginaController {

    private final ProductoService productoService;

    public PaginaController(ProductoService productoService) {
        this.productoService = productoService;
    }
    //empieza generando las tablas de productos,carritos,pedido,registros
    @GetMapping("/")
    public String inicio(
            @RequestParam(required = false) String mensaje,
            Model model) {

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("carrito", productoService.verCarrito());
        model.addAttribute("pedidos", productoService.listarPedidosEnEspera());
        model.addAttribute("registros", productoService.getRegistros().getRegistros());

        if (mensaje != null) {
            model.addAttribute("mensaje", mensaje);
        }

        return "index";
    }
    //captura los datos enviados desde el front
    @PostMapping("/productos/registrar")
    public String registrarProducto(
            @RequestParam String nombre,
            @RequestParam double precio,
            @RequestParam int stock) {

        Producto producto = new Producto(0, nombre, precio, stock);

        if (productoService.registrarProducto(producto)) {
            return "redirect:/?mensaje=Producto+registrado";
        }

        return "redirect:/?mensaje=Datos+invalidos";
    }
    //agrega al carrito e indica si se pudo añadir o no por medio de mensaje en pantalla
    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(
            @RequestParam String nombre,
            @RequestParam int cantidad) {

        if (productoService.agregarAlCarrito(nombre, cantidad)) {
            return "redirect:/?mensaje=Producto+agregado+al+carrito";
        }

        return "redirect:/?mensaje=No+se+pudo+agregar+el+producto";
    }
    //se crea el pedido y se examina si tiene un cupo de rapidez asignado
    @PostMapping("/carrito/confirmar")
    public String confirmarCompra(
            @RequestParam String cliente,
            @RequestParam(defaultValue = "false") boolean cuponRapidez) {

        if (productoService.confirmarCompra(cliente, cuponRapidez)) {
            return "redirect:/?mensaje=Compra+confirmada+y+pedido+creado";
        }

        return "redirect:/?mensaje=No+se+pudo+confirmar+la+compra";
    }
    // da paso al siguiente pedido
    @PostMapping("/pedidos/procesar")
    public String procesarPedido() {

        if (productoService.procesarSiguientePedido() != null) {
            return "redirect:/?mensaje=Pedido+en+camino";
        }

        return "redirect:/?mensaje=No+hay+pedidos+pendientes";
    }
}
