package triplej.banco.Repositories;

import triplej.banco.Models.Usuarios.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de gestionar los datos de los empleados del banco.
 * <p>
 * Administra la carga inicial desde archivos, la creación, actualización
 * y persistencia de los datos relacionados con los empleados.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Cargar empleados desde archivos persistentes.</li>
 *   <li>Guardar nuevos empleados en el sistema.</li>
 *   <li>Actualizar, eliminar o reescribir información de empleados.</li>
 *   <li>Sincronizar los datos con el repositorio de usuarios.</li>
 * </ul>
 *
 * <h3>Patrón aplicado:</h3>
 * Implementa el patrón <b>Singleton</b> para garantizar una única instancia
 * compartida en toda la aplicación.
 */
public class EmpleadoRepository {
    /** Instancia única del repositorio (Singleton). */
    private static EmpleadoRepository instance;

    /** Lista en memoria que contiene todos los empleados cargados o registrados. */
    private final ArrayList<Empleado> empleados;

    /** Repositorio de usuarios, usado para mantener la coherencia entre empleados y usuarios. */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor privado del repositorio.
     * <p>
     * Durante la inicialización:
     * <ol>
     *   <li>Verifica si existe el archivo <code>Empleados.txt</code>.</li>
     *   <li>Si existe, carga los datos desde el archivo.</li>
     *   <li>Si no existe, genera datos de ejemplo para la primera ejecución.</li>
     * </ol>
     * </p>
     */
    private EmpleadoRepository() {
        empleados = new ArrayList<>();
        this.usuarioRepository = UsuarioRepository.getInstancia();

        Path ruta = Paths.get("Banco", "Datos", "Empleados.txt");

        if(Files.exists(ruta)){
            System.out.println("cargando empleados");
            cargarDesdeArchivo();
        }else{
            System.out.println("Primera ejecución");
            cargarDatosEjemplo();
        }

    }

    /**
     * Devuelve la instancia única del repositorio (Singleton).
     */
    public static EmpleadoRepository getInstancia() {
        if(instance == null) {
            instance = new EmpleadoRepository();
        }
        return instance;
    }

    /**
     * Carga empleados de ejemplo para la primera ejecución del sistema.
     * <p>
     * Se crean empleados básicos con datos ficticios para inicializar el sistema
     * la primera vez que se ejecuta.
     * </p>
     */
    private void cargarDatosEjemplo() {
        PersonaNatural juan = new PersonaNatural(
                "Juan", "Henao", "juan@gmail", "1212321", RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA,
                "123213", "2132141", "Colombia", "Bogotá");
        juan.setActivo(true);
        agregarEmpleado(new Empleado(juan, "Jefe",20000, "IT"));

        PersonaNatural paco = new PersonaNatural(
                "Paco", "Jones", "paco@gmail", "123456", RolUsuario.CAJERO, TipoDocumento.CEDULACIUDADANIA,
                "1238912", "21341", "Colombia", "Bogotá");
        paco.setActivo(true);
        agregarEmpleado(new Empleado(paco, "Cajero", 2000, "Seguridad"));

    }

    /** Devuelve la lista completa de empleados en memoria. */
    public List<Empleado> getEmpleados() {
        return empleados;
    }

    /**
     * Agrega un nuevo empleado al repositorio.
     * <p>
     * También guarda al usuario correspondiente y persiste los datos en el archivo.
     */
    public void agregarEmpleado(Empleado empleado){
        UsuarioRepository.getInstancia().guardarUsuario(empleado.getPersona());
        empleados.add(empleado);
        guardarEnArchivo(empleado);
    }

    /**
     * Elimina un empleado del sistema y actualiza el archivo.
     * <p>
     * También elimina el usuario asociado en el repositorio de usuarios.
     * </p>
     */
    public void eliminarEmpleado(Empleado empleado){
        empleados.remove(empleado);
        UsuarioRepository.getInstancia().eliminarUsuario(empleado.getPersona());
        reescribirArchivo();
    }

    /**
     * Actualiza los datos de un empleado y sincroniza los cambios en archivo.
     */
    public void actualizarEmpleado(Empleado empleadoActualizado) {
        for (int i = 0; i < empleados.size(); i++) {
            Empleado empleadoActual = empleados.get(i);
            //  Comparar por documento, accediendo desde PersonaNatural
            if (empleadoActual.getPersona().getId().equals(empleadoActualizado.getPersona().getId())) {
                empleados.set(i, empleadoActualizado);
                break;
            }
        }
        UsuarioRepository.getInstancia().actualizarUsuario(empleadoActualizado.getPersona());
        reescribirArchivo();
    }

    /** Busca un empleado por su correo electrónico. */
    public Optional<Empleado> buscarPorCorreo(String email) {
        return empleados.stream()
                .filter(e -> e.getPersona().getCorreo().equals(email))
                .findFirst();
    }
    /** Verifica si ya existe un empleado registrado con el correo dado. */
    public boolean existeEmpleadoConCorreo(String correo) {
        return empleados.stream()
                .anyMatch(e -> e.getCorreo().equalsIgnoreCase(correo.trim()));
    }

