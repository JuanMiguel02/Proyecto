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

class UsuarioRepositoryTest {

    private final UsuarioRepository usuarioRepository= UsuarioRepository.getInstancia();

    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
            if(Files.exists(ruta)) {
                Files.delete(ruta);
            }
        usuarioRepository.getUsuarios().clear();
    }

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