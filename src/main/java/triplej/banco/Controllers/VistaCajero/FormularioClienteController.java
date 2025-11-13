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
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Services.CajeroService;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;


import java.io.File;
import java.util.Objects;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

/**
 * Controlador para el formulario de registro de clientes en la vista del cajero.
 * Permite registrar tanto personas naturales como jurídicas, validar los datos ingresados,
 * guardarUsuario una imagen opcional y crear automáticamente una cuenta bancaria asociada al cliente.
 * <p>
 * Se comunica con el servicio {@link CajeroService} y el repositorio {@link ClienteRepository}
 * para realizar las operaciones de negocio y persistencia.
 */
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
    @FXML private TextField txtSobregiro;

    @FXML private ComboBox<TipoDocumento> cmbDocumento;
    @FXML private ComboBox<String> cmbCuenta;
    @FXML private ComboBox<String> cmbTipoCliente;

    @FXML private VBox datosPersonaJuridica;    // Sección visible solo para personas jurídicas
    @FXML private VBox datosPersonaNatural;     // Sección visible solo para personas naturales
    @FXML private VBox campoSobregiro;

    @FXML private ImageView imgCliente;     // Imagen del cliente

    private File imagenSeleccionada;
    private static final String IMAGEN_POR_DEFECTO ="/triplej/banco/Images/avatar.png";
    private final CajeroService cajeroService = new CajeroService();
    private CajeroController cajeroController;

    /**
     * Inicializa el formulario configurando las listas desplegables
     * y los comportamientos dinámicos según el tipo de cliente seleccionado.
     */
    @FXML
    public void initialize() {

        cmbTipoCliente.getItems().addAll("Persona Natural", "Persona Jurídica");
        cmbDocumento.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
        cmbCuenta.getItems().addAll("Ahorro", "Corriente", "Empresarial");

        // Detecta cuando se cambia el tipo de cliente y actualiza la interfaz
        cmbTipoCliente.setOnAction(e -> onTipoClienteSeleccionado());

        configurarValidaciones();
    }

    /**
     * Cambia la visibilidad de las secciones del formulario dependiendo
     * del tipo de cliente (natural o jurídica).
     */
    @FXML
    private void onTipoClienteSeleccionado(){
        String tipo = cmbTipoCliente.getValue();
        boolean esJuridica = "Persona Jurídica".equalsIgnoreCase(tipo);

        // Mostrar solo la sección correspondiente
        datosPersonaNatural.setVisible(!esJuridica);
        datosPersonaNatural.setManaged(!esJuridica);
        datosPersonaJuridica.setVisible(esJuridica);
        datosPersonaJuridica.setManaged(esJuridica);

        cmbDocumento.getItems().clear();
        // Configurar opciones válidas de cuenta y documento
        cmbCuenta.getItems().clear();
        if(esJuridica){
            cmbCuenta.getItems().addAll("Empresarial", "Corriente");
            cmbDocumento.getItems().clear();
            cmbDocumento.getItems().addAll(TipoDocumento.NIT);
        }else{
            cmbCuenta.getItems().addAll("Ahorro", "Corriente", "Empresarial");
            cmbDocumento.getItems().addAll(TipoDocumento.values());
        }
    }

    @FXML
    private void onTipoCuentaSeleccionado() {
        String tipoSeleccionado = cmbCuenta.getValue();

        if ("Corriente".equalsIgnoreCase(tipoSeleccionado)) {
            campoSobregiro.setVisible(true);
            campoSobregiro.setManaged(true);
        } else {
            campoSobregiro.setVisible(false);
            campoSobregiro.setManaged(false);
        }

    }

    /**
     * Valida los campos ingresados y, si son válidos, registra el cliente.
     * Llama al servicio correspondiente dependiendo del tipo de cliente.
     */
    @FXML
    private void onRegistrar() {
        if (!validarCampos()) return;

        try {
            double saldo = Double.parseDouble(txtSaldo.getText().trim());
            String tipoCliente = cmbTipoCliente.getValue();
            Double sobregiro = null;

            if ("Corriente".equalsIgnoreCase(cmbCuenta.getValue()) && !txtSobregiro.getText().isBlank()) {
                sobregiro = Double.parseDouble(txtSobregiro.getText().trim());
            }

            CuentaBancaria cuentaCreada;

           if ("Persona Jurídica".equalsIgnoreCase(tipoCliente)) {
                cuentaCreada = cajeroService.registrarPersonaJuridica(
                        txtRazonSocial.getText(), txtRepresentante.getText(), txtTipoEmpresa.getText(),
                        txtCorreo.getText(), txtPassword.getText(), cmbDocumento.getValue(),
                        txtNumDocumento.getText(), txtTelefono.getText(), txtPais.getText(), txtCiudad.getText(),
                        cmbCuenta.getValue(), saldo, sobregiro, imagenSeleccionada
                );
            } else {
                 cuentaCreada = cajeroService.registrarPersonaNatural(
                        txtNombre.getText(), txtApellido.getText(), txtCorreo.getText(),
                        txtPassword.getText(), cmbDocumento.getValue(),
                        txtNumDocumento.getText(), txtTelefono.getText(),
                        txtPais.getText(), txtCiudad.getText(), cmbCuenta.getValue(),
                        saldo, sobregiro, imagenSeleccionada
                );
            }

            mostrarAlerta("ÉXITO","Cliente registrado correctamente \nNúmero de Cuenta: " + cuentaCreada.getNumeroCuenta(), Alert.AlertType.INFORMATION);
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error: " + e.getMessage());
        }
    }

    /**
     * Abre un cuadro de diálogo para seleccionar una imagen desde el sistema
     * de archivos y la muestra en el formulario.
     */
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

    /**
     * Realiza la validación de los campos del formulario.
     *
     * @return {@code true} si todos los campos son válidos, {@code false} en caso contrario.
     * Muestra alertas específicas si se detectan errores.
     */
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
            mostrarAlerta("El saldo debe de ser un número válido");
            txtSaldo.requestFocus();
            return false;
        }

        if ("Corriente".equalsIgnoreCase(cmbCuenta.getValue())) {
            if (txtSobregiro.getText().trim().isEmpty()) {
                mostrarAlerta("Debe ingresar un valor de sobregiro para la cuenta corriente");
                txtSobregiro.requestFocus();
                return false;
            }
            try {
                double sobregiro = Double.parseDouble(txtSobregiro.getText().trim());
                if (sobregiro < 0) {
                    mostrarAlerta("El sobregiro no puede ser negativo");
                    txtSobregiro.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("El sobregiro debe ser un número válido");
                txtSobregiro.requestFocus();
                return false;
            }
        }
        return true;
    }
    /**
     * Limpia todos los campos del formulario y restaura la imagen por defecto.
     */
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

    /**
     * Asocia este formulario con el controlador principal del cajero
     * para permitir la navegación entre vistas.
     *
     * @param cajeroController instancia del controlador principal del cajero
     */
    public void setCajeroController(CajeroController cajeroController){
        this.cajeroController = cajeroController;
    }

    /**
     * Cancela el registro actual y regresa a la pantalla principal del cajero.
     */
    @FXML
    private void cancelar(){
        cajeroController.mostrarInicio();
    }

    /**
     * Configura validaciones básicas en tiempo real para los campos del formulario.
     * Restringe caracteres inválidos mientras el usuario escribe.
     */
    private void configurarValidaciones() {

        // Solo letras y espacios
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]*")) {
                txtNombre.setText(oldVal);
            }
        });

        txtApellido.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]*")) {
                txtApellido.setText(oldVal);
            }
        });

        txtCiudad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]*")) {
                txtCiudad.setText(oldVal);
            }
        });

        txtPais.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]*")) {
                txtPais.setText(oldVal);
            }
        });

        txtRepresentante.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]*")) {
                txtRepresentante.setText(oldVal);
            }
        });

        txtTipoEmpresa.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]*")) {
                txtTipoEmpresa.setText(oldVal);
            }
        });

        // Solo números
        txtNumDocumento.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtNumDocumento.setText(oldVal);
            }
        });

        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtTelefono.setText(oldVal);
            }
        });

        txtSaldo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) { // permite decimales
                txtSaldo.setText(oldVal);
            }
        });

        txtSobregiro.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                txtSobregiro.setText(oldVal);
            }
        });
    }
}
