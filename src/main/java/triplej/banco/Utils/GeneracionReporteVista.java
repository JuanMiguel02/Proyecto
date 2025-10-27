package triplej.banco.Utils;

import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import triplej.banco.Models.Reportes.ReporteGenerado;

public abstract class GeneracionReporteVista {
    public static void generarReporte(ReporteGenerado reporte, TextArea txtContenido, AnchorPane vista, StackPane contenedorCentro) {
        StringBuilder texto = new StringBuilder();
        for(String linea : reporte.getContenido()){
            texto.append(linea).append("\n");
        }

        txtContenido.setText(texto.toString());
        txtContenido.setWrapText(true);

        vista.setVisible(true);
        vista.setManaged(true);

        contenedorCentro.getChildren().clear();
        contenedorCentro.getChildren().add(vista);
    }
}
