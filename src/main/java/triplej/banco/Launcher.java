package triplej.banco;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.IOException;
import java.util.Objects;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        inicializarDatos();

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/triplej/banco/Views/Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/triplej/banco/Styles/login.css")).toExternalForm());
        stage.setMaximized(true);
        stage.setTitle("UQ Bank");
        stage.setScene(scene);
        stage.show();
    }

    private void inicializarDatos(){
        UsuarioRepository.getInstancia();
        ClienteRepository.getInstancia();
        EmpleadoRepository.getInstancia();
    }

}
