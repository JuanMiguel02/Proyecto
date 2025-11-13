package triplej.banco.Utils;

import javafx.scene.control.Alert;

/**
 * Clase de utilidad que centraliza la creación y visualización de alertas.
 * Permite mostrar mensajes de error, advertencia o información sin repetir código.
 */
public class AlertHelper {

    /**
     * Método que muestra una alerta personalizada indicando título, mensaje y tipo.
     * Se usa cuando queremos definir todo manualmente.
     *
     * @param titulo título que aparece en la barra de la ventana de alerta
     * @param mensaje texto que se mostrará dentro de la alerta
     * @param tipo tipo de alerta (ERROR, INFORMATION, WARNING, CONFIRMATION)
     */
    public static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Variante simplificada del método anterior.
     * En este caso, solo recibe el mensaje y el tipo de alerta,
     * usando un título genérico "ERROR" por defecto.
     *
     * @param mensaje texto que se mostrará en la alerta
     * @param tipo tipo de alerta (ERROR, INFORMATION, etc.)
     */
    public static void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("ERROR");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Versión más simple de todas: muestra una alerta de error estándar.
     * Se usa cuando solo queremos mostrar un mensaje sin especificar el tipo.
     *
     * @param mensaje texto que se mostrará al usuario
     */
    public static void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("ERROR");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


}
