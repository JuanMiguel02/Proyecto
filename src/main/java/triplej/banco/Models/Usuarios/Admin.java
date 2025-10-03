package triplej.banco.Models.Usuarios;

public class Admin extends Usuario{
    public Admin(String nombre,TipoDocumento documento, String numeroDocumento, String telefono, String email, String contrasenia, String pais, String ciudad) {
        super(nombre, documento, numeroDocumento, telefono, email, contrasenia, pais, ciudad);
    }


}
