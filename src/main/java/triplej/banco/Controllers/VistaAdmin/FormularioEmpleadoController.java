package triplej.banco.Controllers.VistaAdmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Services.AdminService;

import java.io.File;


import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

/**
 * Controlador encargado del formulario de registro de empleados.
 *
 * Permite a un administrador ingresar los datos de un nuevo empleado, validar la información,
 * cargar una imagen opcional y registrar el empleado en el sistema.
 *
 * Además, implementa verificaciones para evitar correos duplicados y errores de formato.
 */
public class FormularioEmpleadoController {

    // Campos del formulario
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCedula;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCiudad;
    @FXML
    private TextField txtPais;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmarPassword;
    @FXML
    private TextField txtCargo;
    @FXML
    private TextField txtSalario;
    @FXML
    private ComboBox<String> cmbDepartamento;
    @FXML
    private ImageView imgEmpleado;

    // Imagen seleccionada por el usuario (opcional)
    private File imagenSeleccionada;

    // Referencias a otros controladores y repositorios
    private AdminController adminController;
    private EmpleadoRepository empleadoRepository;
    private UsuarioRepository usuarioRepository;

    // Servicio que gestiona la lógica de negocio del administrador
    private final AdminService adminService = new AdminService();

    /**
     * Método que se ejecuta automáticamente al cargar la vista.
     * Inicializa los repositorios, llena el ComboBox de departamentos
     * y configura las validaciones de los campos.
     */
    @FXML
    public void initialize() {

        empleadoRepository = EmpleadoRepository.getInstance();
        usuarioRepository = UsuarioRepository.getInstancia();

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

    /**
     * Vuelve a la vista principal del administrador sin guardar los cambios.
     */
    @FXML
    private void onCancelar() {
        adminController.mostrarInicio();
    }

    /**
     * Maneja el evento del botón "Guardar".
     * Valida los campos del formulario, crea la persona y el empleado,
     * y lo registra mediante el servicio de administración.
     */
    @FXML
    private void onGuardar() {
        if (!validarCampos()) return;   // Verifica que los datos sean correctos

        try {
            // Verifica que ambas contraseñas coincidan
            if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
                mostrarAlerta("Las contraseñas no coinciden");
                return;
            }
            // Construye el objeto PersonaNatural a partir de los datos del formulario
            PersonaNatural persona = getPersonaNatural();

            // Evita correos duplicados
            if (adminService.correoYaExiste(persona.getCorreo())) {
                mostrarAlerta("Este correo ya está registrado");
                return;
            }

            // Convierte el salario a número y crea el nuevo empleado
            double salario = Double.parseDouble(txtSalario.getText());
            Empleado nuevoEmpleado = adminService.registrarEmpleado(
                    persona,
                    txtCargo.getText(),
                    salario,
                    cmbDepartamento.getValue(),
                    imagenSeleccionada
            );

            mostrarAlerta("Éxito", "Empleado registrado correctamente: " + nuevoEmpleado.getPersona().getNombreCompleto(), Alert.AlertType.INFORMATION);
            // Limpia los campos después del registro exitoso
            limpiarFormulario();

        } catch (Exception e) {
            mostrarAlerta("Error: " + e.getMessage());
        }
    }

    /**
     * Crea y devuelve un objeto PersonaNatural con la información del formulario.
     * <p>
     * Determina automáticamente el rol del usuario según su cargo (ADMIN, CAJERO o EMPLEADO).
     */
    private PersonaNatural getPersonaNatural() {
        String cargo = txtCargo.getText().trim().toUpperCase();

        RolUsuario rol;
        if (cargo.contains("ADMIN")) {
            rol = RolUsuario.ADMIN;
        } else if (cargo.contains("CAJERO")) {
            rol = RolUsuario.CAJERO;
        } else {
            rol = RolUsuario.EMPLEADO;
        }

        // Crear PersonaNatural
        PersonaNatural persona = new PersonaNatural(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtCorreo.getText().trim().toLowerCase(),
                txtPassword.getText(),
                rol,
                TipoDocumento.CEDULACIUDADANIA,
                txtCedula.getText().trim(),
                txtTelefono.getText().trim(),
                txtPais.getText().trim(),
                txtCiudad.getText().trim()
        );
        return persona;
    }


    /**
     * Asigna el controlador principal del administrador,
     * necesario para volver a la vista principal tras registrar o cancelar.
     */
    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    /**
     * Permite al administrador seleccionar una imagen desde el sistema de archivos.
     * <p>
     * Si se selecciona correctamente, se muestra la imagen en la vista.
     */
    @FXML
    private void onCargarImagen() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Imagen del Empleado");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File archivo = fc.showOpenDialog(null);
        if (archivo != null) {
            imagenSeleccionada = archivo;
            imgEmpleado.setImage(new Image(archivo.toURI().toString()));
        }
    }

    /**
     * Verifica que los campos del formulario sean válidos antes de guardar.
     * <p>
     * Realiza comprobaciones de formato, vacíos, longitud mínima de contraseñas
     * y formato de correo electrónico.
     *
     * @return true si todos los campos son válidos, false en caso contrario.
     */
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
                mostrarAlerta("El salario debe ser mayor a 0");
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

    /**
     * Limpia todos los campos del formulario y restablece los valores por defecto.
     */
    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtCedula.clear();
        txtTelefono.clear();
        txtCiudad.clear();
        txtPais.clear();
        txtCorreo.clear();
        txtPassword.clear();
        txtConfirmarPassword.clear();
        txtCargo.clear();
        txtSalario.clear();
        txtCorreo.setStyle("");
        cmbDepartamento.setValue("Atención al Cliente");
        txtNombre.requestFocus();
    }

    /**
     * Configura las validaciones automáticas de los campos:
     * - Cédula y teléfono solo aceptan números.
     * - Salario permite números y punto decimal.
     * - El correo valida su formato en tiempo real.
     */
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
        txtCorreo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^[A-Za-z0-9+_.-]*@?[A-Za-z0-9.-]*$") && !newVal.isEmpty()) {
                txtCorreo.setStyle("-fx-border-color: red;");
            } else {
                txtCorreo.setStyle("");
            }
        });
    }
}