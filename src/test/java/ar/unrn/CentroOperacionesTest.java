package ar.unrn;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de operaciones de {@link CentroDistribucion}.
 */
final class CentroOperacionesTest {

    private static final String CENTRO =
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
    void registrarEnvio() {
        CentroDistribucion centro = crearCentro();
        Envio envio = crearEnvio(TRACKER_UNO, 1.0d, Prioridad.ESTANDAR);

        centro.operaciones().registrarEnvio(envio);

        Assertions.assertEquals(
                1,
                centro.envios().size(),
                """
                Se esperaba que el envío quede registrado.
                """
        );
    }

    @Test
    void registrarEnvioDuplicado() {
        CentroDistribucion centro = crearCentro();
        Envio uno = crearEnvio(TRACKER_UNO, 1.0d, Prioridad.ESTANDAR);
        Envio dos = crearEnvio(TRACKER_UNO, 2.0d, Prioridad.URGENTE);

        centro.operaciones().registrarEnvio(uno);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> centro.operaciones().registrarEnvio(dos),
                """
                Se esperaba excepción por trackingId duplicado.
                """
        );
    }

    @Test
    void agregarEventoAlHistorial() {
        CentroDistribucion centro = crearCentro();
        Envio envio = crearEnvio(TRACKER_UNO, 1.0d, Prioridad.ESTANDAR);
        centro.operaciones().registrarEnvio(envio);

        centro.operaciones().actualizarEstado(TRACKER_UNO, "Recibido", FECHA);

        Assertions.assertEquals(
                1,
                envio.historial().size(),
                """
                Se esperaba un evento en el historial.
                """
        );
    }

    @Test
    void eliminarEnvio() {
        CentroDistribucion centro = crearCentro();
        Envio envio = crearEnvio(TRACKER_UNO, 1.0d, Prioridad.ESTANDAR);
        centro.operaciones().registrarEnvio(envio);

        centro.operaciones().eliminarEnvio(TRACKER_UNO);

        Assertions.assertEquals(
                0,
                centro.envios().size(),
                """
                Se esperaba que el envío se elimine del centro.
                """
        );
    }

    @Test
    void ubicacionCiudad() {
        CentroDistribucion centro = crearCentro();

        Assertions.assertEquals(
                CIUDAD,
                centro.ubicacion(),
                """
                Se esperaba que la ubicación del centro sea la ciudad por defecto.
                """
        );
    }

    @Test
    void vaciarEnvios() {
        CentroDistribucion centro = crearCentro();

        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_UNO, 1.0d, Prioridad.ESTANDAR)
        );
        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_DOS, 2.0d, Prioridad.URGENTE)
        );

        centro.operaciones().vaciar();

        Assertions.assertEquals(
                0,
                centro.envios().size(),
                """
                Se esperaba que el centro quede vacío.
                """
        );
    }

    private static CentroDistribucion crearCentro() {
        return new CentroDistribucion(CENTRO, CIUDAD);
    }

    private static Envio crearEnvio(
            String tracker,
            double peso,
            Prioridad prioridad
    ) {
        return new Envio(tracker, peso, DESTINATARIO, prioridad);
    }
}
