package triplej.banco.Models.Usuarios;

import javafx.scene.image.Image;

import java.time.LocalDate;

public abstract class Usuario {
    private String correo;
    private String contrasenia;
    private LocalDate fechaRegistro;
    private RolUsuario rolUsuario;
    private Image foto;
    private boolean activo;

    public Usuario( String correo, String contrasenia, RolUsuario rolUsuario) {
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rolUsuario = rolUsuario;
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
    }


    public boolean verificarCredenciales(String correo, String contrasenia){
        return this.correo.equals(correo) && this.contrasenia.equals(contrasenia);
    }

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

    public abstract String getNombreCompleto();

    public Image getFoto() {
        return foto;
    }

    public void setFoto(Image foto) {
        this.foto = foto;
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
