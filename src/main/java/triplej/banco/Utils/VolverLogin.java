package triplej.banco.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public abstract class VolverLogin {

    public static void volverLogin(Stage ventanaActual) {
        try {
            FXMLLoader loader = new FXMLLoader(VolverLogin.class.getResource("/triplej/banco/Views/Login-view.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    Objects.requireNonNull(VolverLogin.class.getResource("/triplej/banco/Styles/login.css")).toExternalForm()
            );

            Stage loginStage = new Stage();
            loginStage.setTitle("Inicio UQ BANK");
            loginStage.setScene(scene);
            loginStage.setMaximized(true);
            loginStage.show();

            if (ventanaActual != null) {
                ventanaActual.close();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al volver al login: " + e.getMessage(), e);
        }
    }
}
