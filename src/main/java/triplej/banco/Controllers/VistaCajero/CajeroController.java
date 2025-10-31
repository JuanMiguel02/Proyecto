package triplej.banco.Controllers.VistaCajero;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import triplej.banco.Controllers.SignInController;
import triplej.banco.Controllers.VistaAdmin.AdminController;
import triplej.banco.Models.Cajero.Cajero;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.ReporteGenerado;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.Empleado;
import triplej.banco.Repositories.ClienteRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;
import static triplej.banco.Utils.GeneracionReporteVista.generarReporte;

public class CajeroController {

    @FXML private StackPane contenedorCentro;
    @FXML private AnchorPane vistaInicio;
    @FXML private AnchorPane vistaReporte;

    //  Campos de búsqueda y selección
    @FXML private TextField txtBusquedaCliente;
    @FXML private Button btnBuscarCliente;
    @FXML private ComboBox<CuentaBancaria> cmbCuentasCliente;

    //  Campos de información del cliente
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtTipoDocumento;
    @FXML private TextField txtNumeroDocumento;
    @FXML private TextArea  txtReporteGeneral;
    @FXML private ImageView imgFotoCliente;

    @FXML private Label lblNombre;

    // Botones principales
    @FXML private Button btnDepositar;
    @FXML private Button btnRetirar;
    @FXML private Button btnTransferir;
    @FXML private Button btnSalir;

    private final ClienteRepository repoClientes = ClienteRepository.getInstancia();
    private final Cajero cajero = new Cajero();
    private Cliente clienteActual;
    private CuentaBancaria cuentaSeleccionada;

    //  Inicialización
    @FXML
    public void initialize() {
        cmbCuentasCliente.setOnAction(e -> seleccionarCuenta());

    }

    public void setCajero(Empleado cajero) {
        if (lblNombre != null && cajero != null) {
            lblNombre.setText(cajero.getNombreCompleto());
        }
    }

