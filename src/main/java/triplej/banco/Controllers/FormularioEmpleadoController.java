package triplej.banco.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.EmpleadoRepository;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;


public class FormularioEmpleadoController {

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCedula;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtPais;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private TextField txtCargo;
    @FXML private TextField txtSalario;
    @FXML private ComboBox<String> cmbDepartamento;
    @FXML private StackPane contenedorCentro;

    private AdminController adminController;

    @FXML
    public void initialize() {

        // Configurar ComboBox de departamentos
        cmbDepartamento.getItems().addAll(
                "Atención al Cliente",
                "Operaciones",
                "Tesorería",
                "Contabilidad",
                "Sistemas",
                "Recursos Humanos",
                "Gerencia"
        );
        cmbDepartamento.setValue("Atención al Cliente");

        configurarValidaciones();
    }

    private void configurarValidaciones() {
        // Solo números en cédula
        txtCedula.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtCedula.setText(oldVal);
            }
        });

        // Solo números en teléfono
        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtTelefono.setText(oldVal);
            }
        });

        // Solo números y punto decimal en salario
        txtSalario.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtSalario.setText(oldVal);
            }
        });

        // Validación de email en tiempo real
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^[A-Za-z0-9+_.-]*@?[A-Za-z0-9.-]*$") && !newVal.isEmpty()) {
                txtEmail.setStyle("-fx-border-color: red;");
            } else {
                txtEmail.setStyle("");
            }
        });
    }
    @FXML
    private void cancelar(){
        adminController.mostrarInicio();
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        try {
            // Validar contraseñas
            if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
                mostrarAlerta("Las contraseñas no coinciden");
                return;
            }

            // Crear PersonaNatural
            PersonaNatural persona = new PersonaNatural(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtEmail.getText().trim().toLowerCase(),
                    txtPassword.getText(),
                    RolUsuario.EMPLEADO,
                    TipoDocumento.CEDULACIUDADANIA,
                    txtCedula.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtPais.getText().trim(),
                    txtCiudad.getText().trim()
            );

            // Registrar empleado
            double salario = Double.parseDouble(txtSalario.getText().trim());
            Empleado nuevoEmpleado = new Empleado(
                    persona,
                    txtCargo.getText().trim(),
                    salario,
                    cmbDepartamento.getValue()
            );

            EmpleadoRepository.getInstance().agregarEmpleado(nuevoEmpleado);


            mostrarAlerta(
                    "Éxito",
                    "Empleado registrado exitosamente\n\n" +
                            "Cargo de empleado: " + nuevoEmpleado.getCargo() + "\n" +
                            "Nombre: " + nuevoEmpleado.getPersona().getNombreCompleto()
                    , Alert.AlertType.INFORMATION
            );


        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("El salario debe ser un número válido");
        }
    }

    public void setAdminController(AdminController adminController){
        this.adminController = adminController;
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

        if (txtCedula.getText().trim().isEmpty()) {
            mostrarAlerta("La cédula es obligatoria");
            txtCedula.requestFocus();
            return false;
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarAlerta("El teléfono es obligatorio");
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

        if (txtEmail.getText().trim().isEmpty()) {
            mostrarAlerta("El correo electrónico es obligatorio");
            txtEmail.requestFocus();
            return false;
        }

        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarAlerta("El correo electrónico no es válido");
            txtEmail.requestFocus();
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

        if (txtCargo.getText().trim().isEmpty()) {
            mostrarAlerta("El cargo es obligatorio");
            txtCargo.requestFocus();
            return false;
        }

        if (txtSalario.getText().trim().isEmpty()) {
            mostrarAlerta("El salario es obligatorio");
            txtSalario.requestFocus();
            return false;
        }

        try {
            double salario = Double.parseDouble(txtSalario.getText().trim());
            if (salario <= 0) {
                mostrarAlerta( "El salario debe ser mayor a 0");
                txtSalario.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("El salario debe ser un número válido");
            txtSalario.requestFocus();
            return false;
        }

        return true;
    }

}