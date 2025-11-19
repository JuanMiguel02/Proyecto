package triplej.banco.Repositories;

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

/**
 * Clase de prueba unitaria para {@link ClienteRepository}.
 * <p>
 * Este conjunto de pruebas válida el comportamiento del repositorio encargado de manejar
 * la persistencia de los objetos {@link Cliente}, verificando operaciones como:
 * guardar, actualizar y buscar clientes o cuentas asociadas.
 * <p>
 * Se utilizan tanto objetos reales como objetos simulados (mock) para aislar
 * las pruebas del comportamiento de otras clases.
 */
class ClienteRepositoryTest {
    // Repositorio bajo prueba (patrón Singleton)
    private final ClienteRepository clienteRepository = ClienteRepository.getInstancia();

    /**
     * Método que se ejecuta antes de cada test.
     * <p>
     * Su propósito es **asegurar un entorno limpio**, eliminando archivos
     * previos y limpiando las colecciones internas del repositorio.
     */
    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Usuarios.txt");
        if(Files.exists(ruta)) {
            Files.delete(ruta);
        }
        clienteRepository.getClientes().clear();
    }

    /**
     *  Prueba 1: Guardar un nuevo cliente en el repositorio.
     * <p>
     * Se verifica que:
     * - El cliente se agregue correctamente a la lista.
     * - El repositorio no esté vacío después de guardar.
     * - El cliente guardado esté contenido en la lista.
     * - El tamaño de la lista sea exactamente 1.
     */
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

    /**
     *  Prueba 2: Buscar un cliente por su número de documento.
     * <p>
     * Se guarda un cliente y luego se verifica que el método {@code buscarPorDocumento()}
     * retorne un resultado presente (es decir, que lo haya encontrado).
     */
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

    /**
     *  Prueba 3: Buscar un cliente por su correo electrónico.
     * <p>
     * Esta prueba confirma que el método {@code buscarPorCorreo()} funciona correctamente
     * y distingue entre diferentes clientes por su correo.
     */
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

    /**
     * Prueba 4: Buscar cliente a través de una cuenta bancaria asociada.
     * <p>
     * Se utiliza un objeto simulado (mock) de {@link CuentaBancaria} para
     * evitar dependencias externas. Se valida que el repositorio pueda
     * ubicar correctamente al cliente propietario a partir del número de cuenta.
     */
    @Test
    void buscarCuentaClientePorNumero() {
        Persona usuario = new PersonaNatural(
                "Juan", "Henao",
                "juan@test.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.PASAPORTE, "1021",
                "2414", "Colombia", "Medellín"
        );
        Cliente cliente = new Cliente(usuario);
        // Se crea una cuenta simulada (mock) que retorna el cliente y número de cuenta deseado
        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getPropietario()).thenReturn(cliente);
        when(cuenta.getNumeroCuenta()).thenReturn("1234");

        clienteRepository.guardar(cliente);
        cliente.agregarCuenta(cuenta);

        assertTrue(clienteRepository.buscarClientePorCuenta("1234").isPresent());

    }

    /**
     *  Prueba 5: Buscar cliente por número de cuenta.
     * <p>
     * Similar a la prueba anterior, pero se enfoca en validar directamente
     * el método {@code buscarClientePorCuenta()} del repositorio.
     */
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

    /**
     *  Prueba 6: Actualizar la información de un cliente existente.
     * <p>
     * Se modifican datos del cliente (documento, ciudad, correo) y se valida que
     * el repositorio refleje correctamente los cambios después de la actualización.
     */
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

    /**
     *  Prueba 7: (Pendiente de implementación)
     * Validará la búsqueda de todas las cuentas asociadas a un cliente.
     */
    @Test
    void buscarCuentasDeCliente() {
    }

    @Test
    void buscarCuentaPorNumero() {
    }
}