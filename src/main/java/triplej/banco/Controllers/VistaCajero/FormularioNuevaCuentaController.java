package triplej.banco.Controllers.VistaCajero;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import triplej.banco.Models.Cajero.Cajero;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class FormularioNuevaCuentaController {

    @FXML private Label lblNombreCliente;
    @FXML private Label lblDocumentoCliente;
    @FXML private TextField txtNumeroCuenta;
    @FXML private ComboBox<String> cmbTipoCuenta;
    @FXML private TextField txtSaldoInicial;

    private Cliente cliente;
    private final Cajero cajero = new Cajero();

    @FXML
    public void initialize(){
        cmbTipoCuenta.getItems().addAll("AHORRO", "CORRIENTE", "EMPRESARIAL");
    }

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
        lblNombreCliente.setText(cliente.getUsuarioAsociado().getNombreCompleto());
        lblDocumentoCliente.setText(cliente.getDocumento());
    }

    @FXML
    private void crearCuenta(){
        if(cliente == null){
            mostrarAlerta("No se ha seleccionado un cliente");
            return;
        }
        String tipoCuenta = cmbTipoCuenta.getValue();

        if(tipoCuenta == null || txtSaldoInicial.getText() == null){
            mostrarAlerta("Complete todos los campos");
            return;
        }
        double saldo = 0.0;
        if (!txtSaldoInicial.getText().trim().isEmpty()) {
            try {
                saldo = Double.parseDouble(txtSaldoInicial.getText().trim());
            } catch (NumberFormatException e) {
                mostrarAlerta("El saldo debe ser un número válido");
                txtSaldoInicial.requestFocus();
                return;
            }
        }
        try{
            CuentaBancaria cuentaNueva = cajero.agregarCuentaACliente(cliente, tipoCuenta, saldo);
            cuentaNueva.setSaldo(saldo);
            mostrarAlerta("Éxito", "Cuenta: " + cuentaNueva.getNumeroCuenta() + " creada exitosamente" + " \n Propietario: " + cliente.getNombre(), Alert.AlertType.INFORMATION);

        }catch(Exception e){
            mostrarAlerta(e.getMessage());
        }

    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) lblNombreCliente.getScene().getWindow()).close();
    }
}
