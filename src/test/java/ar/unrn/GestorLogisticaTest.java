package ar.unrn;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de operaciones de {@link GestorLogistica}.
 */
final class GestorOperacionesTest {

    private static final String CENTRO_A =
            """
            CentroA
            """;

    private static final String CENTRO_B =
            """
            CentroB
            """;

    private static final String CIUDAD =
            """
            Bariloche
            """;

    private static final String TRACKER_UNO =
            """
            TRACKER-001
            """;

    private static final String DESTINATARIO =
            """
            Ana
            """;

    @Test
    void crearCentro() {
        GestorLogistica gestor = new GestorLogistica();

        gestor.operaciones().crearCentroDistribucion(CENTRO_A, CIUDAD);

        assertEquals(
                1,
                gestor.centros().size(),
                """
                Se esperaba un centro creado.
                """
        );
    }

    @Test
    void centroDuplicado() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(CENTRO_A, CIUDAD);

        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.operaciones().crearCentroDistribucion(CENTRO_A, CIUDAD),
                """
                Se esperaba excepción por centro duplicado.
                """
        );
    }

    @Test
    void trackingDuplicado() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(CENTRO_A, CIUDAD);
        gestor.operaciones().crearCentroDistribucion(CENTRO_B, CIUDAD);

        gestor.operaciones().registrarEnvio(
                CENTRO_A,
                new Envio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.operaciones().registrarEnvio(
                        CENTRO_B,
                        new Envio(TRACKER_UNO, 2.0d, DESTINATARIO, Prioridad.URGENTE)
                ),
                """
                Se esperaba excepción por trackingId duplicado.
                """
        );
    }

    @Test
    void eliminarEnvio() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(CENTRO_A, CIUDAD);

        gestor.operaciones().registrarEnvio(
                CENTRO_A,
                new Envio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );

        gestor.operaciones().eliminarEnvio(TRACKER_UNO);

        CentroDistribucion centro = gestor.centros().get(CENTRO_A);

        assertEquals(
                0,
                centro.envios().size(),
                """
                Se esperaba que el envío se elimine del centro.
                """
        );
    }

    @Test
    void vaciarCentro() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(CENTRO_A, CIUDAD);

        gestor.operaciones().registrarEnvio(
                CENTRO_A,
                new Envio(TRACKER_UNO, 1.0d, DESTINATARIO, Prioridad.ESTANDAR)
        );

        gestor.operaciones().vaciarCentro(CENTRO_A);

        CentroDistribucion centro = gestor.centros().get(CENTRO_A);

        assertEquals(
                0,
                centro.envios().size(),
                """
                Se esperaba que el centro quede vacío.
                """
        );
    }
}

/**
 * Pruebas de persistencia del {@link GestorLogistica}.
 */
final class GestorPersistenciaTest {

    private static final String VACIO =
            """
            """;

    private static final String CENTRO =
            """
            CentroUno
            """;

    private static final String CIUDAD =
            """
            Bariloche
            """;

    @TempDir
    private Path dirTemp;

    @Test
    void recuperarEnvio() throws IOException {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(CENTRO, CIUDAD);

        gestor.operaciones().registrarEnvio(
                CENTRO,
                new Envio("TRACKER-001", 1.0d, "Ana", Prioridad.ESTANDAR)
        );

        Path archivo = this.dirTemp.resolve("gestor.bin");
        gestor.guardar(archivo.toString());

        GestorLogistica cargado = GestorLogistica.cargar(archivo.toString());
        Envio rec = cargado.consultas().buscarEnvioPorTrackingId("TRACKER-001");

        assertEquals(
                "TRACKER-001",
                rec.trackingId(),
                """
                Se esperaba recuperar el envío luego de cargar.
                """
        );
    }

    @Test
    void guardarRutaVacio() {
        GestorLogistica gestor = new GestorLogistica();

        assertThrows(
                IllegalArgumentException.class,
                () -> gestor.guardar(VACIO),
                """
                Se esperaba excepción por ruta inválida.
                """
        );
    }

    @Test
    void cargarRutaVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> GestorLogistica.cargar(VACIO),
                """
                Se esperaba excepción por ruta inválida.
                """
        );
    }

    @Test
    void cargarNoGestor() throws IOException {
        Path archivo = this.dirTemp.resolve("invalido.bin");

        try (ObjectOutputStream salida = new ObjectOutputStream(
                Files.newOutputStream(archivo))) {
            salida.writeObject("NO_ES_GESTOR");
        }

        assertThrows(
                IOException.class,
                () -> GestorLogistica.cargar(archivo.toString()),
                """
                Se esperaba IOException si el contenido no es un gestor.
                """
        );
    }
}

