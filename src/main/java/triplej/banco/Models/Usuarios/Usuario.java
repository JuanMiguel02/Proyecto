package triplej.banco.Models.Usuarios;

import java.time.LocalDate;

public abstract class Usuario {
    private String nombre;
    private TipoDocumento documento;
    private String numeroDocumento;
    private String telefono;
    private String correo;
    private String contrasenia;
    private String pais;
    private String ciudad;
    private LocalDate fechaRegistro;

    public Usuario() {
    }

    public Usuario(String nombre, TipoDocumento documento, String numeroDocumento, String telefono, String correo, String contrasenia, String pais, String ciudad) {
        this.nombre = nombre;
        this.documento = documento;
        this.numeroDocumento = numeroDocumento;
        this.telefono = telefono;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.pais = pais;
        this.ciudad = ciudad;
        this.fechaRegistro = LocalDate.now();
    }

    public boolean verificarCredenciales(String correo, String contrasenia){
        return this.correo.equals(correo) && this.contrasenia.equals(contrasenia);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoDocumento getDocumento() {
        return documento;
    }

    public void setDocumento(TipoDocumento documento) {
        this.documento = documento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return correo;
    }

    public void setEmail(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getDireccion(){
        return pais + ciudad;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
