package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBanco;

import java.util.ArrayList;

public abstract class Cliente extends Usuario {

    private ArrayList<CuentaBanco> cuentas;

    public Cliente(String nombre, TipoDocumento documento, String numeroDocumento, String telefono, String email, String contrasenia, String pais, String ciudad) {
        super(nombre, documento, numeroDocumento, telefono, email, contrasenia, pais, ciudad);
        this.cuentas = new ArrayList<>();
    }

    public void transferir(CuentaBanco origen, CuentaBanco destino, double monto){
        if(origen.getSaldo() >= monto){
            origen.retirar(monto);
            destino.depositar(monto);
        } else{
            throw new IllegalArgumentException("Saldo insuficiente");
        }

    }

    public ArrayList<CuentaBanco> getCuentas() {
        return cuentas;
    }

    public void agregarCuenta(CuentaBanco cuenta) {
        this.cuentas.add(cuenta);
    }

}