/**
 * Pruebas unitarias de utilidades en {@link GestorLogisticaBase}.
 */
final class GestorLogisticaBaseTest {

    private static final String VACIO =
            """
            """;

    private static final String CENTRO =
            """
            CentroUno
            """;

    private static final String CIUDAD =
            """
            Bariloche
            """;

    private static final String TRACKER =
            """
            TRACKER-001
            """;

    private static final String DESTINATARIO =
            """
            Juan Perez
            """;

    @Test
    void exigirNoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> GestorLogisticaBase.exigirNoNulo(null, "El valor"),
                """
                Se esperaba excepción si el valor es null.
                """
        );
    }

    @Test
    void exigirTexto() {
        assertThrows(
                IllegalArgumentException.class,
                () -> GestorLogisticaBase.exigirTexto(
                        VACIO,
                        "El texto"
                ),
                """
                Se esperaba excepción si el texto está en blanco.
                """
        );
    }

    @Test
    void exigirTrackingId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> GestorLogisticaBase.exigirTrackingId(null),
                """
                Se esperaba excepción si el trackingId es null.
                """
        );
    }

    @Test
    void envioMensajeEstandar() {
        IllegalArgumentException excepcion =
                GestorLogisticaBase.errorEnvioInexistente();

        assertTrue(
                excepcion.getMessage().contains("No existe envío"),
                """
                Se esperaba el mensaje estándar del sistema.
                """
        );
    }

    @Test
    void centroNoExistente() {
        Map<String, CentroDistribucion> centros = new ConcurrentHashMap<>();

        assertThrows(
                IllegalArgumentException.class,
                () -> GestorLogisticaBase.obtenerCentro(centros, CENTRO),
                """
                Se esperaba excepción si el centro no existe.
                """
        );
    }

    @Test
    void encontrarCentro() {
        Map<String, CentroDistribucion> centros = new ConcurrentHashMap<>();
        CentroDistribucion centro = new CentroDistribucion(CENTRO, CIUDAD);
        centros.put(centro.nombre(), centro);

        Envio envio = new Envio(TRACKER, 1.0d, DESTINATARIO, Prioridad.ESTANDAR);
        centro.operaciones().registrarEnvio(envio);

        CentroDistribucion encontrado =
                GestorLogisticaBase.encontrarCentro(centros, TRACKER);

        assertEquals(
                CENTRO,
                encontrado.nombre(),
                """
                Se esperaba el centro que contiene el envío.
                """
        );
    }
}

/**
 * Pruebas de consultas del {@link GestorLogistica}.
 */
final class GestorConsultasTest {

    @Test
    void buscarTracking() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        Envio envio = new Envio(
                DatosConsultasTexto.trackingIdUno(),
                DatosConsultasNumeros.pesoUno(),
                DatosConsultasTexto.destinatarioUno(),
                Prioridad.ESTANDAR
        );
        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                envio
        );

        Envio recuperado = gestor.consultas().buscarEnvioPorTrackingId(
                DatosConsultasTexto.trackingIdUno()
        );

        assertEquals(
                DatosConsultasTexto.trackingIdUno(),
                recuperado.trackingId(),
                """
                El trackingId recuperado debería coincidir con el esperado.
                """
        );
    }

    @Test
    void buscarDestinatario() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        Envio envio = new Envio(
                DatosConsultasTexto.trackingIdUno(),
                DatosConsultasNumeros.pesoUno(),
                DatosConsultasTexto.destinatarioUno(),
                Prioridad.ESTANDAR
        );
        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                envio
        );

        List<Envio> envios = gestor.consultas().buscarEnviosPorDestinatario(
                DatosConsultasTexto.destinatarioUno()
        );

        assertEquals(
                DatosConsultasTexto.trackingIdUno(),
                envios.get(0).trackingId(),
                """
                Se esperaba encontrar el envío del destinatario.
                """
        );
    }

    @Test
    void estadoEntregado() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        Envio envio = new Envio(
                DatosConsultasTexto.trackingIdUno(),
                DatosConsultasNumeros.pesoUno(),
                DatosConsultasTexto.destinatarioUno(),
                Prioridad.ESTANDAR
        );
        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                envio
        );

        gestor.operaciones().actualizarEstado(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.trackingIdUno(),
                DatosConsultasEventos.eventoRecibido(),
                DatosConsultasFechas.fechaRecibido()
        );

        gestor.operaciones().actualizarEstado(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.trackingIdUno(),
                DatosConsultasEventos.eventoEntregado(),
                DatosConsultasFechas.fechaEntregado()
        );

        Envio recuperado = gestor.consultas().buscarEnvioPorTrackingId(
                DatosConsultasTexto.trackingIdUno()
        );

        assertTrue(
                recuperado.entregado(),
                """
                El envío debería quedar marcado como entregado.
                """
        );
    }
}

