package triplej.banco.Controllers.VistaCajero;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import triplej.banco.Services.CajeroService;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

/**
 * Controlador del formulario de creación de nuevas cuentas bancarias.
 * <p>
 * Esta clase permite al cajero registrar una nueva cuenta para un cliente seleccionado,
 * especificando el tipo de cuenta y el saldo inicial. Se encarga de validar los datos ingresados,
 * interactuar con el {@link CajeroService} y mostrar mensajes informativos o de error al usuario.
 * </p>
 */
public class FormularioNuevaCuentaController {

    /** Etiqueta con el nombre completo del cliente */
    @FXML private Label lblNombreCliente;

    /** Etiqueta con el documento de identidad del cliente */
    @FXML private Label lblDocumentoCliente;

    /** Selector del tipo de cuenta (Ahorro, Corriente, Empresarial) */
    @FXML private ComboBox<String> cmbTipoCuenta;

    /** Campo para ingresar el saldo inicial de la nueva cuenta */
    @FXML private TextField txtSaldoInicial;

    /** Cliente asociado a la nueva cuenta que se va a crear */
    private Cliente cliente;

    /** Servicio del cajero que contiene la lógica de creación de cuentas */
    private final CajeroService cajeroService = new CajeroService();

    /**
     * Inicializa los componentes del formulario.
     * Carga en el ComboBox los tipos de cuenta disponibles.
     * Se ejecuta automáticamente al cargar el FXML.
     */
    @FXML
    public void initialize(){
        cmbTipoCuenta.getItems().addAll("AHORRO", "CORRIENTE", "EMPRESARIAL");
    }

    /**
     * Asigna el cliente actual que recibirá la nueva cuenta.
     * También actualiza las etiquetas del formulario con su información.
     *
     * @param cliente instancia del cliente seleccionado
     */
    public void setCliente(Cliente cliente){
        this.cliente = cliente;
        lblNombreCliente.setText(cliente.getUsuarioAsociado().getNombreCompleto());
        lblDocumentoCliente.setText(cliente.getDocumento());
    }

    /**
     * Crea una nueva cuenta bancaria para el cliente actual.

     * Valida que se haya seleccionado un cliente y que los campos del formulario
     * estén completos. Convierte el saldo inicial a número y registra la cuenta
     * usando el servicio {@link CajeroService}.

     *
     * Si la operación es exitosa, muestra un mensaje de confirmación con
     * el número de cuenta creada. Si ocurre un error, muestra una alerta.</p>
     */
    @FXML
    private void crearCuenta(){
        // Verificar que haya un cliente asignado
        if(cliente == null){
            mostrarAlerta("No se ha seleccionado un cliente");
            return;
        }
        String tipoCuenta = cmbTipoCuenta.getValue();

        if(tipoCuenta == null || txtSaldoInicial.getText() == null){
            mostrarAlerta("Complete todos los campos");
            return;
        }
        double saldo;

        // Si el campo de saldo está vacío, se asume 0.0
        if(txtSaldoInicial.getText().isEmpty()){
            saldo = 0.0;
        }
        else{
            try {
                saldo = Double.parseDouble(txtSaldoInicial.getText().trim());

            } catch (NumberFormatException e) {
                mostrarAlerta("El saldo debe ser un número válido");
                txtSaldoInicial.requestFocus();
                return;
            }
        }

        // Crear la cuenta bancaria usando el servicio
        try{
            CuentaBancaria cuentaNueva = cajeroService.agregarCuentaACliente(cliente, tipoCuenta, saldo);
            cuentaNueva.setSaldo(saldo);
            mostrarAlerta("Éxito", "Cuenta: " + cuentaNueva.getNumeroCuenta() + " creada exitosamente" + " \n Propietario: " + cliente.getNombre(), Alert.AlertType.INFORMATION);

        }catch(Exception e){
            mostrarAlerta(e.getMessage());
        }

    }

    /**
     * Cierra la ventana actual sin realizar ninguna acción.
     * Se ejecuta cuando el usuario presiona el botón "Cancelar".
     */
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana del formulario de nueva cuenta.
     */
    private void cerrarVentana() {
        ((Stage) lblNombreCliente.getScene().getWindow()).close();
    }
}
