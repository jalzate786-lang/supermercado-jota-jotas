
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

    @GetMapping("/")
    public String inicio(
            @RequestParam(required = false) String mensaje,
            Model model) {

        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("carrito", productoService.verCarrito());

        if (mensaje != null) {
            model.addAttribute("mensaje", mensaje);
        }

        return "index";
    }

    @PostMapping("/productos/registrar")
    public String registrarProducto(
            @RequestParam int id,
            @RequestParam String nombre,
            @RequestParam double precio,
            @RequestParam int stock) {

        Producto producto = new Producto(id, nombre, precio, stock);

        if (productoService.registrarProducto(producto)) {
            return "redirect:/?mensaje=Producto+registrado";
        }

        return "redirect:/?mensaje=Datos+invalidos";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(
            @RequestParam String nombre,
            @RequestParam int cantidad) {

        if (productoService.agregarAlCarrito(nombre, cantidad)) {
            return "redirect:/?mensaje=Producto+agregado+al+carrito";
        }

        return "redirect:/?mensaje=No+se+pudo+agregar+el+producto";
    }

    @PostMapping("/carrito/confirmar")
    public String confirmarCompra() {

        if (productoService.confirmarCompra()) {
            return "redirect:/?mensaje=Compra+confirmada";
        }

        return "redirect:/?mensaje=No+se+pudo+confirmar+la+compra";
    }
}
