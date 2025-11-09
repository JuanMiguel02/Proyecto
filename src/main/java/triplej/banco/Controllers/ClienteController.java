package triplej.banco.Controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import triplej.banco.Models.Banco;
import triplej.banco.Services.CajeroService;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.ReporteGenerado;

import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.IOException;
import java.util.Optional;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;
import static triplej.banco.Utils.GeneracionReporteVista.generarReporte;

public class ClienteController {
    private Cliente cliente;
    @FXML private Label lblNombre;
    @FXML private Label lblDinero;
    @FXML private Label lblNumCuenta;
    @FXML private Button btnSalir;
    @FXML private ComboBox<CuentaBancaria> cmbCuentas;
    private ObservableList<CuentaBancaria> cuentasCliente = FXCollections.observableArrayList();

    private final CajeroService cajeroService = new CajeroService();

    @FXML
    public void initialize() {
        Banco banco = Banco.getInstancia();
        UsuarioRepository usuarioRepository = banco.getUsuarioRepository();

        // Mostrar saldo y número de cuenta al seleccionar una
        cmbCuentas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, nuevaCuenta) -> {
            if (nuevaCuenta != null) {
                lblDinero.setText(String.format("$%,.2f", nuevaCuenta.getSaldo()));
                lblNumCuenta.setText(String.valueOf(nuevaCuenta.getNumeroCuenta()));
            }
        });

        // Seleccionar la primera por defecto
        if (!cuentasCliente.isEmpty()) {
            cmbCuentas.getSelectionModel().selectFirst();
        }

    }

    public void setCliente(Cliente cliente) {
        ClienteRepository repo = ClienteRepository.getInstancia();

        // Buscar si ya existe un cliente en memoria o en archivo
        Optional<Cliente> clienteExistente = repo.buscarPorCorreo(cliente.getUsuarioAsociado().getCorreo());

        if (clienteExistente.isPresent()) {
            this.cliente = clienteExistente.get();
            System.out.println(" Cliente encontrado en el archivo, usando datos persistentes.");
        } else {
            // No existe -> crear uno nuevo
            this.cliente = cliente;
            System.out.println(" Cliente nuevo, creando cuenta de ahorro...");
            CuentaBancaria cuentaActiva = new CuentaAhorro(cliente);
            cliente.agregarCuenta(cuentaActiva);
            cliente.setCuentaActiva(cuentaActiva);
            repo.guardar(cliente);
        }

        //  Si el cliente ya existía, pero no tiene cuenta activa, creamos una
        if (this.cliente.getCuentaActiva() == null) {
            System.out.println(" Cliente sin cuenta activa, generando una nueva...");
            CuentaBancaria cuentaActiva = new CuentaAhorro(this.cliente);
            this.cliente.agregarCuenta(cuentaActiva);
            this.cliente.setCuentaActiva(cuentaActiva);
            repo.guardar(this.cliente);
        }

        // Mostrar datos en interfaz
        lblNombre.setText(this.cliente.getNombre());
        lblDinero.setText(String.format("%.2f", this.cliente.getCuentaActiva().getSaldo()));
        lblNumCuenta.setText(this.cliente.getCuentaActiva().getNumeroCuenta());

        cuentasCliente.setAll(ClienteRepository.getInstancia().buscarCuentasDeCliente(this.cliente));
        cmbCuentas.setItems(cuentasCliente);

        // Seleccionar la primera cuenta
        if (!cuentasCliente.isEmpty()) {
            cmbCuentas.getSelectionModel().selectFirst();
        }
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
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/Login-view.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Inicio");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            ((Stage) btnSalir.getScene().getWindow()).close();

        }
        catch (IOException e){
            throw new RuntimeException("Error al volver al menú " + e.getMessage(), e);
        }
    }

    private void actualizarInterfaz() {
        // Actualizar desde la cuenta activa para asegurar datos frescos
        double saldoActual = cliente.getCuentaActiva().getSaldo();
        lblDinero.setText(String.format("$%,.2f", saldoActual));

        System.out.println(" Interfaz actualizada - Saldo: " + saldoActual);
    }
}
