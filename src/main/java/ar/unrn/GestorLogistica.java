package ar.unrn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestiona centros de distribución, envíos y persistencia del sistema.
 * Implementa composición para separar operaciones, consultas y persistencia.
 */
public final class GestorLogistica implements Guardable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Centros de distribución indexados por nombre.
     */
    private final Map<String, CentroDistribucion> centrosPorNombre;

    /**
     * Componente de operaciones del gestor.
     */
    private final GestorLogisticaOperable operable;

    /**
     * Componente de consultas del gestor.
     */
    private final GestorLogisticaConsultable consultable;

    /**
     * Crea un gestor vacío.
     */
    public GestorLogistica() {
        super();
        this.centrosPorNombre = new ConcurrentHashMap<>();
        this.operable = new OperacionesGestor(this.centrosPorNombre);
        this.consultable = new ConsultasGestor(this.centrosPorNombre);
    }

    /**
     * Devuelve el componente operable del gestor.
     *
     * @return operaciones del gestor
     */
    public GestorLogisticaOperable operaciones() {
        return this.operable;
    }

    /**
     * Devuelve el componente consultable del gestor.
     *
     * @return consultas del gestor
     */
    public GestorLogisticaConsultable consultas() {
        return this.consultable;
    }

    /**
     * Devuelve una vista inmodificable de los centros del gestor.
     *
     * @return mapa inmodificable de centros
     */
    public Map<String, CentroDistribucion> centros() {
        return Collections.unmodifiableMap(this.centrosPorNombre);
    }

    /**
     * Guarda el gestor a disco mediante serialización.
     *
     * @param ruta ruta del archivo destino
     * @throws IOException              si ocurre un error de entrada/salida
     * @throws IllegalArgumentException si ruta es nulo o está en blanco
     */
    @Override
    public void guardar(String ruta) throws IOException {
        GestorLogisticaBase.exigirTexto(ruta, "La ruta");

        Path rutaArchivo = Path.of(ruta);
        try (ObjectOutputStream salida = new ObjectOutputStream(
                Files.newOutputStream(rutaArchivo))) {
            salida.writeObject(this);
        }
    }

    /**
     * Carga un gestor desde disco mediante serialización.
     *
     * @param ruta ruta del archivo origen
     * @return gestor cargado
     * @throws IOException              si ocurre un error de entrada/salida o formato
     * @throws IllegalArgumentException si ruta es nulo o está en blanco
     */
    public static GestorLogistica cargar(String ruta) throws IOException {
        GestorLogisticaBase.exigirTexto(ruta, "La ruta");

        Path rutaArchivo = Path.of(ruta);
        try (ObjectInputStream entrada = new ObjectInputStream(
                Files.newInputStream(rutaArchivo))) {

            Object leido = entrada.readObject();
            if (leido instanceof GestorLogistica gestor) {
                return gestor;
            }
        } catch (ClassNotFoundException excepcion) {
            throw new IOException(
                    """
                    No se pudo leer el objeto.
                    """,
                    excepcion
            );
        }

        throw new IOException(
                """
                El archivo no contiene un "GestorLogística".
                """
        );
    }

    /**
     * Compara este gestor con otro objeto.
     *
     * @param objeto objeto a comparar
     * @return true si representan el mismo gestor, false en caso contrario
     */
    @Override
    public boolean equals(Object objeto) {
        return this == objeto;
    }

    /**
     * Calcula el código hash consistente con {@link #equals(Object)}.
     *
     * @return código hash del gestor
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Implementa las operaciones del gestor.
     */
    private static final class OperacionesGestor
            implements GestorLogisticaOperable, Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Centros de distribución indexados por nombre.
         */
        private final Map<String, CentroDistribucion> centrosPorNombre;

        private OperacionesGestor(Map<String, CentroDistribucion> centrosParam) {
            super();
            GestorLogisticaBase.exigirNoNulo(centrosParam, "Los centros");
            this.centrosPorNombre = centrosParam;
        }

        @Override
        public void crearCentroDistribucion(String nombre, String ubicacion) {
            GestorLogisticaBase.exigirTexto(nombre, "El nombre");
            GestorLogisticaBase.exigirTexto(ubicacion, "La ubicación");

            if (this.centrosPorNombre.containsKey(nombre)) {
                throw new IllegalArgumentException(
                        """
                        El centro ya existe.
                        """
                );
            }

            CentroDistribucion centro = new CentroDistribucion(nombre, ubicacion);
            this.centrosPorNombre.put(centro.nombre(), centro);
        }

        @Override
        public void registrarEnvio(String nombreCentro, Envio envio) {
            GestorLogisticaBase.exigirNoNulo(envio, "El envío");
            GestorLogisticaBase.exigirTrackingId(envio.trackingId());

            if (existeTrackingIdGlobal(envio.trackingId())) {
                throw new IllegalArgumentException(
                        """
                        El trackingId ya existe en el sistema.
                        """
                );
            }

            CentroDistribucion centro = GestorLogisticaBase.obtenerCentro(
                    this.centrosPorNombre,
                    nombreCentro
            );
            centro.operaciones().registrarEnvio(envio);
        }

        @Override
        public void actualizarEstado(
                String nombreCentro,
                String trackingId,
                String descripcion,
                LocalDateTime fechaHora) {

            CentroDistribucion centro = GestorLogisticaBase.obtenerCentro(
                    this.centrosPorNombre,
                    nombreCentro
            );
            centro.operaciones().actualizarEstado(trackingId, descripcion, fechaHora);
        }

        @Override
        public void eliminarEnvio(String trackingId) {
            CentroDistribucion centro = GestorLogisticaBase.encontrarCentro(
                    this.centrosPorNombre,
                    trackingId
            );
            centro.operaciones().eliminarEnvio(trackingId);
        }

        @Override
        public void vaciarCentro(String nombreCentro) {
            CentroDistribucion centro = GestorLogisticaBase.obtenerCentro(
                    this.centrosPorNombre,
                    nombreCentro
            );
            centro.operaciones().vaciar();
        }

        /**
         * Indica si el trackingId ya existe en algún centro.
         *
         * @param trackingId trackingId a verificar
         * @return true si ya existe, false en caso contrario
         */
        private boolean existeTrackingIdGlobal(String trackingId) {
            return this.centrosPorNombre.values()
                    .stream()
                    .anyMatch(centro -> centro.envios().containsKey(trackingId));
        }
    }

    /**
     * Implementa las consultas del gestor.
     */
    private static final class ConsultasGestor
            implements GestorLogisticaConsultable, Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Centros de distribución indexados por nombre.
         */
        private final Map<String, CentroDistribucion> centrosPorNombre;

        private ConsultasGestor(Map<String, CentroDistribucion> centrosParam) {
            super();
            GestorLogisticaBase.exigirNoNulo(centrosParam, "Los centros");
            this.centrosPorNombre = centrosParam;
        }

        @Override
        public Envio buscarEnvioPorTrackingId(String trackingId) {
            CentroDistribucion centro = GestorLogisticaBase.encontrarCentro(
                    this.centrosPorNombre,
                    trackingId
            );
            return centro.consultas().buscarEnvio(trackingId);
        }

        @Override
        public List<Envio> buscarEnviosPorDestinatario(String destinatario) {
            GestorLogisticaBase.exigirTexto(destinatario, "El destinatario");

            List<Envio> encontrados = new LinkedList<>();
            for (CentroDistribucion centro : this.centrosPorNombre.values()) {
                encontrados.addAll(
                        centro.consultas().buscarEnviosPorDestinatario(destinatario)
                );
            }
            return Collections.unmodifiableList(encontrados);
        }

        @Override
        public List<Envio> listarEnviosPorPeso(String nombreCentro) {
            CentroDistribucion centro = GestorLogisticaBase.obtenerCentro(
                    this.centrosPorNombre,
                    nombreCentro
            );
            return centro.consultas().listarEnviosPorPeso();
        }

        @Override
        public List<Envio> listarEnviosPorPrioridad(String nombreCentro) {
            CentroDistribucion centro = GestorLogisticaBase.obtenerCentro(
                    this.centrosPorNombre,
                    nombreCentro
            );
            return centro.consultas().listarEnviosPorPrioridad();
        }

        @Override
        public EstadisticasCentro estadisticasCentro(String nombreCentro) {
            CentroDistribucion centro = GestorLogisticaBase.obtenerCentro(
                    this.centrosPorNombre,
                    nombreCentro
            );
            return centro.consultas().estadisticas();
        }

    }
}
