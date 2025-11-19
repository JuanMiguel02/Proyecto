package triplej.banco.Services;

import triplej.banco.Models.Reportes.Reporte;
import triplej.banco.Models.Reportes.ReporteAdmin;
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

/**
 * Servicio encargado de gestionar la lógica del módulo de administración.
 * <p>
 * Incluye:
 * <ul>
 *   <li>Registro, actualización y eliminación de empleados.</li>
 *   <li>Validación de correos.</li>
 *   <li>Gestión y almacenamiento de imágenes de perfil.</li>
 *   <li>Generación de reportes administrativos.</li>
 * </ul>
 */
public class AdminService {
    // Carpeta donde se almacenan las imágenes de empleados dentro del directorio del usuario.
    private static final String RUTA_IMAGENES =
            System.getProperty("user.home") + File.separator + "UQBank" + File.separator + "imagenes";
    // Imagen por defecto si el usuario no selecciona una foto.
    private static final String IMAGEN_POR_DEFECTO = "/triplej/banco/Images/avatar.png";

    // Repositorios usados por el servicio
    private final EmpleadoRepository empleadoRepository = EmpleadoRepository.getInstancia();
    private final UsuarioRepository usuarioRepository = UsuarioRepository.getInstancia();

    // ---------------------------------------
    //  GESTIÓN DE EMPLEADOS
    // ---------------------------------------

    /**
     * Registra un nuevo empleado en el sistema.
     *
     * <p><b>Paso a paso:</b></p>
     * <ol>
     *   <li>Guarda la imagen del empleado en la carpeta designada (si fue seleccionada).</li>
     *   <li>Crea una instancia de {@link Empleado} con los datos proporcionados.</li>
     *   <li>Agrega el empleado al repositorio de empleados para persistencia.</li>
     *   <li>Si ocurre algún error, lanza una excepción con un mensaje descriptivo.</li>
     * </ol>
     *
     * @param persona datos personales del empleado
     * @param cargo puesto que ocupará
     * @param salario salario asignado
     * @param departamento área o dependencia donde trabajará
     * @param imagenSeleccionada archivo de imagen (puede ser null)
     * @return el empleado registrado
     */
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

    /**
     * Verifica si un correo ya está registrado en el sistema (ya sea en usuarios o empleados).
     *
     * @param correo correo electrónico a verificar
     * @return true si el correo ya existe; false si es nuevo
     */
    public boolean correoYaExiste(String correo) {
        String correoNormalizado = correo.trim().toLowerCase();
        return empleadoRepository.existeEmpleadoConCorreo(correoNormalizado)
                || usuarioRepository.existeUsuarioConCorreo(correoNormalizado);
    }

    // ---------------------------------------
    //  MANEJO DE IMÁGENES
    // ---------------------------------------

