package triplej.banco.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import triplej.banco.Controllers.VistaAdmin.AdminController;
import triplej.banco.Controllers.VistaCajero.CajeroController;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;
/**
 * Controlador de la vista de inicio de sesión del sistema UQBank.
 * <p>
 * Esta clase gestiona la autenticación de usuarios (Administrador, Cajero o Cliente),
 * validando las credenciales ingresadas y redirigiendo a la ventana correspondiente
 * según el rol asignado al usuario.
 * </p>
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Validar las credenciales de acceso contra los datos almacenados.</li>
 *     <li>Activar la sesión del usuario autenticado.</li>
 *     <li>Abrir la vista correspondiente al rol del usuario.</li>
 *     <li>Permitir la visualización opcional de la contraseña ingresada.</li>
 * </ul>
 *
 * <p>Vista asociada: <b>Login-view.fxml</b></p>
 */
public class LoginController {

    //Campos de la vista
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasenia;
    @FXML private TextField txtPasswordSignInMask;
    @FXML private CheckBox checkViewPassSignIn;

    //Repositorio de la gestión de usuarios
    private UsuarioRepository usuarioRepository;
    //Repositorio de la gestión de empleados
    private EmpleadoRepository empleadoRepository;

    /**
     * Inicializa los componentes y repositorios al cargar la vista de login.
     * Obtiene las instancias de {@link UsuarioRepository} y {@link EmpleadoRepository}
     * a partir del banco singleton, y configura la opción de mostrar/ocultar contraseña.
     */
    @FXML
    public  void initialize(){
        usuarioRepository = UsuarioRepository.getInstancia();
        empleadoRepository = EmpleadoRepository.getInstancia();
        System.out.println(usuarioRepository.getUsuarios().size());
        mostrarContrasenia(txtContrasenia,txtPasswordSignInMask,checkViewPassSignIn);

    }

    /**
     * Procesa el intento de inicio de sesión con las credenciales ingresadas.
     * <p>
     * Valida la existencia del usuario y la coincidencia de la contraseña.
     * Si el acceso es correcto, redirige a la vista correspondiente según el rol.
     * </p>
     *
     * @param event evento de acción generado al presionar el botón "Iniciar sesión"
     */
    @FXML
    private void login(ActionEvent event) {
        String correo = txtCorreo.getText();
        String contrasenia = txtContrasenia.getText();

        Optional<Usuario> usuarioOpt = usuarioRepository.buscarUsuarioPorCorreo(correo);

        if (usuarioOpt.isEmpty()) {
            mostrarAlerta("Usuario no encontrado");
            return;
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getContrasenia().equals(contrasenia)) {
            mostrarAlerta("Contraseña incorrecta");
            return;
        }

        usuario.setActivo(true);
        usuarioRepository.actualizarUsuario(usuario);

        // Login exitoso: abrir ventana según rol
        switch (usuario.getRolUsuario()) {
            case ADMIN -> abrirVentanaAdmin(usuario);
            case CLIENTE -> abrirVentanaCliente(usuario);
            case CAJERO -> abrirVentanaCajero(usuario);
        }

        // cerrar ventana de login
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        stage.close();
    }

    /**
     * Habilita la funcionalidad de mostrar u ocultar la contraseña según el estado del checkbox.
     *
     * @param pass  campo de tipo PasswordField (oculto)
     * @param text  campo de tipo TextField (visible)
     * @param check checkbox que controla la visibilidad
     */
    public void mostrarContrasenia(PasswordField pass, TextField text, CheckBox check){
        text.setVisible(false);
        text.setManaged(false);

        text.managedProperty().bind(check.selectedProperty());
        text.visibleProperty().bind(check.selectedProperty());

        text.textProperty().bindBidirectional(pass.textProperty());
    }

