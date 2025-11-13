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

public class UsuarioRepository {
    private static UsuarioRepository instancia;
    // Unica lista para todos los usuarios.
    private final ObservableList<Usuario> usuarios;

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

    public static UsuarioRepository getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioRepository();
        }
        return instancia;
    }

    // Unico metodo para guardarUsuario cualquier tipo de usuario.
    public void guardarUsuario(Usuario usuario) {
        Optional<Usuario> existente = buscarUsuarioPorCorreo(usuario.getCorreo());
        if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
            return;
        }

        if (existente.isEmpty()) {
            usuarios.add(usuario);
        }
        guardarEnArchivo(usuario);
    }

    public void actualizarUsuario(Usuario usuarioActualizado) {
        Optional<Usuario> existenteOpt = buscarUsuarioPorId(usuarioActualizado.getId());

        if (existenteOpt.isPresent()) {
            Usuario existente = existenteOpt.get();

            existente.setCorreo(usuarioActualizado.getCorreo());
            existente.setContrasenia(usuarioActualizado.getContrasenia());
            existente.setRolUsuario(usuarioActualizado.getRolUsuario());

            if (existente instanceof PersonaNatural personaExistente && usuarioActualizado instanceof PersonaNatural personaNueva) {
                personaExistente.setNombre(personaNueva.getNombre());
                personaExistente.setApellido(personaNueva.getApellido());
                personaExistente.setTelefono(personaNueva.getTelefono());
                personaExistente.setCiudad(personaNueva.getCiudad());
                personaExistente.setPais(personaNueva.getPais());
            }

            reescribirArchivo();
        }
    }

    public Optional<Usuario> buscarUsuarioPorCorreo(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    public Optional<Usuario> buscarUsuarioPorId(UUID id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public boolean existeUsuarioConCorreo(String correo) {
        return usuarios.stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo.trim()));
    }

    public List<Usuario> obtenerPorRol(RolUsuario rol) {
        return usuarios.stream()
                .filter(u -> u.getRolUsuario() == rol)
                .collect(Collectors.toList());
    }

    public ObservableList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void eliminarUsuario(Usuario usuario) {
        usuarios.removeIf(u -> u.getCorreo().equals(usuario.getCorreo()));
        reescribirArchivo();
    }

    public int contarTodos() {
        return usuarios.size();
    }

    private void cargarDatosEjemplo(){
        UUID idSancho = UUID.fromString("11111111-1111-1111-1111-111111111111");
        PersonaNatural admin = new PersonaNatural("Sancho", "Panza", "sancho@uqbank", "123456", RolUsuario.ADMIN,
                TipoDocumento.CEDULACIUDADANIA, "312412", "313414", "Colombia", "Armenia");
        admin.setId(idSancho);
      guardarUsuario(admin);
    }


    private void cargarDesdeArchivo() {
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
        if (!Files.exists(ruta)) return;

        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            lector.readLine(); // Saltar encabezado
            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\t");
                if (datos.length < 14) continue; // ahora esperamos 14 columnas

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

                usuario.setId(id);
                usuario.setFoto(rutaFoto);
                usuario.setActivo(activo); //  Guardamos el estado
                usuarios.add(usuario);
            }

            System.out.println("Usuarios cargados: " + usuarios.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void guardarEnArchivo(Usuario usuario) {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");

            if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());

            if (!Files.exists(ruta)) {
                String encabezado = String.join("\t",
                        "Id", "Nombre/RazónSocial", "Apellido/Representante", "Correo", "Contraseña", "Rol",
                        "TipoDocumento", "Documento", "Teléfono", "País", "Ciudad", "TipoEmpresa", "Foto", "Activo"
                ) + "\n";
                Files.writeString(ruta, encabezado, StandardOpenOption.CREATE_NEW);
            }

            String linea = formatearLinea(usuario);
            Files.writeString(ruta, linea, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardarUsuario en archivo: " + e.getMessage(), e);
        }
    }

    public void reescribirArchivo() {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
            if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());

            StringBuilder contenido = new StringBuilder();
            contenido.append(String.join("\t",
                    "Id", "Nombre/RazónSocial", "Apellido/Representante", "Correo", "Contraseña", "Rol",
                    "TipoDocumento", "Documento", "Teléfono", "País", "Ciudad", "TipoEmpresa", "Foto", "Activo"
            )).append("\n");

            for (Usuario usuario : usuarios) {
                contenido.append(formatearLinea(usuario));
            }

            Files.writeString(ruta, contenido.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Error al reescribir archivo: " + e.getMessage(), e);
        }
    }

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
