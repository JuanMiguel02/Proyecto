package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

public class CuentaAhorro extends CuentaBancaria {

    public CuentaAhorro( Cliente propietario){
        super(propietario);
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "1";
    }

    @Override
    public void retirar(Double monto){
        if(monto < getSaldo()){
            setSaldo(getSaldo() - monto);
        }
    };


}
