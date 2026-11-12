
package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Producto;

import java.util.ArrayList;
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

        stock.put(producto.getNombre(), producto.getStock());
        productos.put(producto.getNombre(), producto);

        return true;
    }

    public Integer buscarStock(String nombre) {
        return stock.get(nombre);
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
        return stock.containsKey(nombre);
    }

    public HashMap<String, Integer> getStock() {
        return stock;
    }

    public Collection<Producto> listarProductos() {
        return new ArrayList<>(productos.values());
    }
}