    /**
     * Guarda la foto de perfil del empleado en la carpeta local “UQBank/imagenes”.
     *
     * <p><b>Paso a paso:</b></p>
     * <ol>
     *   <li>Define la ruta base donde se almacenarán las imágenes.</li>
     *   <li>Si la carpeta no existe, la crea automáticamente.</li>
     *   <li>Genera un nombre de archivo único usando el número de documento del empleado.</li>
     *   <li>Si el usuario seleccionó una imagen, la copia a la carpeta destino.</li>
     *   <li>Si no seleccionó ninguna, se asigna la imagen por defecto.</li>
     * </ol>
     *
     * @param persona objeto {@link PersonaNatural} al que se le asociará la foto
     * @param imagenSeleccionada archivo de imagen seleccionado (puede ser null)
     * @throws IOException si ocurre un error al copiar o crear la carpeta
     */
    private void guardarFotoEmpleado(PersonaNatural persona, File imagenSeleccionada) throws IOException {
        // Crear (si no existe) la carpeta de destino donde se guardarán las fotos.
        Path carpeta = Paths.get(RUTA_IMAGENES);
        Files.createDirectories(carpeta);

        //  Definir el nombre del archivo basado en el número de documento del empleado.
        String nombreArchivo = persona.getNumeroDocumento() + ".jpg";
        Path destino = carpeta.resolve(nombreArchivo);

        // Si el usuario eligió una foto, se copia al destino y se guarda la ruta.
        if (imagenSeleccionada != null) {
            Files.copy(imagenSeleccionada.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            persona.setFoto(destino.toString());
        } else {
            // Si no hay imagen seleccionada, se asigna la imagen por defecto.
            persona.setFoto(IMAGEN_POR_DEFECTO);
        }
    }

    /**
     * Obtiene la lista completa de empleados registrados.
     *
     * @return lista de empleados
     */
    public List<Empleado> obtenerEmpleados() {
        return empleadoRepository.getEmpleados();
    }

    /**
     * Elimina un empleado del sistema (tanto de la lista en memoria como del archivo).
     *
     * @param empleado empleado a eliminar
     * @return true si se eliminó correctamente, false si el parámetro era null
     */
    public boolean eliminarEmpleado(Empleado empleado) {
        if (empleado == null) return false;
        empleadoRepository.eliminarEmpleado(empleado);
        return true;
    }

    /**
     * Actualiza los datos de un empleado existente.
     *
     * <p><b>Paso a paso:</b></p>
     * <ol>
     *   <li>Actualiza la información personal (nombre, correo, teléfono, ciudad, rol, etc.).</li>
     *   <li>Actualiza los datos laborales (cargo, departamento, salario).</li>
     *   <li>Si la contraseña no está vacía, también se reemplaza.</li>
     *   <li>Finalmente, guarda los cambios en los respectivos repositorios para persistencia.</li>
     * </ol>
     *
     * @param empleado empleado a actualizar
     * @param nombre nuevo nombre
     * @param apellido nuevo apellido
     * @param correo nuevo correo electrónico
     * @param telefono nuevo número telefónico
     * @param ciudad nueva ciudad
     * @param cargo nuevo cargo
     * @param departamento nuevo departamento
     * @param salario nuevo salario
     * @param contrasenia nueva contraseña (si se deja vacía, no se cambia)
     * @param rol nuevo rol del usuario (ADMIN, CAJERO, EMPLEADO, etc.)
     */
    public void actualizarEmpleado(
            Empleado empleado,
            String nombre, String apellido, String correo, String telefono, String ciudad,
            String cargo, String departamento, double salario, String contrasenia, RolUsuario rol
    ) {
        // Actualiza la información personal asociada al empleado.
        empleado.getPersona().setNombre(nombre);
        empleado.getPersona().setApellido(apellido);
        empleado.getPersona().setCorreo(correo);
        empleado.getPersona().setTelefono(telefono);
        empleado.getPersona().setCiudad(ciudad);
        empleado.getPersona().setRolUsuario(rol);

        // Actualiza la información laboral.
        empleado.setCargo(cargo);
        empleado.setDepartamento(departamento);
        empleado.setSalario(salario);

        // Si la contraseña no está vacía, también se actualiza
        if (!contrasenia.isBlank()) {
            empleado.getPersona().setContrasenia(contrasenia);
        }

        // Persiste los cambios tanto en UsuarioRepository como en EmpleadoRepository.
        usuarioRepository.actualizarUsuario(empleado.getPersona());
        empleadoRepository.actualizarEmpleado(empleado);
    }

    /**
     * Determina el rol del usuario según el texto del cargo.
     *
     * <p>Ejemplo:
     * <ul>
     *   <li>“Administrador General” → RolUsuario.ADMIN</li>
     *   <li>“Cajero Principal” → RolUsuario. CAJERO</li>
     *   <li>Cualquier otro → RolUsuario. EMPLEADO</li>
     * </ul>
     * </p>
     *
     * @param cargo texto del cargo
     * @return el rol correspondiente
     */
    public RolUsuario determinarRolPorCargo(String cargo) {
        String upper = cargo.toUpperCase();
        if (upper.contains("ADMIN")) return RolUsuario.ADMIN;
        if (upper.contains("CAJERO")) return RolUsuario.CAJERO;
        return RolUsuario.EMPLEADO;
    }

    /**
     * Genera un reporte administrativo avanzado.
     *
     * <p>Este método crea una instancia de {@link ReporteAdmin} y ejecuta su método
     * {@code generarReporte()}, devolviendo el objeto {@link Reporte} generado.</p>
     *
     * @return objeto de tipo Reporte con la información consolidada del sistema
     */
    public Reporte generarReporteAvanzado(){
        ReporteAdmin reporteAvanzado = new ReporteAdmin();
        return reporteAvanzado.generarReporte();
    }
}


