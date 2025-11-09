package triplej.banco.Controllers.VistaAdmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Models.Usuarios.Empleado;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Services.AdminService;

import java.io.File;
import java.util.Objects;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

/**
 * Controlador de la vista de administración de empleados.
 *
 * Esta clase gestiona las operaciones que el administrador puede realizar
 * sobre los empleados del banco, como visualizar, filtrar, editar y eliminar.
 *
 * También maneja la interfaz gráfica para alternar entre la vista de tabla
 * (donde se listan los empleados) y la vista de edición (donde se actualizan sus datos).
 */
public class TablaEmpleadosController {

    /** Tabla que muestra los empleados registrados */
    @FXML private TableView<Empleado> tablaEmpleados;

    /** Contenedores (paneles) para alternar entre la vista de tabla y la de edición */
    @FXML private AnchorPane vistaTabla;
    @FXML private AnchorPane vistaEdicion;

    /** Columnas de la tabla, una por cada atributo del empleado mostrado */
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colApellido;
    @FXML private TableColumn<Empleado, String> colCargo;
    @FXML private TableColumn<Empleado, String> colDocumento;
    @FXML private TableColumn<Empleado, String> colTelefono;
    @FXML private TableColumn<Empleado, String> colCorreo;
    @FXML private TableColumn<Empleado, String> colDepartamento;
    @FXML private TableColumn<Empleado, Integer> colSalario;
    @FXML private TableColumn<Empleado, String> colCiudad;

    /** Campos de texto del formulario de edición */
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtCedula;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtSalario;
    @FXML private TextField txtCargo;
    @FXML private TextField txtContraseniaVisible;
    @FXML private PasswordField txtContrasenia;
    @FXML private ComboBox<String> cmbDepartamento;

    /** Checkbox para alternar la visibilidad de la contraseña */
    @FXML
    private CheckBox chkMostrarContrasenia;

    /** Campo de búsqueda de empleados */
    @FXML private TextField txtBuscar;

    /** Imagen del empleado seleccionado */
    @FXML private ImageView imgEmpleado;
    private static final String IMAGEN_POR_DEFECTO ="/triplej/banco/Images/avatar.png";

    /** Empleado actualmente seleccionado para edición */
    private Empleado empleadoSeleccionado;

    /** Repositorios y servicios para gestionar los datos */
    private EmpleadoRepository empleadoRepository;
    private UsuarioRepository usuarioRepository;
    private ObservableList<Empleado> listaEmpleados;

    /** Servicio que centraliza las operaciones del administrador */
    private final AdminService adminService = new AdminService();

