package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBancaria;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Representa a un cliente del banco dentro del sistema.
 * <p>
 * Esta clase asocia una {@link Persona} con una o varias cuentas bancarias,
 * permitiendo acceder a su información personal, consultar saldos,
 * y gestionar las cuentas activas.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *     <li>Mantener la relación entre la persona y sus cuentas bancarias.</li>
 *     <li>Permitir la obtención del saldo de la cuenta activa.</li>
 *     <li>Facilitar la búsqueda y administración de cuentas asociadas.</li>
 * </ul>
 *
 * <p>
 * Los métodos de esta clase encapsulan la lógica necesaria para acceder
 * a los datos de las cuentas y del usuario sin exponer directamente
 * las estructuras internas.
 * </p>
 */
public class Cliente {

    /** Persona asociada al cliente (información personal y de usuario). */
    private Persona personaAsociada;

    /** Lista de cuentas bancarias pertenecientes al cliente. */
    private final ArrayList<CuentaBancaria> cuentas;

    /** Cuenta actualmente activa o seleccionada por el cliente. */
    private CuentaBancaria cuentaActiva;

    /**
     * Crea un nuevo cliente asociado a una persona específica.
     *
     * @param personaAsociada Persona a la que pertenece el cliente.
     */
    public Cliente(Persona personaAsociada) {
        this.personaAsociada = personaAsociada;
        this.cuentas = new ArrayList<>();

    }

    /**
     * Retorna el saldo de la cuenta activa del cliente.
     * Si no hay una cuenta activa, devuelve 0.
     *
     * @return Saldo actual o 0.0 si no hay cuenta activa.
     */
    public double getSaldo(){
       if(cuentaActiva != null){
           return cuentaActiva.getSaldo();
       }
        return 0.0;
    }

    //Getters y Setters
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

    /**
     * Agrega una nueva cuenta bancaria al cliente.
     *
     * @param cuenta La cuenta bancaria a agregar.
     */
    public void agregarCuenta(CuentaBancaria cuenta) {
        this.cuentas.add(cuenta);
    }

    public String getDocumento(){
        return personaAsociada.getNumeroDocumento();
    }

    public CuentaBancaria getCuentaPorNumero() {
        return cuentaActiva;
    }

    /**
     * Busca una cuenta bancaria por su número.
     *
     * @param numeroCuenta Número de cuenta a buscar.
     * @return La cuenta encontrada o {@code null} si no existe.
     */
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

    /**
     * Genera una descripción breve de cada cuenta con su tipo.
     *
     * @return Cadena con el número y tipo de cada cuenta.
     */
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

