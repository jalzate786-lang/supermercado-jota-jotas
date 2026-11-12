
package org.example.supermercadojotajotas.service;

import org.example.supermercadojotajotas.model.Pedido;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogEntrega {

    private final String ruta = "logs/entregas.txt";

    public void registrar(Pedido pedido, LocalDateTime fechaCambio) {

        File carpeta = new File("logs");

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        try (FileWriter escritor = new FileWriter(ruta, true)) {

            escritor.write("========================================\n");
            escritor.write("PEDIDO: " + pedido.getId() + "\n");
            escritor.write("CLIENTE: " + pedido.getCliente() + "\n");
            escritor.write("PRODUCTOS: " + pedido.getProductos() + "\n");
            escritor.write("FECHA DE CAMBIO: " + fechaCambio + "\n");
            escritor.write("ESTADO: " + pedido.getEstado() + "\n");
            escritor.write("========================================\n\n");

        } catch (IOException e) {
            System.out.println("Error al crear el registro de entrega: " + e.getMessage());
        }
    }
}
