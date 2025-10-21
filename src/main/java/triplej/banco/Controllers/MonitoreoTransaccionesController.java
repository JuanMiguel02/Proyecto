package triplej.banco.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import triplej.banco.Models.Cuentas.Transaccion;
import triplej.banco.Repositories.TransaccionRepository;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class MonitoreoTransaccionesController {
    @FXML
    private TableView<Transaccion> tablaTransacciones;

    @FXML private TableColumn<Transaccion, String> colId;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colCuentaOrigen;
    @FXML private TableColumn<Transaccion, String> colCuentaDestino;
    @FXML private TableColumn<Transaccion, Integer> colMonto;
    @FXML private TableColumn<Transaccion, String> colTipo;

    private TransaccionRepository repoTransacciones;
    private ObservableList<Transaccion> listaTransacciones;

    public void initialize(){
        repoTransacciones = TransaccionRepository.getInstance();

        colId.setCellValueFactory(new PropertyValueFactory<>("id")); //
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada")); //
        colCuentaOrigen.setCellValueFactory(new PropertyValueFactory<>("cuentaOrigen"));  //
        colCuentaDestino.setCellValueFactory(new PropertyValueFactory<>("cuentaDestino"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

       cargarTransacciones();


       tablaTransacciones.setRowFactory(tv -> new TableRow<>(){
           @Override
           protected void updateItem(Transaccion trans, boolean empty){
               if(trans == null || empty){
                   setStyle("");
               }else if(trans.esSospechosa()){
                   setStyle("-fx-background-color: rgb(255, 80, 80, 0.3); -fx-font-weight: bold" );
               }else{
                   setStyle("");
               }
           }
        });

    }

    @FXML
    private void filtrarSospechosas(){
        ObservableList<Transaccion> sospechosas = listaTransacciones.filtered(Transaccion::esSospechosa);
        tablaTransacciones.setItems(sospechosas);
        tablaTransacciones.refresh();

        if(sospechosas.isEmpty()){
            mostrarAlerta("No se detectaron transacciones sospechosas");
        }
    }

    @FXML
    private void mostrarTodasTransacciones(){
        tablaTransacciones.setItems(listaTransacciones);
        tablaTransacciones.refresh();
    }

    private void cargarTransacciones(){
        listaTransacciones = FXCollections.observableArrayList(repoTransacciones.getTodasTransacciones());
        tablaTransacciones.setItems(listaTransacciones);
    }
}
