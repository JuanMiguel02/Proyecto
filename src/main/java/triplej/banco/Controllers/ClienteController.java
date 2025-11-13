package triplej.banco.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import triplej.banco.Controllers.VistaCajero.TransferenciaController;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.VolverLogin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

/**
 * Controlador que gestiona la vista del cliente una vez inicia sesión.
 * Se encarga de mostrar los datos personales, las cuentas asociadas y el saldo.
 */
public class ClienteController {

    //Cliente cargado en la vista
    private Cliente cliente;

    //Datos del cliente
    @FXML private ImageView imgCliente;
    @FXML private Label lblNombre;
    @FXML private Label lblDinero;
    @FXML private Label lblNumCuenta;
    @FXML private Button btnSalir;
    @FXML private Button btnTransferir;
    @FXML private ComboBox<CuentaBancaria> cmbCuentas;

    private final ObservableList<CuentaBancaria> cuentasCliente = FXCollections.observableArrayList();
    private final ClienteRepository clienteRepository = ClienteRepository.getInstancia();

    /**
     * Inicializa la vista del cliente.
     * Configura el comportamiento del ComboBox para actualizar el saldo y número de cuenta.
     */
    @FXML
    public void initialize() {
        // Cuando el usuario selecciona una cuenta, se actualizan los datos mostrados.
        cmbCuentas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, nuevaCuenta) -> {
            if (nuevaCuenta != null) {
                lblDinero.setText(String.format("$%,.2f", nuevaCuenta.getSaldo()));
                lblNumCuenta.setText(String.valueOf(nuevaCuenta.getNumeroCuenta()));
            }
        });

        // Si el cliente ya fue seteado antes de initialize(), lo cargamos.
        if (cliente != null) {
            cargarCliente();
        }
    }

    /**
     * Recibe el cliente desde el controlador de login y carga sus datos.
     * @param cliente cliente autenticado.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        // Si los elementos FXML ya están cargados, se puede mostrar la información de inmediato.
        if (imgCliente != null) {
            cargarCliente();
        }
    }

    /**
     * Carga los datos del cliente en la interfaz: nombre, cuentas, saldo e imagen.
     */
    private void cargarCliente() {
        // Obtener la versión actual del cliente desde el repositorio

        this.cliente = clienteRepository.buscarPorCorreo(cliente.getUsuarioAsociado().getCorreo())
                .orElse(cliente);

        // Mostrar imagen
        mostrarImagenCliente();

        // Mostrar información general
        lblNombre.setText(cliente.getNombre());
        if (cliente.getCuentaPorNumero() != null) {
            lblDinero.setText(String.format("$%,.2f", cliente.getCuentaPorNumero().getSaldo()));
            lblNumCuenta.setText(cliente.getCuentaPorNumero().getNumeroCuenta());
        } else {
            lblDinero.setText("$0.00");
            lblNumCuenta.setText("Sin cuenta activa");
        }

        // Cargar las cuentas asociadas al cliente
        cuentasCliente.setAll(clienteRepository.buscarCuentasDeCliente(cliente));
        cmbCuentas.setItems(cuentasCliente);

        // Seleccionar automáticamente la primera cuenta si existe
        if (!cuentasCliente.isEmpty()) {
            cmbCuentas.getSelectionModel().selectFirst();
        }
    }

    /**
     * Muestra la imagen del cliente, cargándola desde la ruta guardada o una imagen por defecto.
     */
    private void mostrarImagenCliente() {
        try {
            String rutaFoto = cliente.getFoto();

            if (rutaFoto != null && !rutaFoto.isBlank()) {
                if (rutaFoto.startsWith("/")) {
                    // Imagen guardada en los recursos del proyecto
                    imgCliente.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(rutaFoto))));
                } else {
                    // Imagen guardada en el sistema de archivos del usuario
                    Path path = Paths.get(rutaFoto);
                    if (Files.exists(path)) {
                        imgCliente.setImage(new Image(path.toUri().toString()));
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del cliente: " + e.getMessage());
        }

        // Imagen por defecto si no hay ninguna guardada
        imgCliente.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/triplej/banco/Images/avatar.png"))));
    }

    /**
     * Permite cerrar la sesión del cliente y volver al login.
     * Se actualiza el estado del usuario como inactivo antes de salir.
     */
    @FXML
    private void volverMenu() {
        cliente.getUsuarioAsociado().setActivo(false);
        UsuarioRepository.getInstancia().actualizarUsuario(cliente.getUsuarioAsociado());

        Stage ventanaActual = (Stage) btnSalir.getScene().getWindow();
        VolverLogin.volverLogin(ventanaActual);
    }

    @FXML
    private void onTransferir() {
        CuentaBancaria cuentaSeleccionada = cmbCuentas.getSelectionModel().getSelectedItem();
        if (cuentaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una cuenta para operar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/CajeroViews/Transferencia-view.fxml"));
            Parent root = loader.load();

            TransferenciaController controller = loader.getController();
            controller.setDatosOperacion(cliente, cuentaSeleccionada);

            controller.setOnTransferenciaExitosa(this::actualizarInterfaz);


            Stage stage = new Stage();
            stage.setTitle("Déposito de dinero");
            stage.setScene(new Scene(root));
            stage.initOwner(btnTransferir.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de déposito.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Actualiza el saldo mostrado en pantalla, por ejemplo, tras una transacción.
     */
    private void actualizarInterfaz() {
        if (cliente.getCuentaPorNumero() != null) {
            double saldoActual = cliente.getCuentaPorNumero().getSaldo();
            lblDinero.setText(String.format("$%,.2f", saldoActual));
            System.out.println("Interfaz actualizada - Saldo: " + saldoActual);
        }
    }

    public Cliente getCliente() {
        return cliente;
    }

}
