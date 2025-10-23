package triplej.banco.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import triplej.banco.Models.Usuarios.Cliente;

import java.io.IOException;

public class ClienteController {
    private Cliente cliente;
    @FXML private Label lblNombre;
    @FXML private Button btnSalir;

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
        lblNombre.setText(cliente.getNombre());
    }

    public Cliente getCliente(){
        return cliente;
    }

    @FXML
    private void volverMenu(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/SingIn-view.fxml"));
            Parent root = loader.load();

            SignInController signInController= loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Inicio");
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) btnSalir.getScene().getWindow()).close();


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
