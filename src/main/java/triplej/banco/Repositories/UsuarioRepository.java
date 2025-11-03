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

    public UsuarioRepository() {
        this.usuarios = FXCollections.observableArrayList();
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");

        if (Files.exists(ruta)) {
                System.out.println("cargando empleados");
                cargarDesdeArchivo();

        }else{
            System.out.println("Primera ejecución");
//            cargarDatosEjemplo();
        }

    }

    public static UsuarioRepository getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioRepository();
        }
        return instancia;
    }

    // Unico metodo para guardar cualquier tipo de usuario.
    public void guardar(Usuario usuario) {
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

    public void cargarDesdeArchivo() {
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
        if (!Files.exists(ruta)) return;

        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            // Saltar encabezado
            lector.readLine();

            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\t");
                if (datos.length < 12) continue;

                UUID id = UUID.fromString(datos[0]);
                String campo12 = datos[11].trim(); // TipoEmpresa

                // Si está vacío o es "-", es persona natural
                if (campo12.isEmpty() || campo12.equals("-")) {
                    String nombre = datos[1];
                    String apellido = datos[2];
                    String correo = datos[3];
                    String contrasenia = datos[4];
                    RolUsuario rol = RolUsuario.valueOf(datos[5]);
                    TipoDocumento tipoDoc = TipoDocumento.valueOf(datos[6]);
                    String documento = datos[7];
                    String telefono = datos[8];
                    String pais = datos[9];
                    String ciudad = datos[10];

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
                    persona.setId(id);
                    usuarios.add(persona);

                } else {
                    // Persona Jurídica
                    String razonSocial = datos[1];
                    String representante = datos[2];
                    String correo = datos[3];
                    String contrasenia = datos[4];
                    RolUsuario rol = RolUsuario.valueOf(datos[5]);
                    TipoDocumento tipoDoc = TipoDocumento.valueOf(datos[6]);
                    String nit = datos[7];
                    String telefono = datos[8];
                    String pais = datos[9];
                    String ciudad = datos[10];
                    String tipoEmpresa = datos[11];

                    PersonaJuridica persona = new PersonaJuridica(
                            razonSocial,
                            representante,
                            tipoEmpresa,
                            correo,
                            contrasenia,
                            rol,
                            tipoDoc,
                            nit,
                            telefono,
                            pais,
                            ciudad
                    );
                    persona.setId(id);
                    usuarios.add(persona);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void guardarEnArchivo(Usuario usuario) {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }

            // Si el archivo no existe, agregamos encabezado
            if (!Files.exists(ruta)) {
                String encabezado = String.join("\t",
                        "Id", "Nombre/RazónSocial", "Apellido/Representante", "Correo", "Contraseña", "Rol",
                        "TipoDocumento", "Documento", "Teléfono", "País", "Ciudad", "TipoEmpresa"
                ) + "\n";
                Files.writeString(ruta, encabezado, StandardOpenOption.CREATE);
            }

            String linea = "";

            if (usuario instanceof PersonaNatural persona) {
                linea = String.format(
                        "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
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
                        ""
                );

            }

            else if (usuario instanceof PersonaJuridica persona) {
                 linea = String.format(
                        "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
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
                        persona.getTipoEmpresa()
                );
            }

        if(!linea.isEmpty()){
            Files.writeString(ruta, linea, StandardOpenOption.APPEND);
        }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reescribirArchivo() {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }

            StringBuilder contenido = new StringBuilder();
            contenido.append(String.join(
                    "\t",
                    "Id", "Nombre/RazónSocial", "Apellido/Representante", "Correo", "Contraseña", "Rol",
                    "TipoDocumento", "Documento", "Teléfono", "País", "Ciudad", "TipoEmpresa"
            )).append("\n");

            for (Usuario usuario : usuarios) {

                if (usuario instanceof PersonaNatural persona) {

                    contenido.append(String.format(
                            "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
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
                            "-"
                    ));
                }
                else if (usuario instanceof PersonaJuridica persona) {
                    contenido.append(String.format(
                            "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n",
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
                            persona.getTipoEmpresa()
                    ));
                }
            }

            Files.writeString(ruta, contenido.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
