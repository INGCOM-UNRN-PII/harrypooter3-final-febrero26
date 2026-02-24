package ar.unrn;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Representa un centro de distribución que administra envíos.
 * Implementa composición mediante componentes operables y consultables.
 */
public final class CentroDistribucion implements Serializable {

    /**
     * Identificador de serialización.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Nombre único del centro.
     */
    private final String nombreCentro;

    /**
     * Ubicación geográfica del centro.
     */
    private final String ubicacionCentro;

    /**
     * Componente de operaciones del centro.
     */
    private final CentroDistribucionOperable operable;

    /**
     * Componente de consultas del centro.
     */
    private final CentroDistribucionConsultable consultable;

    /**
     * Operaciones de modificación sobre un {@link CentroDistribucion}.
     */
    public interface CentroDistribucionOperable {

        /**
         * Registra un envío en el centro.
         *
         * @param envio envío a registrar
         * @throws IllegalArgumentException si el envío es nulo o si el trackingId
         *                                  ya existe
         */
        void registrarEnvio(Envio envio);

        /**
         * Actualiza el estado de un envío.
         *
         * @param trackingId  código de seguimiento
         * @param descripcion descripción del evento
         * @param fechaHora   fecha y hora del evento
         * @throws IllegalArgumentException si el envío no existe o si los argumentos
         *                                  son inválidos
         */
         void actualizarEstado(
                String trackingId,
                String descripcion,
                LocalDateTime fechaHora);

        /**
         * Elimina un envío por trackingId.
         *
         * @param trackingId código de seguimiento
         * @throws IllegalArgumentException si el envío no existe
         */
        void eliminarEnvio(String trackingId);

        /**
         * Elimina todos los envíos del centro.
         */
        void vaciar();
    }

    /**
     * Operaciones de consulta sobre un {@link CentroDistribucion}.
     */
    public interface CentroDistribucionConsultable {

        /**
         * Busca un envío por trackingId.
         *
         * @param trackingId código de seguimiento
         * @return envío encontrado
         * @throws IllegalArgumentException si no existe el envío
         */
        Envio buscarEnvio(String trackingId);

        /**
         * Lista de envíos por destinatario.
         *
         * @param destinatario nombre del destinatario
         * @return lista de envíos asociados
         * @throws IllegalArgumentException si destinatario es nulo o está en blanco
         */
        List<Envio> buscarEnviosPorDestinatario(String destinatario);

        /**
         * Lista de envíos ordenados por peso de mayor a menor.
         *
         * @return lista ordenada por peso descendente
         */
        List<Envio> listarEnviosPorPeso();

        /**
         * Lista de envíos ordenados por jerarquía de prioridad.
         *
         * @return lista ordenada por prioridad
         */
        List<Envio> listarEnviosPorPrioridad();

        /**
         * Calcula estadísticas del centro.
         *
         * @return estadísticas del centro
         */
        EstadisticasCentro estadisticas();

        /**
         * Devuelve una vista inmodificable de los envíos del centro.
         *
         * @return mapa inmodificable de envíos
         */
        Map<String, Envio> envios();
    }

    /**
     * Válida los argumentos usados para construir un {@link CentroDistribucion}.
     */
    private static final class ValidadorCentro {

        private static String validarNombre(String nombreCentroParam) {
            GestorLogisticaBase.exigirTexto(
                    nombreCentroParam,
                    "El nombre"
            );
            return nombreCentroParam;
        }

        private static String validarUbicacion(String ubiCentroParam) {
            GestorLogisticaBase.exigirTexto(
                    ubiCentroParam,
                    "La ubicación"
            );
            return ubiCentroParam;
        }
    }

    /**
     * Crea un centro de distribución.
     *
     * @param nombreCentroParam nombre único del centro
     * @param ubiCentroParam    ubicación geográfica
     * @throws IllegalArgumentException si los argumentos son inválidos
     */
    public CentroDistribucion(String nombreCentroParam, String ubiCentroParam) {
        super();
        this.nombreCentro = ValidadorCentro.validarNombre(nombreCentroParam);
        this.ubicacionCentro = ValidadorCentro.validarUbicacion(ubiCentroParam);

        Map<String, Envio> envios = new ConcurrentHashMap<>();
        this.operable = new OperacionesCentro(envios);
        this.consultable = new ConsultasCentro(envios);
    }

