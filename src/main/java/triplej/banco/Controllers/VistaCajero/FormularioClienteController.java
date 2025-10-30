package triplej.banco.Controllers.VistaCajero;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import triplej.banco.Controllers.VistaAdmin.AdminController;
import triplej.banco.Models.Cajero.Cajero;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;

import java.io.IOException;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class FormularioClienteController {


    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtNumDocumento;
    @FXML private ComboBox<TipoDocumento> cmbDocumento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtPais;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private TextField txtSaldo;
    @FXML private ComboBox<String> cmbCuenta;
    private final Cajero cajero = new Cajero();
    private CajeroController cajeroController;

    private final ClienteRepository clienteRepository = ClienteRepository.getInstancia();

    @FXML
    public void initialize() {
        cmbDocumento.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
        cmbCuenta.getItems().addAll("Ahorro", "Corriente", "Empresarial");
    }

    @FXML
    private void onRegistrar() {
        if (!validarCampos()) return;

        try {

            if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
                mostrarAlerta("Las contraseñas no coinciden");
                return;
            }

            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            TipoDocumento tipoDocumento = cmbDocumento.getSelectionModel().getSelectedItem();
            String numDocumento = txtNumDocumento.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String ciudad = txtCiudad.getText().trim();
            String pais = txtPais.getText().trim();
            String correo = txtCorreo.getText().trim();
            String contrasenia = txtPassword.getText().trim();
            String tipoCuenta = cmbCuenta.getValue();

            double saldo = 0.0;
            if (!txtSaldo.getText().trim().isEmpty()) {
                try {
                    saldo = Double.parseDouble(txtSaldo.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarAlerta("El saldo debe ser un número válido");
                    txtSaldo.requestFocus();
                    return;
                }
            }

            if (clienteRepository.buscarPorEmail(correo).isPresent() || clienteRepository.buscarPorDocumento(numDocumento).isPresent()) {
                mostrarAlerta("Este cliente ya está registrado");
                return;
            }

            Persona persona = new PersonaNatural(nombre, apellido, correo, contrasenia, RolUsuario.CLIENTE, tipoDocumento, numDocumento, telefono, pais, ciudad);

            Cliente nuevoCliente = cajero.registrarCliente(persona, tipoCuenta);

            CuentaBancaria cuenta = nuevoCliente.getCuentas().getFirst();
            nuevoCliente.setCuentaActiva(cuenta);
            cuenta.setSaldo(saldo);

            mostrarAlerta(
                    "Éxito",
                    "Cliente y cuenta creados correctamente.\nNúmero de cuenta: " + cuenta.getNumeroCuenta(),
                    Alert.AlertType.INFORMATION
            );

            limpiarCampos();
        }catch (RuntimeException e){
            mostrarAlerta(e.getMessage());
        }

    }
    private boolean validarCampos() {
            if (txtNombre.getText().trim().isEmpty()) {
                mostrarAlerta("El nombre es obligatorio");
                txtNombre.requestFocus();
                return false;
            }

            if (txtApellido.getText().trim().isEmpty()) {
                mostrarAlerta("El apellido es obligatorio");
                txtApellido.requestFocus();
                return false;
            }

            if (cmbDocumento.getSelectionModel().isEmpty()) {
                mostrarAlerta("Debe seleccionar un tipo de documento");
                cmbDocumento.requestFocus();
                return false;
            }

            if (txtNumDocumento.getText().trim().isEmpty()) {
                mostrarAlerta("El número de documento es obligatorio");
                txtNumDocumento.requestFocus();
                return false;
            }

            if (!txtNumDocumento.getText().matches("\\d{5,}")) {
                mostrarAlerta("El número de documento debe contener solo dígitos (mínimo 5)");
                txtNumDocumento.requestFocus();
                return false;
            }

            if (txtTelefono.getText().trim().isEmpty()) {
                mostrarAlerta("El teléfono es obligatorio");
                txtTelefono.requestFocus();
                return false;
            }

            if (!txtTelefono.getText().matches("\\d{7,15}")) {
                mostrarAlerta("El teléfono debe contener solo dígitos (mínimo 7)");
                txtTelefono.requestFocus();
                return false;
            }

            if (txtCiudad.getText().trim().isEmpty()) {
                mostrarAlerta("La ciudad es obligatoria");
                txtCiudad.requestFocus();
                return false;
            }

            if (txtPais.getText().trim().isEmpty()) {
                mostrarAlerta("El país es obligatorio");
                txtPais.requestFocus();
                return false;
            }

            if (txtCorreo.getText().trim().isEmpty()) {
                mostrarAlerta("El correo electrónico es obligatorio");
                txtCorreo.requestFocus();
                return false;
            }

            if (!txtCorreo.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                mostrarAlerta("El correo electrónico no es válido");
                txtCorreo.requestFocus();
                return false;
            }

            if (txtPassword.getText().isEmpty()) {
                mostrarAlerta("La contraseña es obligatoria");
                txtPassword.requestFocus();
                return false;
            }

            if (txtPassword.getText().length() < 6) {
                mostrarAlerta("La contraseña debe tener al menos 6 caracteres");
                txtPassword.requestFocus();
                return false;
            }

            if (txtConfirmarPassword.getText().isEmpty()) {
                mostrarAlerta("Debe confirmar la contraseña");
                txtConfirmarPassword.requestFocus();
                return false;
            }

            if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
                mostrarAlerta("Las contraseñas no coinciden");
                txtConfirmarPassword.requestFocus();
                return false;
            }

            if (cmbCuenta.getValue() == null) {
                mostrarAlerta("Debe seleccionar un tipo de cuenta");
                cmbCuenta.requestFocus();
                return false;
            }

            try {
                double saldo = Double.parseDouble(txtSaldo.getText().trim());
                if (saldo < 0) {
                    mostrarAlerta("El saldo no puede ser negativo");
                    txtSaldo.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("El saldo debe ser un número válido");
                txtSaldo.requestFocus();
                return false;
            }

            return true;

    }

    private void limpiarCampos(){
        txtNombre.clear();
        txtApellido.clear();
        txtNumDocumento.clear();
        txtTelefono.clear();
        txtCiudad.clear();
        txtPais.clear();
        txtCorreo.clear();
        txtPassword.clear();
        txtConfirmarPassword.clear();
        txtSaldo.clear();
        cmbCuenta.setValue(null);
        cmbDocumento.setValue(null);
    }

    public void setCajeroController(CajeroController cajeroController){
        this.cajeroController = cajeroController;
    }

    @FXML
    private void cancelar(){
        cajeroController.mostrarInicio();
    }

}
