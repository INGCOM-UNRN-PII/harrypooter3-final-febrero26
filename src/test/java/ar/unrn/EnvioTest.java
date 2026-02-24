package ar.unrn;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de validaciones del constructor de {@link Envio}.
 */
final class EnvioTest {

    private static final String TRACKER =
            """
            TRACKER-001
            """;

    private static final String DESTINATARIO =
            """
            Juan Perez
            """;

    private static final String VACIO =
            """
            """;

    private static final double PESO_OK = 1.0d;

    @Test
    void pesoCero() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Envio(TRACKER, 0.0d, DESTINATARIO, Prioridad.ESTANDAR),
                """
                Se esperaba excepción por peso inválido.
                """
        );
    }

    @Test
    void pesoNegativo() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Envio(TRACKER, -0.1d, DESTINATARIO, Prioridad.ESTANDAR),
                """
                Se esperaba excepción por peso inválido.
                """
        );
    }

    @Test
    void destinatarioNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Envio(TRACKER, PESO_OK, null, Prioridad.ESTANDAR),
                """
                Se esperaba excepción por destinatario inválido.
                """
        );
    }

    @Test
    void destinatarioEnVacio() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Envio(TRACKER, PESO_OK, VACIO, Prioridad.ESTANDAR),
                """
                Se esperaba excepción por destinatario inválido.
                """
        );
    }

    @Test
    void trackingIdNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Envio(null, PESO_OK, DESTINATARIO, Prioridad.ESTANDAR),
                """
                Se esperaba excepción por trackingId inválido.
                """
        );
    }

    @Test
    void trackingIdEnVacio() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Envio(VACIO, PESO_OK, DESTINATARIO, Prioridad.ESTANDAR),
                """
                Se esperaba excepción por trackingId inválido.
                """
        );
    }

    @Test
    void prioridadNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Envio(TRACKER, PESO_OK, DESTINATARIO, null),
                """
                Se esperaba excepción por prioridad null.
                """
        );
    }
}

/**
 * Pruebas de comportamiento general de {@link Envio}.
 */
final class EnvioComportamientoTest {

    private static final String TRACKER_UNO =
            """
            TRACKER-001
            """;

    private static final String TRACKER_DOS =
            """
            TRACKER-002
            """;

    private static final String DESTINATARIO =
            """
            Juan Perez
            """;

    private static final String EVENTO_RECIBIDO =
            """
            Recibido
            """;

    private static final LocalDateTime FECHA =
            LocalDateTime.of(2026, 2, 1, 10, 0);

    private static final double PESO = 1.0d;

    @Test
    void historialEvento() {
        Envio envio = crearEnvio(TRACKER_UNO);

        envio.registrarEstado(EVENTO_RECIBIDO, FECHA);

        Assertions.assertEquals(
                1,
                envio.historial().size(),
                """
                Se esperaba un evento en el historial.
                """
        );
    }

    @Test
    void entregadoFalse() {
        Envio envio = crearEnvio(TRACKER_UNO);

        Assertions.assertFalse(
                envio.entregado(),
                """
                Se esperaba false sin evento "Entregado".
                """
        );
    }

    @Test
    void mismaInstancia() {
        Envio envio = crearEnvio(TRACKER_UNO);

        Assertions.assertEquals(
                envio,
                envio,
                """
                Se esperaba equals true para la misma instancia.
                """
        );
    }

    @Test
    void distintoDeNull() {
        Envio envio = crearEnvio(TRACKER_UNO);

        Assertions.assertNotEquals(
                null,
                envio,
                """
                Se esperaba que el envío sea distinto de null.
                """
        );
    }

    @Test
    void trackingDistinto() {
        Envio uno = crearEnvio(TRACKER_UNO);
        Envio otro = crearEnvio(TRACKER_DOS);

        Assertions.assertNotEquals(
                uno,
                otro,
                """
                Se esperaba equals false con trackingId distinto.
                """
        );
    }

