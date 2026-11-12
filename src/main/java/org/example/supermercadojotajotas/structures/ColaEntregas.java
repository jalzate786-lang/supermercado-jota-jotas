
package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ColaEntregas {

    private PriorityQueue<Pedido> pedidos;

    public ColaEntregas() {

        pedidos = new PriorityQueue<>((p1, p2) -> {

            if (p1.isCuponRapidez() && !p2.isCuponRapidez()) {
                return -1;
            }

            if (!p1.isCuponRapidez() && p2.isCuponRapidez()) {
                return 1;
            }

            return p1.getFechaCreacion().compareTo(p2.getFechaCreacion());
        });
    }

    public void agregarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    public Pedido siguientePedido() {
        return pedidos.poll();
    }

    public Pedido verSiguiente() {
        return pedidos.peek();
    }

    public boolean estaVacia() {
        return pedidos.isEmpty();
    }

    public int cantidadPedidos() {
        return pedidos.size();
    }

    public List<Pedido> listarPedidos() {

        PriorityQueue<Pedido> copia = new PriorityQueue<>(pedidos);
        List<Pedido> lista = new ArrayList<>();

        while (!copia.isEmpty()) {
            lista.add(copia.poll());
        }

        return lista;
    }
}