    /**
     * Carga los empleados desde el archivo “Empleados.txt”.
     * <p>
     * Cada línea del archivo representa un empleado con sus datos básicos.
     * Si el usuario ya existe, se asocia; de lo contrario, se crea uno nuevo.
     */
    public void cargarDesdeArchivo(){
        // Se define la ruta del archivo de empleados
        Path ruta = Paths.get("Banco", "Datos", "Empleados.txt");
        // Si el archivo no existe, no hay nada que cargar
        if(!Files.exists(ruta)) return;
        // Se abre el archivo y se leen las líneas una a una
        try(BufferedReader lector = Files.newBufferedReader(ruta)) {
            // Se omite la primera línea (encabezado)
            lector.readLine();

            String linea;
            // Se recorre cada línea del archivo mientras no sea nula
            while ((linea = lector.readLine()) != null) {
                // Se separan los datos usando tabulaciones como delimitadores
                String[] datos = linea.split("\t");
                // Si los datos son insuficientes, se salta la línea
                if(datos.length < 9) continue;

                String correo = datos[4];

                // Se busca si el usuario ya existe
                Optional<Usuario> usuarioExistente = usuarioRepository.buscarUsuarioPorCorreo(correo);

                PersonaNatural persona;
                if (usuarioExistente.isPresent() && usuarioExistente.get() instanceof PersonaNatural) {
                    // Usar el usuario completo del repositorio
                    persona = (PersonaNatural) usuarioExistente.get();
                } else {
                    // Si no existe, crear uno nuevo
                    persona = new PersonaNatural(
                            datos[0],
                            datos[1],
                            correo,
                            "", // Contraseña vacía por defecto
                            RolUsuario.EMPLEADO,
                            TipoDocumento.CEDULACIUDADANIA,
                            datos[2],
                            datos[3],
                            "",
                            datos[7]
                    );
                }

                // Se convierte el salario de texto a número
                double salario = Double.parseDouble(datos[8].replace(",", "."));

                // Se crea el objeto Empleado completo
                Empleado empleado = new Empleado(persona, datos[5], salario, datos[6]);

                // Se agrega a la lista en memoria
                empleados.add(empleado);
            }
        } catch (IOException e) {
            // Si ocurre un error de lectura, se lanza una excepción
            throw new RuntimeException("Error al cargar empleados desde archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Guarda un empleado nuevo en el archivo “Empleados.txt”.
     * <p>
     * Si el archivo no existe, lo crea con una línea de encabezado.
     * Luego agrega una nueva línea con la información del empleado.
     * </p>
     */
    private void guardarEnArchivo(Empleado empleado){
        try {
            // Aquí se define la ruta donde se almacenan los empleados
            Path ruta = Paths.get( "Banco","Datos", "Empleados.txt");

            // Si la carpeta “Banco/Datos” no existe, se crea
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }

            // Si el archivo NO existe, se agrega el encabezado primero
            if (!Files.exists(ruta)) {
                String encabezado = String.join(
                        "\t",
                        "Nombre", "Apellido", "Documento", "Teléfono",
                        "Correo", "Cargo", "Departamento", "Ciudad", "Salario"
                ) + "\n";
                Files.writeString(ruta, encabezado, StandardOpenOption.CREATE);
            }

            // Se arma la línea con los datos del empleado
            String linea = String.format(
                    "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%.2f%n",
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getDocumento(),
                    empleado.getTelefono(),
                    empleado.getCorreo(),
                    empleado.getCargo(),
                    empleado.getDepartamento(),
                    empleado.getCiudad(),
                    empleado.getSalario()
            );

            // Se escribe la línea al final del archivo
            Files.writeString(
                    ruta,
                    linea,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            // Si ocurre un error, se lanza una excepción con el detalle
            throw new RuntimeException("Error al guardar empleado en archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Reescribe completamente el archivo “Empleados.txt” con los datos actuales.
     * <p>
     * Se usa cuando se elimina o actualiza un empleado, para mantener el archivo coherente.
     * </p>
     */
    private void reescribirArchivo(){
        try{
            // Se define la ruta del archivo
            Path ruta = Paths.get( "Banco", "Datos", "Empleados.txt");

            // Si la carpeta no existe, se crea
            if(ruta.getParent() != null){
                Files.createDirectories(ruta.getParent());
            }

            // Se prepara el contenido del archivo comenzando con el encabezado
            StringBuilder contenido = new StringBuilder();
            contenido.append(String.join(
                    "\t",
                    "Nombre", "Apellido", "Documento", "Teléfono",
                    "Correo", "Cargo", "Departamento", "Ciudad", "Salario"
            )).append("\n");

            // Se agrega una línea por cada empleado actual
            for(Empleado empleado : empleados){
                contenido.append(String.format(
                        "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%.2f%n",
                        empleado.getNombre(),
                        empleado.getApellido(),
                        empleado.getDocumento(),
                        empleado.getTelefono(),
                        empleado.getCorreo(),
                        empleado.getCargo(),
                        empleado.getDepartamento(),
                        empleado.getCiudad(),
                        empleado.getSalario()
                ));
            }
            // Se sobrescribe el archivo con los nuevos datos
            Files.writeString(ruta, contenido.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
