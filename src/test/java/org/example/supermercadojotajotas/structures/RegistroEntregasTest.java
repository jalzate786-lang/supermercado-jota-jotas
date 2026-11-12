
package org.example.supermercadojotajotas.structures;

import org.example.supermercadojotajotas.model.RegistroEntrega;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RegistroEntregasTest {

    @Test
    void iniciarRegistroVacio() {
        RegistroEntregas registros = new RegistroEntregas();

        assertEquals(0, registros.cantidadRegistros());
        assertTrue(registros.getRegistros().isEmpty());
    }

    @Test
    void cargarListaDeRegistros() {
        RegistroEntregas registros = new RegistroEntregas();

        registros.cargarRegistros(new ArrayList<>());

        assertEquals(0, registros.cantidadRegistros());
    }
}
