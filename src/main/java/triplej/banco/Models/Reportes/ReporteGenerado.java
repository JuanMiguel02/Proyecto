package triplej.banco.Models.Reportes;

import java.time.LocalDateTime;
import java.util.List;

public class ReporteGenerado {
    private String titulo;
    private LocalDateTime fecha;
    private List<String> contenido;

    public ReporteGenerado(String titulo, LocalDateTime fecha, List<String> contenido) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.contenido = contenido;
    }
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
