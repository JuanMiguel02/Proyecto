package triplej.banco.Controllers.VistaAdmin;

import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import triplej.banco.Models.Reportes.ReporteAdmin;
import triplej.banco.Models.Reportes.Reporte;
import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.GeneracionReporteVista;
import triplej.banco.Utils.VolverLogin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

/**
 * Controlador principal del panel de administración del banco.
 * <p>
 * Esta clase se encarga de gestionar la vista del administrador, incluyendo:
 * - Mostrar información general sobre los usuarios del sistema.
 * - Generar y guardarUsuario reportes administrativos.
 * - Mostrar gráficas con estadísticas.
 * - Navegar entre las diferentes vistas del módulo de administración
 *   (inicio, empleados, formularios, monitoreo de transacciones, etc.).
 * - Controlar la sesión del administrador (cerrar sesión).
 * <p>
 * Se comunica principalmente con:
 * - {@link UsuarioRepository}: para obtener la lista de usuarios activos/inactivos.
 * - {@link ReporteAdmin} y {@link GeneracionReporteVista}: para generar y mostrar reportes.
 */

public class AdminController {

    //Referencia al empleado que está logueado como administrador.
    private Usuario admin;

    //Contenedor principal donde se cargan dinámicamente las vistas del panel.
    @FXML private StackPane contenedorCentro;

    //Vista inicial que se muestra al entrar al panel del administrador.
    @FXML private AnchorPane vistaInicio;

    // Vista donde se muestran los reportes generados.
    @FXML private AnchorPane vistaReporte;

    //Área de texto donde se despliega el contenido del reporte.
    @FXML private TextArea txtContenido;

    //Botón de salida para cerrar sesión y volver al login.
    @FXML private Button btnSalir;

    // Etiquetas informativas que muestran totales y estadísticas.
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblNombre;
    @FXML private Label lblUsuariosActivos;
    @FXML private Label lblUsuariosInactivos;

    //Gráfica de área que muestra la cantidad total de usuarios registrados.
    @FXML
    private AreaChart<String, Number> graficaUsuarios;
    private XYChart.Series<String, Number> serieUsuarios;

    //Repositorio que contiene la lista observable de todos los usuarios del sistema.
    private UsuarioRepository usuarioRepository;

    /**
     *  Método que se ejecuta automáticamente al cargar la vista del administrador.
     * <p>
     * - Obtiene la instancia del banco y su repositorio de usuarios.
     * - Vincula las etiquetas de la interfaz con el tamaño actual de la lista de usuarios.
     * - Configura las etiquetas de usuarios activos e inactivos.
     * - Inicializa la gráfica de usuarios.
     * - Imprime por consola el total de usuarios cargados (modo depuración).
     */
    @FXML
    public void initialize() {

        usuarioRepository = UsuarioRepository.getInstancia();

        // Muestra el total de usuarios registrados (valor siempre sincronizado)
        lblTotalUsuarios.textProperty().bind(
              Bindings.size(usuarioRepository.getUsuarios()).asString()
        );

        mostrarUsuariosActivos();
        mostrarUsuariosInactivos();

        inicializarGraficoUsuarios();

        System.out.println(" Usuarios cargados: " + usuarioRepository.getUsuarios().size());
    }

    /**
     * Recibe el empleado que inició sesión como administrador y actualiza
     * la etiqueta con su nombre en la interfaz.
     *
     * @param admin objeto Empleado que representa al usuario administrador actual.
     */
    public void setAdmin(Usuario admin) {
        this.admin = admin;
        if (lblNombre != null && admin != null) {
            lblNombre.setText(admin.getNombreUsuario());
        }
    }

