package ar.unrn;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un evento de seguimiento asociado a un envío.
 */
public final class EventoEstado implements Serializable {

    /**
     * Identificador de serialización.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Descripción textual del evento de estado.
     */
    private final String descripcionTexto;

    /**
     * Fecha y hora en la que ocurrió el evento.
     */
    private final LocalDateTime fechaHoraEvento;

    /**
     * Crea un evento de estado.
     *
     * @param descripcionEvento descripción del evento
     * @param fechaHora         fecha y hora del evento
     * @throws IllegalArgumentException si algún argumento es nulo o está en blanco
     */
    public EventoEstado(
            String descripcionEvento,
            LocalDateTime fechaHora) {
        super();

        GestorLogisticaBase.exigirNoNulo(descripcionEvento, "La descripción");

        String descripcionStrip = descripcionEvento.strip();
        GestorLogisticaBase.exigirTexto(descripcionStrip, "La descripción");

        GestorLogisticaBase.exigirNoNulo(
                fechaHora,
                "La fecha y hora"
        );

        this.descripcionTexto = descripcionStrip;
        this.fechaHoraEvento = fechaHora;
    }

    /**
     * Devuelve la descripción del evento.
     *
     * @return descripción del evento
     */
    public String descripcion() {
        return this.descripcionTexto;
    }

    /**
     * Devuelve la fecha y hora del evento.
     *
     * @return fecha y hora del evento
     */
    public LocalDateTime fechaHora() {
        return this.fechaHoraEvento;
    }

    /**
     * Compara este evento con otro objeto.
     * Se consideran iguales si descripción y fecha/hora coinciden.
     *
     * @param objeto objeto a comparar
     * @return true si representan el mismo evento, false en caso contrario
     */
    @Override
    public boolean equals(Object objeto) {
        return this == objeto
                || (objeto instanceof EventoEstado evento
                && Objects.equals(this.descripcionTexto, evento.descripcionTexto)
                && Objects.equals(this.fechaHoraEvento, evento.fechaHoraEvento));
    }

    /**
     * Calcula el código hash consistente con {@link #equals(Object)}.
     *
     * @return código hash del evento
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.descripcionTexto, this.fechaHoraEvento);
    }
}
