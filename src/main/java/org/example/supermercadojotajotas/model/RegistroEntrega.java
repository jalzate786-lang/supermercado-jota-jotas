
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
@Table(name = "registros_entrega")
public class RegistroEntrega {
    //inicializacion de las variables y de las tablas de la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int pedidoId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "registro_entrega_productos",
            joinColumns = @JoinColumn(name = "registro_id")
    )
    @MapKeyColumn(name = "nombre_producto")
    @Column(name = "cantidad")
    private Map<String, Integer> productos;

    private LocalDateTime fecha;
    private String estado;

    public RegistroEntrega() {
    }

    public RegistroEntrega(int pedidoId, HashMap<String, Integer> productos,
                           LocalDateTime fecha, String estado) {
        this.pedidoId = pedidoId;
        this.productos = productos;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public Map<String, Integer> getProductos() {
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
                "id=" + id +
                ", pedidoId=" + pedidoId +
                ", productos=" + productos +
                ", fecha=" + fecha +
                ", estado='" + estado + '\'' +
                '}';
    }
}