    /**
     * Devuelve el nombre del centro.
     *
     * @return nombre del centro
     */
    public String nombre() {
        return this.nombreCentro;
    }

    /**
     * Devuelve la ubicación del centro.
     *
     * @return ubicación del centro
     */
    public String ubicacion() {
        return this.ubicacionCentro;
    }

    /**
     * Devuelve el componente operable del centro.
     *
     * @return operaciones del centro
     */
    public CentroDistribucionOperable operaciones() {
        return this.operable;
    }

    /**
     * Devuelve el componente consultable del centro.
     *
     * @return consultas del centro
     */
    public CentroDistribucionConsultable consultas() {
        return this.consultable;
    }

    /**
     * Devuelve una vista inmodificable de los envíos del centro.
     * Se mantiene como método de conveniencia para no romper usos existentes.
     *
     * @return mapa inmodificable de envíos
     */
    public Map<String, Envio> envios() {
        return this.consultable.envios();
    }

    /**
     * Implementa las operaciones del centro.
     */
    private static final class OperacionesCentro
            implements CentroDistribucionOperable, Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Envíos indexados por trackingId.
         */
        private final Map<String, Envio> enviosPorId;

        private OperacionesCentro(Map<String, Envio> enviosParam) {
            super();
            GestorLogisticaBase.exigirNoNulo(enviosParam, "Los envíos");
            this.enviosPorId = enviosParam;
        }

        @Override
        public void registrarEnvio(Envio envio) {
            GestorLogisticaBase.exigirNoNulo(envio, "El envío");
            GestorLogisticaBase.exigirTrackingId(envio.trackingId());

            if (this.enviosPorId.containsKey(envio.trackingId())) {
                throw new IllegalArgumentException(
                        """
                        El trackingId ya existe en el centro.
                        """
                );
            }
            this.enviosPorId.put(envio.trackingId(), envio);
        }

        @Override
        public void actualizarEstado(
                String trackingId,
                String descripcion,
                LocalDateTime fechaHora) {

            GestorLogisticaBase.exigirTrackingId(trackingId);
            GestorLogisticaBase.exigirTexto(descripcion, "La descripción");
            GestorLogisticaBase.exigirNoNulo(fechaHora, "La fecha y hora");

            Envio envio = this.enviosPorId.get(trackingId);
            GestorLogisticaBase.exigirEnvioExistente(envio);

            envio.registrarEstado(descripcion, fechaHora);
        }

        @Override
        public void eliminarEnvio(String trackingId) {
            GestorLogisticaBase.exigirTrackingId(trackingId);
            Envio eliminado = this.enviosPorId.remove(trackingId);
            GestorLogisticaBase.exigirEnvioExistente(eliminado);
        }

