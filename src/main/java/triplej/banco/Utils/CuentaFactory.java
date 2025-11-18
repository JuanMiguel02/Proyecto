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

    public static CuentaBancaria crearCuenta(String tipoCuenta, Cliente propietario, Double sobregiro) {
        return switch (tipoCuenta.toUpperCase()) {
            case "AHORRO" -> new CuentaAhorro(propietario);
            case "CORRIENTE" -> {
                CuentaCorriente cta = new CuentaCorriente(propietario);
                if (sobregiro != null && sobregiro > 0) {
                    cta.setLimiteSobregiro(sobregiro);
                }
                yield cta;
            }
            case "EMPRESARIAL" -> new CuentaEmpresarial(propietario);
            default -> throw new IllegalArgumentException("Tipo de cuenta no válido: " + tipoCuenta);
        };
    }

    public static CuentaBancaria crearCuentaConDatos(String tipo, Cliente cliente, String numeroCuenta,Double saldo, Double sobregiro) {
        if (tipo.equals("1")) {
            return new CuentaAhorro(cliente, numeroCuenta, saldo);
        } else if (tipo.equals("2")) {
            return new CuentaCorriente(cliente, numeroCuenta, saldo, sobregiro);
        } else if(tipo.equals("3")) {
            return new CuentaEmpresarial(cliente, numeroCuenta, saldo);
        }else{
            throw new IllegalArgumentException("Tipo de cuenta desconocido: " + tipo);
        }
    }

}
