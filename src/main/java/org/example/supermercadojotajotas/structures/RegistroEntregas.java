package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.RegistroEntrega;

import java.util.ArrayList;
import java.util.List;

public class RegistroEntregas {

    private List<RegistroEntrega> registros;

    public RegistroEntregas() {
        registros = new ArrayList<>();
    }

    public void agregarRegistro(RegistroEntrega registro) {
        registros.add(registro);
    }

    public List<RegistroEntrega> getRegistros() {
        return registros;
    }

    public int cantidadRegistros() {
        return registros.size();
    }
}