/**
 * Pruebas de ordenamiento de envíos en él {@link GestorLogistica}.
 */
final class GestorConsultasOrdenTest {

    @Test
    void listarPesoDescendiente() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdUno(),
                        DatosConsultasNumeros.pesoUno(),
                        DatosConsultasTexto.destinatarioUno(),
                        Prioridad.ESTANDAR
                )
        );
        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdDos(),
                        DatosConsultasNumeros.pesoDos(),
                        DatosConsultasTexto.destinatarioDos(),
                        Prioridad.URGENTE
                )
        );

        List<Envio> envios = gestor.consultas().listarEnviosPorPeso(
                DatosConsultasTexto.centroUno()
        );

        assertEquals(
                DatosConsultasTexto.trackingIdUno(),
                envios.get(0).trackingId(),
                """
                Se esperaba que el primer envío sea el de mayor peso.
                """
        );
    }

    @Test
    void listarPrioridadDescendiente() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdUno(),
                        DatosConsultasNumeros.pesoUno(),
                        DatosConsultasTexto.destinatarioUno(),
                        Prioridad.ESTANDAR
                )
        );
        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdDos(),
                        DatosConsultasNumeros.pesoDos(),
                        DatosConsultasTexto.destinatarioDos(),
                        Prioridad.URGENTE
                )
        );

        List<Envio> envios = gestor.consultas().listarEnviosPorPrioridad(
                DatosConsultasTexto.centroUno()
        );

        assertEquals(
                DatosConsultasTexto.trackingIdDos(),
                envios.get(0).trackingId(),
                """
                Se esperaba que el primer envío sea el de prioridad más alta.
                """
        );
    }
}

/**
 * Pruebas de cálculo de estadísticas en él {@link GestorLogistica}.
 */
final class GestorConsultasEstadisticasTest {

    @Test
    void pesoTotal() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdUno(),
                        DatosConsultasNumeros.pesoUno(),
                        DatosConsultasTexto.destinatarioUno(),
                        Prioridad.ESTANDAR
                )
        );
        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdDos(),
                        DatosConsultasNumeros.pesoDos(),
                        DatosConsultasTexto.destinatarioDos(),
                        Prioridad.URGENTE
                )
        );

        EstadisticasCentro estadisticas = gestor.consultas().estadisticasCentro(
                DatosConsultasTexto.centroUno()
        );

        assertEquals(
                12.5d,
                estadisticas.pesoTotalTransportado(),
                DatosConsultasNumeros.deltaDoble(),
                """
                El peso total debería ser la suma de los pesos registrados.
                """
        );
    }

    @Test
    void porcentajeEntregado() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdUno(),
                        DatosConsultasNumeros.pesoUno(),
                        DatosConsultasTexto.destinatarioUno(),
                        Prioridad.ESTANDAR
                )
        );
        gestor.operaciones().registrarEnvio(
                DatosConsultasTexto.centroUno(),
                new Envio(
                        DatosConsultasTexto.trackingIdDos(),
                        DatosConsultasNumeros.pesoDos(),
                        DatosConsultasTexto.destinatarioDos(),
                        Prioridad.URGENTE
                )
        );

        gestor.operaciones().actualizarEstado(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.trackingIdDos(),
                DatosConsultasEventos.eventoEntregado(),
                DatosConsultasFechas.fechaEntregado()
        );

        EstadisticasCentro estadisticas = gestor.consultas().estadisticasCentro(
                DatosConsultasTexto.centroUno()
        );

        assertEquals(
                50.0d,
                estadisticas.porcentajeEntregado(),
                DatosConsultasNumeros.deltaDoble(),
                """
                El porcentaje debería reflejar la proporción entregada.
                """
        );
    }
}

/**
 * Pruebas de estadísticas cuando no existen envíos en él {@link GestorLogistica}.
 */
