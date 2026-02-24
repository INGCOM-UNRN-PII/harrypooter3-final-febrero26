package ar.unrn;

import java.io.IOException;

/**
 * Define un contrato para guardar el estado de un objeto a disco.
 */
public interface Guardable {

    /**
     * Guarda el estado del objeto en disco.
     *
     * @param ruta ruta del archivo destino
     * @throws IllegalArgumentException si {@code ruta} es nulo o está vacía
     * @throws IOException si ocurre un error de entrada/salida
     */
    void guardar(String ruta) throws IOException;
}
