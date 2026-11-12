package org.example.supermercadojotajotas.model;

import java.time.LocalDateTime;
import java.util.HashMap;

public class RegistroEntrega {

    private int pedidoId;
    private HashMap<String, Integer> productos;
    private LocalDateTime fecha;
    private String estado;

    public RegistroEntrega(
            int pedidoId,
            HashMap<String, Integer> productos,
            LocalDateTime fecha,
            String estado) {

        this.pedidoId = pedidoId;
        this.productos = productos;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public HashMap<String, Integer> getProductos() {
        return productos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "RegistroEntrega{" +
                "pedidoId=" + pedidoId +
                ", productos=" + productos +
                ", fecha=" + fecha +
                ", estado='" + estado + '\'' +
                '}';
    }
}