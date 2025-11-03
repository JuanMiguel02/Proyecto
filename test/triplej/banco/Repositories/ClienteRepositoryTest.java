package triplej.banco.Repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteRepositoryTest {

    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
        if(Files.exists(ruta)) {
            Files.delete(ruta);
        }
        clienteRepository = new ClienteRepository();
    }

    @AfterEach
    void tearDown() {
        //Limpiar los datos del repositorio para no cargar los del ejemplo
        clienteRepository.getClientes().clear();
    }

    @Test
    void guardar() {
        Persona usuario = new PersonaJuridica(
                "Fundación Amigos Peludos", "Julian Casablancas","Fundación",
                "julian@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.NIT, "1021",
                "2414", "Colombia", "Medellín"
        );
        Cliente cliente = new Cliente(usuario);
        clienteRepository.guardar(cliente);

        assertNotNull(clienteRepository.getClientes());
        assertFalse(clienteRepository.getClientes().isEmpty());
        assertTrue(clienteRepository.getClientes().contains(cliente));
        assertEquals(1, clienteRepository.getClientes().size());
    }

    @Test
    void buscarPorDocumento() {
        Persona usuario = new PersonaNatural(
                "Tom", "York",
                "tom@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.PASAPORTE, "1021",
                "2414", "Colombia", "Medellín"
        );
        Cliente cliente = new Cliente(usuario);
        clienteRepository.guardar(cliente);

        assertTrue(clienteRepository.buscarPorDocumento("1021").isPresent());
    }

    @Test
    void buscarPorCorreo() {
        Persona usuario = new PersonaNatural(
                "Juan", "Henao",
                "juan@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.PASAPORTE, "1021",
                "2414", "Colombia", "Medellín"
        );
        Cliente cliente = new Cliente(usuario);
        clienteRepository.guardar(cliente);

        assertTrue(clienteRepository.buscarPorCorreo("juan@test.com").isPresent());
    }

    @Test
    void buscarCuentaClientePorNumero() {
        Persona usuario = new PersonaNatural(
                "Juan", "Henao",
                "juan@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.PASAPORTE, "1021",
                "2414", "Colombia", "Medellín"
        );
        Cliente cliente = new Cliente(usuario);

        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getPropietario()).thenReturn(cliente);
        when(cuenta.getNumeroCuenta()).thenReturn("1234");

        clienteRepository.guardar(cliente);
        cliente.agregarCuenta(cuenta);

        assertTrue(clienteRepository.buscarCuentaDeClientePorNumero("1234").isPresent());

    }

    @Test
    void buscarClientePorCuenta() {
        Persona usuario = new PersonaNatural(
                "Juan", "Henao",
                "juan@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.PASAPORTE, "1021",
                "2414", "Colombia", "Medellín"
        );
        Cliente cliente = new Cliente(usuario);

        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getPropietario()).thenReturn(cliente);
        when(cuenta.getNumeroCuenta()).thenReturn("1234");

        clienteRepository.guardar(cliente);
        cliente.agregarCuenta(cuenta);

        assertTrue(clienteRepository.buscarClientePorCuenta("1234").isPresent());
    }

    @Test
    void actualizarCliente() {
        Persona usuario = new PersonaNatural(
                "Tom", "York",
                "tom@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.PASAPORTE, "1021",
                "2414", "Colombia", "Medellín"
        );
        Cliente cliente = new Cliente(usuario);
        clienteRepository.guardar(cliente);

        assertTrue(clienteRepository.buscarPorDocumento("1021").isPresent());
        assertEquals("Medellín", cliente.getCiudad());
        assertEquals("tom@test.com", cliente.getCorreo());

        usuario.setNumeroDocumento("1234");
        usuario.setCiudad("Armenia");
        usuario.setCorreo("tom123@gmail.com");

        clienteRepository.actualizarCliente(cliente);
        assertTrue(clienteRepository.buscarPorDocumento("1234").isPresent());
        assertEquals("Armenia", cliente.getCiudad());
        assertEquals("tom123@gmail.com", cliente.getCorreo());
    }
}