    /**
     * Método que se ejecuta automáticamente al cargar la vista.
     *
     * - Configura las columnas de la tabla para mostrar los datos del empleado.
     * - Carga la lista de empleados desde el repositorio.
     * - Configura la búsqueda dinámica por nombre, apellido o cargo.
     * - Muestra la imagen del empleado seleccionado.
     */
    @FXML
    public void initialize() {
        empleadoRepository = EmpleadoRepository.getInstance();
        usuarioRepository = UsuarioRepository.getInstancia();

        // Configuración de columnas con los getters de Empleado
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // llama a getNombre()
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido")); // llama a getApellido()
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));  // llama a getCargo()
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));

        // Opciones del ComboBox de departamentos
        cmbDepartamento.getItems().addAll(
                "Atención al Cliente",
                "Operaciones",
                "Tesorería",
                "Contabilidad",
                "Sistemas",
                "Recursos Humanos",
                "Gerencia"
        );

        cargarEmpleados();

        // Mostrar imagen del empleado seleccionado
        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                mostrarImagenEmpleado(newValue);
            } else {
                imgEmpleado.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMAGEN_POR_DEFECTO))));
            }
        });

        // Configurar filtrado dinámico
        listaEmpleados = FXCollections.observableArrayList(empleadoRepository.getEmpleados());
        FilteredList<Empleado> listaFiltrada = new FilteredList<>(listaEmpleados);
        tablaEmpleados.setItems(listaFiltrada);

        // Filtro de búsqueda: se actualiza con cada cambio de texto
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> listaFiltrada.setPredicate(emp -> {
            if (newVal == null || newVal.isEmpty()) return true;
            String filtro = newVal.toLowerCase();
            return emp.getNombre().toLowerCase().contains(filtro)
                    || emp.getApellido().toLowerCase().contains(filtro)
                    || emp.getCargo().toLowerCase().contains(filtro);

        }));
    }

    /**
     * Carga la lista completa de empleados desde el repositorio y la asigna a la tabla.
     */
    private void cargarEmpleados(){
        listaEmpleados = FXCollections.observableArrayList(empleadoRepository.getEmpleados());
        tablaEmpleados.setItems(listaEmpleados);
    }

    /**
     * Elimina el empleado actualmente seleccionado.
     *
     * Si no hay selección, muestra una alerta.
     * Si la eliminación es exitosa, se elimina también de la lista de la tabla.
     */
    @FXML
    private void eliminarEmpleado() {
        Empleado seleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Por favor seleccione un empleado para eliminar");
            return;
        }

        if (adminService.eliminarEmpleado(seleccionado)) {
            listaEmpleados.remove(seleccionado);
            mostrarAlerta("Éxito", "Empleado eliminado correctamente.", Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Permite editar la información del empleado seleccionado.
     *
     * Carga sus datos en los campos del formulario de edición,
     * incluyendo la contraseña, y cambia la vista de tabla a la vista de edición.
     */
    @FXML
    private void editarEmpleado(){
            empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();

            if (empleadoSeleccionado == null) {
                mostrarAlerta("Por favor seleccione un empleado para editar");
                return;
            }

            // Llenar los campos del formulario con los datos actuales
            txtNombre.setText(empleadoSeleccionado.getNombre());
            txtApellido.setText(empleadoSeleccionado.getApellido());
            txtCorreo.setText(empleadoSeleccionado.getCorreo());
            txtCargo.setText(empleadoSeleccionado.getCargo());
            txtCedula.setText(empleadoSeleccionado.getDocumento());
            txtTelefono.setText(empleadoSeleccionado.getTelefono());
            txtCiudad.setText(empleadoSeleccionado.getCiudad());
            txtSalario.setText(String.valueOf(empleadoSeleccionado.getSalario()));
            cmbDepartamento.setValue(empleadoSeleccionado.getDepartamento());

            // Manejo de contraseña visible/oculta ---
            String contraseniaActual = empleadoSeleccionado.getPersona().getContrasenia();

            // Mostrar la contraseña tanto en el PasswordField como en el TextField
            txtContrasenia.setText(contraseniaActual);
            txtContraseniaVisible.setText(contraseniaActual);

            // Por defecto ocultar el campo visible (mostrar el PasswordField)
            txtContraseniaVisible.setVisible(false);
            txtContraseniaVisible.setManaged(false);
            txtContrasenia.setVisible(true);
            txtContrasenia.setManaged(true);

            // Asegurarnos de que el checkbox esté desmarcado
            chkMostrarContrasenia.setSelected(false);

            // Mostrar el panel de edición
            vistaTabla.setVisible(false);
            vistaTabla.setManaged(false);
            vistaEdicion.setVisible(true);
            vistaEdicion.setManaged(true);
    }

    /**
     * Guarda los cambios realizados en el formulario de edición.
     *
     * Los valores se obtienen de los campos del formulario y se envían al `AdminService`,
     * que actualiza tanto la información personal como los datos laborales del empleado.
     *
     * También actualiza la tabla y regresa a la vista principal.
     */
    @FXML
    private void guardarEdicion() {
        if (empleadoSeleccionado != null) {
            // Obtener la contraseña dependiendo del modo visible
            String nuevaContrasenia = chkMostrarContrasenia.isSelected()
                    ? txtContraseniaVisible.getText()
                    : txtContrasenia.getText();

            // Actualizar datos personales
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String ciudad = txtCiudad.getText();
            String cargo = txtCargo.getText().trim().toUpperCase();
            String departamento = cmbDepartamento.getValue();
            double salario = Double.parseDouble(txtSalario.getText());
            String contrasenia = chkMostrarContrasenia.isSelected()
                    ? txtContraseniaVisible.getText()
                    : txtContrasenia.getText();

            // Determinar rol según el cargo
            RolUsuario nuevoRol = adminService.determinarRolPorCargo(txtCargo.getText());

            adminService.actualizarEmpleado(
                    empleadoSeleccionado,
                    nombre, apellido, correo, telefono, ciudad,
                    cargo, departamento, salario, contrasenia, nuevoRol
            );

            tablaEmpleados.refresh();
            mostrarAlerta("Éxito", "Empleado actualizado correctamente", Alert.AlertType.INFORMATION);
            cancelarEdicion();

            // Refrescar tabla y mostrar confirmación
            tablaEmpleados.refresh();
            mostrarAlerta("Éxito", "Empleado actualizado correctamente", Alert.AlertType.INFORMATION);

            // Volver a la vista de tabla
            cancelarEdicion();
        } else {
            mostrarAlerta("No hay empleado seleccionado para editar");
        }
    }

    /**
     * Muestra la imagen del empleado seleccionado en la vista.
     *
     * Si no tiene foto personalizada, se muestra una imagen predeterminada.
     * Soporta imágenes tanto del sistema de archivos como del classpath del proyecto.
     */

    private void mostrarImagenEmpleado(Empleado empleado) {
        try {
            String rutaFoto = empleado.getPersona().getFoto();

            if (rutaFoto == null || rutaFoto.isBlank()) {
                imgEmpleado.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMAGEN_POR_DEFECTO))));
                return;
            }

            Image imagen;
            // Carga desde el classpath o desde el disco local
            if (rutaFoto.startsWith("/")) {
                imagen = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream(rutaFoto)
                ));
            } else {
                File archivo = new File(rutaFoto);
                imagen = archivo.exists()
                        ? new Image(archivo.toURI().toString())
                        : new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMAGEN_POR_DEFECTO)));
            }

            imgEmpleado.setImage(imagen);

        } catch (Exception e) {
            System.err.println(" No se pudo cargar la imagen del empleado: " + e.getMessage());
            imgEmpleado.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMAGEN_POR_DEFECTO))));
        }
    }

    /**
     * Cancela la edición y regresa a la vista principal de la tabla.
     */
    @FXML
    private void cancelarEdicion(){
        vistaEdicion.setVisible(false);
        vistaEdicion.setManaged(false);

        vistaTabla.setManaged(true);
        vistaTabla.setVisible(true);
    }

    /**
     * Alterna entre mostrar y ocultar la contraseña en el formulario de edición.
     *
     * Se realiza intercambiando la visibilidad entre el campo `PasswordField` y el `TextField`.
     *
     * Este enfoque permite ver el texto de la contraseña sin perder la funcionalidad de ocultarla.
     */
    @FXML
    private void mostrarContrasenia() {
        boolean mostrar = chkMostrarContrasenia.isSelected();
        txtContraseniaVisible.setVisible(mostrar);
        txtContraseniaVisible.setManaged(mostrar);
        txtContrasenia.setVisible(!mostrar);
        txtContrasenia.setManaged(!mostrar);

        if (mostrar) {
            txtContraseniaVisible.setText(txtContrasenia.getText());
        } else {
            txtContrasenia.setText(txtContraseniaVisible.getText());
        }
    }
}

