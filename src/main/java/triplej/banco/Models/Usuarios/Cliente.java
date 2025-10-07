package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBancaria;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Cliente extends Usuario {


    private final ArrayList<CuentaBancaria> cuentas;

    public Cliente(String nombre, TipoDocumento documento, String numeroDocumento, String telefono, String email, String contrasenia, String pais, String ciudad) {
        super(nombre, documento, numeroDocumento, telefono, email, contrasenia, pais, ciudad);
        this.cuentas = new ArrayList<>();

    }

    public ArrayList<CuentaBancaria> getCuentas() {
        return cuentas;
    }

    public void agregarCuenta(CuentaBancaria cuenta) {
        this.cuentas.add(cuenta);
    }

    public Optional<CuentaBancaria> buscarCuenta(String numeroCuenta){
     if(numeroCuenta == null || numeroCuenta.trim().isEmpty()){
         return Optional.empty();
     }
     return cuentas.stream().filter(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta)).findFirst();
    }
    @Override
    public String toString() {

        String cuentasString = nombrarCuentasTipo();

        return "Usuario{" +
                "nombre='" + getNombre() + '\'' +
                ", tipoDocumento='" + getDocumento() + '\'' +
                ", numeroDocumento='" + getNumeroDocumento() + '\'' +
                ", telefono='" + getTelefono() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", fechaRegistro=" + getFechaRegistro() +'\'' +
                ", cuentas= " + cuentasString +'\'' +
                '}';
    }

    private String nombrarCuentasTipo() {
        return getCuentas().stream().map(cuenta -> {
            String tipoLegible;
            switch (cuenta.getCodigoTipoCuenta()) {
                case "1" -> tipoLegible = "Cuenta Ahorro";
                case "2" -> tipoLegible = "Cuenta Corriente";
                case "3" -> tipoLegible = "Cuenta Empresarial";
                default -> tipoLegible = "";
            }
            return cuenta.getNumeroCuenta() + " (" + tipoLegible + ")";
        }).collect(Collectors.joining(", "));

    }
    }

