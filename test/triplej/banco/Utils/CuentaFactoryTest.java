package triplej.banco.Utils;

import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaCorriente;
import triplej.banco.Models.Cuentas.CuentaEmpresarial;
import triplej.banco.Models.Usuarios.Cliente;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Esta clase prueba los métodos de la clase {@link CuentaFactory},
 * la cual se encarga de crear instancias de diferentes tipos de cuentas bancarias
 * según el tipo indicado ("AHORRO", "CORRIENTE", "EMPRESARIAL").
 *
 * <p>También se valida que el método gestione correctamente
 * casos inválidos y que los datos de las cuentas creadas sean coherentes.</p>
 */
class CuentaFactoryTest {

    /**
     * Prueba 1. Verifica que el método {@code crearCuenta("AHORRO", cliente)}
     * cree correctamente una instancia de {@link CuentaAhorro}.
     */
    @Test
    void crearCuentaAhorro() {
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuenta("AHORRO", cliente);

        assertNotNull(cuenta);
        assertInstanceOf(CuentaAhorro.class, cuenta);
    }

    /**
     * Prueba 2. Verifica que el método {@code crearCuenta("CORRIENTE", cliente)}
     * cree correctamente una instancia de {@link CuentaCorriente}.
     */
    @Test
    void crearCuentaCorriente(){
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuenta("CORRIENTE", cliente);

        assertNotNull(cuenta);
        assertInstanceOf(CuentaCorriente.class, cuenta);
    }

    /**
     * Prueba 3. Verifica que el método {@code crearCuenta("EMPRESARIAL", cliente)}
     * cree correctamente una instancia de {@link CuentaEmpresarial}.
     */
    @Test
    void crearCuentaEmpresarial(){
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuenta("EMPRESARIAL", cliente);

        assertNotNull(cuenta);
        assertInstanceOf(CuentaEmpresarial.class, cuenta);
    }

    /**
     * Prueba 4. Verifica que si se intenta crear una cuenta con un tipo inválido,
     * el método {@link CuentaFactory#crearCuenta(String, Cliente)} lance una {@link IllegalArgumentException}.
     */
    @Test
    void crearCuentaInvalida(){
        Cliente cliente = mock(Cliente.class);

        assertThrows(IllegalArgumentException.class, () -> CuentaFactory.crearCuenta("INVALIDA", cliente));
    }

    /**
     * Prueba 5. Verifica que el método {@link CuentaFactory#crearCuentaConDatos(String, Cliente, String, double, Double)}
     * cree una cuenta correctamente con los datos proporcionados:
     * número de cuenta, saldo y propietario.
     * <p>
     * En este caso se usa "1" como tipo, lo cual corresponde a una {@link CuentaAhorro}.
     */
    @Test
    void crearCuentaConDatos() {
        Cliente cliente = mock(Cliente.class);

        var cuenta = CuentaFactory.crearCuentaConDatos("1", cliente, "1234", 5000.0, null);

        assertAll(
                () -> assertInstanceOf(CuentaAhorro.class, cuenta),
                () -> assertEquals("1234", cuenta.getNumeroCuenta()),
                () -> assertEquals(5000.0, cuenta.getSaldo()),
                () -> assertEquals(cliente, cuenta.getPropietario())
        );
    }

    /**
     * Prueba 6. Verifica que si se pasa un código de tipo inválido al método
     * {@code crearCuentaConDatos}, se lance una {@link IllegalArgumentException}.
     */
    @Test
    void crearCuentaConDatosInvalida() {
        Cliente cliente = mock(Cliente.class);

        assertThrows(IllegalArgumentException.class, () ->
                CuentaFactory.crearCuentaConDatos("9", cliente, "123", 5000.0, null)
        );
    }
}