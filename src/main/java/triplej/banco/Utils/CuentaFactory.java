package triplej.banco.Utils;

import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.CuentaCorriente;
import triplej.banco.Models.Cuentas.CuentaEmpresarial;
import triplej.banco.Models.Usuarios.Cliente;

public class CuentaFactory {

    public static CuentaBancaria crearCuenta(String tipoCuenta, Cliente propietario){
        return switch (tipoCuenta.toUpperCase()){
            case "AHORRO" -> new CuentaAhorro(propietario);
            case "CORRIENTE" -> new CuentaCorriente(propietario);
            case "EMPRESARIAL" -> new CuentaEmpresarial(propietario);
            default -> throw new IllegalArgumentException("Tipo de cuenta no valido");
        };
    }


}
