package triplej.banco.Controllers.VistaCajero;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import triplej.banco.Services.CajeroService;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class FormularioClienteController {

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtNumDocumento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtPais;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtRepresentante;
    @FXML private TextField txtTipoEmpresa;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private TextField txtSaldo;

    @FXML private ComboBox<TipoDocumento> cmbDocumento;
    @FXML private ComboBox<String> cmbCuenta;
    @FXML private ComboBox<String> cmbTipoCliente;

    @FXML private VBox boxJuridica;
    @FXML private VBox datosPersonaNatural;

    @FXML private ImageView imgCliente;

    private File imagenSeleccionada;
    private static final String RUTA_IMAGENES = "triplej/banco/Images";
    private static final String IMAGEN_POR_DEFECTO ="/triplej/banco/Images/avatar.png";
    private final CajeroService cajeroService = new CajeroService();
    private CajeroController cajeroController;

    private final ClienteRepository clienteRepository = ClienteRepository.getInstancia();

    @FXML
    public void initialize() {
        cmbTipoCliente.getItems().addAll("Persona Natural", "Persona Jurídica");
        cmbDocumento.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
        cmbTipoCliente.setOnAction(e -> onTipoClienteSeleccionado());
        cmbCuenta.getItems().addAll("Ahorro", "Corriente", "Empresarial");
    }

    @FXML
    private void onTipoClienteSeleccionado(){
        String tipo = cmbTipoCliente.getValue();
        boolean esJuridica = "Persona Jurídica".equalsIgnoreCase(tipo);

        datosPersonaNatural.setVisible(!esJuridica);
        datosPersonaNatural.setManaged(!esJuridica);

        boxJuridica.setVisible(esJuridica);
        boxJuridica.setManaged(esJuridica);

        cmbCuenta.getItems().clear();
        if(esJuridica){
            cmbCuenta.getItems().addAll("Empresarial", "Corriente");
            cmbDocumento.getItems().clear();
            cmbDocumento.getItems().addAll(TipoDocumento.NIT);
        }else{
            cmbCuenta.getItems().addAll("Ahorro", "Corriente", "Empresarial");

        }
    }

    @FXML
    private void onRegistrar() {
        if (!validarCampos()) return;

        try {
            if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
                mostrarAlerta("Las contraseñas no coinciden");
                return;
            }

            String telefono = txtTelefono.getText().trim();
            String ciudad = txtCiudad.getText().trim();
            String pais = txtPais.getText().trim();
            String correo = txtCorreo.getText().trim();
            String contrasenia = txtPassword.getText().trim();
            String numDocumento = txtNumDocumento.getText().trim();
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

            if (clienteRepository.buscarPorCorreo(correo).isPresent()) {
                mostrarAlerta("Este correo ya está registrado");
                return;
            }

            //----CREAR PERSONA SEGÚN EL TIPO DE CLIENTE----
            String tipoCliente = cmbTipoCliente.getValue();
            Persona persona;

            if("Persona Jurídica".equalsIgnoreCase(tipoCliente)){
                //----CREAR PERSONA JURÍDICA-----
                String razonSocial = txtRazonSocial.getText().trim();
                String representante = txtRepresentante.getText().trim();
                String tipoEmpresa = txtTipoEmpresa.getText().trim();
                TipoDocumento nit = this.cmbDocumento.getValue();

                if(razonSocial.isEmpty() || representante.isEmpty() || tipoEmpresa.isEmpty()){
                    mostrarAlerta("Debe completar todos los campos");
                    return;
                }
                if(clienteRepository.buscarPorDocumento(nit.toString()).isPresent()){
                    mostrarAlerta("Ya existe una empresa registrada con este NIT");
                    return;
                }
                persona = new PersonaJuridica(
                        razonSocial,
                        representante,
                        tipoEmpresa,
                        correo,
                        contrasenia,
                        RolUsuario.CLIENTE,
                        nit,
                        numDocumento,
                        telefono,
                        pais,
                        ciudad
                );
            }else{
                //---- PERSONA NATURAL----//
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                TipoDocumento tipoDocumento = cmbDocumento.getSelectionModel().getSelectedItem();

                if(nombre.isEmpty() || apellido.isEmpty()){
                    mostrarAlerta("Debe completar todos los campos");
                    return;
                }
                persona = new PersonaNatural(nombre, apellido, correo, contrasenia, RolUsuario.CLIENTE, tipoDocumento, numDocumento, telefono, pais, ciudad);
            }
            try{
                Path carpeta = Paths.get(RUTA_IMAGENES);
                Files.createDirectories(carpeta);

                String nombreArchivo = persona.getNumeroDocumento() + ".jpg";
                Path destino = carpeta.resolve(nombreArchivo);

                if(imagenSeleccionada != null){
                    Files.copy(imagenSeleccionada.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
                    persona.setFoto(destino.toString());

                }else{
                    persona.setFoto(IMAGEN_POR_DEFECTO);
                }
            }catch (IOException e) {
                mostrarAlerta("No se puedo guardar la imagen " + e.getMessage());
                persona.setFoto(IMAGEN_POR_DEFECTO);
            }
            Cliente nuevoCliente = cajeroService.registrarCliente(persona, tipoCuenta, saldo);

            CuentaBancaria cuenta = nuevoCliente.getCuentas().getFirst();
            nuevoCliente.setCuentaActiva(cuenta);

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

    @FXML
    private void onCargarImagen(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Imagen del Cliente");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File archivo = fc.showOpenDialog(null);
        if(archivo != null){
            imagenSeleccionada = archivo;
            imgCliente.setImage(new Image(archivo.toURI().toString()));
        }
    }

    private boolean validarCampos() {

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

        imgCliente.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMAGEN_POR_DEFECTO))));
        imagenSeleccionada = null;
    }

    public void setCajeroController(CajeroController cajeroController){
        this.cajeroController = cajeroController;
    }

    @FXML
    private void cancelar(){
        cajeroController.mostrarInicio();
    }

}