final class GestorConsultasEstadisticasSinEnviosTest {

    @Test
    void pesoTotalCero() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        EstadisticasCentro estadisticas = gestor.consultas().estadisticasCentro(
                DatosConsultasTexto.centroUno()
        );

        assertEquals(
                0.0d,
                estadisticas.pesoTotalTransportado(),
                DatosConsultasNumeros.deltaDoble(),
                """
                Se esperaba peso total 0 cuando el centro no tiene envíos.
                """
        );
    }

    @Test
    void porcentajeCero() {
        GestorLogistica gestor = new GestorLogistica();
        gestor.operaciones().crearCentroDistribucion(
                DatosConsultasTexto.centroUno(),
                DatosConsultasTexto.ciudadBariloche()
        );

        EstadisticasCentro estadisticas = gestor.consultas().estadisticasCentro(
                DatosConsultasTexto.centroUno()
        );

        assertEquals(
                0.0d,
                estadisticas.porcentajeEntregado(),
                DatosConsultasNumeros.deltaDoble(),
                """
                Se esperaba porcentaje 0 cuando el centro no tiene envíos.
                """
        );
    }
}

/**
 * Utilidad de datos de texto reutilizables para pruebas de consultas.
 *
 * <p>
 * Se exponen como métodos {@code public static} para evitar "default access"
 * y cumplir reglas de documentación en métodos públicos.
 * </p>
 */
final class DatosConsultasTexto {

    /**
     * Centro utilizado en las pruebas.
     *
     * @return nombre del centro
     */
    public static String centroUno() {
        return """
               CentroUno
               """;
    }

    /**
     * Ciudad utilizada en las pruebas.
     *
     * @return nombre de la ciudad
     */
    public static String ciudadBariloche() {
        return """
               Bariloche
               """;
    }

    /**
     * TrackingId 1 utilizado en las pruebas.
     *
     * @return trackingId
     */
    public static String trackingIdUno() {
        return """
               TRACKER-001
               """;
    }

    /**
     * TrackingId 2 utilizado en las pruebas.
     *
     * @return trackingId
     */
    public static String trackingIdDos() {
        return """
               TRACKER-002
               """;
    }

    /**
     * Destinatario 1 utilizado en las pruebas.
     *
     * @return destinatario
     */
    public static String destinatarioUno() {
        return """
               Juan Perez
               """;
    }

    /**
     * Destinatario 2 utilizado en las pruebas.
     *
     * @return destinatario
     */
    public static String destinatarioDos() {
        return """
               Ana Gomez
               """;
    }

    private DatosConsultasTexto() {
        super();
    }
}

/**
 * Utilidad de datos numéricos reutilizables para pruebas de consultas.
 */
final class DatosConsultasNumeros {

    /**
     * Peso 1 utilizado en las pruebas.
     *
     * @return peso
     */
    public static double pesoUno() {
        return 10.0d;
    }

    /**
     * Peso 2 utilizado en las pruebas.
     *
     * @return peso
     */
    public static double pesoDos() {
        return 2.5d;
    }

    /**
     * Delta de comparación para aserciones de doubles.
     *
     * @return delta
     */
    public static double deltaDoble() {
        return 0.0001d;
    }

    private DatosConsultasNumeros() {
        super();
    }
}

/**
 * Utilidad de textos de eventos reutilizables para pruebas de consultas.
 */
final class DatosConsultasEventos {

    /**
     * Evento de recibido utilizado en las pruebas.
     *
     * @return descripción del evento
     */
    public static String eventoRecibido() {
        return """
               Recibido en centro
               """;
    }

    /**
     * Evento de entregado utilizado en las pruebas.
     *
     * @return descripción del evento
     */
    public static String eventoEntregado() {
        return """
               Entregado
               """;
    }

    private DatosConsultasEventos() {
        super();
    }
}

/**
 * Utilidad de fechas reutilizables para pruebas de consultas.
 */
final class DatosConsultasFechas {

    /**
     * Fecha de recibido utilizada en las pruebas.
     *
     * @return fecha/hora
     */
    public static LocalDateTime fechaRecibido() {
        return LocalDateTime.of(2026, 2, 1, 10, 0);
    }

    /**
     * Fecha de entregado utilizada en las pruebas.
     *
     * @return fecha/hora
     */
    public static LocalDateTime fechaEntregado() {
        return LocalDateTime.of(2026, 2, 2, 11, 30);
    }

    private DatosConsultasFechas() {
        super();
    }
}
