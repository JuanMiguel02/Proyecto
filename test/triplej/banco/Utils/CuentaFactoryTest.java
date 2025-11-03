package triplej.banco.Utils;

import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaCorriente;
import triplej.banco.Models.Cuentas.CuentaEmpresarial;
import triplej.banco.Models.Usuarios.Cliente;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CuentaFactoryTest {

    @Test
    void crearCuentaAhorro() {
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuenta("AHORRO", cliente);

        assertNotNull(cuenta);
        assertInstanceOf(CuentaAhorro.class, cuenta);
    }

    @Test
    void crearCuentaCorriente(){
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuenta("CORRIENTE", cliente);

        assertNotNull(cuenta);
        assertInstanceOf(CuentaCorriente.class, cuenta);
    }

    @Test
    void crearCuentaEmpresarial(){
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuenta("EMPRESARIAL", cliente);

        assertNotNull(cuenta);
        assertInstanceOf(CuentaEmpresarial.class, cuenta);
    }

    @Test
    void crearCuentaInvalida(){
        Cliente cliente = mock(Cliente.class);

        assertThrows(IllegalArgumentException.class, () -> CuentaFactory.crearCuenta("INVALIDA", cliente));
    }

    @Test
    void crearCuentaConDatos() {
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuentaConDatos("1", cliente, "1234", 5000.0);

        assertAll(
                () -> assertInstanceOf(CuentaAhorro.class, cuenta),
                () -> assertEquals("1234", cuenta.getNumeroCuenta()),
                () -> assertEquals(5000.0, cuenta.getSaldo()),
                () -> assertEquals(cliente, cuenta.getPropietario())
        );
    }

    @Test
    void crearCuentaConDatosInvalida() {
        Cliente cliente = mock(Cliente.class);

        assertThrows(IllegalArgumentException.class, () ->
                CuentaFactory.crearCuentaConDatos("9", cliente, "123", 5000.0)
        );
    }
}