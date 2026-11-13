
package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Producto;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class Inventario {

    private HashMap<String, Integer> stock;
    private HashMap<String, Producto> productos;

    public Inventario() {
        stock = new HashMap<>();
        productos = new HashMap<>();
    }

    public boolean agregarProducto(Producto producto) {
        //verificaciones de seguridad para que el producto entre con los valores correctos
        if (producto == null ||
                producto.getNombre() == null ||
                producto.getNombre().trim().isEmpty() ||
                producto.getPrecio() <= 0 ||
                producto.getStock() < 0) {
            return false;
        }

        String nombre = producto.getNombre().trim();

        if (existeProducto(nombre)) {
            return false;
        }

        producto.setNombre(nombre);
        //agrega a cada hashmap si paso las pruebas de seguridad
        stock.put(nombre, producto.getStock());
        productos.put(nombre, producto);

        return true;
    }

    public String buscarNombre(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        String nombreBuscado = nombre.trim();

        for (String nombreGuardado : productos.keySet()) {

            if (nombreGuardado.equalsIgnoreCase(nombreBuscado)) {
                return nombreGuardado;
            }
        }

        return null;
    }

    public int buscarStock(String nombre) {

        String nombreOficial = buscarNombre(nombre);

        if (nombreOficial == null) {
            return 0;
        }

        return stock.get(nombreOficial);
    }

    public boolean actualizarStock(String nombre, int nuevoStock) {

        String nombreOficial = buscarNombre(nombre);

        if (nombreOficial == null || nuevoStock < 0) {
            return false;
        }

        stock.put(nombreOficial, nuevoStock);
        //ingresa a la clase del producto en cuestion , y luego cambia solo el parametro stcock
        //recordemos que el hashmap productos recibe HashMap<String, Producto>
        productos.get(nombreOficial).setStock(nuevoStock);

        return true;
    }

    public boolean existeProducto(String nombre) {
        return buscarNombre(nombre) != null;
    }



    public LinkedList<Producto> listarProductos() {
        return new LinkedList<>(productos.values());
    }

    public void cargarProductos(LinkedList<Producto> productosGuardados) {
        stock.clear();
        productos.clear();

        if (productosGuardados == null) {
            return;
        }

        for (Producto producto : productosGuardados) {
                //se itera la lista productosGuardados
            // se verifica la seguridad del nombre y del stock
            if (producto != null &&
                    producto.getNombre() != null &&
                    !producto.getNombre().trim().isEmpty() &&
                    producto.getPrecio() > 0 &&
                    producto.getStock() >= 0) {

                String nombre = producto.getNombre().trim();
                //la primera vez que se añade el nombre de cada producto, si ya estaba registrado no hace nada
                if (!existeProducto(nombre)) {

                    producto.setNombre(nombre);

                    stock.put(nombre, producto.getStock());
                    productos.put(nombre, producto);
                }
            }
        }
    }
}
