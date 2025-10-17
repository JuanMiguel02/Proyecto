package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBancaria;

public class PersonaNatural extends Persona{

    private String nombre;
    private String apellido;

    public PersonaNatural(String nombre, String apellido, String correo, String contrasenia, RolUsuario rolUsuario, TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String pais, String ciudad){
        super(correo, contrasenia, rolUsuario, tipoDocumento, numeroDocumento, telefono, pais, ciudad);
        this.nombre = nombre;
        this.apellido = apellido;
    }

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

    @Override
    public String getNombreCompleto(){
        return this.nombre + " " + this.apellido;
    }

}
