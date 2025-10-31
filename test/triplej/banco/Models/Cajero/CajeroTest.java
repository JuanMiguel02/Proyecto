package triplej.banco.Models.Cajero;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CajeroTest {
    private UsuarioRepository usuarioRepository;
    private ClienteRepository clienteRepository;
    private Cajero cajero;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        clienteRepository = mock(ClienteRepository.class);

        cajero = new Cajero(){
            {
                try{
                    var userRepoField = Cajero.class.getDeclaredField("usuarioRepository");
                    userRepoField.setAccessible(true);
                    userRepoField.set(this,usuarioRepository);

                    var clienteRepoField = Cajero.class.getDeclaredField("clienteRepository");
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
        assertThrows(IllegalArgumentException.class, () ->
                cajero.registrarCliente(usuario, "AHORROS"));
    }

    @Test
    void registrarClienteExitoso(){
        Usuario usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");

        Cliente cliente = cajero.registrarCliente(usuario, "CORRIENTE");
        assertNotNull(cliente);
        assertEquals("correo@test.com", cliente.getCorreo());
        verify(clienteRepository).guardar(any(Cliente.class));
    }

    @Test
    void agregarCuentaACliente() {
        Usuario usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");
        Cliente cliente = cajero.registrarCliente(usuario, "CORRIENTE");
        cajero.agregarCuentaACliente(cliente, "AHORRO");
        assertEquals(2, cliente.getNumeroCuentas());
    }

    @Test
    void realizarDeposito() {
        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        cajero.realizarDeposito(cuenta, 1000, "déposito inicial");

        verify(cuenta, times(1)).depositar(1000.0);
    }

    @Test
    void consultarSaldoExitoso() {
        Persona usuario = new PersonaNatural("Juan", "Henao", "correo@test.com",
                "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "14124", "13414", "Colombia", "Armenia");
        Cliente cliente = new Cliente(usuario);
        CuentaBancaria cuenta = new CuentaAhorro(cliente, "12414", 10000);
        double saldo = cajero.consultarSaldo(cuenta);
        assertEquals(10000, saldo);
    }

    @Test
    void consultarSaldoExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                cajero.consultarSaldo(null));
    }

    @Test
    void generarReporteCliente() {
    }
}