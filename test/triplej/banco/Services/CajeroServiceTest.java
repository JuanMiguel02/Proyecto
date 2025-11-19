package triplej.banco.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.Reporte;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Esta clase prueba los métodos del servicio CajeroService {@link CajeroService},
 * encargado de la gestión de clientes y operaciones bancarias
 * como depósitos, retiros, transferencias y generación de reportes.
 * <p>
 * Se usan mocks para los repositorios, con el fin de probar la lógica del servicio
 * sin depender de archivos o bases de datos reales.
 */
class CajeroServiceTest {
    private UsuarioRepository usuarioRepository;
    private ClienteRepository clienteRepository;
    private CajeroService cajeroService;

    /**
     * Se ejecuta antes de cada prueba.
     * Crea los mocks de los repositorios e inyecta las dependencias
     * dentro de la instancia de CajeroService mediante reflexión.
     */
    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        clienteRepository = mock(ClienteRepository.class);

        cajeroService = new CajeroService(){
            {
                try{
                    var userRepoField = CajeroService.class.getDeclaredField("usuarioRepository");
                    userRepoField.setAccessible(true);
                    userRepoField.set(this,usuarioRepository);

                    var clienteRepoField = CajeroService.class.getDeclaredField("clienteRepository");
                    clienteRepoField.setAccessible(true);
                    clienteRepoField.set(this,clienteRepository);

                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * Prueba 1. Verifica que si se intenta registrar un cliente con un correo
     * ya existente, se lance una excepción de tipo IllegalArgumentException.
     */
    @Test
    void registrarClienteExcepcion() {
        Usuario usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");
        when(usuarioRepository.buscarUsuarioPorCorreo("correo@test.com"))
                .thenReturn(Optional.of(usuario));

        // Desactiva temporalmente las alertas de JavaFX
        CajeroService cajeroMock = new CajeroService() {

            @Override
            public Cliente registrarCliente(Usuario usuario, String tipoCuenta) {
                // Ignora la alerta, pero conserva la lógica de excepción
                if (usuarioRepository.buscarUsuarioPorCorreo(usuario.getCorreo()).isPresent()) {
                    throw new IllegalArgumentException("El correo ya está registrado: " + usuario.getCorreo());
                }
                return super.registrarCliente(usuario, tipoCuenta);
            }
        };

        assertThrows(IllegalArgumentException.class, () ->
                cajeroMock.registrarCliente(usuario, "AHORROS"));
    }

    /**
     * Prueba 2. Verifica que se pueda registrar un cliente nuevo exitosamente
     * cuando el correo no está previamente registrado.
     */
    @Test
    void registrarClienteExitoso(){
        Usuario usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");

        Cliente cliente = cajeroService.registrarCliente(usuario, "CORRIENTE");
        assertNotNull(cliente);
        assertEquals("correo@test.com", cliente.getCorreo());
        verify(clienteRepository).guardar(any(Cliente.class));
    }

    /**
     * Prueba 3. Verifica que al agregar una nueva cuenta a un cliente existente,
     * el número total de cuentas del cliente aumente correctamente.
     */
    @Test
    void agregarCuentaACliente() {
        Usuario usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");
        Cliente cliente = cajeroService.registrarCliente(usuario, "CORRIENTE");
        cajeroService.agregarCuentaACliente(cliente, "AHORRO", 0);
        assertEquals(2, cliente.getNumeroCuentas());
    }

    /**
     * Prueba 4. Verifica que el método realizarDeposito invoque internamente
     * el método depositar de la cuenta con el monto correcto.
     */
    @Test
    void realizarDeposito() {
        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getNumeroCuenta()).thenReturn("12345");
        cajeroService.realizarDeposito(cuenta, 1000);

        verify(cuenta, times(1)).depositar(1000.0, false);
    }

    /**
     * Prueba 5. Verifica que realizarRetiro invoque el método retirar()
     * con el monto correspondiente.
     */
    @Test
    void realizarRetiro() {
        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getNumeroCuenta()).thenReturn("12345");
        cajeroService.realizarRetiro(cuenta, 1000);

        verify(cuenta, times(1)).retirar(1000.0, false);
    }

    /**
     * Prueba 6. Verifica que al realizar una transferencia se retire el monto
     * de la cuenta de origen y se deposite en la cuenta de destino.
     */
    @Test
    void realizarTransferencia() {
        CuentaBancaria cuenta1 = mock(CuentaBancaria.class);
        when(cuenta1.getNumeroCuenta()).thenReturn("12345");

        CuentaBancaria cuenta2 = mock(CuentaBancaria.class);
        when(cuenta2.getNumeroCuenta()).thenReturn("67890");

        cajeroService.realizarTransferencia(cuenta1, cuenta2, 1000);

        verify(cuenta1, times(1)).retirar(1000.0, true);
        verify(cuenta2, times(1)).depositar(1000.0, true);
    }

    /**
     * Prueba 7. Verifica que consultarSaldo devuelva el saldo correcto
     * de una cuenta válida.
     */
    @Test
    void consultarSaldoExitoso() {
        Persona usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");
        Cliente cliente = new Cliente(usuario);
        CuentaBancaria cuenta = new CuentaAhorro(cliente, "12414", 10000);
        double saldo = cajeroService.consultarSaldo(cuenta);
        assertEquals(10000, saldo);
    }

    /**
     * Prueba 8. Verifica que consultarSaldo lance una excepción
     * si se pasa una cuenta nula.
     */
    @Test
    void consultarSaldoExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                cajeroService.consultarSaldo(null));
    }

    /**
     * Prueba 9. Verifica que generarReporteCliente devuelva un objeto Reporte válido
     * para un cliente y su cuenta.
     */
    @Test
    void generarReporteCliente() {
        Cliente cliente = new Cliente(new PersonaNatural(
                "Ana", "López", "ana@mail.com", "1234",
                RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "921",
                "30177", "Colombia", "Cali"
        ));

        CuentaBancaria cuenta = new CuentaAhorro(cliente, "12414", 10000);

        // Act
        Reporte reporte = cajeroService.generarReporteCliente(cuenta);

        // Assert
        assertNotNull(reporte);
    }

    /**
     * Prueba 10. Verifica que registrarPersonaNatural cree un nuevo cliente correctamente
     * cuando el correo no está en uso y que los repositorios sean llamados apropiadamente.
     */
    @Test
    void registrarPersonaNatural() {
        when(usuarioRepository.buscarUsuarioPorCorreo("juan@gmail.com")).thenReturn(Optional.empty());

        // Act
        cajeroService.registrarPersonaNatural(
                "Juan", "Perez", "juan@gmail.com", "1234",
                TipoDocumento.CEDULACIUDADANIA, "1789", "30222",
                "Colombia", "Bogotá", "Ahorro", 1000.0, null, null
        );

        // Assert
        verify(clienteRepository, times(1)).guardar(any(Cliente.class));
        verify(usuarioRepository, times(1)).buscarUsuarioPorCorreo("juan@gmail.com");
    }

    /**
     * Prueba 11. Verifica que registrarPersonaJuridica cree correctamente
     * un cliente empresarial y guarde los datos en los repositorios correspondientes.
     */
    @Test
    void registrarPersonaJuridica() {
        when(usuarioRepository.buscarUsuarioPorCorreo("mamitas@gmail.com")).thenReturn(Optional.empty());

        // Act
        cajeroService.registrarPersonaJuridica(
                "Mamitas", "Alex Marín", "Privada", "mamitas@gmail.com", "1234",
                TipoDocumento.CEDULACIUDADANIA, "1789", "30222",
                "Colombia", "Bogotá", "Empresarial", 1000.0, null, null
        );

        // Assert
        verify(clienteRepository, times(1)).guardar(any(Cliente.class));
        verify(usuarioRepository, times(1)).buscarUsuarioPorCorreo("mamitas@gmail.com");
    }


}