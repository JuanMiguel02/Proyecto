package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBancaria;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cliente {

    private Usuario usuarioAsociado;
    private final ArrayList<CuentaBancaria> cuentas;
    private CuentaBancaria cuentaActiva;

    public Cliente(Usuario usuarioAsociado) {
        this.usuarioAsociado = usuarioAsociado;
        this.cuentas = new ArrayList<>();

    }

    public double getSaldo(){
       if(cuentaActiva != null){
           return cuentaActiva.getSaldo();
       }
        return 0.0;
    }

    public Usuario getUsuarioAsociado() {
        return usuarioAsociado;
    }

    public String getNombre(){
        return usuarioAsociado.getNombreCompleto();
    }

    public void setUsuarioAsociado(Usuario usuarioAsociado) {
        this.usuarioAsociado = usuarioAsociado;
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

    public CuentaBancaria getCuentaActiva() {
        return cuentaActiva;
    }

    public void setCuentaActiva(CuentaBancaria cuentaActiva) {
        this.cuentaActiva = cuentaActiva;
    }

    @Override
    public String toString() {

        String cuentasString = nombrarCuentasTipo();

        return "Cliente{" +
                "usuario=" + usuarioAsociado.getNombreCompleto() +
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

