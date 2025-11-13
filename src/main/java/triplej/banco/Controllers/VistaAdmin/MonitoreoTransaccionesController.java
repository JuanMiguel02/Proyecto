package triplej.banco.Controllers.VistaAdmin;

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

/**
 * Controlador encargado de la vista de monitoreo de transacciones.
 *
 * Permite al administrador visualizar todas las transacciones registradas en el sistema
 * y detectar aquellas que puedan ser sospechosas.
 *
 * Muestra la información en una tabla (TableView) con datos obtenidos desde el repositorio
 * de transacciones, y ofrece opciones para filtrar y resaltar aquellas con comportamientos anómalos.
 */
public class MonitoreoTransaccionesController {
    /** Tabla principal donde se muestran las transacciones */
    @FXML
    private TableView<Transaccion> tablaTransacciones;

    /** Columnas que muestran los diferentes atributos de una transacción */
    @FXML private TableColumn<Transaccion, String> colId;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colCuentaOrigen;
    @FXML private TableColumn<Transaccion, String> colCuentaDestino;
    @FXML private TableColumn<Transaccion, Integer> colMonto;
    @FXML private TableColumn<Transaccion, String> colTipo;

    /** Repositorio que gestiona el acceso a las transacciones almacenadas */
    private TransaccionRepository transaccionRepository;
    /** Lista observable que contiene las transacciones mostradas en la tabla */
    private ObservableList<Transaccion> listaTransacciones;

    /**
     * Método que se ejecuta automáticamente al inicializar la vista.
     *
     * Se encarga de:
     * - Configurar las columnas de la tabla con las propiedades de la clase `Transaccion`.
     * - Cargar las transacciones desde el repositorio.
     * - Personalizar la apariencia de las filas, resaltando aquellas que sean sospechosas.
     */
    public void initialize(){
        // Inicializa el repositorio (patrón Singleton)
        transaccionRepository = TransaccionRepository.getInstancia();

        // Configurar las columnas para que tomen los valores de las propiedades de Transaccion
        colId.setCellValueFactory(new PropertyValueFactory<>("id")); //
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada")); //
        colCuentaOrigen.setCellValueFactory(new PropertyValueFactory<>("cuentaOrigen"));  //
        colCuentaDestino.setCellValueFactory(new PropertyValueFactory<>("cuentaDestino"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Cargar los datos en la tabla
        cargarTransacciones();
        System.out.println("Transacciones cargadas: " + transaccionRepository.getTodasTransacciones().size());

        // Personalizar las filas para resaltar visualmente las transacciones sospechosas
       tablaTransacciones.setRowFactory(tv -> new TableRow<>(){
           @Override
           protected void updateItem(Transaccion trans, boolean empty){
               if(trans == null || empty){
                   setStyle("");  // Sin estilo si la fila está vacía
               }else if(trans.esSospechosa()){
                   // Se aplica un fondo rojizo si la transacción fue marcada como sospechosa
                   setStyle("-fx-background-color: rgb(255, 80, 80, 0.3); -fx-font-weight: bold" );
               }else{
                   setStyle("");       setStyle(""); // Estilo normal si no es sospechosa
               }
           }
        });

    }

    /**
     * Filtra y muestra únicamente las transacciones consideradas sospechosas.
     *
     * Utiliza el método `esSospechosa()` de la clase `Transaccion` para identificar
     * aquellas que podrían ser irregulares, por ejemplo por montos inusuales o
     * patrones extraños en las transferencias.
     *
     * Si no hay transacciones sospechosas, se muestra una alerta informativa.
     */
    @FXML
    private void filtrarSospechosas(){
        // Filtra la lista usando el método de cada transacción
        ObservableList<Transaccion> sospechosas = listaTransacciones.filtered(Transaccion::esSospechosa);
        tablaTransacciones.setItems(sospechosas);
        tablaTransacciones.refresh();

        // Si no se encuentran transacciones sospechosas, se informa al usuario
        if(sospechosas.isEmpty()){
            mostrarAlerta("No se detectaron transacciones sospechosas");
        }
    }

    /**
     * Restablece la vista para mostrar nuevamente todas las transacciones.
     *
     * Es útil luego de haber aplicado un filtro de sospechosas,
     * permitiendo al administrador ver la lista completa otra vez.
     */
    @FXML
    private void mostrarTodasTransacciones(){
        tablaTransacciones.setItems(listaTransacciones);
        tablaTransacciones.refresh();
    }

    /**
     * Carga todas las transacciones almacenadas en el repositorio
     * y las asigna a la tabla principal.
     *
     * Convierte la lista obtenida desde el repositorio en una `ObservableList`,
     * lo que permite que la tabla se actualice automáticamente si los datos cambian.
     */
    private void cargarTransacciones(){
        listaTransacciones = FXCollections.observableArrayList(transaccionRepository.getTodasTransacciones());
        tablaTransacciones.setItems(listaTransacciones);
    }
}
