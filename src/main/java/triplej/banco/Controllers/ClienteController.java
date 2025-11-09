package triplej.banco.Controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import triplej.banco.Models.Banco;
import triplej.banco.Services.CajeroService;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.ReporteGenerado;

import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.VolverLogin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;
import static triplej.banco.Utils.GeneracionReporteVista.generarReporte;

public class ClienteController {
    private Cliente cliente;

    @FXML private ImageView imgCliente;

    @FXML private Label lblNombre;
    @FXML private Label lblDinero;
    @FXML private Label lblNumCuenta;
    @FXML private Button btnSalir;
    @FXML private ComboBox<CuentaBancaria> cmbCuentas;
    private final ObservableList<CuentaBancaria> cuentasCliente = FXCollections.observableArrayList();

    private final CajeroService cajeroService = new CajeroService();

    @FXML
    public void initialize() {
        // Configurar listener de selección de cuenta
        cmbCuentas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, nuevaCuenta) -> {
            if (nuevaCuenta != null) {
                lblDinero.setText(String.format("$%,.2f", nuevaCuenta.getSaldo()));
                lblNumCuenta.setText(String.valueOf(nuevaCuenta.getNumeroCuenta()));
            }
        });

        // Si el cliente ya está seteado antes de initialize(), lo cargamos
        if (cliente != null) {
            cargarCliente();
        }
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        // Si el FXML ya está cargado, cargamos datos
        if (imgCliente != null) {
            cargarCliente();
        }
    }

    private void cargarCliente() {
        ClienteRepository repo = ClienteRepository.getInstancia();

        // Buscar cliente existente
        Optional<Cliente> clienteExistente = repo.buscarPorCorreo(cliente.getUsuarioAsociado().getCorreo());

        if (clienteExistente.isPresent()) {
            this.cliente = clienteExistente.get();
            System.out.println(" Cliente encontrado en archivo, usando datos persistentes.");
        } else {
            this.cliente = cliente;
            System.out.println(" Cliente nuevo, creando cuenta...");
            CuentaBancaria cuentaActiva = new CuentaAhorro(cliente);
            cliente.agregarCuenta(cuentaActiva);
            cliente.setCuentaActiva(cuentaActiva);
            repo.guardar(cliente);
        }

        if (this.cliente.getCuentaActiva() == null) {
            CuentaBancaria cuentaActiva = new CuentaAhorro(this.cliente);
            this.cliente.agregarCuenta(cuentaActiva);
            this.cliente.setCuentaActiva(cuentaActiva);
            repo.guardar(this.cliente);
        }

        //  Mostrar imagen del cliente
        mostrarImagenCliente();

        //  Mostrar datos básicos
        lblNombre.setText(this.cliente.getNombre());
        lblDinero.setText(String.format("$%,.2f", this.cliente.getCuentaActiva().getSaldo()));
        lblNumCuenta.setText(this.cliente.getCuentaActiva().getNumeroCuenta());

        //  Cargar cuentas
        cuentasCliente.setAll(ClienteRepository.getInstancia().buscarCuentasDeCliente(this.cliente));
        cmbCuentas.setItems(cuentasCliente);

        if (!cuentasCliente.isEmpty()) {
            cmbCuentas.getSelectionModel().selectFirst();
        }
    }

    private void mostrarImagenCliente() {
        try {
            String rutaFoto = cliente.getFoto();

            if (rutaFoto != null && !rutaFoto.isBlank()) {
                if (rutaFoto.startsWith("/")) {
                    imgCliente.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(rutaFoto))));
                } else {
                    Path path = Paths.get(rutaFoto);
                    if (Files.exists(path)) {
                        imgCliente.setImage(new Image(path.toUri().toString()));
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + e.getMessage());
        }

        // Imagen por defecto
        imgCliente.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/triplej/banco/Images/avatar.png"))));
    }

    public Cliente getCliente(){
        return cliente;
    }


    @FXML
    private void onTransferir(){

    }

//    @FXML
//    private void verTransacciones(){
//        if(cliente == null || cliente.getCuentaActiva() == null){
//            mostrarAlerta("No se encontró la cuenta activa del cliente");
//            return;
//        }
//        ReporteGenerado reporte = cajeroService.generarReporteCliente(cliente.getCuentaActiva());
//
//        generarReporte(reporte, txtContenido, vistaTransacciones, contenedorCentro);
//    }
//
//
//    @FXML
//    private void mostrarInicio() {
//        contenedorCentro.getChildren().clear();
//        vistaInicio.setVisible(true);
//        vistaInicio.setManaged(true);
//        contenedorCentro.getChildren().add(vistaInicio);
//    }


    @FXML
    private void volverMenu(){
        cliente.getUsuarioAsociado().setActivo(false);
        UsuarioRepository.getInstancia().actualizarUsuario(cliente.getUsuarioAsociado());

        Stage ventanaActual = (Stage) btnSalir.getScene().getWindow();
        VolverLogin.volverLogin(ventanaActual);
    }

    private void actualizarInterfaz() {
        // Actualizar desde la cuenta activa para asegurar datos frescos
        double saldoActual = cliente.getCuentaActiva().getSaldo();
        lblDinero.setText(String.format("$%,.2f", saldoActual));

        System.out.println(" Interfaz actualizada - Saldo: " + saldoActual);
    }
}
