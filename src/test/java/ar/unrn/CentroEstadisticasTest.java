package ar.unrn;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de estadísticas en {@link CentroDistribucion}.
 */
final class CentroEstadisticasTest {

    private static final String CENTRO_UNO =
            """
            CentroUno
            """;

    private static final String CIUDAD =
            """
            Bariloche
            """;

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
            Ana
            """;

    private static final LocalDateTime FECHA =
            LocalDateTime.of(2026, 2, 1, 10, 0);

    @Test
    void estadisticasPesoCero() {
        CentroDistribucion centro = new CentroDistribucion(CENTRO_UNO, CIUDAD);

        EstadisticasCentro est = centro.consultas().estadisticas();

        Assertions.assertEquals(
                0.0d,
                est.pesoTotalTransportado(),
                """
                Se esperaba peso total 0 si no hay envíos.
                """
        );
    }

    @Test
    void estadisticasPorcentajeCero() {
        CentroDistribucion centro = new CentroDistribucion(CENTRO_UNO, CIUDAD);

        EstadisticasCentro est = centro.consultas().estadisticas();

        Assertions.assertEquals(
                0.0d,
                est.porcentajeEntregado(),
                """
                Se esperaba porcentaje 0 si no hay envíos.
                """
        );
    }

    @Test
    void estadisticasPesoTotal() {
        CentroDistribucion centro = new CentroDistribucion(CENTRO_UNO, CIUDAD);

        centro.operaciones().registrarEnvio(
                new Envio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );
        centro.operaciones().registrarEnvio(
                new Envio(TRACKER_DOS, 2.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );

        EstadisticasCentro est = centro.consultas().estadisticas();

        Assertions.assertEquals(
                3.0d,
                est.pesoTotalTransportado(),
                """
                Se esperaba suma de pesos correcta.
                """
        );
    }

    @Test
    void estadisticasPorcentajeEntregado() {
        CentroDistribucion centro = new CentroDistribucion(CENTRO_UNO, CIUDAD);

        Envio uno = new Envio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR);
        Envio dos = new Envio(TRACKER_DOS, 1.0d, DESTINATARIO, Prioridad.ESTANDAR);

        dos.registrarEstado("Entregado", FECHA);

        centro.operaciones().registrarEnvio(uno);
        centro.operaciones().registrarEnvio(dos);

        EstadisticasCentro est = centro.consultas().estadisticas();

        Assertions.assertEquals(
                50.0d,
                est.porcentajeEntregado(),
                """
                Se esperaba 50% con 1 entregado sobre 2.
                """
        );
    }
}

/**
 * Pruebas de {@link EstadisticasCentro}.
 */
final class EstadisticasCentroTest {

    @Test
    void noPermitePesoNegativo() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new EstadisticasCentro(-1.0d, 0.0d),
                """
                Se esperaba excepción por peso inválido.
                """
        );
    }

    @Test
    void noPermitePorcentajeNegativo() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new EstadisticasCentro(0.0d, -1.0d),
                """
                Se esperaba excepción por porcentaje inválido.
                """
        );
    }

    @Test
    void porcentajeMayorCien() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new EstadisticasCentro(0.0d, 101.0d),
                """
                Se esperaba excepción por porcentaje fuera de rango.
                """
        );
    }

    @Test
    void pesoTotalValor() {
        EstadisticasCentro est = new EstadisticasCentro(3.0d, 50.0d);

        Assertions.assertEquals(
                3.0d,
                est.pesoTotalTransportado(),
                """
                Se esperaba el peso del constructor.
                """
        );
    }

    @Test
    void porcentajeValor() {
        EstadisticasCentro est = new EstadisticasCentro(3.0d, 50.0d);

        Assertions.assertEquals(
                50.0d,
                est.porcentajeEntregado(),
                """
                Se esperaba el porcentaje del constructor.
                """
        );
    }

    @Test
    void equalsMismaInstancia() {
        EstadisticasCentro est = new EstadisticasCentro(3.0d, 50.0d);

        Assertions.assertEquals(
                est,
                est,
                """
                Se esperaba equals true para la misma instancia.
                """
        );
    }

    @Test
    void equalsFalseContraNull() {
        EstadisticasCentro est = new EstadisticasCentro(3.0d, 50.0d);

        Assertions.assertNotEquals(
                null,
                est,
                """
                Se esperaba equals false contra null.
                """
        );
    }

    @Test
    void hashCodeValoresIguales() {
        EstadisticasCentro uno = new EstadisticasCentro(3.0d, 50.0d);
        EstadisticasCentro dos = new EstadisticasCentro(3.0d, 50.0d);

        Assertions.assertEquals(
                uno.hashCode(),
                dos.hashCode(),
                """
                Se esperaba mismo hashCode para valores iguales.
                """
        );
    }
}
