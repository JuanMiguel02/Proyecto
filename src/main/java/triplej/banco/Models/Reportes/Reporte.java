package triplej.banco.Models.Reportes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa un reporte generado dentro del sistema bancario.
 * <p>
 * Los reportes son utilizados para registrar y visualizar información relevante
 * sobre operaciones, movimientos, auditorías o estados de cuenta.
 * Cada reporte contiene un título, una fecha de generación y un conjunto
 * de líneas o secciones que conforman su contenido.
 * </p>
 *
 */
public class Reporte {
    /** Título o encabezado del reporte. */
    private String titulo;

    /** Fecha y hora en que el reporte fue generado. */
    private LocalDateTime fecha;

    /** Contenido del reporte, representado como una lista de líneas o secciones. */
    private List<String> contenido;

    /**
     * Crea un nuevo reporte con los datos especificados.
     *
     * @param titulo     Título o nombre del reporte.
     * @param fecha      Fecha y hora de generación.
     * @param contenido  Lista de elementos o líneas que conforman el cuerpo del reporte.
     */
    public Reporte(String titulo, LocalDateTime fecha, List<String> contenido) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.contenido = contenido;
    }

    //Getters y Setters
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<String> getContenido() {
        return contenido;
    }

    public void setContenido(List<String> contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "ReporteGenerado{" +
                "titulo='" + titulo + '\'' +
                ", fecha=" + fecha +
                ", contenido=" + contenido +
                '}';
    }
}
