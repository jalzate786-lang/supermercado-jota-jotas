package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Producto;

import java.util.HashMap;

public class Inventario {

    private HashMap<String, Integer> stock;

    public Inventario() {
        stock = new HashMap<>();
    }

    public boolean agregarProducto(Producto producto) {

        if (producto.getPrecio() <= 0) {
            return false;
        }

        if (producto.getStock() < 0) {
            return false;
        }

        stock.put(producto.getNombre(), producto.getStock());
        return true;
    }

    public Integer buscarStock(String nombre) {
        return stock.get(nombre);
    }

    public boolean actualizarStock(String nombre, int nuevoStock) {

        if (nuevoStock < 0) {
            return false;
        }

        if (!stock.containsKey(nombre)) {
            return false;
        }

        stock.put(nombre, nuevoStock);
        return true;
    }

    public boolean existeProducto(String nombre) {
        return stock.containsKey(nombre);
    }

    public HashMap<String, Integer> getStock() {
        return stock;
    }
}