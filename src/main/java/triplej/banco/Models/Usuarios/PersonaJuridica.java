package triplej.banco.Models.Usuarios;

/**
 * Representa a una persona jurídica (empresa o entidad) dentro del sistema bancario.
 * <p>
 * Extiende de {@link Persona}, añadiendo información específica de empresas
 * como la razón social, el representante legal y el tipo de empresa.
 * </p>
 *
 * <h3>Propósito:</h3>
 * Modelar los datos de clientes empresariales o corporativos que mantienen
 * cuentas bancarias a nombre de una organización.
 */
public class PersonaJuridica extends Persona{

    /** Nombre legal registrado de la empresa. */
    private String razonSocial;

    /** Persona que actúa como representante legal de la empresa. */
    private String representanteLegal;

    /** Clasificación o tipo de empresa (por ejemplo, S.A., S.A.S., E.U., etc.). */
    private String tipoEmpresa;

    /**
     * Crea una nueva persona jurídica con la información empresarial y de contacto.
     *
     * @param razonSocial        Razón social o nombre legal de la empresa.
     * @param representanteLegal Nombre del representante legal.
     * @param tipoEmpresa        Tipo o categoría de empresa (S.A., S.A.S., etc.).
     * @param correo             Correo electrónico de contacto.
     * @param contrasenia        Contraseña para el acceso al sistema.
     * @param rolUsuario         Rol asignado dentro del sistema (por ejemplo, CLIENTE_EMPRESARIAL).
     * @param tipoDocumento      Tipo de documento de identificación (por ejemplo, NIT).
     * @param numeroDocumento    Número de documento de la empresa.
     * @param telefono           Número de contacto.
     * @param pais               País de residencia o registro.
     * @param ciudad             Ciudad de residencia o registro.
     */
    public PersonaJuridica(String razonSocial, String representanteLegal, String tipoEmpresa, String correo, String contrasenia, RolUsuario rolUsuario, TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String pais, String ciudad){
        super(correo, contrasenia, rolUsuario, tipoDocumento, numeroDocumento, telefono, pais, ciudad);
        this.razonSocial = razonSocial;
        this.representanteLegal = representanteLegal;
        this.tipoEmpresa = tipoEmpresa;
    }

    //Getters y Setters

    /**
     * Retorna el nombre de usuario asociado a esta cuenta, que corresponde a la razón social.
     *
     * @return Nombre legal de la empresa.
     */
    @Override
    public String getNombreUsuario(){
        return this.razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTipoEmpresa() {
        return this.tipoEmpresa;
    }

    public String getRazonSocial() {return this.razonSocial;}

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

}
