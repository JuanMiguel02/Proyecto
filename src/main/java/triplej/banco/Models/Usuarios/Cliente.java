package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBancaria;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Cliente {

    private Persona personaAsociada;
    private final ArrayList<CuentaBancaria> cuentas;
    private CuentaBancaria cuentaActiva;

    public Cliente(Persona personaAsociada) {
        this.personaAsociada = personaAsociada;
        this.cuentas = new ArrayList<>();

    }

    public double getSaldo(){
       if(cuentaActiva != null){
           return cuentaActiva.getSaldo();
       }
        return 0.0;
    }

    public Usuario getPersonaAsociada() {
        return personaAsociada;
    }

    public String getCorreo(){return personaAsociada.getCorreo();}

    public String getNombre(){
        return personaAsociada.getNombreUsuario();
    }

    public String getCiudad(){ return personaAsociada.getCiudad();}

    public String getTelefono(){return personaAsociada.getTelefono();}

    public String getTipoDocumento(){ return personaAsociada.getTipoDocumento().toString();}

    public void setPersonaAsociada(Persona personaAsociada) {
        this.personaAsociada = personaAsociada;
    }

    public String getFoto(){ return personaAsociada.getFoto();}

    public ArrayList<CuentaBancaria> getCuentas() {
        return cuentas;
    }

    public int getNumeroCuentas(){
        return cuentas.size();
    }

    public void agregarCuenta(CuentaBancaria cuenta) {
        this.cuentas.add(cuenta);
    }

    public String getDocumento(){
        return personaAsociada.getNumeroDocumento();
    }

    public CuentaBancaria getCuentaPorNumero() {
        return cuentaActiva;
    }

    public CuentaBancaria getCuentaPorNumero(String numeroCuenta) {
        for(CuentaBancaria cuenta : cuentas){
            if(cuenta.getNumeroCuenta().equalsIgnoreCase(numeroCuenta)){
                return cuenta;
            }
        }
        return null;
    }

    public void setCuentaActiva(CuentaBancaria cuentaActiva) {
        this.cuentaActiva = cuentaActiva;
    }

    @Override
    public String toString() {

        String cuentasString = nombrarCuentasTipo();

        return "Cliente: " +
                personaAsociada.getNombreUsuario() +
                ", cuentas= " + cuentasString +'\'';
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

