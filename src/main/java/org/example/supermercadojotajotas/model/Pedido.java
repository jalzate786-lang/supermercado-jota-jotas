
package org.example.supermercadojotajotas.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String cliente;

    private boolean cuponRapidez;

    private String estado;

    private LocalDateTime fechaCreacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "pedido_productos",
            joinColumns = @JoinColumn(name = "pedido_id")
    )
    @MapKeyColumn(name = "nombre_producto")
    @Column(name = "cantidad")
    private Map<String, Integer> productos;

    public Pedido() {
    }

    public Pedido(String cliente, boolean cuponRapidez) {
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

    public Map<String, Integer> getProductos() {
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
