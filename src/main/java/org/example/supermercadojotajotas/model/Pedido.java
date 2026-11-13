package org.example.supermercadojotajotas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "pedidos")
public class Pedido implements Comparable<Pedido> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String cliente;
    private boolean cuponRapidez;
    private String estado;
    private LocalDateTime fechaCreacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_productos", joinColumns = @JoinColumn(name = "pedido_id"))
    @MapKeyColumn(name = "nombre_producto")
    @Column(name = "cantidad")
    private Map<String, Integer> productos;

    public Pedido() {}

    public Pedido(String cliente, boolean cuponRapidez) {
        this.cliente = cliente;
        this.cuponRapidez = cuponRapidez;
        this.estado = "ESPERA";
        this.fechaCreacion = LocalDateTime.now();
        this.productos = new HashMap<>();
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public boolean isCuponRapidez() { return cuponRapidez; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Map<String, Integer> getProductos() { return productos; }

    public void agregarProducto(String nombre, int cantidad) { productos.put(nombre, cantidad); }
    public void setEstado(String estado) { this.estado = estado; }


    @Override
    public int compareTo(Pedido otro) {


        if (this.cuponRapidez && !otro.cuponRapidez) {
            return -1;
        }


        if (!this.cuponRapidez && otro.cuponRapidez) {
            return 1;
        }

        return this.fechaCreacion.compareTo(otro.fechaCreacion);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Pedido pedido)) return false;
        return cuponRapidez == pedido.cuponRapidez && Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cuponRapidez);
    }
}
