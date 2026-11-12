package org.example.supermercadojotajotas.model;

import java.util.HashMap;

public class Carrito {

    private HashMap<String, Integer> productos;

    public Carrito() {
        productos = new HashMap<>();
    }

    public boolean agregarProducto(String nombre, int cantidad, int stockDisponible) {

        if (cantidad <= 0) {
            return false;
        }

        if (cantidad > stockDisponible) {
            return false;
        }

        int cantidadActual = 0;

        if (productos.containsKey(nombre)) {
            cantidadActual = productos.get(nombre);
        }

        if (cantidadActual + cantidad > stockDisponible) {
            return false;
        }

        productos.put(nombre, cantidadActual + cantidad);

        return true;
    }

    public HashMap<String, Integer> getProductos() {
        return productos;
    }

    public boolean estaVacio() {
        return productos.isEmpty();
    }

    public void vaciar() {
        productos.clear();
    }

    @Override
    public String toString() {
        return "Carrito{" +
                "productos=" + productos +
                '}';
    }
}