    /**
     * Limpia los campos de texto del formulario de inicio de sesión.
     *
     * @param event evento de acción generado al presionar el botón "Limpiar"
     */
    @FXML
    private void limpiar (ActionEvent event){
        txtCorreo.clear();
        txtContrasenia.clear();
    }

    /**
     * Abre la ventana principal del rol <b>Administrador</b>.
     * Carga el controlador {@link AdminController} y asigna el empleado correspondiente.
     *
     * @param usuario usuario autenticado con rol ADMIN
     */
    private void abrirVentanaAdmin(Usuario usuario) {
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/AdminViews/Admin-view.fxml"));
            Parent root = loader.load();

            Optional<Usuario> adminExistente = usuarioRepository.buscarUsuarioPorCorreo(usuario.getCorreo());

            Usuario admin;

            if (adminExistente.isPresent()) {
                admin = adminExistente.get();
                System.out.println("Admin existente encontrado: " + admin.getNombreUsuario());

                AdminController adminController = loader.getController();
                adminController.setAdmin(admin);

                Scene scene=new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/triplej/banco/Styles/admin.css")).toExternalForm());

                Stage stage = new Stage();
                stage.setTitle("Sistema de Administración UQBANK");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            }

        }
        catch (IOException e){
            throw new RuntimeException("Error al abrir la ventana del admin: " + e.getMessage(), e);
        }
        System.out.println("Admin: " + usuario.getNombreUsuario() + " inició sesión");

    }

    /**
     * Abre la ventana principal del rol <b>Cajero</b>.
     * Carga el controlador {@link CajeroController} y asigna el empleado correspondiente.
     *
     * @param usuario usuario autenticado con rol CAJERO
     */
    private void abrirVentanaCajero(Usuario usuario) {
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/CajeroViews/Cajero-view.fxml"));
            Parent root = loader.load();

            Optional<Empleado> empleadoExistente = empleadoRepository.buscarPorCorreo(usuario.getCorreo());

            Empleado empleado;

            if (empleadoExistente.isPresent()) {
                empleado = empleadoExistente.get();
                System.out.println("Cliente existente encontrado: " + empleado.getNombre());

                CajeroController cajeroController = loader.getController();
                cajeroController.setCajero(empleado);

                Scene scene=new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/triplej/banco/Styles/cajero.css")).toExternalForm());

                Stage stage = new Stage();
                stage.setTitle("Sistema de Cajero UQBANK");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            }
        }
        catch (IOException e){
            throw new RuntimeException("Error al abrir la ventana del cajero " + e.getMessage(), e);
        }
        System.out.println("Cajero: " + usuario.getNombreUsuario() + " inició sesión");

    }


    /**
     * Abre la ventana principal del rol <b>Cliente</b>.
     * Si el cliente no existe en el repositorio, se crea automáticamente una nueva instancia.
     *
     * @param usuario usuario autenticado con rol CLIENTE
     */
    private void abrirVentanaCliente(Usuario usuario) {
        try {
            ClienteRepository clienteRepo = ClienteRepository.getInstancia();

            // Buscar cliente existente por correo
            Optional<Cliente> clienteExistente = clienteRepo.buscarPorCorreo(usuario.getCorreo());

            Cliente cliente;

            if (clienteExistente.isPresent()) {
                cliente = clienteExistente.get();
                System.out.println("Cliente existente encontrado: " + cliente.getNombre());
            } else {
                cliente = new Cliente((Persona) usuario);
                clienteRepo.guardar(cliente);
                System.out.println("Nuevo cliente creado y guardado: " + cliente.getNombre());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/Cliente-view.fxml"));
            Parent root = loader.load();

            ClienteController clienteController = loader.getController();
            clienteController.setCliente(cliente);


            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/triplej/banco/Styles/cliente.css")).toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle("UQ Bank");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException("Error al abrir la ventana del cliente: " + e.getMessage(), e);
        }

        System.out.println("Cliente " + usuario.getNombreUsuario() + " inició sesión");
    }

}