        @Override
        public void vaciar() {
            this.enviosPorId.clear();
        }
    }

    /**
     * Implementa las consultas del centro.
     */
    private static final class ConsultasCentro
            implements CentroDistribucionConsultable, Serializable {

        /**
         * Valor cero para cálculos.
         */
        private static final double PESO_CERO = 0.0d;

        /**
         * Constante cero para cálculos enteros.
         */
        private static final int ENTERO_CERO = 0;

        /**
         * Porcentaje completo.
         */
        private static final double PORCENTAJE_CIEN = 100.0d;

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Envíos indexados por trackingId.
         */
        private final Map<String, Envio> enviosPorId;

        private ConsultasCentro(Map<String, Envio> enviosParam) {
            super();
            GestorLogisticaBase.exigirNoNulo(enviosParam, "Los envíos");
            this.enviosPorId = enviosParam;
        }

        @Override
        public Envio buscarEnvio(String trackingId) {
            GestorLogisticaBase.exigirTrackingId(trackingId);
            Envio envio = this.enviosPorId.get(trackingId);
            GestorLogisticaBase.exigirEnvioExistente(envio);
            return envio;
        }

        @Override
        public List<Envio> buscarEnviosPorDestinatario(String destinatario) {
            GestorLogisticaBase.exigirTexto(destinatario, "El destinatario");

            List<Envio> encontrados = new LinkedList<>();
            for (Envio envio : this.enviosPorId.values()) {
                if (destinatario.equals(envio.destinatario())) {
                    encontrados.add(envio);
                }
            }
            return Collections.unmodifiableList(encontrados);
        }

        @Override
        public List<Envio> listarEnviosPorPeso() {
            List<Envio> lista = new LinkedList<>(this.enviosPorId.values());
            lista.sort(Comparator.comparingDouble(Envio::peso).reversed());
            return Collections.unmodifiableList(lista);
        }

        @Override
        public List<Envio> listarEnviosPorPrioridad() {
            List<Envio> lista = new LinkedList<>(this.enviosPorId.values());
            lista.sort(new ComparadorPrioridad());
            return Collections.unmodifiableList(lista);
        }

        @Override
        public EstadisticasCentro estadisticas() {
            final AcumuladorEstadisticas acumulador = new AcumuladorEstadisticas();

            for (Envio envio : this.enviosPorId.values()) {
                acumulador.agregar(envio);
            }

            return new EstadisticasCentro(
                    acumulador.obtenerPesoTotal(),
                    acumulador.porcentajeEntrega()
            );
        }

        @Override
        public Map<String, Envio> envios() {
            return Collections.unmodifiableMap(this.enviosPorId);
        }

        /**
         * Acumulador de estadísticas del centro.
         */
        private static final class AcumuladorEstadisticas {

            /**
             * Suma de los pesos de todos los envíos considerados.
             */
            private double pesoTotal;

            /**
             * Cantidad total de envíos considerados.
             */
            private int totalEnvios;

            /**
             * Cantidad de envíos marcados como entregados.
             */
            private int entregados;

            /**
             * Incorpora un envío al cálculo de estadísticas.
             *
             * @param envio envío a considerar
             * @throws IllegalArgumentException si {@code envio} es nulo
             */
            private void agregar(Envio envio) {
                GestorLogisticaBase.exigirNoNulo(envio, "El envío");
                this.pesoTotal = this.pesoTotal + envio.peso();
                this.totalEnvios = this.totalEnvios + 1;
                if (envio.entregado()) {
                    this.entregados = this.entregados + 1;
                }
            }

            /**
             * Calcula el porcentaje de entregas sobre el total de envíos.
             *
             * @return porcentaje de entregas (0 si no hay envíos)
             */
            private double porcentajeEntrega() {
                final double porcentaje;
                if (this.totalEnvios > ENTERO_CERO) {
                    porcentaje = this.entregados * PORCENTAJE_CIEN / this.totalEnvios;
                } else {
                    porcentaje = PESO_CERO;
                }
                return porcentaje;
            }

            /**
             * Devuelve el peso total acumulado.
             *
             * @return suma de los pesos de los envíos
             */
            private double obtenerPesoTotal() {
                return this.pesoTotal;
            }
        }

        /**
         * Ordena envíos por prioridad jerárquica.
         */
        private static final class ComparadorPrioridad
                implements Comparator<Envio>, Serializable {

            @Serial
            private static final long serialVersionUID = 1L;

            /**
             * Niveles de prioridad.
             * Convención: a menor valor numérico, mayor prioridad.
             */
            private static final int NIVEL_URGENTE = 0;

            /**
             * Nivel correspondiente a prioridad ESTANDAR.
             */
            private static final int NIVEL_ESTANDAR = 1;

            /**
             * Nivel correspondiente a prioridad ECONOMICO.
             */
            private static final int NIVEL_ECONOMICO = 2;

            @Override
            public int compare(Envio envioUno, Envio envioDos) {
                return Integer.compare(
                        jerarquia(envioUno.prioridad()),
                        jerarquia(envioDos.prioridad())
                );
            }

            /**
             * Calcula el nivel jerárquico.
             *
             * @param prioridad prioridad a evaluar
             * @return nivel jerárquico
             * @throws IllegalArgumentException si la prioridad es nulo
             */
            private static int jerarquia(Prioridad prioridad) {
                GestorLogisticaBase.exigirNoNulo(prioridad, "La prioridad");

                return switch (prioridad) {
                    case URGENTE -> NIVEL_URGENTE;
                    case ESTANDAR -> NIVEL_ESTANDAR;
                    case ECONOMICO -> NIVEL_ECONOMICO;
                };
            }
        }
    }
}
