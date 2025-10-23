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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsuarioRepository {
    private static UsuarioRepository instancia;
    // Unica lista para todos los usuarios.
    private final ObservableList<Usuario> usuarios;


    public UsuarioRepository() {
        this.usuarios = FXCollections.observableArrayList();
        cargarDesdeArchivo();

    }

    public static UsuarioRepository getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioRepository();
        }
        return instancia;
    }

    // Unico metodo para guardar cualquier tipo de usuario.
    public void guardar(Usuario usuario) {
        if (buscarUsuarioPorEmail(usuario.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Advertencia: Se intentó guardar un cliente con un email que ya existe: " + usuario.getCorreo());
        }
        usuarios.add(usuario);
        guardarEnArchivo(usuario);
    }

    public Optional<Usuario> buscarUsuarioPorEmail(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
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
        usuarios.remove(usuario);
        reescribirArchivo();
    }

    public int contarTodos() {
        return usuarios.size();
    }

    public void cargarDesdeArchivo() {
        Path ruta = Paths.get("Banco", "Datos", "Usuario");
        if (!Files.exists(ruta)) return;

        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            // Saltar encabezado
            lector.readLine();

            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\t");
                if (datos.length < 10) continue;

                String nombre = datos[0];
                String apellido = datos[1];
                String correo = datos[2];
                String contrasenia = datos[3];
                RolUsuario rol = RolUsuario.valueOf(datos[4]);
                TipoDocumento tipoDoc = TipoDocumento.valueOf(datos[5]);
                String documento = datos[6];
                String telefono = datos[7];
                String pais = datos[8];
                String ciudad = datos[9];

                PersonaNatural persona = new PersonaNatural(
                        nombre,
                        apellido,
                        correo,
                        contrasenia,
                        rol,
                        tipoDoc,
                        documento,
                        telefono,
                        pais,
                        ciudad
                );

                usuarios.add(persona);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void guardarEnArchivo(Usuario usuario) {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Usuario");
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }

            // Si el archivo no existe, agregamos encabezado
            if (!Files.exists(ruta)) {
                String encabezado = String.join("\t",
                        "Nombre", "Apellido", "Correo", "Contraseña", "Rol",
                        "TipoDocumento", "Documento", "Teléfono", "País", "Ciudad"
                ) + "\n";
                Files.writeString(ruta, encabezado, StandardOpenOption.CREATE);
            }

            if (usuario instanceof PersonaNatural persona) {
                String linea = String.format(
                        "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
                        persona.getNombre(),
                        persona.getApellido(),
                        persona.getCorreo(),
                        persona.getContrasenia(),
                        persona.getRolUsuario(),
                        persona.getTipoDocumento(),
                        persona.getNumeroDocumento(),
                        persona.getTelefono(),
                        persona.getPais(),
                        persona.getCiudad()
                );

                Files.writeString(ruta, linea, StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reescribirArchivo() {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Usuario");
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }


            StringBuilder contenido = new StringBuilder();
            contenido.append(String.join(
                    "\t",
                    "Correo", "Contraseña", "Rol usuario"
            )).append("\n");

            for (Usuario usuario : usuarios) {
                if (usuario instanceof PersonaNatural persona) {

                    contenido.append(String.format(
                            "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
                            persona.getNombre(),
                            persona.getApellido(),
                            persona.getCorreo(),
                            persona.getContrasenia(),
                            persona.getRolUsuario(),
                            persona.getTipoDocumento(),
                            persona.getNumeroDocumento(),
                            persona.getTelefono(),
                            persona.getPais(),
                            persona.getCiudad()

                    ));
                }
            }
            Files.writeString(ruta, contenido.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
