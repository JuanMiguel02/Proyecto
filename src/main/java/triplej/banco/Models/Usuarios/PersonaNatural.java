package triplej.banco.Models.Usuarios;

public class PersonaNatural extends Cliente{

    private String apellido;

    public PersonaNatural(String nombre, String apellido, TipoDocumento documento, String numeroDocumento, String telefono, String email, String contrasenia, String pais, String ciudad){
        super(nombre, documento, numeroDocumento, telefono, email, contrasenia, pais, ciudad);
        this.apellido = apellido;
    }

    public String getNombreCompleto(){
        return getNombre() + " " + this.apellido;
    }
}
