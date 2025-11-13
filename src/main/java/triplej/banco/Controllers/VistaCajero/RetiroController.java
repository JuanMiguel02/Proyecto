package triplej.banco.Controllers.VistaCajero;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Services.CajeroService;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;

public class RetiroController {

  @FXML private Label lblCuenta;
  @FXML private TextField txtMonto;

  private final CajeroService cajeroService = new CajeroService();
  private Cliente clienteActual;
  private CuentaBancaria cuentaSeleccionada;

  public void setDatosOperacion(Cliente cliente, CuentaBancaria cuenta){
      this.clienteActual = cliente;
      this.cuentaSeleccionada = cuenta;
      lblCuenta.setText("Cuenta: " + cuenta.getNumeroCuenta());
  }

  @FXML
   private void onConfirmarRetiro(){
      try{
          double monto = Double.parseDouble(txtMonto.getText());
          cajeroService.realizarRetiro(cuentaSeleccionada, monto);
          mostrarAlerta("Éxito", "Retiro realizado correctamente", Alert.AlertType.INFORMATION);
          cerrarVentana();
      }catch (NumberFormatException e){
          mostrarAlerta("Ingrese un motno válido");
      } catch(IllegalArgumentException e){
          mostrarAlerta(e.getMessage());
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
