package triplej.banco.Controllers.VistaCajero;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaCorriente;
import triplej.banco.Models.Cuentas.CuentaEmpresarial;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Services.CajeroService;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.Reporte;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.Empleado;
import triplej.banco.Models.Usuarios.PersonaJuridica;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Utils.VolverLogin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;
import static triplej.banco.Utils.GeneracionReporteVista.generarReporte;

/**
 * Controlador principal para la vista del Cajero en el sistema bancario.
 * Se encarga de gestionar las operaciones que un cajero puede realizar,
 * como buscar clientes, consultar sus cuentas, hacer depósitos, generar reportes
 * y registrar nuevas cuentas.

 * Esta clase conecta la interfaz de usuario (FXML) con la lógica de negocio
 * que reside en los servicios y repositorios del modelo.
 */
public class CajeroController {

    /** Empleado actual que ha iniciado sesión como cajero */
    private Empleado cajero;

    /** Contenedor principal donde se cargan las vistas internas */
    @FXML private StackPane contenedorCentro;

    /** Contenedor principal donde se cargan las vistas internas */
    @FXML private AnchorPane vistaInicio;

    /** Vista donde se muestra el contenido del reporte */
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
    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtTipoEmpresa;
    @FXML private TextField txtRepresentanteLegal;
    @FXML private TextArea  txtReporteGeneral;
    @FXML private ImageView imgCliente;

    // --- Etiquetas para mostrar nombres dinámicos ---
    @FXML private Label lblNombreCajero;
    @FXML private Label lblTipoEmpresa;
    @FXML private Label lblRepresentanteLegal;
    @FXML private Label lblRazonSocial;
    @FXML private Label lblDatoNombre;

    // Botones principales
    @FXML private Button btnDepositar;
    @FXML private Button btnRetirar;
    @FXML private Button btnTransferir;
    @FXML private Button btnSalir;

    /** Repositorio de clientes para acceder a los datos */
    private final ClienteRepository clienteRepository = ClienteRepository.getInstancia();

    /** Servicio del cajero que contiene la lógica de operaciones bancarias */
    private final CajeroService cajeroService = new CajeroService();

    /** Cliente actualmente seleccionado por el cajero */
    private Cliente clienteActual;

    /** Cuenta bancaria seleccionada del cliente */
    private CuentaBancaria cuentaSeleccionada;

    /**
     * Inicializa el controlador. Configura los eventos del ComboBox de cuentas.
     * Se ejecuta automáticamente al cargar la vista FXML.
     */
    @FXML
    public void initialize() {
        cmbCuentasCliente.setOnAction(e -> seleccionarCuenta());
    }

    /**
     * Asigna el cajero actual y muestra su nombre en la interfaz.
     * @param cajero empleado que ha iniciado sesión como cajero
     */
    public void setCajero(Empleado cajero) {
        this.cajero = cajero;
        if (lblNombreCajero != null && cajero != null) {
            lblNombreCajero.setText(cajero.getNombreCompleto());
        }
    }

