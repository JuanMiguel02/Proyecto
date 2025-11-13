package triplej.banco.Services;

import triplej.banco.Models.Usuarios.Empleado;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AdminService {

    private static final String RUTA_IMAGENES =
            System.getProperty("user.home") + File.separator + "UQBank" + File.separator + "imagenes";
    private static final String IMAGEN_POR_DEFECTO = "/triplej/banco/Images/avatar.png";

    private final EmpleadoRepository empleadoRepository = EmpleadoRepository.getInstance();
    private final UsuarioRepository usuarioRepository = UsuarioRepository.getInstancia();

    // ---------------------------------------
    //  GESTIÓN DE EMPLEADOS
    // ---------------------------------------
    public Empleado registrarEmpleado(PersonaNatural persona, String cargo, double salario, String departamento, File imagenSeleccionada) {
        try {
            guardarFotoEmpleado(persona, imagenSeleccionada);

            Empleado empleado = new Empleado(persona, cargo, salario, departamento);
            empleadoRepository.agregarEmpleado(empleado);
            return empleado;

        } catch (Exception e) {
            throw new RuntimeException("Error al registrar empleado: " + e.getMessage(), e);
        }
    }

    public boolean correoYaExiste(String correo) {
        String correoNormalizado = correo.trim().toLowerCase();
        return empleadoRepository.existeEmpleadoConCorreo(correoNormalizado)
                || usuarioRepository.existeUsuarioConCorreo(correoNormalizado);
    }

    // ---------------------------------------
    //  MANEJO DE IMÁGENES
    // ---------------------------------------
    private void guardarFotoEmpleado(PersonaNatural persona, File imagenSeleccionada) throws IOException {
        Path carpeta = Paths.get(RUTA_IMAGENES);
        Files.createDirectories(carpeta);

        String nombreArchivo = persona.getNumeroDocumento() + ".jpg";
        Path destino = carpeta.resolve(nombreArchivo);

        if (imagenSeleccionada != null) {
            Files.copy(imagenSeleccionada.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            persona.setFoto(destino.toString());
        } else {
            persona.setFoto(IMAGEN_POR_DEFECTO);
        }
    }

    public List<Empleado> obtenerEmpleados() {
        return empleadoRepository.getEmpleados();
    }

    public boolean eliminarEmpleado(Empleado empleado) {
        if (empleado == null) return false;
        empleadoRepository.eliminarEmpleado(empleado);
        return true;
    }

    public void actualizarEmpleado(
            Empleado empleado,
            String nombre, String apellido, String correo, String telefono, String ciudad,
            String cargo, String departamento, double salario, String contrasenia, RolUsuario rol
    ) {
        empleado.getPersona().setNombre(nombre);
        empleado.getPersona().setApellido(apellido);
        empleado.getPersona().setCorreo(correo);
        empleado.getPersona().setTelefono(telefono);
        empleado.getPersona().setCiudad(ciudad);
        empleado.getPersona().setRolUsuario(rol);

        empleado.setCargo(cargo);
        empleado.setDepartamento(departamento);
        empleado.setSalario(salario);

        if (!contrasenia.isBlank()) {
            empleado.getPersona().setContrasenia(contrasenia);
        }

        usuarioRepository.actualizarUsuario(empleado.getPersona());
        empleadoRepository.actualizarEmpleado(empleado);
    }

    public RolUsuario determinarRolPorCargo(String cargo) {
        String upper = cargo.toUpperCase();
        if (upper.contains("ADMIN")) return RolUsuario.ADMIN;
        if (upper.contains("CAJERO")) return RolUsuario.CAJERO;
        return RolUsuario.EMPLEADO;
    }
}


