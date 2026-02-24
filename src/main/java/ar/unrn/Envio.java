package ar.unrn;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Clase que representa un paquete en el sistema con su historial de estados.
 */
public final class Envio implements Serializable {

    /**
     * Id de serialización.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Estado final para paquetes entregados.
     */
    private static final String ESTADO_ENTREGADO = "Entregado";

    /**
     * Peso mínimo permitido.
     */
    private static final double PESO_MINIMO = 0.0d;

    /**
     * Código de seguimiento único.
     */
    private final String trackingIdEnvio;

    /**
     * Peso del envío.
     */
    private final double pesoEnvio;

    /**
     * Nombre del destinatario.
     */
    private final String destinatarioEnvio;

    /**
     * Prioridad del envío.
     */
    private final Prioridad prioridadEnvio;

    /**
     * Historial de eventos de estado.
     */
    private final List<EventoEstado> historialEnvio;

    /**
     * Válida los argumentos usados para construir un {@link Envio}.
     */
    private static final class ValidadorEnvio {

        private static void validarTrackingId(String trackingId) {
            GestorLogisticaBase.exigirTrackingId(trackingId);
        }

        private static void validarPeso(double peso) {
            if (peso <= PESO_MINIMO) {
                throw new IllegalArgumentException(
                        """
                        El peso debe ser mayor a cero.
                        """
                );
            }
        }

        private static void validarDestinatario(String destinatario) {
            GestorLogisticaBase.exigirTexto(
                    destinatario,
                    "El destinatario"
            );
        }

        private static void validarPrioridad(Prioridad prioridad) {
            GestorLogisticaBase.exigirNoNulo(
                    prioridad,
                    "La prioridad"
            );
        }
    }

    /**
     * Crea un envío.
     *
     * @param trackingId   código de seguimiento único
     * @param peso         peso del envío
     * @param destinatario nombre del destinatario
     * @param prioridad    prioridad del envío
     * @throws IllegalArgumentException si algún argumento es nulo, está en blanco
     *                                  o tiene valores fuera de rango
     */
    public Envio(
            String trackingId,
            double peso,
            String destinatario,
            Prioridad prioridad) {
        super();
        ValidadorEnvio.validarTrackingId(trackingId);
        ValidadorEnvio.validarPeso(peso);
        ValidadorEnvio.validarDestinatario(destinatario);
        ValidadorEnvio.validarPrioridad(prioridad);

        this.trackingIdEnvio = trackingId;
        this.pesoEnvio = peso;
        this.destinatarioEnvio = destinatario;
        this.prioridadEnvio = prioridad;
        this.historialEnvio = new LinkedList<>();
    }

    /**
     * Devuelve el trackingId.
     *
     * @return trackingId del envío
     */
    public String trackingId() {
        return this.trackingIdEnvio;
    }

    /**
     * Devuelve el peso.
     *
     * @return peso del envío
     */
    public double peso() {
        return this.pesoEnvio;
    }

    /**
     * Devuelve el destinatario.
     *
     * @return destinatario del envío
     */
    public String destinatario() {
        return this.destinatarioEnvio;
    }

    /**
     * Devuelve la prioridad.
     *
     * @return prioridad del envío
     */
    public Prioridad prioridad() {
        return this.prioridadEnvio;
    }

    /**
     * Agrega un evento de estado al historial.
     *
     * @param descripcion descripción del evento
     * @param fechaHora   fecha y hora del evento
     * @throws IllegalArgumentException si {@link EventoEstado} no puede crearse
     *                                  por argumentos inválidos
     */
    public void registrarEstado(
            String descripcion,
            LocalDateTime fechaHora) {
        EventoEstado evento = new EventoEstado(descripcion, fechaHora);
        this.historialEnvio.add(evento);
    }

    /**
     * Devuelve una vista inmodificable del historial.
     *
     * @return lista inmodificable de eventos
     */
    public List<EventoEstado> historial() {
        return Collections.unmodifiableList(this.historialEnvio);
    }

    /**
     * Indica si el envío tiene al menos un evento "Entregado".
     *
     * @return true si está entregado, false en caso contrario
     */
    public boolean entregado() {
        return this.historialEnvio.stream()
                .anyMatch(evento -> ESTADO_ENTREGADO.equals(evento.descripcion()));
    }

    /**
     * Dos envíos son iguales si todos sus atributo coinciden.
     *
     * @param objeto objeto a comparar
     * @return true si son el mismo envío, false en caso contrario
     */
    @Override
    public boolean equals(Object objeto) {
        return this == objeto
                || (objeto instanceof Envio envio
                && Objects.equals(this.trackingIdEnvio, envio.trackingIdEnvio)
                && Double.compare(this.pesoEnvio, envio.pesoEnvio) == 0
                && Objects.equals(this.destinatarioEnvio, envio.destinatarioEnvio)
                && this.prioridadEnvio == envio.prioridadEnvio
                && Objects.equals(this.historialEnvio, envio.historialEnvio));
    }

    /**
     * Calcula el código hash consistente con {@link #equals(Object)}.
     *
     * @return código hash del envío
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                this.trackingIdEnvio,
                this.pesoEnvio,
                this.destinatarioEnvio,
                this.prioridadEnvio,
                this.historialEnvio);
    }
}
