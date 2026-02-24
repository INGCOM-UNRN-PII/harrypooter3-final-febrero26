package ar.unrn;

import java.time.LocalDateTime;

/**
 * Define operaciones del gestor sobre centros y envíos.
 */
public interface GestorLogisticaOperable {

    /**
     * Crea un centro de distribución.
     *
     * @param nombre    nombre único del centro
     * @param ubicacion ubicación geográfica
     * @throws IllegalArgumentException si existe el centro o hay argumentos inválidos
     */
    void crearCentroDistribucion(String nombre, String ubicacion);

    /**
     * Registra un envío en un centro específico.
     * Garantiza la unicidad global del trackingId dentro del sistema.
     *
     * @param nombreCentro nombre del centro
     * @param envio        envío a registrar
     * @throws IllegalArgumentException si el centro no existe, si el envío es nulo,
     *                                  si el trackingId es inválido o si ya existe
     */
    void registrarEnvio(String nombreCentro, Envio envio);

    /**
     * Actualiza el estado de un envío en un centro.
     *
     * @param nombreCentro nombre del centro
     * @param trackingId   trackingId del envío
     * @param descripcion  descripción del evento
     * @param fechaHora    fecha y hora del evento
     * @throws IllegalArgumentException si el centro o envío no existe o hay
     *                                  argumentos inválidos
     */
    void actualizarEstado(
            String nombreCentro,
            String trackingId,
            String descripcion,
            LocalDateTime fechaHora);

    /**
     * Elimina un envío del sistema por trackingId.
     *
     * @param trackingId trackingId del envío
     * @throws IllegalArgumentException si no existe el envío
     */
    void eliminarEnvio(String trackingId);

    /**
     * Vacía un centro eliminando todos sus envíos.
     *
     * @param nombreCentro nombre del centro
     * @throws IllegalArgumentException si el centro no existe
     */
    void vaciarCentro(String nombreCentro);
}
