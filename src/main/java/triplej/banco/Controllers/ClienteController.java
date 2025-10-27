package triplej.banco.Controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import triplej.banco.Models.Banco;
import triplej.banco.Models.Cajero.Cajero;
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
    @FXML private TextField txtNumCuenta;
    @FXML private TextField txtConfirmacion;
    @FXML private TextField txtValorDeposito;
    @FXML private TextArea txtContenido;
    @FXML private AnchorPane vistaInicio;
    @FXML private AnchorPane vistaDeposito;
    @FXML private AnchorPane vistaTransacciones;
    @FXML private StackPane contenedorCentro;
    private final Cajero cajero = new Cajero();

    @FXML
    public void initialize() {
        Banco banco = Banco.getInstancia();
        UsuarioRepository usuarioRepository = banco.getUsuarioRepository();
    }

    public void setCliente(Cliente cliente) {
        ClienteRepository repo = ClienteRepository.getInstancia();

        // Buscar si ya existe un cliente en memoria o en archivo
        Optional<Cliente> clienteExistente = repo.buscarPorEmail(cliente.getUsuarioAsociado().getCorreo());

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
    }

    public Cliente getCliente(){
        return cliente;
    }

    private void depositar(){
         cliente.getCuentaActiva().depositar(200000.00);
    }

    @FXML
    private void onDepositar(){
        vistaDeposito.setVisible(true);
        vistaDeposito.setManaged(true);

        contenedorCentro.getChildren().clear();
        contenedorCentro.getChildren().add(vistaDeposito);
    }

    @FXML
    private void confirmarDeposito(){
        String numCuenta = txtNumCuenta.getText();
        String confirmacion = txtConfirmacion.getText();
        double valor = Double.parseDouble(txtValorDeposito.getText());

        if(numCuenta.trim().isEmpty() || confirmacion.trim().isEmpty() || String.valueOf(valor).trim().isEmpty()){
            mostrarAlerta("Por favor rellene todos los campos");
            return;
        }
        if(!numCuenta.equals(confirmacion)) {
            mostrarAlerta("Los números de cuenta no coinciden");
            return;
        }
        if(txtValorDeposito.getText().isEmpty() || valor < 0){
            mostrarAlerta("Ingrese un valor válido");
            return;
        }

        Optional<CuentaBancaria> cuentaDestino = ClienteRepository.getInstancia().buscarCuentaPorNumero(numCuenta);
        if(!cuentaDestino.isPresent()) {
            mostrarAlerta("Error", "El número de cuenta no existe", Alert.AlertType.ERROR);
            return;
        }

        // 5. Realizar el depósito
        cuentaDestino.get().depositar(valor);

        // 6. Actualizar el repositorio para guardar el cambio
        ClienteRepository.getInstancia().guardar(cliente);

        actualizarInterfaz();

        mostrarAlerta("Éxito", "Depósito de: " + valor + " realizado exitosamente", Alert.AlertType.INFORMATION);
    }


    @FXML
    private void onRetirar(){

    }

    @FXML
    private void onTransferir(){

    }

    @FXML
    private void verTransacciones(){
        if(cliente == null || cliente.getCuentaActiva() == null){
            mostrarAlerta("No se encontró la cuenta activa del cliente");
            return;
        }
        ReporteGenerado reporte = cajero.generarReporteCliente(cliente.getCuentaActiva());

        generarReporte(reporte, txtContenido, vistaTransacciones, contenedorCentro);
    }


    @FXML
    private void mostrarInicio() {
        contenedorCentro.getChildren().clear();
        vistaInicio.setVisible(true);
        vistaInicio.setManaged(true);
        contenedorCentro.getChildren().add(vistaInicio);
    }


    @FXML
    private void volverMenu(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/SingIn-view.fxml"));
            Parent root = loader.load();

            SignInController signInController= loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Inicio");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            ((Stage) btnSalir.getScene().getWindow()).close();


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void actualizarInterfaz() {
        // Actualizar desde la cuenta activa para asegurar datos frescos
        double saldoActual = cliente.getCuentaActiva().getSaldo();
        lblDinero.setText(String.format("$%,.2f", saldoActual));

        System.out.println("🔄 Interfaz actualizada - Saldo: " + saldoActual);
    }
}
