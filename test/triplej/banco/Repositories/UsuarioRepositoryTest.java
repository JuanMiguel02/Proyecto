package triplej.banco.Repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.TipoDocumento;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para {@link UsuarioRepository}.
 * Válida las operaciones CRUD (crear, leer, actualizar, eliminar) y de filtrado
 * sobre los objetos {@link triplej.banco.Models.Usuarios.Usuario}.
 * <p>
 * Estas pruebas garantizan que el repositorio gestione correctamente la persistencia
 * de usuarios y el manejo en memoria dentro del sistema bancario.
 */
class UsuarioRepositoryTest {
    // Repositorio bajo prueba (patrón Singleton)
    private final UsuarioRepository usuarioRepository= UsuarioRepository.getInstancia();

    /**
     * Se ejecuta antes de cada prueba.
     * Elimina el archivo de datos existente y limpia la lista de usuarios en memoria
     * para garantizar que cada test se ejecute en un entorno limpio y aislado.
     */
    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
            if(Files.exists(ruta)) {
                Files.delete(ruta);
            }
        usuarioRepository.getUsuarios().clear();
    }

    /**
     * Prueba que un usuario pueda ser guardado correctamente en el repositorio
     * y luego encontrado mediante diferentes métodos de búsqueda.
     */
    @Test
    void guardarUsuarioYBuscarUsuario() {
        PersonaNatural usuario = new PersonaNatural(
                "Armando", "Casas", "armando@test.com", "1234",
                RolUsuario.CAJERO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Medellín"
        );
        usuarioRepository.guardarUsuario(usuario);
        assertTrue(usuarioRepository.existeUsuarioConCorreo("armando@test.com"));
        assertEquals(1, usuarioRepository.contarTodos());
        assertTrue(usuarioRepository.buscarUsuarioPorCorreo("armando@test.com").isPresent());
        assertTrue(usuarioRepository.buscarUsuarioPorId(usuario.getId()).isPresent());

    }

    /**
     * Prueba 1. Verifica que el repositorio detecte correctamente si un usuario
     * ya existe mediante su correo electrónico.
     */
    @Test
    void existeUsuarioConCorreo() {
        PersonaNatural usuario1 = new PersonaNatural(
                "Rosario", "Tijeras", "rosario@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.REGISTROCIVIL, "1021",
                "2414", "Colombia", "Medellín"
        );

        PersonaNatural usuario2 = new PersonaNatural(
                "Profe", "Montoya", "montoya@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.REGISTROCIVIL, "10421",
                "21414", "Colombia", "Medellín"
        );

        usuarioRepository.guardarUsuario(usuario1);
        usuarioRepository.guardarUsuario(usuario2);

        assertTrue(usuarioRepository.existeUsuarioConCorreo("rosario@test.com"));
        assertEquals(2, usuarioRepository.contarTodos());
    }

    /**
     * Prueba 2 el filtrado de usuarios según su rol dentro del sistema.
     * Se asegura de que solo se devuelvan los usuarios que correspondan al rol indicado.
     */
    @Test
    void obtenerPorRol() {
        PersonaNatural admin = new PersonaNatural(
                "Chino", "Moreno", "chino@test.com", "1234",
                RolUsuario.ADMIN, TipoDocumento.CEDULACIUDADANIA, "102411",
                "241451", "Colombia", "Medellín"
        );

        PersonaNatural cliente = new PersonaNatural(
                "Armando", "Paredes", "armando@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.REGISTROCIVIL, "104121",
                "241144", "Colombia", "Medellín"
        );

        usuarioRepository.guardarUsuario(admin);
        usuarioRepository.guardarUsuario(cliente);

        var clientes = usuarioRepository.obtenerPorRol(RolUsuario.CLIENTE);
        assertEquals(1, clientes.size());
        assertEquals("Armando Paredes", clientes.getFirst().getNombreUsuario());

        var admins = usuarioRepository.obtenerPorRol(RolUsuario.ADMIN);
        assertEquals(1, admins.size());
        assertEquals("Chino Moreno", admins.getFirst().getNombreUsuario());

    }

    /**
     * Prueba 3. Verifica la eliminación correcta de un usuario.
     * Luego de eliminarlo, no debe quedar registro alguno en el repositorio.
     */
    @Test
    void eliminarUsuario() {
        PersonaNatural usuario = new PersonaNatural(
                "Luis", "Henao", "luis@test.com", "1234",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "104121",
                "414141", "Colombia", "Bogotá"
        );
        usuarioRepository.guardarUsuario(usuario);
        assertEquals(1, usuarioRepository.contarTodos());

        usuarioRepository.eliminarUsuario(usuario);
        assertEquals(0, usuarioRepository.contarTodos());
    }

    /**
     * Prueba 4. Verifica la actualización de los datos de un usuario existente.
     * Válida que los cambios en sus atributos (correo, ciudad, etc.)
     * se reflejen correctamente dentro del repositorio.
     */
    @Test
    void actualizarUsuario() {
        PersonaNatural usuario = new PersonaNatural(
                "Armando", "Casas", "armando@test.com", "1234",
                RolUsuario.CAJERO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Medellín"
        );
        usuarioRepository.guardarUsuario(usuario);
        assertTrue(usuarioRepository.existeUsuarioConCorreo("armando@test.com"));
        assertEquals(1, usuarioRepository.contarTodos());
        assertTrue(usuarioRepository.buscarUsuarioPorCorreo("armando@test.com").isPresent());

        usuario.setCorreo("armando@gmail.com");
        usuario.setCiudad("Cali");

        usuarioRepository.actualizarUsuario(usuario);
        assertTrue(usuarioRepository.existeUsuarioConCorreo("armando@gmail.com"));
        assertEquals("Cali", usuario.getCiudad());

    }
}