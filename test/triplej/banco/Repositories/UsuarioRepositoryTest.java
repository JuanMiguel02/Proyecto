package triplej.banco.Repositories;

import org.junit.jupiter.api.AfterEach;
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

    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
            if(Files.exists(ruta)) {
                Files.delete(ruta);
            }
        PersonaNatural usuarioOriginal = new PersonaNatural(
                "Paco", "Jones", "paco@gmail", "12345",
                RolUsuario.EMPLEADO,
                TipoDocumento.CEDULACIUDADANIA, "123456", "321654", "Colombia", "Bogotá"
        );
        usuarioRepository = new UsuarioRepository();
        usuarioRepository.guardar(usuarioOriginal);
    }

    @AfterEach
    void tearDown() {
        //Limpiar los datos del repositorio para no cargar los del ejemplo
        usuarioRepository.getUsuarios().clear();
    }

    @Test
    void guardarYBuscarUsuario() {
        PersonaNatural usuario = new PersonaNatural(
                "Armando", "Casas", "armando@test.com", "1234",
                RolUsuario.CAJERO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Medellín"
        );
        usuarioRepository.guardar(usuario);
        assertTrue(usuarioRepository.existeUsuarioConCorreo("armando@test.com"));
        assertEquals(1, usuarioRepository.contarTodos());
        assertTrue(usuarioRepository.buscarUsuarioPorCorreo("armando@test.com").isPresent());
    }


    @Test
    void existeUsuarioConCorreo() {
        PersonaNatural usuario1 = new PersonaNatural(
                "Rosario", "Tijeras", "rosario@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.REGISTROCIVIL, "1021",
                "2414", "Colombia", "Medellín"
        );

        PersonaNatural usuario2 = new PersonaNatural(
                "Rosario", "Tijeras", "rosario@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.REGISTROCIVIL, "1021",
                "2414", "Colombia", "Medellín"
        );

        usuarioRepository.guardar(usuario1);
        usuarioRepository.guardar(usuario2);

        assertTrue(usuarioRepository.existeUsuarioConCorreo("rosario@test.com"));
        assertEquals(1, usuarioRepository.contarTodos());
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

        usuarioRepository.guardar(admin);
        usuarioRepository.guardar(cliente);

        var clientes = usuarioRepository.obtenerPorRol(RolUsuario.CLIENTE);
        assertEquals(1, clientes.size());
        assertEquals("Armando Paredes", clientes.getFirst().getNombreCompleto());

        var admins = usuarioRepository.obtenerPorRol(RolUsuario.ADMIN);
        assertEquals(1, admins.size());
        assertEquals("Chino Moreno", admins.getFirst().getNombreCompleto());

    }

    @Test
    void eliminarUsuario() {
        PersonaNatural usuario = new PersonaNatural(
                "Luis", "Henao", "luis@test.com", "1234",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "104121",
                "414141", "Colombia", "Bogotá"
        );
        usuarioRepository.guardar(usuario);
        assertEquals(1, usuarioRepository.contarTodos());

        usuarioRepository.eliminarUsuario(usuario);
        assertEquals(0, usuarioRepository.contarTodos());
    }

    @Test
    void actualizarUsuario() {
    }
}