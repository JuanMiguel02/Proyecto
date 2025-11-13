package triplej.banco.Utils;

import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import triplej.banco.Models.Reportes.Reporte;

/**
 * Clase abstracta que centraliza la lógica para mostrar un reporte
 * en la interfaz gráfica de la aplicación.
 * <p>
 * Esta clase no puede instanciarse directamente, ya que actúa como
 * una utilidad común para las diferentes vistas que generan y muestran reportes.
 */
public abstract class GeneracionReporteVista {

    /**
     * Genera la visualización de un reporte dentro del área de texto y la vista indicada.
     *
     * @param reporte instancia del reporte a mostrar (ya debe estar generado)
     * @param txtContenido componente {@link TextArea} donde se mostrará el texto del reporte
     * @param vista contenedor {@link AnchorPane} que representa la vista del reporte
     * @param contenedorCentro contenedor principal {@link StackPane} donde se insertará la vista
     */
    public static void generarReporte(Reporte reporte, TextArea txtContenido, AnchorPane vista, StackPane contenedorCentro) {
        // Crea un objeto StringBuilder para armar el contenido del reporte línea por línea.
        StringBuilder texto = new StringBuilder();
        // Recorre cada línea del contenido del reporte y la agrega con salto de línea.
        for(String linea : reporte.getContenido()){
            texto.append(linea).append("\n");
        }
        // Coloca el texto construido dentro del TextArea de la interfaz.
        txtContenido.setText(texto.toString());
        // Activa el ajuste de línea automático para que el texto no se salga del área visible.
        txtContenido.setWrapText(true);

        // Limpia cualquier otro contenido previo del contenedor central
        // (por ejemplo, otras vistas o formularios).
        vista.setVisible(true);
        vista.setManaged(true);

        // Agrega la vista del reporte al centro de la interfaz gráfica.
        contenedorCentro.getChildren().clear();
        contenedorCentro.getChildren().add(vista);
    }
}