    @Test
    void hashCodeIguales() {
        Envio uno = crearEnvio(TRACKER_UNO);
        Envio dos = crearEnvio(TRACKER_UNO);

        Assertions.assertEquals(
                uno.hashCode(),
                dos.hashCode(),
                """
                Se esperaba mismo hashCode para envíos iguales.
                """
        );
    }

    /**
     * Crea un envío válido para pruebas.
     *
     * @param tracker trackingId
     * @return envío válido
     */
    private static Envio crearEnvio(String tracker) {
        return new Envio(tracker, PESO, DESTINATARIO, Prioridad.ESTANDAR);
    }
}

/**
 * Pruebas puntuales para cubrir ramas de entrega en {@link Envio}.
 */
final class EnvioEntregaTest {

    private static final LocalDateTime FECHA =
            LocalDateTime.of(2026, 2, 1, 10, 0);

    @Test
    void eventoEntregado() {
        Envio envio = new Envio("TRACKER-001", 1.0d, "Ana", Prioridad.ESTANDAR);

        envio.registrarEstado("Entregado", FECHA);

        Assertions.assertTrue(
                envio.entregado(),
                """
                Se esperaba true si existe evento Entregado.
                """
        );
    }

    @Test
    void historialInmodificable() {
        Envio envio = new Envio("TRACKER-001", 1.0d, "Ana", Prioridad.ESTANDAR);

        Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> envio.historial().add(new EventoEstado("X", FECHA)),
                """
                Se esperaba lista inmodificable.
                """
        );
    }
}

/**
 * Pruebas de {@link EventoEstado}.
 */
final class EventoEstadoTest {

    private static final String ESTADO =
            """
            Recibido
            """;

    private static final String VACIO =
            """
            """;

    private static final LocalDateTime FECHA =
            LocalDateTime.of(2026, 2, 1, 10, 0);

    @Test
    void descripcionEnVacio() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new EventoEstado(VACIO, FECHA),
                """
                Se esperaba excepción por descripción inválida.
                """
        );
    }

    @Test
    void fechaHoraNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new EventoEstado(ESTADO, null),
                """
                Se esperaba excepción por fecha/hora null.
                """
        );
    }

    @Test
    void fechaHoraConstructor() {
        EventoEstado evento = new EventoEstado(ESTADO, FECHA);

        Assertions.assertEquals(
                FECHA,
                evento.fechaHora(),
                """
                Se esperaba que fechaHora devuelva el valor del constructor.
                """
        );
    }

    @Test
    void equalsMismaInstancia() {
        EventoEstado evento = new EventoEstado(ESTADO, FECHA);

        Assertions.assertEquals(
                evento,
                evento,
                """
                Se esperaba equals true para la misma instancia.
                """
        );
    }

    @Test
    void equalsFalseNull() {
        EventoEstado evento = new EventoEstado(ESTADO, FECHA);

        Assertions.assertNotEquals(
                null,
                evento,
                """
                Se esperaba equals false contra null.
                """
        );
    }

    @Test
    void equalsOtroTipo() {
        EventoEstado evento = new EventoEstado(ESTADO, FECHA);

        Assertions.assertNotEquals(
                ESTADO,
                evento,
                """
                Se esperaba equals false contra otro tipo.
                """
        );
    }

    @Test
    void equalsEventosIguales() {
        EventoEstado uno = new EventoEstado(ESTADO, FECHA);
        EventoEstado dos = new EventoEstado(ESTADO, FECHA);

        Assertions.assertEquals(
                uno,
                dos,
                """
                Se esperaba equals true para eventos iguales.
                """
        );
    }

    @Test
    void hashCodeEventosIguales() {
        EventoEstado uno = new EventoEstado(ESTADO, FECHA);
        EventoEstado dos = new EventoEstado(ESTADO, FECHA);

        Assertions.assertEquals(
                uno.hashCode(),
                dos.hashCode(),
                """
                Se esperaba mismo hashCode para eventos iguales.
                """
        );
    }
}
