package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

public class CuentaCorriente extends CuentaBanco{

    public CuentaCorriente(Cliente propietario, double saldo){
        super(propietario, saldo);
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "2";
    }

    @Override
    public void retirar(Double monto){
        //Falta
    };

    @Override
    public void depositar(Double monto){
        //Falta
    };
}
