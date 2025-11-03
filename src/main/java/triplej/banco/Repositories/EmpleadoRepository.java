package triplej.banco.Repositories;

import triplej.banco.Models.Usuarios.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Optional;

public class EmpleadoRepository {
    private static EmpleadoRepository instance;
    private final ArrayList<Empleado> empleados;
    private final UsuarioRepository usuarioRepository;

    public EmpleadoRepository() {
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
                "1238912", "21341", "Colombia", "Bogotá");
        agregarEmpleado(new Empleado(paco, "Celador", 2000, "Seguridad"));

        PersonaNatural persona = new PersonaNatural("Sancho", "Panza", "sancho@uqbank", "123456", RolUsuario.ADMIN,
                TipoDocumento.CEDULACIUDADANIA, "312412", "313414", "Colombia", "Armenia");
        agregarEmpleado(new Empleado(persona, "Admin", 1000.0, "Gestión"));

    }

    public ArrayList<Empleado> getEmpleados() {
        return empleados;
    }

    public void agregarEmpleado(Empleado empleado){
        UsuarioRepository.getInstancia().guardar(empleado.getPersona());
        empleados.add(empleado);
        guardarEnArchivo(empleado);
    }

    public void eliminarEmpleado(Empleado empleado){
        empleados.remove(empleado);
        UsuarioRepository.getInstancia().eliminarUsuario(empleado.getPersona());
        reescribirArchivo();
    }

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

    public Optional<Empleado> buscarPorCorreo(String email) {
        return empleados.stream()
                .filter(e -> e.getPersona().getCorreo().equals(email))
                .findFirst();
    }

    public boolean existeEmpleadoConCorreo(String correo) {
        return empleados.stream()
                .anyMatch(e -> e.getCorreo().equalsIgnoreCase(correo.trim()));
    }

    public void cargarDesdeArchivo(){
        Path ruta = Paths.get("Banco", "Datos", "Empleados.txt");
        if(!Files.exists(ruta)) return;

        try(BufferedReader lector = Files.newBufferedReader(ruta)) {
            lector.readLine();

            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\t");
                if(datos.length < 9) continue;

                String correo = datos[4];
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

                double salario = Double.parseDouble(datos[8].replace(",", "."));
                Empleado empleado = new Empleado(persona, datos[5], salario, datos[6]);
                empleados.add(empleado);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void guardarEnArchivo(Empleado empleado){
        try {
            Path ruta = Paths.get( "Banco","Datos", "Empleados.txt");
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }

            // Si el archivo NO existe, agregamos encabezados primero
            if (!Files.exists(ruta)) {
                String encabezado = String.join(
                        "\t",
                        "Nombre", "Apellido", "Documento", "Teléfono",
                        "Correo", "Cargo", "Departamento", "Ciudad", "Salario"
                ) + "\n";
                Files.writeString(ruta, encabezado, StandardOpenOption.CREATE);
            }

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

            Files.writeString(
                    ruta,
                    linea,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reescribirArchivo(){
        try{
            Path ruta = Paths.get( "Banco", "Datos", "Empleados.txt");
            if(ruta.getParent() != null){
                Files.createDirectories(ruta.getParent());
            }


            StringBuilder contenido = new StringBuilder();
            contenido.append(String.join(
                    "\t",
                    "Nombre", "Apellido", "Documento", "Teléfono",
                    "Correo", "Cargo", "Departamento", "Ciudad", "Salario"
            )).append("\n");

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
            Files.writeString(ruta, contenido.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