    /**
     * Carga dinámicamente una vista FXML dentro del contenedor principal.
     *
     * @param fxmlRuta ruta del archivo FXML a cargar.
     * <p>
     * Funcionamiento:
     * 1. Crea un FXMLLoader con la ruta indicada.
     * 2. Carga el archivo y obtiene su controlador.
     * 3. Si el controlador tiene un método "setAdminController", se le pasa
     *    una referencia de este controlador.
     * 4. Limpia el StackPane principal y añade la nueva vista al centro.
     * 5. Ajusta los anclajes si la vista cargada es un AnchorPane.
     *
     * Si ocurre un error al cargar la vista, lanza una RuntimeException.
     */
    private <T> T cargarVistaEnCentro(String fxmlRuta) {
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

            return (T) controller;
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la vista: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un reporte administrativo y lo muestra en la interfaz.
     * <p>
     * - Crea una instancia de ReporteAdmin.
     * - Genera el reporte con datos actuales.
     * - Usa {@link GeneracionReporteVista} para mostrar el contenido en la vista.
     */
    @FXML
    private void generarReporte(){
        ReporteAdmin reporteAdmin = new ReporteAdmin();
        Reporte reporte = reporteAdmin.generarReporte();

        GeneracionReporteVista.generarReporte(reporte, txtContenido, vistaReporte, contenedorCentro);

    }

    /**
     * Guarda el reporte mostrado actualmente en un archivo de texto.
     * Si no hay contenido en el área de texto, muestra una alerta.
     */
    @FXML
    private void guardarReporte(){
        String contenido = txtContenido.getText();
        if(contenido == null || contenido.isBlank()){
            mostrarAlerta("No hay reporte para guardarUsuario");
            return;
        }
        exportarReporteTxt(contenido);
    }

    /**
     * Exporta el contenido del reporte a un archivo .txt dentro de la carpeta "Reportes Admin".
     *
     * @param contenido texto del reporte.
     */
    private void exportarReporteTxt(String contenido){
        try{
            Path ruta = Paths.get("Reportes Admin", "ReporteAdmin.txt");
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
     * Inicializa la gráfica que muestra el número total de usuarios.
     * <p>
     * - Crea una serie de datos y la agrega a la gráfica.
     * - Llama a {@link #actualizarGrafico()} para mostrar los valores iniciales.
     * - Añade un listener para actualizar automáticamente la gráfica
     *   cada vez que cambie la lista de usuarios.
     */
    private void inicializarGraficoUsuarios(){
        serieUsuarios = new XYChart.Series<>();
        serieUsuarios.setName("Usuarios actuales");

        actualizarGrafico();

         graficaUsuarios.getData().add(serieUsuarios);

         usuarioRepository.getUsuarios().addListener((javafx.collections.ListChangeListener<? super Object>) c-> actualizarGrafico());

    }

    /**
     * Actualiza los valores de la gráfica según el número total de usuarios.
     */
    private void actualizarGrafico(){
        int total = usuarioRepository.contarTodos();

        serieUsuarios.getData().clear();
        serieUsuarios.getData().add(new XYChart.Data<>("Total", total));
    }

    /** Abre la vista de empleados. */
    @FXML
    private void mostrarEmpleados() {
        TablaEmpleadosController controller = cargarVistaEnCentro("/triplej/banco/Views/AdminViews/TablaEmpleados-view.fxml");
        controller.setAdminActual(admin);

    }

    /** Abre el formulario para registrar nuevos empleados. */
    @FXML
    private void mostrarFormulario() {
        cargarVistaEnCentro("/triplej/banco/Views/AdminViews/FormularioEmpleado-view.fxml");
    }

    /** Abre la vista de monitoreo de transacciones. */
    @FXML
    private void mostrarTransacciones(){
        cargarVistaEnCentro("/triplej/banco/Views/AdminViews/MonitoreoTransacciones-view.fxml");
    }

    /** Vuelve a la vista inicial del panel de administrador. */
    @FXML
    public void mostrarInicio() {
        contenedorCentro.getChildren().clear();
        vistaInicio.setVisible(true);
        vistaInicio.setManaged(true);
        contenedorCentro.getChildren().add(vistaInicio);

    }

    /**
     * Muestra en tiempo real la cantidad de usuarios activos en el sistema.
     * Utiliza un {@link FilteredList} para filtrar solo los usuarios cuyo
     * atributo "activo" es true, y vincula el tamaño de esa lista con la etiqueta.
     */
    private void mostrarUsuariosActivos(){
        FilteredList<Usuario> usuariosActivos = new FilteredList<>(
                usuarioRepository.getUsuarios(),
                Usuario::isActivo
        );

        lblUsuariosActivos.textProperty().bind(
                Bindings.size(usuariosActivos).asString()
        );
    }

    /**
     * Muestra la cantidad de usuarios inactivos en el sistema.
     * Filtra todos los usuarios cuyo atributo "activo" sea false.
     */
    private void mostrarUsuariosInactivos(){
        FilteredList<Usuario> usuariosActivos = new FilteredList<>(
                usuarioRepository.getUsuarios(),
               usuario -> !usuario.isActivo()
        );

        lblUsuariosInactivos.textProperty().bind(
                Bindings.size(usuariosActivos).asString()
        );
    }

    /**
     * Cierra la sesión del administrador y vuelve a la pantalla de login.
     * <p>
     * Pasos:
     * 1. Marca al administrador como inactivo.
     * 2. Actualiza el estado en el repositorio de usuarios.
     * 3. Obtiene la ventana actual (Stage) y usa {@link VolverLogin#volverLogin(Stage)}
     *    para cargar la vista de inicio de sesión.
     */
    @FXML
    private void volverMenu() {
        admin.setActivo(false);
        usuarioRepository.actualizarUsuario(admin);

        Stage ventanaActual = (Stage) btnSalir.getScene().getWindow();
        VolverLogin.volverLogin(ventanaActual);
    }

}
