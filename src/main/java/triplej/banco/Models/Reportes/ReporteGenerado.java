package triplej.banco.Models.Reportes;
/**
 * Define el contrato para la generación de reportes dentro del sistema bancario.
 * <p>
 * Las clases que implementan esta interfaz deben proveer su propia lógica
 * para construir un objeto {@link Reporte} a partir de los datos que gestionan.
 * </p>
 *
 * <p>
 * Este diseño permite la creación de distintos tipos de reportes, como:
 * </p>
 * <ul>
 *     <li>{@link ReporteCliente}: Reportes individuales para cada cuenta bancaria.</li>
 *     <li>{@link ReporteAdmin}: Reportes globales del sistema con estadísticas generales.</li>
 * </ul>
 *
 */
public interface ReporteGenerado {
    Reporte generarReporte();
}
