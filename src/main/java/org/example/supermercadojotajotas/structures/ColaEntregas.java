package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.Pedido;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ColaEntregas {

    private PriorityQueue<Pedido> pedidos;

    public ColaEntregas() {

        pedidos = new PriorityQueue<>();
    }

    public void agregarPedido(Pedido pedido) {
        if (pedido != null) {
            pedidos.add(pedido);
        }
    }

    public Pedido siguientePedido() { return pedidos.poll(); }
    public Pedido verSiguiente() { return pedidos.peek(); }
    public boolean eliminarPedido(int pedidoId) { return pedidos.removeIf(p -> p.getId() == pedidoId); }
    public boolean estaVacia() { return pedidos.isEmpty(); }
    public int cantidadPedidos() { return pedidos.size(); }

    public List<Pedido> listarPedidos() {
        PriorityQueue<Pedido> copia = new PriorityQueue<>(pedidos);
        List<Pedido> lista = new ArrayList<>();
        while (!copia.isEmpty()) {
            lista.add(copia.poll());
        }
        return lista;
    }
}