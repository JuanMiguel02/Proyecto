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
import triplej.banco.Models.Banco;
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

    private UsuarioRepository usuarioRepo;

    @FXML
    public  void initialize(){
        Banco banco = Banco.getInstancia();
        usuarioRepo = banco.getUsuarioRepository();
        System.out.println(usuarioRepo.getUsuarios().size());
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
            case ADMIN -> abrirVentanaAdmin(usuario);
            case CLIENTE -> abrirVentanaCliente(usuario);
            // puedes agregar ADMIN, etc.
        }

        // cerrar ventana de login
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        stage.close();
    }

    private void abrirVentanaAdmin(Usuario usuario) {
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/Admin-view.fxml"));
            Parent root = loader.load();

            AdminController adminController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Administrador");
            stage.setScene(new Scene(root));
            stage.show();

        }
        catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Admin" + usuario.getNombreCompleto() + " inició sesión");

    }

    private void abrirVentanaCliente(Usuario usuario) {
        try {
            ClienteRepository clienteRepo = ClienteRepository.getInstancia();

            // Buscar cliente existente por correo
            Optional<Cliente> clienteExistente = clienteRepo.buscarPorEmail(usuario.getCorreo());

            Cliente cliente;

            if (clienteExistente.isPresent()) {
                cliente = clienteExistente.get();
                System.out.println("Cliente existente encontrado: " + cliente.getNombre());
            } else {
                cliente = new Cliente(usuario);
                clienteRepo.guardar(cliente);
                System.out.println("Nuevo cliente creado y guardado: " + cliente.getNombre());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/Cliente-view.fxml"));
            Parent root = loader.load();

            ClienteController clienteController = loader.getController();
            clienteController.setCliente(cliente);

            Stage stage = new Stage();
            stage.setTitle("Cliente");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Cliente " + usuario.getNombreCompleto() + " inició sesión");
    }

}
