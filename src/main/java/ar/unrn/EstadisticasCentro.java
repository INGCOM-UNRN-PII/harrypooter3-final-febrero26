package ar.unrn;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa estadísticas calculadas para un centro de distribución.
 */
public final class EstadisticasCentro implements Serializable {

    /**
     * Identificador de serialización.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Porcentaje mínimo admitido.
     */
    private static final double PORCENTAJE_MINIMO = 0.0d;

    /**
     * Peso mínimo admitido.
     */
    private static final double PESO_MINIMO = 0.0d;

    /**
     * Porcentaje máximo admitido.
     */
    private static final double PORCENTAJE_MAXIMO = 100.0d;

    /**
     * Peso total transportado en el centro.
     */
    private final double pesoTotal;

    /**
     * Porcentaje de envíos entregados.
     */
    private final double porcentajeEntrega;

    /**
     * Crea estadísticas.
     *
     * @param peso       peso total transportado
     * @param porcentaje porcentaje entregado (0 a 100)
     * @throws IllegalArgumentException si los valores son inválidos
     */
    public EstadisticasCentro(double peso, double porcentaje) {
        super();
        if (peso < PESO_MINIMO) {
            throw new IllegalArgumentException(
                    """
                    El peso total no puede ser negativo.
                    """
            );
        }
        if (porcentaje < PORCENTAJE_MINIMO) {
            throw new IllegalArgumentException(
                    """
                    El porcentaje no puede ser negativo.
                    """
            );
        }
        if (porcentaje > PORCENTAJE_MAXIMO) {
            throw new IllegalArgumentException(
                    """
                    El porcentaje no puede superar 100.
                    """
            );
        }
        this.pesoTotal = peso;
        this.porcentajeEntrega = porcentaje;
    }

    /**
     * Devuelve el peso total transportado.
     *
     * @return peso total transportado
     */
    public double pesoTotalTransportado() {
        return this.pesoTotal;
    }

    /**
     * Devuelve el porcentaje de envíos entregados.
     *
     * @return porcentaje entregado
     */
    public double porcentajeEntregado() {
        return this.porcentajeEntrega;
    }

    /**
     * Compara las estadísticas con otro objeto.
     *
     * @param objeto objeto a comparar
     * @return true si tienen los mismos valores, false en caso contrario
     */
    @Override
    public boolean equals(Object objeto) {
        return this == objeto
                || (objeto instanceof EstadisticasCentro estadisticas
                && Double.compare(this.pesoTotal, estadisticas.pesoTotal) == 0
                && Double.compare(
                this.porcentajeEntrega,
                estadisticas.porcentajeEntrega) == 0);
    }

    /**
     * Calcula el código hash consistente con {@link #equals(Object)}.
     *
     * @return código hash de las estadísticas
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.pesoTotal, this.porcentajeEntrega);
    }
}
