package triplej.banco.Models.Usuarios;

/**
 * Clase abstracta que representa una persona dentro del sistema bancario.
 * <p>
 * Extiende de {@link Usuario} para incluir información de identificación
 * y contacto adicional, como documento, teléfono, país y ciudad.
 * </p>
 *
 * <h3>Propósito:</h3>
 * Esta clase sirve como base para las clases concretas {@link PersonaNatural}
 * o {@code PersonaJuridica}, que representan distintos tipos de clientes o empleados.
 */
public abstract class Persona extends Usuario{

    /** Tipo de documento de identidad (por ejemplo, Cédula, Pasaporte, NIT). */
    private TipoDocumento tipoDocumento;

    /** Número de documento de identificación. */
    private String numeroDocumento;

    /** Número telefónico de contacto. */
    private String telefono;

    /** País de residencia. */
    private String pais;

    /** Ciudad de residencia. */
    private String ciudad;

    /**
     * Crea una nueva persona con los datos básicos de usuario e información de identificación.
     *
     * @param correo          Correo electrónico del usuario.
     * @param contrasenia     Contraseña asociada a la cuenta.
     * @param rolUsuario      Rol que cumple la persona en el sistema (Cliente, Empleado, etc.).
     * @param tipoDocumento   Tipo de documento de identidad.
     * @param numeroDocumento Número de documento de identidad.
     * @param telefono        Número telefónico.
     * @param pais            País de residencia.
     * @param ciudad          Ciudad de residencia.
     */
    public Persona(String correo, String contrasenia, RolUsuario rolUsuario, TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String pais, String ciudad){
        super(correo, contrasenia, rolUsuario);
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.telefono = telefono;
        this.pais = pais;
        this.ciudad = ciudad;

    }

    //Getters y Setters
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "tipoDocumento=" + tipoDocumento +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", telefono='" + telefono + '\'' +
                ", pais='" + pais + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
