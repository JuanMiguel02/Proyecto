package triplej.banco.Controllers;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.IOException;

public class AdminController {

    @FXML private StackPane contenedorCentro;
    @FXML private AnchorPane vistaInicio;
    @FXML private Label lblTotalUsuarios;
    private UsuarioRepository usuarioRepository;

    @FXML
    public void initialize() {
         usuarioRepository = UsuarioRepository.getInstancia();

        lblTotalUsuarios.textProperty().bind(
              Bindings.size(usuarioRepository.getUsuarios()).asString()
        );
    }

    private void cargarVistaEnCentro(String fxmlRuta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            Parent vista = loader.load();

            // Si quieres acceder al controlador después
            Object controller = loader.getController();

            // Limpiar el StackPane y agregar la nueva vista
            contenedorCentro.getChildren().clear();
            contenedorCentro.getChildren().add(vista);

            // Ajustar anclajes si es AnchorPane
            if (vista != null) {
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);
            }else if(vista instanceof Region region){
                // Si es VBox o BorderPane, lo centramos y lo expandimos
                region.prefWidthProperty().bind(contenedorCentro.widthProperty());
                region.prefHeightProperty().bind(contenedorCentro.heightProperty());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void mostrarInicio() {
        contenedorCentro.getChildren().clear();
        contenedorCentro.getChildren().add(vistaInicio);
    }


}
