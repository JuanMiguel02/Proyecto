package triplej.banco.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Models.Usuarios.Empleado;

import java.util.ArrayList;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class TablaEmpleadosController {
    @FXML private TableView<Empleado> tablaEmpleados;
    @FXML private AnchorPane vistaTabla;
    @FXML private AnchorPane vistaEdicion;

    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colApellido;
    @FXML private TableColumn<Empleado, String> colCargo;
    @FXML private TableColumn<Empleado, String> colDocumento;
    @FXML private TableColumn<Empleado, String> colTelefono;
    @FXML private TableColumn<Empleado, String> colCorreo;
    @FXML private TableColumn<Empleado, String> colDepartamento;
    @FXML private TableColumn<Empleado, Integer> colSalario;
    @FXML private TableColumn<Empleado, String> colCiudad;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtCedula;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtSalario;
    @FXML private TextField txtCargo;
    @FXML private ComboBox<String> cmbDepartamento;

    @FXML private TextField txtBuscar;
    @FXML private ImageView imgEmpleado;

    private Empleado empleadoSeleccionado;

    private static final Image IMAGEN_POR_DEFECTO = new Image(AdminController.class.getResource("/triplej/banco/Images/avatar.png").toExternalForm());

    private EmpleadoRepository repo;
    private ObservableList<Empleado> listaEmpleados;

    @FXML
    public void initialize() {
        repo = EmpleadoRepository.getInstance();

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // llama a getNombre()
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido")); // llama a getApellido()
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));  // llama a getCargo()
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));

        cmbDepartamento.getItems().addAll(
                "Atención al Cliente",
                "Operaciones",
                "Tesorería",
                "Contabilidad",
                "Sistemas",
                "Recursos Humanos",
                "Gerencia"
        );

        cargarEmpleados();

//        tablaEmpleados.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue != null){
//                Image foto = newValue.getPersona().getFoto() != null ? newValue.getPersona().getFoto() : IMAGEN_POR_DEFECTO;
//                imgEmpleado.setImage(foto);
//            }else{
//                imgEmpleado.setImage(null);
//            }
//        });


        listaEmpleados = FXCollections.observableArrayList(repo.getEmpleados());
        FilteredList<Empleado> listaFiltrada = new FilteredList<>(listaEmpleados);

        tablaEmpleados.setItems(listaFiltrada);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(emp -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filtro = newVal.toLowerCase();
                return emp.getNombre().toLowerCase().contains(filtro)
                        || emp.getApellido().toLowerCase().contains(filtro)
                        || emp.getCargo().toLowerCase().contains(filtro);
            });
        });
    }

    private void cargarEmpleados(){
        listaEmpleados = FXCollections.observableArrayList(repo.getEmpleados());
        tablaEmpleados.setItems(listaEmpleados);
    }


    @FXML
    private void eliminarEmpleado() {
        Empleado empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            mostrarAlerta("Por favor seleccione un empleado para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Seguro que deseas eliminar este empleado?");
        confirmacion.setContentText("Empleado: " + empleadoSeleccionado.getNombreCompleto());

        confirmacion.showAndWait().ifPresent(respuesta ->{
            if(respuesta == ButtonType.OK){
                repo.eliminarEmpleado(empleadoSeleccionado);
                listaEmpleados.remove(empleadoSeleccionado);

                mostrarAlerta("Éxito", "Empleado eliminado correctamente.", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void editarEmpleado(){
        empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();

        if(empleadoSeleccionado == null){
            mostrarAlerta("Por favor seleccione un empleado para editar");
            return;
        }

        // Llenar los campos del formulario
        txtNombre.setText(empleadoSeleccionado.getNombre());
        txtApellido.setText(empleadoSeleccionado.getApellido());
        txtCorreo.setText(empleadoSeleccionado.getCorreo());
        txtCargo.setText(empleadoSeleccionado.getCargo());
        txtCedula.setText(empleadoSeleccionado.getDocumento());
        txtTelefono.setText(empleadoSeleccionado.getTelefono());
        txtCiudad.setText(empleadoSeleccionado.getCiudad());
        txtSalario.setText(String.valueOf(empleadoSeleccionado.getSalario()));
        cmbDepartamento.setValue(empleadoSeleccionado.getDepartamento());


        // Mostrar panel de edición
        vistaTabla.setVisible(true);
        vistaTabla.setManaged(false);

        vistaEdicion.setVisible(true);
        vistaEdicion.setManaged(true);
    }

    @FXML
    private void guardarEdicion(){
        if(empleadoSeleccionado != null){
            empleadoSeleccionado.getPersona().setCiudad(txtCiudad.getText());
            empleadoSeleccionado.getPersona().setTelefono(txtTelefono.getText());
            empleadoSeleccionado.getPersona().setCorreo(txtCorreo.getText());
            empleadoSeleccionado.setSalario(Double.parseDouble(txtSalario.getText()));
            empleadoSeleccionado.setCargo(txtCargo.getText());
            empleadoSeleccionado.setDepartamento(cmbDepartamento.getValue());

            tablaEmpleados.refresh();

            mostrarAlerta("Éxito", "Empleado actualizado correctamente", Alert.AlertType.INFORMATION);

            cancelarEdicion();
        }else{
            mostrarAlerta("No hay empleado seleccionado para editar");
        }
    }

    @FXML
    private void cancelarEdicion(){
        vistaEdicion.setVisible(false);
        vistaEdicion.setManaged(false);

        vistaTabla.setManaged(true);
        vistaTabla.setVisible(true);
    }
}

