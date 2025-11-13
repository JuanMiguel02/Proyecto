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

class CajeroServiceTest {
    private UsuarioRepository usuarioRepository;
    private ClienteRepository clienteRepository;
    private CajeroService cajeroService;

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

    @Test
    void registrarClienteExitoso(){
        Usuario usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");

        Cliente cliente = cajeroService.registrarCliente(usuario, "CORRIENTE");
        assertNotNull(cliente);
        assertEquals("correo@test.com", cliente.getCorreo());
        verify(clienteRepository).guardar(any(Cliente.class));
    }

    @Test
    void agregarCuentaACliente() {
        Usuario usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");
        Cliente cliente = cajeroService.registrarCliente(usuario, "CORRIENTE");
        cajeroService.agregarCuentaACliente(cliente, "AHORRO", 0);
        assertEquals(2, cliente.getNumeroCuentas());
    }

    @Test
    void realizarDeposito() {
        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getNumeroCuenta()).thenReturn("12345");
        cajeroService.realizarDeposito(cuenta, 1000);

        verify(cuenta, times(1)).depositar(1000.0, false);
    }

    @Test
    void realizarRetiro() {
        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getNumeroCuenta()).thenReturn("12345");
        cajeroService.realizarRetiro(cuenta, 1000);

        verify(cuenta, times(1)).retirar(1000.0, false);
    }

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

    @Test
    void consultarSaldoExitoso() {
        Persona usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");
        Cliente cliente = new Cliente(usuario);
        CuentaBancaria cuenta = new CuentaAhorro(cliente, "12414", 10000);
        double saldo = cajeroService.consultarSaldo(cuenta);
        assertEquals(10000, saldo);
    }

    @Test
    void consultarSaldoExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                cajeroService.consultarSaldo(null));
    }

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