    //  Buscar cliente por documento
    @FXML
    private void buscarCliente() {
        String documento = txtBusquedaCliente.getText().trim();

        if (documento.isEmpty()) {
            mostrarAlerta("Error", "Ingrese un número de documento.", Alert.AlertType.WARNING);
            return;
        }

        Optional<Cliente> clienteOpt = repoClientes.buscarPorDocumento(documento);


        if (clienteOpt.isEmpty()) {
            mostrarAlerta("No encontrado", "No se encontró ningún cliente con ese documento.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            return;
        }

        clienteActual = clienteOpt.get();
        cargarDatosCliente();
    }

    //  Cargar información del cliente encontrado
    private void cargarDatosCliente() {

        txtNombre.setText(clienteActual.getNombre());
        txtCorreo.setText(clienteActual.getCorreo());
        txtTelefono.setText(clienteActual.getTelefono());
        txtCiudad.setText(clienteActual.getCiudad());
        txtTipoDocumento.setText(clienteActual.getTipoDocumento());
        txtNumeroDocumento.setText(clienteActual.getDocumento());

//        // Si tiene foto
//        if (clienteActual.getFoto() != null && !clienteActual.getFoto().isBlank()) {
//            try {
//                imgFotoCliente.setImage(new Image(clienteActual.getFoto()));
//            } catch (Exception e) {
//                imgFotoCliente.setImage(new Image(getClass().getResourceAsStream("/triplej/banco/Images/avatar.png")));
//            }
//        } else {
//            imgFotoCliente.setImage(new Image(getClass().getResourceAsStream("/triplej/banco/Images/avatar.png")));
//        }

        // Llenar ComboBox con las cuentas del cliente
        cmbCuentasCliente.getItems().clear();
        cmbCuentasCliente.getItems().addAll(clienteActual.getCuentas());
        if (!clienteActual.getCuentas().isEmpty()) {
            cmbCuentasCliente.getSelectionModel().selectFirst();
            cuentaSeleccionada = cmbCuentasCliente.getValue();
        }
    }

    //  Limpiar campos
    private void limpiarCampos() {
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtCiudad.clear();
        cmbCuentasCliente.getItems().clear();
//        imgFotoCliente.setImage(new Image(getClass().getResourceAsStream("/triplej/banco/Images/avatar.png")));
        clienteActual = null;
        cuentaSeleccionada = null;
    }

    // Seleccionar cuenta activa
    private void seleccionarCuenta() {
        cuentaSeleccionada = cmbCuentasCliente.getValue();
        if (cuentaSeleccionada != null) {
            System.out.println("Cuenta seleccionada: " + cuentaSeleccionada.getNumeroCuenta());
        }
    }

    //  Depositar
    @FXML
    private void onDepositar() {
        if (cuentaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una cuenta para operar.", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Depósito");
        dialog.setHeaderText("Depositar dinero en la cuenta " + cuentaSeleccionada.getNumeroCuenta());
        dialog.setContentText("Ingrese el monto a depositar:");

        dialog.showAndWait().ifPresent(valor -> {
            try {
                double monto = Double.parseDouble(valor);
                cajero.realizarDeposito(cuentaSeleccionada, monto, "Depósito por cajero");
                ClienteRepository.getInstancia().guardar(clienteActual);
                mostrarAlerta("Éxito", "Depósito realizado correctamente.", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Monto inválido.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void onConsultarSaldo(){
        if (cuentaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una cuenta para operar.", Alert.AlertType.WARNING);
            return;
        }
        double saldo = cajero.consultarSaldo(cuentaSeleccionada);

        Stage ventanaSaldo = new Stage();
        ventanaSaldo.setTitle("Saldo de la cuenta");

        Label lblCuenta = new Label("Cliente: " + clienteActual.getNombre() + " Cuenta: " + cuentaSeleccionada.getNumeroCuenta());
        Label lblSaldo = new Label("Saldo actual: $" + String.format("%,.2f", saldo));
        lblCuenta.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        lblSaldo.setStyle("-fx-font-size: 20px; -fx-text-fill: green;");

        VBox layout = new VBox(15, lblCuenta, lblSaldo);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 250);
        ventanaSaldo.setScene(scene);
        ventanaSaldo.show();
    }


    @FXML
    private void volverMenu(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/Login-view.fxml"));
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
            throw new RuntimeException("Error al volver al menú " + e.getMessage(), e);
        }
    }

    private void cargarVistaEnCentro(String fxmlRuta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent vista = loader.load();

            Object controller = loader.getController();

            try {
                controller.getClass()
                        .getMethod("setAdminController", AdminController.class)
                        .invoke(controller, this);
            }  catch (Exception ignored) {}


            // Limpiar el StackPane y agregar la nueva vista
            contenedorCentro.getChildren().clear();
            contenedorCentro.getChildren().add(vista);

            // Ajustar anclajes si es AnchorPane
            if (vista != null) {
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la vista: " + e.getMessage(), e);
        }
    }

    @FXML
    private void onGenerarReporte(){
        if(clienteActual == null || clienteActual.getCuentaActiva() == null){
            mostrarAlerta("No se encontró la cuenta activa del cliente");
            return;
        }
        ReporteGenerado reporte = cajero.generarReporteCliente(clienteActual.getCuentaActiva());

        generarReporte(reporte, txtReporteGeneral, vistaReporte, contenedorCentro);
    }

    @FXML
    private void onGuardarReporte(){
        String contenido = txtReporteGeneral.getText();
        if(contenido == null || contenido.isBlank()){
            mostrarAlerta("No hay reporte para guardar");
            return;
        }
        exportarReporteTxt(contenido);
    }

    private void exportarReporteTxt(String contenido){
        try{
            Path ruta = Paths.get("Reportes Clientes", "ReporteCliente.txt");
            if(ruta.getParent() != null){
                Files.createDirectories(ruta.getParent());
            }
            Files.writeString(ruta, contenido);
            mostrarAlerta("Reporte Guardado","Reporte guardado exitosamente en: " + ruta.getFileName()
                    , Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void mostrarFormulario() {
        cargarVistaEnCentro("/triplej/banco/Views/CajeroViews/FormularioCliente-view.fxml");
    }

    @FXML
    public void mostrarInicio() {
        contenedorCentro.getChildren().clear();
        vistaInicio.setVisible(true);
        vistaInicio.setManaged(true);
        contenedorCentro.getChildren().add(vistaInicio);

    }
}
