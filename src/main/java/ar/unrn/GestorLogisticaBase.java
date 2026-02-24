package ar.unrn;

import java.util.Map;

/**
 * Componentes compartidos del gestor de logística.
 * Reúne utilidades para validación y búsqueda de centros.
 */
public final class GestorLogisticaBase {

    /**
     * Exige que el valor no sea nulo.
     *
     * @param valor    valor a validar
     * @param etiqueta nombre del argumento para el mensaje de error
     * @throws IllegalArgumentException si {@code valor} es nulo
     */
    public static void exigirNoNulo(Object valor, String etiqueta) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    etiqueta
                            +
                            """
                            no puede ser nulo.
                            """
            );
        }
    }

    /**
     * Exige que el texto no sea nulo ni esté en blanco.
     *
     * @param texto    texto a validar
     * @param etiqueta nombre del argumento para el mensaje de error
     * @throws IllegalArgumentException si {@code texto} es nulo o está en blanco
     */
    public static void exigirTexto(String texto, String etiqueta) {
        exigirNoNulo(texto, etiqueta);
        if (texto.isBlank()) {
            throw new IllegalArgumentException(
                    etiqueta
                            +
                            """
                            no puede estar en blanco.
                            """
            );
        }
    }

    /**
     * Exige que el trackingId no sea nulo ni esté en blanco.
     *
     * @param trackingId trackingId a validar
     * @throws IllegalArgumentException si {@code trackingId} es nulo o está en blanco
     */
    public static void exigirTrackingId(String trackingId) {
        exigirTexto(trackingId, "El trackingId");
    }

    /**
     * Crea una excepción estándar para indicar que un envío no existe
     * para el trackingId provisto.
     *
     * @return excepción con el mensaje estándar
     */
    public static IllegalArgumentException errorEnvioInexistente() {
        return new IllegalArgumentException(
                """
                No existe envío para el trackingId.
                """
        );
    }

    /**
     * Exige que el envío exista y, si no existe, lanza la
     * excepción estándar del sistema.
     *
     * @param envio envío a validar
     * @throws IllegalArgumentException si {@code envio} es nulo
     */
    public static void exigirEnvioExistente(Object envio) {
        if (envio == null) {
            throw errorEnvioInexistente();
        }
    }

    /**
     * Obtiene un centro por nombre validando los argumentos.
     *
     * @param centros      mapa de centros
     * @param nombreCentro nombre del centro
     * @return centro encontrado
     * @throws IllegalArgumentException si {@code nombreCentro} es nulo, está en blanco
     *                                  o si el centro no existe
     */
    public static CentroDistribucion obtenerCentro(
            Map<String, CentroDistribucion> centros,
            String nombreCentro) {

        exigirTexto(
                nombreCentro,
                "El nombreCentro"
        );

        CentroDistribucion centro = centros.get(nombreCentro);
        if (centro == null) {
            throw new IllegalArgumentException(
                    """
                    No existe el centro.
                    """
            );
        }
        return centro;
    }

    /**
     * Encuentra el centro que contiene un envío para el trackingId.
     *
     * @param centros    mapa de centros
     * @param trackingId trackingId a buscar
     * @return centro que contiene el envío
     * @throws IllegalArgumentException si {@code trackingId} es inválido o si el
     *                                  envío no existe en ningún centro
     */
    public static CentroDistribucion encontrarCentro(
            Map<String, CentroDistribucion> centros,
            String trackingId) {

        exigirNoNulo(centros, "Los centros");
        exigirTrackingId(trackingId);

        return centros.values()
                .stream()
                .filter(centro -> centro.envios().containsKey(trackingId))
                .findFirst()
                .orElseThrow(GestorLogisticaBase::errorEnvioInexistente);
    }
}
