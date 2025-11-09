package triplej.banco.Controllers.VistaAdmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;


public class FormularioEmpleadoController {

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCedula;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtPais;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private TextField txtCargo;
    @FXML private TextField txtSalario;
    @FXML private ComboBox<String> cmbDepartamento;

    @FXML private ImageView imgEmpleado;

    private File imagenSeleccionada;
    private static final String RUTA_IMAGENES =
            System.getProperty("user.home") + File.separator + "UQBank" + File.separator + "imagenes";
    private static final String IMAGEN_POR_DEFECTO ="/triplej/banco/Images/avatar.png";

    private AdminController adminController;
    private EmpleadoRepository empleadoRepository;
    private UsuarioRepository usuarioRepository;

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

    @FXML
    private void onCancelar(){
        adminController.mostrarInicio();
    }

    @FXML
    private void onGuardar() {
        if (!validarCampos()) {
            return;
        }

        try {
            // Validar contraseñas
            if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
                mostrarAlerta("Las contraseñas no coinciden");
                return;
            }

            if(correoYaExiste(txtCorreo.getText())){
                mostrarAlerta("Este correo ya está registrado");
                return;
            }

            PersonaNatural persona = getPersonaNatural();

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

            limpiarFormulario();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("El salario debe ser un número válido");
        }
    }

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

    public void setAdminController(AdminController adminController){
        this.adminController = adminController;
    }

    private boolean correoYaExiste(String email) {
        String emailNormalizado = email.trim().toLowerCase();

        // Verificar en empleados
        boolean existeEnEmpleados = empleadoRepository.existeEmpleadoConCorreo(emailNormalizado);

        // Verificar en usuarios generales
        boolean existeEnUsuarios = usuarioRepository.existeUsuarioConCorreo(emailNormalizado);

        return existeEnEmpleados || existeEnUsuarios;
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
            imgEmpleado.setImage(new Image(archivo.toURI().toString()));
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

}