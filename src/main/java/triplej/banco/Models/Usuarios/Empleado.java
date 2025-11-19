package triplej.banco.Models.Usuarios;

/**
 * Representa a un empleado del banco.
 * <p>
 * Esta clase modela la información laboral de un empleado,
 * incluyendo su cargo, salario, departamento y la persona natural
 * asociada que contiene los datos personales.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *     <li>Encapsular la información laboral del empleado.</li>
 *     <li>Acceder a los datos personales del empleado a través de la clase {@link PersonaNatural}.</li>
 *     <li>Permitir modificar atributos clave como el salario, cargo o contraseña.</li>
 * </ul>
 */
public class Empleado{

    /** Cargo que ocupa el empleado dentro del banco (por ejemplo, Cajero, Gerente, etc.). */
    private String cargo;

    /** Salario mensual del empleado. */
    private double salario;

    /** Información personal del empleado (nombre, correo, documento, etc.). */
    private final PersonaNatural persona;

    /** Departamento o área a la que pertenece el empleado (por ejemplo, Finanzas, Atención al cliente, etc.). */
    private String departamento;

    /**
     * Crea un nuevo empleado con la información personal y laboral correspondiente.
     *
     * @param persona      Objeto {@link PersonaNatural} con los datos personales del empleado.
     * @param cargo        Cargo que ocupa el empleado.
     * @param salario      Salario asignado.
     * @param departamento Departamento donde trabaja.
     */
    public Empleado(PersonaNatural persona, String cargo, double salario, String departamento){
        this.cargo = cargo;
        this.salario = salario;
        this.persona = persona;
        this.departamento = departamento;
    }


    /**
     * Obtiene la persona natural asociada al empleado.
     *
     * @return Objeto {@link PersonaNatural} con los datos personales.
     */
    public PersonaNatural getPersona() {
        return persona;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    //Getters y Setters
    public String getNombre(){
        return this.persona.getNombre();
    }

    public String getApellido(){
        return this.persona.getApellido();
    }

    public String getFoto(){
        return this.persona.getFoto();
    }

    public String getNombreCompleto(){
        return this.persona.getNombreUsuario();
    }

    public String getCorreo(){
        return this.persona.getCorreo();
    }

    public String getTelefono(){
        return this.persona.getTelefono();
    }

    public String getCiudad(){
        return this.persona.getCiudad();
    }

    public String getDocumento(){
        return this.persona.getNumeroDocumento();
    }

    public String getDepartamento() {
        return this.departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setContrasenia(String contrasenia) {
        this.persona.setContrasenia(contrasenia);
    }
}
