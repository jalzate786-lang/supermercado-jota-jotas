package org.example.supermercadojotajotas.model;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Pedido {

    private int id;
    private String cliente;
    private boolean cuponRapidez;
    private String estado;
    private LocalDateTime fechaCreacion;
    private HashMap<String, Integer> productos;

    public Pedido(int id, String cliente, boolean cuponRapidez) {
        this.id = id;
        this.cliente = cliente;
        this.cuponRapidez = cuponRapidez;
        this.estado = "ESPERA";
        this.fechaCreacion = LocalDateTime.now();
        this.productos = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public boolean isCuponRapidez() {
        return cuponRapidez;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public HashMap<String, Integer> getProductos() {
        return productos;
    }

    public void agregarProducto(String nombre, int cantidad) {
        productos.put(nombre, cantidad);
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente='" + cliente + '\'' +
                ", cuponRapidez=" + cuponRapidez +
                ", estado='" + estado + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", productos=" + productos +
                '}';
    }
}