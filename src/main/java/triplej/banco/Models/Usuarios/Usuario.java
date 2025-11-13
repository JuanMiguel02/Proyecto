package triplej.banco.Models.Usuarios;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Clase abstracta que representa un usuario dentro del sistema bancario.
 * <p>
 * Esta clase sirve como base para diferentes tipos de usuarios, tanto naturales
 * como jurídicos, proporcionando atributos y comportamientos comunes como
 * la identificación, el correo, la contraseña, la fecha de registro y el estado de activación.
 * </p>
 *
 * <h3>Propósito:</h3>
 * Definir las propiedades generales de cualquier usuario del sistema y
 * establecer la estructura que las clases hijas deben implementar,
 * incluyendo el método {@link #getNombreUsuario()}.
 *
 * @see PersonaNatural
 * @see PersonaJuridica
 */
public abstract class Usuario {

    /** Correo electrónico único del usuario. */
    private String correo;

    /** Contraseña utilizada para autenticación. */
    private String contrasenia;

    /** Fecha en la que el usuario fue registrado en el sistema. */
    private LocalDate fechaRegistro;

    /** Rol asignado al usuario (por ejemplo: CLIENTE, ADMINISTRADOR, EMPLEADO). */
    private RolUsuario rolUsuario;

    /** Ruta o nombre del archivo de imagen de perfil del usuario. */
    private String rutaImagen;

    /** Indica si la cuenta del usuario está activa. */
    private boolean activo;

    /** Identificador único universal (UUID) del usuario. */
    private UUID id;

    /**
     * Constructor base para inicializar un nuevo usuario.
     *
     * @param correo       Correo electrónico del usuario.
     * @param contrasenia  Contraseña de acceso.
     * @param rolUsuario   Rol del usuario dentro del sistema.
     */
    public Usuario( String correo, String contrasenia, RolUsuario rolUsuario) {
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rolUsuario = rolUsuario;
        this.fechaRegistro = LocalDate.now();
        this.activo = false;
        this.id = UUID.randomUUID();
    }

    //Getters y Setters

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public UUID getId(){return this.id; }

    public void setId(UUID id){this.id = id;}

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public RolUsuario getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(RolUsuario rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    /**
     * Obtiene el nombre con el que se identifica al usuario en el sistema.
     * <p>
     * Este método debe ser implementado por las clases hijas para
     * especificar cómo se construye el nombre de usuario (por ejemplo,
     * con nombre y apellido o razón social).
     * </p>
     *
     * @return Nombre legible del usuario.
     */
    public abstract String getNombreUsuario();

    public String getFoto() {
        return this.rutaImagen;
    }

    public void setFoto(String foto) {
        this.rutaImagen = foto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                ", correo='" + correo + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
