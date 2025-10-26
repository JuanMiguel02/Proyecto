package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

public class CuentaCorriente extends CuentaBancaria {

    public CuentaCorriente(Cliente propietario){
        super(propietario);
    }

    public CuentaCorriente (Cliente propietario, String numeroCuenta, double saldo){
        super(propietario,numeroCuenta,saldo);
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "2";
    }

    @Override
    public void retirar(Double monto){
        if(getSaldo()> monto){
            setSaldo(getSaldo() - monto);
        }
    }


}
