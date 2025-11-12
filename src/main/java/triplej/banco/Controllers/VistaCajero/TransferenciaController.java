package triplej.banco.Controllers.VistaCajero;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Services.CajeroService;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class TransferenciaController {

    @FXML
    private Label lblCuenta;
    @FXML private TextField txtMonto;
    @FXML private TextField txtCuentaDestino;
    @FXML private TextField txtCuentaDestinoConfirmacion;

    private final CajeroService cajeroService = new CajeroService();
    private Cliente clienteActual;
    private CuentaBancaria cuentaOrigen;
    private final ClienteRepository clienteRepository = ClienteRepository.getInstancia();

    public void setDatosOperacion(Cliente cliente, CuentaBancaria cuenta){
        this.clienteActual = cliente;
        this.cuentaOrigen = cuenta;
        lblCuenta.setText("Cuenta Origen: " + cuenta.getNumeroCuenta());
    }

    @FXML
    private void onConfirmarTransferencia(){
       if(clienteActual == null || cuentaOrigen == null){
           mostrarAlerta("No se ha seleccionado una cuenta de origen");
           return;
       }

       String numeroCuentaDestino = txtCuentaDestino.getText().trim();
       String numeroCuentaDestinoConfirmacion = txtCuentaDestinoConfirmacion.getText().trim();

       if(!numeroCuentaDestino.equalsIgnoreCase(numeroCuentaDestinoConfirmacion)){
           mostrarAlerta("Los números de cuenta no coinciden");
           return;
       }

       String valorMonto = txtMonto.getText().trim();

       if(numeroCuentaDestino.isEmpty() || valorMonto.isEmpty()){
           mostrarAlerta("Debe completar todos los campos");
           return;
       }

       try{
           double monto = Double.parseDouble(valorMonto);
           CuentaBancaria cuentaDestino =clienteRepository.buscarCuentaPorNumero(numeroCuentaDestino)
                   .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

           cajeroService.realizarTransferencia(cuentaOrigen, cuentaDestino, monto);
           mostrarAlerta("Éxito", "Transferencia realizada correctamente a: " + cuentaDestino.getNumeroCuenta(), Alert.AlertType.INFORMATION );

       } catch (NumberFormatException e) {
        mostrarAlerta( "El monto debe ser un número válido.");
    } catch (IllegalArgumentException e) {
        mostrarAlerta( e.getMessage());
    } catch (Exception e) {
        mostrarAlerta("Error inesperado " +  e.getMessage());
    }
    }

    @FXML
    private void onCancelar(){
        cerrarVentana();
    }

    private void cerrarVentana(){
        ((Stage) txtMonto.getScene().getWindow()).close();
    }
}
