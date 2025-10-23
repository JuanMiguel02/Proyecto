package triplej.banco.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;

import java.io.IOException;

public class ClienteController {
    private Cliente cliente;
    @FXML private Label lblNombre;
    @FXML private Label lblDinero;
    @FXML private Button btnSalir;

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
        CuentaBancaria cuentaActiva = new CuentaAhorro(cliente);

        cliente.agregarCuenta(cuentaActiva);
        cliente.setCuentaActiva(cuentaActiva);

        lblNombre.setText(cliente.getNombre());
        depositar();
        lblDinero.setText(String.valueOf(cliente.getSaldo()));
    }

    public Cliente getCliente(){
        return cliente;
    }

    private void depositar(){
         cliente.getCuentaActiva().depositar(200000.00);
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
            stage.setMaximized(true);
            stage.show();

            ((Stage) btnSalir.getScene().getWindow()).close();


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