    /**
     * Busca un cliente en el repositorio usando su número de documento o número de cuenta.
     * Si no se encuentra, se muestra una alerta.
     */
    @FXML
    private void buscarCliente() {
        String busqueda = txtBusquedaCliente.getText().trim();

        if (busqueda.isEmpty()) {
            mostrarAlerta("Error", "Ingrese un número de documento.", Alert.AlertType.WARNING);
            return;
        }

        Optional<Cliente> clienteOpt = clienteRepository.buscarPorDocumento(busqueda);

        // Si no se encontró por documento, intenta buscar por número de cuenta
        if(clienteOpt.isEmpty()){
            Optional<Cliente> clienteCuenta = clienteRepository.buscarClientePorCuenta(busqueda);
            if(clienteCuenta.isPresent()){
                clienteOpt = clienteCuenta;
            }
        }

        if (clienteOpt.isEmpty()) {
            mostrarAlerta("No encontrado", "No se encontró ningún cliente con ese documento.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            return;
        }

        clienteActual = clienteOpt.get();
        cargarDatosCliente();
    }

    /**
     * Carga los datos del cliente encontrado en los campos de texto,
     * incluyendo información personal, tipo de usuario y cuentas asociadas.
     */
    private void cargarDatosCliente() {

        // Diferenciar entre persona natural y jurídica
        if(clienteActual.getUsuarioAsociado() instanceof PersonaJuridica juridica){
           mostrarDatosPersonaJuridica(juridica);
        }
        else if(clienteActual.getUsuarioAsociado() instanceof PersonaNatural personaNatural){
            mostrarDatosPersonaNatural(personaNatural);
        }
        // Campos comunes
        txtCorreo.setText(clienteActual.getCorreo());
        txtTelefono.setText(clienteActual.getTelefono());
        txtCiudad.setText(clienteActual.getCiudad());
        txtTipoDocumento.setText(clienteActual.getTipoDocumento());
        txtNumeroDocumento.setText(clienteActual.getDocumento());

        // Carga de foto del cliente (si existe)
        if(clienteActual.getFoto() != null && !clienteActual.getFoto().isBlank()) {
            try {
                String rutaFoto = clienteActual.getFoto();

                if (rutaFoto.startsWith("/")) {
                    // Es una imagen en recursos (classpath)
                    imgCliente.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(rutaFoto))));
                } else {
                    // Es una ruta en el sistema de archivos
                    Path path = Paths.get(rutaFoto);
                    if (Files.exists(path)) {
                        imgCliente.setImage(new Image(path.toUri().toString()));
                    }
                }
            } catch (Exception e) {
                imgCliente.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/triplej/banco/Images/avatar.png"))));
            }
        } else {
            imgCliente.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/triplej/banco/Images/avatar.png"))));
        }

        // Llenar ComboBox con las cuentas del cliente
        cmbCuentasCliente.getItems().clear();
        cmbCuentasCliente.getItems().addAll(clienteActual.getCuentas());
        if (!clienteActual.getCuentas().isEmpty()) {
            cmbCuentasCliente.getSelectionModel().selectFirst();
            cuentaSeleccionada = cmbCuentasCliente.getValue();
        }
    }

    /**
     * Limpia los campos de información y reinicia la selección del cliente.
     */
    private void limpiarCampos() {
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtCiudad.clear();
        cmbCuentasCliente.getItems().clear();
        clienteActual = null;
        cuentaSeleccionada = null;
    }

    /**
     * Actualiza la cuenta actualmente seleccionada en el ComboBox.
     */
    private void seleccionarCuenta() {
        cuentaSeleccionada = cmbCuentasCliente.getValue();
        if (cuentaSeleccionada != null) {
            clienteActual.setCuentaActiva(cuentaSeleccionada);
            System.out.println("Cuenta seleccionada: " + cuentaSeleccionada.getNumeroCuenta());
        }
    }

    /**
     * Permite al cajero depositar dinero en la cuenta seleccionada.
     * Se pide el monto mediante un cuadro de diálogo.
     */
    @FXML
    private void onDepositar() {
        if (cuentaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una cuenta para operar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/CajeroViews/Deposito-view.fxml"));
            Parent root = loader.load();

            DepositoController controller = loader.getController();
            controller.setDatosOperacion(clienteActual, cuentaSeleccionada);

            Stage stage = new Stage();
            stage.setTitle("Déposito de dinero");
            stage.setScene(new Scene(root));
            stage.initOwner(btnDepositar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de déposito.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onRetirar() {
        if (cuentaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una cuenta para operar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/CajeroViews/Retiro-view.fxml"));
            Parent root = loader.load();

            RetiroController controller = loader.getController();
            controller.setDatosOperacion(clienteActual, cuentaSeleccionada);

            Stage stage = new Stage();
            stage.setTitle("Retiro de dinero");
            stage.setScene(new Scene(root));
            stage.initOwner(btnRetirar.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de retiro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onTransferir() {
        if (cuentaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una cuenta para operar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/CajeroViews/Transferencia-view.fxml"));
            Parent root = loader.load();

            TransferenciaController controller = loader.getController();
            controller.setDatosOperacion(clienteActual, cuentaSeleccionada);

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
     * Muestra una ventana emergente con el saldo actual de la cuenta seleccionada.
     */

    @FXML
    private void onConsultarSaldo(){
        if (cuentaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una cuenta para operar.", Alert.AlertType.WARNING);
            return;
        }

        double saldo = cajeroService.consultarSaldo(cuentaSeleccionada);

        Stage ventanaSaldo = new Stage();
        ventanaSaldo.setTitle("Detalles de la cuenta");

        Label lblCliente = new Label("Cliente: " + clienteActual.getNombre());
        Label lblCuenta = new Label("Cuenta N°: " + cuentaSeleccionada.getNumeroCuenta());
        Label lblSaldo = new Label("Saldo actual: $" + String.format("%,.2f", saldo));

        lblCliente.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        lblSaldo.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");

        VBox layout = new VBox(10, lblCliente, lblCuenta, lblSaldo);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(20));

        // 🔍 Detectar el tipo de cuenta y mostrar detalles específicos
        if (cuentaSeleccionada instanceof CuentaAhorro ahorro) {
            Label lblTasa = new Label("Tasa de interés: " + (ahorro.getTasaInteres() * 100) + " % anual");
            layout.getChildren().add(lblTasa);
        }
        else if (cuentaSeleccionada instanceof CuentaCorriente corriente) {
            Label lblSobregiro = new Label("Límite de sobregiro: $" + String.format("%,.2f", corriente.getLimiteSobregiro()));
            layout.getChildren().add(lblSobregiro);
        }
        else if (cuentaSeleccionada instanceof CuentaEmpresarial emp) {
            Label lblTope = new Label("Tope de transferencia: $" + String.format("%,.2f", emp.getTopeTransferencia()));
            layout.getChildren().add(lblTope);
        }

        Scene scene = new Scene(layout, 400, 300);
        ventanaSaldo.setScene(scene);
        ventanaSaldo.show();
    }

    /**
     * Cierra la sesión del cajero y regresa al menú de inicio de sesión.
     */
    @FXML
    private void volverMenu(){
        cajero.getPersona().setActivo(false);
        UsuarioRepository.getInstancia().actualizarUsuario(cajero.getPersona());

        Stage ventanaActual = (Stage) btnSalir.getScene().getWindow();
        VolverLogin.volverLogin(ventanaActual);
    }

    /**
     * Carga dinámicamente una vista FXML dentro del contenedor central del cajero.
     * También intenta pasar una referencia al controlador principal.
     */
    private void cargarVistaEnCentro(String fxmlRuta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent vista = loader.load();

            Object controller = loader.getController();

            try {
                controller.getClass()
                        .getMethod("setCajeroController", CajeroController.class)
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

    /**
     * Abre el formulario para registrar una nueva cuenta bancaria asociada al cliente actual.
     */
    @FXML
    private void abrirFormularioNuevaCuenta(){
        if (clienteActual == null) {
            mostrarAlerta("Error", "Debe buscar y seleccionar un cliente antes de crear una nueva cuenta.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/triplej/banco/Views/CajeroViews/FormularioNuevaCuenta-view.fxml"));
            Parent root = loader.load();

            // Obtener el controlador del formulario
            FormularioNuevaCuentaController controlador = loader.getController();

            // Pasar el cliente actual al nuevo formulario
            controlador.setCliente(clienteActual);

            // Crear y mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Apertura de nueva cuenta");
            stage.setScene(new Scene(root));
            stage.initOwner(btnBuscarCliente.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el formulario de nueva cuenta.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Genera un reporte de movimientos de la cuenta activa del cliente.
     * El reporte se muestra en un área de texto en la vista.
     */
    @FXML
    private void onGenerarReporte(){
        if(clienteActual == null || clienteActual.getCuentaPorNumero() == null){
            mostrarAlerta("No se encontró la cuenta activa del cliente");
            return;
        }

        Reporte reporte = cajeroService.generarReporteCliente(cuentaSeleccionada);

        generarReporte(reporte, txtReporteGeneral, vistaReporte, contenedorCentro);
    }

    /**
     * Guarda el contenido del reporte actual en un archivo de texto.
     */
    @FXML
    private void onGuardarReporte(){
        String contenido = txtReporteGeneral.getText();
        if(contenido == null || contenido.isBlank()){
            mostrarAlerta("No hay reporte para guardar");
            return;
        }
        exportarReporteTxt(contenido);
    }

    /**
     * Exporta el texto del reporte a un archivo en la carpeta "Reportes Clientes".
     */
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

    /**
     * Carga el formulario de registro de un nuevo cliente dentro del contenedor central.
     */
    @FXML
    private void mostrarFormulario() {
        cargarVistaEnCentro("/triplej/banco/Views/CajeroViews/FormularioCliente-view.fxml");
    }

    /**
     * Muestra nuevamente la vista de inicio del cajero.
     */
    @FXML
    public void mostrarInicio() {
        contenedorCentro.getChildren().clear();
        vistaInicio.setVisible(true);
        vistaInicio.setManaged(true);
        contenedorCentro.getChildren().add(vistaInicio);
    }

    /**
     * Muestra los datos específicos de una persona jurídica (empresa).
     * @param personaJuridica instancia de PersonaJuridica con los datos a mostrar
     */
    private void mostrarDatosPersonaJuridica(PersonaJuridica personaJuridica){
        lblRazonSocial.setVisible(true);
        lblRepresentanteLegal.setVisible(true);
        lblTipoEmpresa.setVisible(true);
        lblDatoNombre.setVisible(false);

        txtRazonSocial.setText(personaJuridica.getRazonSocial());
        txtRepresentanteLegal.setText(personaJuridica.getRepresentanteLegal());
        txtTipoEmpresa.setText(personaJuridica.getTipoEmpresa());
        txtRazonSocial.setVisible(true);
        txtRepresentanteLegal.setVisible(true);
        txtTipoEmpresa.setVisible(true);
    }

    /**
     * Muestra los datos de una persona natural, ocultando los campos propios de empresa.
     * @param personaNatural instancia de PersonaNatural
     */
    private void mostrarDatosPersonaNatural(PersonaNatural personaNatural){
        txtNombre.setText(clienteActual.getNombre());

        lblRazonSocial.setVisible(false);
        lblRepresentanteLegal.setVisible(false);
        lblTipoEmpresa.setVisible(false);
        lblDatoNombre.setVisible(true);
    }
}
