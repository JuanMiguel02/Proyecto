package triplej.banco;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import triplej.banco.Models.Banco; // Importar Banco

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Banco.getInstancia();

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/triplej/banco/Views/Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 900);
        stage.setMaximized(true);
        stage.setTitle("UQ Bank");
        stage.setScene(scene);
        stage.show();
    }

}
