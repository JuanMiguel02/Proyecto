package triplej.banco.Controllers.VistaAdmin;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import triplej.banco.Controllers.SignInController;
import triplej.banco.Models.Banco;
import triplej.banco.Models.Reportes.ReporteAdmin;
import triplej.banco.Models.Reportes.ReporteGenerado;
import triplej.banco.Models.Usuarios.Empleado;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.GeneracionReporteVista;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class AdminController {

    @FXML private StackPane contenedorCentro;
    @FXML private AnchorPane vistaInicio;
    @FXML private Label lblTotalUsuarios;
    @FXML private AnchorPane vistaReporte;
    @FXML private TextArea txtContenido;
    @FXML private Button btnSalir;

    @FXML private Label lblNombre;

    @FXML
    private AreaChart<String, Number> graficaUsuarios;
    private XYChart.Series<String, Number> serieUsuarios;

    private UsuarioRepository usuarioRepository;

    @FXML
    public void initialize() {

        Banco banco = Banco.getInstancia();
        usuarioRepository = banco.getUsuarioRepository();

        lblTotalUsuarios.textProperty().bind(
              Bindings.size(usuarioRepository.getUsuarios()).asString()
        );

        inicializarGraficoUsuarios();

        // DEBUG: Verificar cuántos usuarios hay realmente
        System.out.println(" Usuarios cargados: " + usuarioRepository.getUsuarios().size());
    }

    public void setAdmin(Empleado admin) {
        if (lblNombre != null && admin != null) {
            lblNombre.setText(admin.getNombreCompleto());
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
    private void generarReporte(){
        ReporteAdmin reporteAdmin = new ReporteAdmin();
        ReporteGenerado reporte = reporteAdmin.generarReporte();

        GeneracionReporteVista.generarReporte(reporte, txtContenido, vistaReporte, contenedorCentro);

    }

    @FXML
    private void guardarReporte(){
        String contenido = txtContenido.getText();
        if(contenido == null || contenido.isBlank()){
            mostrarAlerta("No hay reporte para guardar");
            return;
        }
        exportarReporteTxt(contenido);
    }

    private void inicializarGraficoUsuarios(){
        serieUsuarios = new XYChart.Series<>();
        serieUsuarios.setName("Usuarios actuales");

        actualizarGrafico();

         graficaUsuarios.getData().add(serieUsuarios);

         usuarioRepository.getUsuarios().addListener((javafx.collections.ListChangeListener<? super Object>) c->{
             actualizarGrafico();
         });

    }

    private void actualizarGrafico(){
        int total = usuarioRepository.getUsuarios().size();

        serieUsuarios.getData().clear();
        serieUsuarios.getData().add(new XYChart.Data<>("Total", total));
    }

    @FXML
    private void mostrarEmpleados() {
        cargarVistaEnCentro("/triplej/banco/Views/AdminViews/TablaEmpleados-view.fxml");
    }

    @FXML
    private void mostrarFormulario() {
        cargarVistaEnCentro("/triplej/banco/Views/AdminViews/FormularioEmpleado-view.fxml");
    }

    @FXML
    private void mostrarTransacciones(){
        cargarVistaEnCentro("/triplej/banco/Views/AdminViews/MonitoreoTransacciones-view.fxml");
    }

    @FXML
    public void mostrarInicio() {
        contenedorCentro.getChildren().clear();
        vistaInicio.setVisible(true);
        vistaInicio.setManaged(true);
        contenedorCentro.getChildren().add(vistaInicio);

    }

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

}
