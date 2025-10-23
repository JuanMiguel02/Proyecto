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

    private EmpleadoRepository() {
        empleados = new ArrayList<>();
        this.usuarioRepository = UsuarioRepository.getInstancia();

        cargarDesdeArchivo();

        if(empleados.isEmpty()){
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
                "1233", "21341", "Colombia", "Bogotá");
        agregarEmpleado(new Empleado(paco, "Celador", 2000, "Seguridad"));
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

    public Empleado buscarPorNombreYApellido(String nombre,String apellido){
        return empleados.stream()
                .filter(e -> e.getPersona().getNombreCompleto().equals(nombre + " " + apellido))
                .findFirst()
                .orElse(null);
    }

    public void cargarDesdeArchivo(){
        Path ruta = Paths.get("Banco", "Datos", "Empleado");
        if(!Files.exists(ruta)) return;

        try(BufferedReader lector = Files.newBufferedReader(ruta)) {
            lector.readLine();

            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\t");
                if(datos.length < 9) continue;

                String correo = datos[4];
                Optional<Usuario> usuarioExistente = usuarioRepository.buscarUsuarioPorEmail(correo);
                String contrasenia = usuarioExistente.map(Usuario::getContrasenia).orElse("");

                PersonaNatural persona = new PersonaNatural(
                        datos[0],
                        datos[1],
                        correo,
                        contrasenia,
                        RolUsuario.EMPLEADO,
                        TipoDocumento.CEDULACIUDADANIA,
                        datos[2],
                        datos[3],
                        "",
                        datos[7]

                );

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
            Path ruta = Paths.get( "Banco","Datos", "Empleado");
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }

            // Si el archivo NO existe, agregamos encabezados primero
            if (!Files.exists(ruta)) {
                String encabezado = String.join(
                        "\t",
                        "Nombre", "Apellido", "Documento", "Teléfono",
                        "Correo", "Cargo", "Departamento", "Ciudad", "Salario Empleado"
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
            Path ruta = Paths.get( "Banco", "Datos", "Empleado");
            if(ruta.getParent() != null){
                Files.createDirectories(ruta.getParent());
            }


        StringBuilder contenido = new StringBuilder();
            contenido.append(String.join(
                    "\t",
                    "Nombre", "Apellido", "Documento", "Teléfono",
                    "Correo", "Cargo", "Departamento", "Ciudad", "Salario Empleado"
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
