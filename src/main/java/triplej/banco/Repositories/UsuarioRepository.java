package triplej.banco.Repositories;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import triplej.banco.Models.Usuarios.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repositorio encargado de gestionar los datos de todos los usuarios del banco.
 * <p>
 * Administra la carga inicial desde archivos, la creación, actualización
 * y persistencia de los datos relacionados con los usuarios.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Cargar usuarios desde archivos persistentes.</li>
 *   <li>Guardar nuevos usuarios en el sistema.</li>
 *   <li>Actualizar, eliminar o reescribir información de usuarios.</li>
 *   <li>Sincronizar los datos.</li>
 * </ul>
 *
 * <h3>Patrón aplicado:</h3>
 * Implementa el patrón <b>Singleton</b> para garantizar una única instancia
 * compartida en toda la aplicación.
 */

public class UsuarioRepository {
    private static UsuarioRepository instancia;
    // Unica lista para todos los usuarios.
    private final ObservableList<Usuario> usuarios;

    /**
     * Constructor privado para aplicar el patrón Singleton.
     * Al instanciarse, verifica si ya existen usuarios guardados en disco; si no, carga datos de ejemplo.
     */
    private UsuarioRepository() {
        this.usuarios = FXCollections.observableArrayList();
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");

        if (Files.exists(ruta)) {
            System.out.println("cargando usuarios");
            cargarDesdeArchivo();

        }else{
            cargarDatosEjemplo();
            System.out.println("Primera ejecución");
        }

    }

