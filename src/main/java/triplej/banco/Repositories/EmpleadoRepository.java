package triplej.banco.Repositories;

import triplej.banco.Models.Usuarios.Empleado;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.TipoDocumento;


import java.util.ArrayList;

public class EmpleadoRepository {
    private static EmpleadoRepository instance;
    private final ArrayList<Empleado> empleados;
    private final UsuarioRepository usuarioRepository;

    private EmpleadoRepository() {
        empleados = new ArrayList<>();
        this.usuarioRepository = UsuarioRepository.getInstancia();
        cargarDatosEjemplo();
    }

    public static EmpleadoRepository getInstance() {
        if(instance == null) {
            instance = new EmpleadoRepository();
        }
        return instance;
    }

    private void cargarDatosEjemplo() {
        PersonaNatural juan = new PersonaNatural(
                "Juan", "Henao", "juan@gmail", "1212321", RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA,
                "123213", "2132141", "Colombia", "Bogotá");
        agregarEmpleado(new Empleado(juan, "Jefe",20000, "IT"));

        PersonaNatural paco = new PersonaNatural(
                "Paco", "Jones", "paco@gmail", "1212321", RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA,
                "1233", "21341", "Colombia", "Bogotá");
        agregarEmpleado(new Empleado(paco, "Celador", 2000, "Seguridad"));
    }

    public ArrayList<Empleado> getEmpleados() {
        return empleados;
    }

    public void agregarEmpleado(Empleado empleado){
        UsuarioRepository.getInstancia().guardar(empleado.getPersona());
        empleados.add(empleado);
    }

    public void eliminarEmpleado(Empleado empleado){
        empleados.remove(empleado);
        UsuarioRepository.getInstancia().eliminarUsuario(empleado.getPersona());
    }

    public Empleado buscarPorNombreYApellido(String nombre,String apellido){
        return empleados.stream()
                .filter(e -> e.getPersona().getNombreCompleto().equals(nombre + " " + apellido))
                .findFirst()
                .orElse(null);
    }
}
