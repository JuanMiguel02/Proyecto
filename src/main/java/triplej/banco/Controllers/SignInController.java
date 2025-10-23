package triplej.banco.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.IOException;
import java.util.Optional;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class SignInController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasenia;

    private UsuarioRepository usuarioRepo = UsuarioRepository.getInstancia();

    @FXML
    public  void initialize(){
        usuarioRepo = UsuarioRepository.getInstancia();
        EmpleadoRepository empleadoRepository = EmpleadoRepository.getInstance();
        ClienteRepository clienteRepository = ClienteRepository.getInstancia();
    }

    @FXML
    private void login(ActionEvent event) {
        String correo = txtCorreo.getText();
        String contrasenia = txtContrasenia.getText();

        Optional<Usuario> usuarioOpt = usuarioRepo.buscarUsuarioPorEmail(correo);

        if (usuarioOpt.isEmpty()) {
            mostrarAlerta("Usuario no encontrado");
            return;
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getContrasenia().equals(contrasenia)) {
            mostrarAlerta("Contraseña incorrecta");
            return;
        }

        // Login exitoso: abrir ventana según rol
        switch (usuario.getRolUsuario()) {
            case EMPLEADO -> abrirVentanaEmpleado(usuario);
            case CLIENTE -> abrirVentanaCliente(usuario);
            // puedes agregar ADMIN, etc.
        }

        // cerrar ventana de login
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        stage.close();
    }

    private void abrirVentanaEmpleado(Usuario usuario) {
        // Aquí cargas el FXML de la ventana de empleado
        // Por simplicidad, mostramos un mensaje
        System.out.println("Empleado " + usuario.getNombreCompleto() + " inició sesión");
    }

    private void abrirVentanaCliente(Usuario usuario) {
        try{
            Cliente cliente = new Cliente(usuario);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/Cliente-view.fxml"));
            Parent root = loader.load();

            ClienteController clienteController = loader.getController();
            clienteController.setCliente(cliente);

            Stage stage = new Stage();
            stage.setTitle("Cliente");
            stage.setScene(new Scene(root));
            stage.show();

        }
        catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Cliente " + usuario.getNombreCompleto() + " inició sesión");
    }
}
