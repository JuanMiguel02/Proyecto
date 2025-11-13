package triplej.banco.Models.Usuarios;

/**
 * Representa a una persona natural dentro del sistema bancario.
 * <p>
 * Extiende de {@link Persona} y añade información personal básica como
 * el nombre y el apellido. Este tipo de usuario corresponde a clientes individuales,
 * a diferencia de {@link PersonaJuridica} que representa entidades o empresas.
 * </p>
 *
 * <h3>Propósito:</h3>
 * Modelar los datos de usuarios particulares que poseen cuentas bancarias
 * personales dentro del sistema.
 */
public class PersonaNatural extends Persona{

    /** Nombre propio de la persona. */
    private String nombre;

    /** Apellido de la persona. */
    private String apellido;

    /**
     * Crea una nueva persona natural con los datos personales y de contacto.
     *
     * @param nombre           Nombre del cliente.
     * @param apellido         Apellido del cliente.
     * @param correo           Correo electrónico del cliente.
     * @param contrasenia      Contraseña de acceso al sistema.
     * @param rolUsuario       Rol asignado dentro del sistema (por ejemplo, CLIENTE).
     * @param tipoDocumento    Tipo de documento de identidad (por ejemplo, C.C. o C.E.).
     * @param numeroDocumento  Número del documento de identidad.
     * @param telefono         Número de contacto.
     * @param pais             País de residencia.
     * @param ciudad           Ciudad de residencia.
     */
    public PersonaNatural(String nombre, String apellido, String correo, String contrasenia, RolUsuario rolUsuario, TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String pais, String ciudad){
        super(correo, contrasenia, rolUsuario, tipoDocumento, numeroDocumento, telefono, pais, ciudad);
        this.nombre = nombre;
        this.apellido = apellido;
    }

    //Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }


    /**
     * Devuelve el nombre de usuario mostrado en el sistema,
     * compuesto por el nombre y el apellido.
     *
     * @return Nombre completo del cliente.
     */
    @Override
    public String getNombreUsuario(){
        return this.nombre + " " + this.apellido;
    }

}
