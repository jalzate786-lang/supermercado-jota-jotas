
package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Producto;

import java.util.Collection;
import java.util.HashMap;

public class Inventario {

    private HashMap<String, Integer> stock;
    private HashMap<String, Producto> productos;

    public Inventario() {
        stock = new HashMap<>();
        productos = new HashMap<>();
    }

    public boolean agregarProducto(Producto producto) {

        if (producto.getPrecio() <= 0 || producto.getStock() < 0) {
            return false;
        }

        if (productos.containsKey(producto.getNombre())) {
            return false;
        }

        stock.put(producto.getNombre(), producto.getStock());
        productos.put(producto.getNombre(), producto);

        return true;
    }

    public int buscarStock(String nombre) {
        return stock.getOrDefault(nombre, 0);
    }

    public boolean actualizarStock(String nombre, int nuevoStock) {

        if (nuevoStock < 0 || !stock.containsKey(nombre)) {
            return false;
        }

        stock.put(nombre, nuevoStock);
        productos.get(nombre).setStock(nuevoStock);

        return true;
    }

    public boolean existeProducto(String nombre) {
        return productos.containsKey(nombre);
    }

    public HashMap<String, Integer> getStock() {
        return stock;
    }

    public Collection<Producto> listarProductos() {
        return productos.values();
    }

    public void cargarProductos(Collection<Producto> productosGuardados) {

        stock.clear();
        productos.clear();

        for (Producto producto : productosGuardados) {
            stock.put(producto.getNombre(), producto.getStock());
            productos.put(producto.getNombre(), producto);
        }
    }
}
