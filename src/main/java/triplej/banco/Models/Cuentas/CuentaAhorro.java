package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

public class CuentaAhorro extends CuentaBanco{

    public CuentaAhorro(Cliente propietario, double saldo){
        super(propietario, saldo);
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "1";
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
