package ar.unrn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de búsquedas y listados de {@link CentroDistribucion}.
 */
final class CentroListadosTest {

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

    @Test
    void envioRegistrado() {
        CentroDistribucion centro = crearCentro();
        Envio envio = crearEnvio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR);
        centro.operaciones().registrarEnvio(envio);

        Envio encontrado = centro.consultas().buscarEnvio(TRACKER_UNO);

        Assertions.assertEquals(
                TRACKER_UNO,
                encontrado.trackingId(),
                """
                Se esperaba encontrar el envío por trackingId.
                """
        );
    }

    @Test
    void envioNoExiste() {
        CentroDistribucion centro = crearCentro();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> centro.consultas().buscarEnvio(TRACKER_UNO),
                """
                Se esperaba excepción si el envío no existe.
                """
        );
    }

    @Test
    void buscarEnvios() {
        CentroDistribucion centro = crearCentro();

        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );
        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_DOS, 2.0d, "Bob", Prioridad.ESTANDAR)
        );

        int cantidad = centro.consultas()
                .buscarEnviosPorDestinatario(DESTINATARIO)
                .size();

        Assertions.assertEquals(
                1,
                cantidad,
                """
                Se esperaba un único envío para el destinatario.
                """
        );
    }

    @Test
    void listarEnviosPorPeso() {
        CentroDistribucion centro = crearCentro();

        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );
        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_DOS, 5.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );

        String primero = centro.consultas()
                .listarEnviosPorPeso()
                .get(0)
                .trackingId();

        Assertions.assertEquals(
                TRACKER_DOS,
                primero,
                """
                Se esperaba el envío más pesado primero.
                """
        );
    }

    @Test
    void listarEnviosPrioridad() {
        CentroDistribucion centro = crearCentro();

        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ECONOMICO)
        );
        centro.operaciones().registrarEnvio(
                crearEnvio(TRACKER_DOS, 1.0d, DESTINATARIO, Prioridad.URGENTE)
        );

        String primero = centro.consultas()
                .listarEnviosPorPrioridad()
                .get(0)
                .trackingId();

        Assertions.assertEquals(
                TRACKER_DOS,
                primero,
                """
                Se esperaba el envío urgente primero.
                """
        );
    }

    private static CentroDistribucion crearCentro() {
        return new CentroDistribucion(CENTRO_UNO, CIUDAD);
    }

    private static Envio crearEnvio(
            String tracker,
            double peso,
            String destinatario,
            Prioridad prioridad
    ) {
        return new Envio(tracker, peso, destinatario, prioridad);
    }
}
