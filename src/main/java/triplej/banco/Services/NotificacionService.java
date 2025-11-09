package triplej.banco.Services;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class NotificacionService {

    public void enviarCorreo(String destinatario, String asunto, String mensaje){
        System.out.println("Correo enviado para "+destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Mensaje: \n " + mensaje);
        System.out.println("----------------------------");

        Platform.runLater(() -> {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Correo enviado para "+ destinatario);
            alerta.setHeaderText(asunto);
            alerta.setContentText("Para: " +  destinatario + "\n\n" +mensaje);
            alerta.show();
        });

    }
}
