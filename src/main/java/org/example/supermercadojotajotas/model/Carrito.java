package org.example.supermercadojotajotas.model;

import java.util.HashMap;

public class Carrito {
    //se inicializa normal la plantilla de carrito sin ninguna etqiueta ya que no necesita guardarse en base de datos
    //lo que se guarda en base de datos es el pedido ya realizado

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
        //si el producto ya estaba en el carrito comprueba cuantas unidades tenia
        if (productos.containsKey(nombre)) {
            cantidadActual = productos.get(nombre);
        }
        //si la cantidad actual mas la cantidad que necesita es mayor al stockDisponible retrona false para que se cancele el metodo
        if (cantidadActual + cantidad > stockDisponible) {
            return false;
        }
        //sitodo esta bien se agrega en el carrito
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