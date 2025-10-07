package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

public class CuentaEmpresarial extends CuentaBancaria {
    public CuentaEmpresarial(Cliente propietario){
        super(propietario);
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "3";
    }

    @Override
    public void retirar(Double monto){
        //Falta
    };


}
