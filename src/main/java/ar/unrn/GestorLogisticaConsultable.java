package ar.unrn;

import java.util.List;

/**
 * Define consultas del gestor sobre centros y envíos.
 */
public interface GestorLogisticaConsultable {

    /**
     * Busca un envío por trackingId.
     *
     * @param trackingId trackingId del envío
     * @return envío encontrado
     * @throws IllegalArgumentException si el trackingId es nulo o está en blanco,
     *                                  o si no existe el envío
     */
    Envio buscarEnvioPorTrackingId(String trackingId);

    /**
     * Busca envíos por destinatario en todos los centros.
     *
     * @param destinatario nombre del destinatario
     * @return lista de envíos encontrados
     * @throws IllegalArgumentException si {@code destinatario} es nulo o está en blanco
     */
    List<Envio> buscarEnviosPorDestinatario(String destinatario);

    /**
     * Lista los envíos por peso (mayor a menor) en un centro.
     *
     * @param nombreCentro nombre del centro
     * @return lista ordenada por peso
     * @throws IllegalArgumentException si el centro no existe
     */
    List<Envio> listarEnviosPorPeso(String nombreCentro);

    /**
     * Lista los envíos por prioridad en un centro.
     *
     * @param nombreCentro nombre del centro
     * @return lista ordenada por prioridad
     * @throws IllegalArgumentException si el centro no existe
     */
    List<Envio> listarEnviosPorPrioridad(String nombreCentro);

    /**
     * Calcula estadísticas de un centro.
     *
     * @param nombreCentro nombre del centro
     * @return estadísticas calculadas
     * @throws IllegalArgumentException si el centro no existe
     */
    EstadisticasCentro estadisticasCentro(String nombreCentro);
}
