package triplej.banco;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import triplej.banco.Models.Banco; // Importar Banco

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Banco.getInstancia();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/triplej/banco/Views/CajeroViews/Cajero-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 900);
        stage.setMaximized(true);
        stage.setTitle("UQ Bank");
        stage.setScene(scene);
        stage.show();
    }

}
