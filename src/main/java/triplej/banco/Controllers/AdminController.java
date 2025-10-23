package triplej.banco.Controllers;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import triplej.banco.Models.Reportes.ReporteAdmin;
import triplej.banco.Models.Reportes.ReporteGenerado;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

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

    @FXML
    private AreaChart<String, Number> graficaUsuarios;
    private XYChart.Series<String, Number> serieUsuarios;

    private UsuarioRepository usuarioRepository;

    @FXML
    public void initialize() {

        ClienteRepository clienteRepository = ClienteRepository.getInstancia();
        EmpleadoRepository empleadoRepository = EmpleadoRepository.getInstance();
        usuarioRepository = UsuarioRepository.getInstancia();


        lblTotalUsuarios.textProperty().bind(
              Bindings.size(usuarioRepository.getUsuarios()).asString()
        );

        inicializarGraficoUsuarios();
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
            e.printStackTrace();
        }
    }

    @FXML
    private void generarReporte(){
        ReporteAdmin reporteAdmin = new ReporteAdmin();
        ReporteGenerado reporte = reporteAdmin.generarReporte();

        StringBuilder texto = new StringBuilder();
        for(String linea : reporte.getContenido()){
            texto.append(linea).append("\n");
        }
        txtContenido.setText(texto.toString());
        txtContenido.setWrapText(true);

        // Asegurarnos de que la vistaReporte esté preparada para layout
        vistaReporte.setVisible(true);
        vistaReporte.setManaged(true);

        // Mostrar sólo vistaReporte dentro del contenedor central
        contenedorCentro.getChildren().clear();
        contenedorCentro.getChildren().add(vistaReporte);

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
        cargarVistaEnCentro("/triplej/banco/Views/TablaEmpleados-view.fxml");
    }

    @FXML
    private void mostrarFormulario() {
        cargarVistaEnCentro("/triplej/banco/Views/FormularioEmpleado-view.fxml");
    }

    @FXML
    private void mostrarTransacciones(){
        cargarVistaEnCentro("/triplej/banco/Views/MonitoreoTransacciones-view.fxml");
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
            Path ruta = Paths.get("reportes", "Reporte.txt");
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

}
