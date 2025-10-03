package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBanco;

import java.util.ArrayList;

public class Cliente extends Usuario{

    private ArrayList<CuentaBanco> cuentas;

    public Cliente(String nombre, TipoDocumento documento, String numeroDocumento, String telefono, String email, String contrasenia, String pais, String ciudad){
        super(nombre, documento, numeroDocumento, telefono, email, contrasenia, pais, ciudad);
        this.cuentas = new ArrayList<>();
    }

    public void transferir(){
        //falta
    }

    public void consultarSaldo(){
        //falta
    }
}
