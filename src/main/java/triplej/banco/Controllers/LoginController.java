package triplej.banco.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import triplej.banco.Controllers.VistaAdmin.AdminController;
import triplej.banco.Controllers.VistaCajero.CajeroController;
import triplej.banco.Models.Banco;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.IOException;
import java.util.Optional;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasenia;

    private UsuarioRepository usuarioRepo;
    private EmpleadoRepository empleadoRepository;

    @FXML
    public  void initialize(){
        Banco banco = Banco.getInstancia();
        usuarioRepo = banco.getUsuarioRepository();
        empleadoRepository = banco.getEmpleadoRepository();
        System.out.println(usuarioRepo.getUsuarios().size());
    }

    @FXML
    private void login(ActionEvent event) {
        String correo = txtCorreo.getText();
        String contrasenia = txtContrasenia.getText();

        Optional<Usuario> usuarioOpt = usuarioRepo.buscarUsuarioPorCorreo(correo);

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
            case CAJERO -> abrirVentanaCajero(usuario);
        }

        // cerrar ventana de login
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        stage.close();
    }

    private void abrirVentanaAdmin(Usuario usuario) {
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/AdminViews/Admin-view.fxml"));
            Parent root = loader.load();

            Optional<Empleado> adminExistente = empleadoRepository.buscarPorCorreo(usuario.getCorreo());

            Empleado admin;

            if (adminExistente.isPresent()) {
                admin = adminExistente.get();
                System.out.println("Admin existente encontrado: " + admin.getNombre());

                AdminController adminController = loader.getController();
                adminController.setAdmin(admin);

                Stage stage = new Stage();
                stage.setTitle("Sistema de Administración UQBANK");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();
            }

        }
        catch (IOException e){
            throw new RuntimeException("Error al abrir la ventana del admin: " + e.getMessage(), e);
        }
        System.out.println("Admin " + usuario.getNombreCompleto() + " inició sesión");

    }

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

                Stage stage = new Stage();
                stage.setTitle("Sistema de Trabajo UQBANK");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();
            }
        }
        catch (IOException e){
            throw new RuntimeException("Error al abrir la ventana del cajero " + e.getMessage(), e);
        }
        System.out.println("cajero" + usuario.getNombreCompleto() + " inició sesión");

    }

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

            Stage stage = new Stage();
            stage.setTitle("UQ Bank");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException("Error al abrir la ventana del cliente: " + e.getMessage(), e);
        }

        System.out.println("Cliente " + usuario.getNombreCompleto() + " inició sesión");
    }

}