    /**
     * Devuelve la única instancia del repositorio (Singleton).
     */
    public static UsuarioRepository getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioRepository();
        }
        return instancia;
    }

     /**
     * Guarda un usuario en la lista y en el archivo de texto.
     * Si el correo ya existe, evita duplicados.
     */
    public void guardarUsuario(Usuario usuario) {
        Optional<Usuario> existente = buscarUsuarioPorCorreo(usuario.getCorreo());
        if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
            return; // Evita duplicar usuarios con el mismo correo
        }

        if (existente.isEmpty()) {
            usuarios.add(usuario);
        }
        guardarEnArchivo(usuario);  // Se escribe o agrega la línea al archivo
    }

    /**
     * Actualiza los datos de un usuario ya existente tanto en memoria como en el archivo.
     */
    public void actualizarUsuario(Usuario usuarioActualizado) {
        Optional<Usuario> existenteOpt = buscarUsuarioPorId(usuarioActualizado.getId());

        if (existenteOpt.isPresent()) {
            Usuario existente = existenteOpt.get();
            // Actualizar campos comunes
            existente.setCorreo(usuarioActualizado.getCorreo());
            existente.setContrasenia(usuarioActualizado.getContrasenia());
            existente.setRolUsuario(usuarioActualizado.getRolUsuario());

            // Si es PersonaNatural, se actualizan sus campos específicos
            if (existente instanceof PersonaNatural personaExistente && usuarioActualizado instanceof PersonaNatural personaNueva) {
                personaExistente.setNombre(personaNueva.getNombre());
                personaExistente.setApellido(personaNueva.getApellido());
                personaExistente.setTelefono(personaNueva.getTelefono());
                personaExistente.setCiudad(personaNueva.getCiudad());
                personaExistente.setPais(personaNueva.getPais());
            }
            // Reescribir archivo completo con los cambios
            reescribirArchivo();
        }
    }

    /**
     * Busca un usuario según su correo.
     */
    public Optional<Usuario> buscarUsuarioPorCorreo(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    /**
     * Busca un usuario según su ID único.
     */
    public Optional<Usuario> buscarUsuarioPorId(UUID id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    /**
     * Verifica si existe un usuario con un correo específico.
     */
    public boolean existeUsuarioConCorreo(String correo) {
        return usuarios.stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo.trim()));
    }

    /**
     * Devuelve todos los usuarios con un rol determinado (ADMIN, EMPLEADO, CLIENTE...).
     */
    public List<Usuario> obtenerPorRol(RolUsuario rol) {
        return usuarios.stream()
                .filter(u -> u.getRolUsuario() == rol)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve la lista observable con todos los usuarios (para usar en interfaces JavaFX).
     */
    public ObservableList<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Elimina un usuario de la lista y reescribe el archivo para reflejar el cambio.
     */
    public void eliminarUsuario(Usuario usuario) {
        usuarios.removeIf(u -> u.getCorreo().equals(usuario.getCorreo()));
        reescribirArchivo();
    }

    /**
     * Retorna la cantidad total de usuarios almacenados.
     */
    public int contarTodos() {
        return usuarios.size();
    }

    /**
     * Carga un usuario de ejemplo por defecto si no existen datos.
     */
    private void cargarDatosEjemplo(){
        UUID idSancho = UUID.fromString("11111111-1111-1111-1111-111111111111");
        PersonaNatural admin = new PersonaNatural("Sancho", "Panza", "sancho@uqbank", "123456", RolUsuario.ADMIN,
                TipoDocumento.CEDULACIUDADANIA, "312412", "313414", "Colombia", "Armenia");
        admin.setId(idSancho);
      guardarUsuario(admin);
    }

    /**
     * Carga todos los usuarios desde el archivo de texto.
     * Cada línea representa un usuario natural o jurídico.
     */
    private void cargarDesdeArchivo() {
        // Ruta del archivo
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
        // Si no existe, no hacemos nada
        if (!Files.exists(ruta)) return;

        // Abrir archivo para lectura (se cierra automáticamente al finalizar)
        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            lector.readLine(); // Saltar encabezado
            String linea;
            // Se recorre cada línea del archivo mientras no sea nula
            while ((linea = lector.readLine()) != null) {
                //  Separar por tabulador
                String[] datos = linea.split("\t");
                //  Validación de columnas mínimas
                if (datos.length < 14) continue;

                // Parseo de campos
                UUID id = UUID.fromString(datos[0]);
                String nombreRazon = datos[1];
                String apellidoRepresentante = datos[2];
                String correo = datos[3];
                String contrasenia = datos[4];
                RolUsuario rol = RolUsuario.valueOf(datos[5]);
                TipoDocumento tipoDoc = TipoDocumento.valueOf(datos[6]);
                String documento = datos[7];
                String telefono = datos[8];
                String pais = datos[9];
                String ciudad = datos[10];
                String tipoEmpresa = datos[11];
                String rutaFoto = datos[12].equals("-") ? null : datos[12];
                boolean activo = Boolean.parseBoolean(datos[13]); //  Nuevo campo

                Usuario usuario;

                //  Construir objeto Usuario según tipoEmpresa
                if (tipoEmpresa.equals("-") || tipoEmpresa.isBlank()) {
                    usuario = new PersonaNatural(
                            nombreRazon, apellidoRepresentante, correo, contrasenia, rol,
                            tipoDoc, documento, telefono, pais, ciudad
                    );
                } else {
                    usuario = new PersonaJuridica(
                            nombreRazon, apellidoRepresentante, tipoEmpresa, correo, contrasenia,
                            rol, tipoDoc, documento, telefono, pais, ciudad
                    );
                }

                // Asignar id, ruta de foto y estado activo
                usuario.setId(id);
                usuario.setFoto(rutaFoto);
                usuario.setActivo(activo);
               // Agregar a la lista en memoria
                usuarios.add(usuario);
            }
            // Impresión informativa
            System.out.println("Usuarios cargados: " + usuarios.size());
        } catch (IOException e) {
            // 6) En caso de error lanzar excepción
            throw new RuntimeException("Error al cargar usuarios desde archivo: " + e.getMessage(), e);
        }
    }

    private void guardarEnArchivo(Usuario usuario) {
        try {
            // Se define la ruta del archivo de usuarios
            Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");

            if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());

            // Si el archivo no existe, crearlo e insertar encabezado
            if (!Files.exists(ruta)) {
                String encabezado = String.join("\t",
                        "Id", "Nombre/RazónSocial", "Apellido/Representante", "Correo", "Contraseña", "Rol",
                        "TipoDocumento", "Documento", "Teléfono", "País", "Ciudad", "TipoEmpresa", "Foto", "Activo"
                ) + "\n";
                Files.writeString(ruta, encabezado, StandardOpenOption.CREATE_NEW);
            }

            // Formatear la línea del usuario
            String linea = formatearLinea(usuario);

            // Escribir la línea al final del archivo (append)
            Files.writeString(ruta, linea, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            // 6) En caso de error lanzar excepción con mensaje claro
            throw new RuntimeException("Error al guardar usuario en archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Reescribe completamente el archivo “Usuarios.txt” con los datos actuales.
     * <p>
     * Se usa cuando se elimina o actualiza un usuario, para mantener el archivo coherente.
     * </p>
     */
    public void reescribirArchivo() {
        try {
            // Se define la ruta del archivo de usuarios
            Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");

            // Si la carpeta no existe, se crea
            if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());

            //Crear el encabezado
            StringBuilder contenido = new StringBuilder();
            contenido.append(String.join("\t",
                    "Id", "Nombre/RazónSocial", "Apellido/Representante", "Correo", "Contraseña", "Rol",
                    "TipoDocumento", "Documento", "Teléfono", "País", "Ciudad", "TipoEmpresa", "Foto", "Activo"
            )).append("\n");

            //Agregar cada usuario con los campos formateados
            for (Usuario usuario : usuarios) {
                contenido.append(formatearLinea(usuario));
            }

            // 5) Escribir todo el contenido borrando el archivo anterior
            Files.writeString(ruta, contenido.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            //Si ocurre un error, se lanza una excepción con el detaller
            throw new RuntimeException("Error al reescribir archivo: " + e.getMessage(), e);
        }
    }


    /**
     * Formatea un usuario en una línea tabulada para almacenar en el archivo.
     * <p>
     * Ejemplo de salida (persona natural):
     * UUID\tNombre\tApellido\tcorreo@x\tcontraseña\tROL\tTIPO_DOC\tNUM_DOC\tTELEFONO\tPAIS\tCIUDAD\t-\tfoto.jpg\ttrue
     */
    private String formatearLinea(Usuario usuario) {
        String base = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n";

        if (usuario instanceof PersonaNatural persona) {
            return String.format(base,
                    persona.getId(),
                    persona.getNombre(),
                    persona.getApellido(),
                    persona.getCorreo(),
                    persona.getContrasenia(),
                    persona.getRolUsuario(),
                    persona.getTipoDocumento(),
                    persona.getNumeroDocumento(),
                    persona.getTelefono(),
                    persona.getPais(),
                    persona.getCiudad(),
                    "-", // Persona natural no tiene empresa
                    persona.getFoto() != null ? persona.getFoto() : "-",
                    persona.isActivo() // nuevo campo
            );
        } else if (usuario instanceof PersonaJuridica persona) {
            return String.format(base,
                    persona.getId(),
                    persona.getRazonSocial(),
                    persona.getRepresentanteLegal(),
                    persona.getCorreo(),
                    persona.getContrasenia(),
                    persona.getRolUsuario(),
                    persona.getTipoDocumento(),
                    persona.getNumeroDocumento(),
                    persona.getTelefono(),
                    persona.getPais(),
                    persona.getCiudad(),
                    persona.getTipoEmpresa(),
                    persona.getFoto() != null ? persona.getFoto() : "-",
                    persona.isActivo()
            );
        }
        return "";
    }

}
