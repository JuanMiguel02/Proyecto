package triplej.banco.Models.Cuentas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.TipoDocumento;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba unitaria para la jerarquía de clases que heredan de {@link CuentaBancaria}.
 * <p>
 * Este test verifica el comportamiento general de las cuentas bancarias del sistema,
 * incluyendo operaciones de depósito, retiro, validaciones del retiro mínimo,
 * y la correcta identificación de tipo y código de cada cuenta.
 * <p>
 * Se aplican pruebas comunes a las tres clases concretas:
 * - {@link CuentaAhorro}
 * - {@link CuentaCorriente}
 * - {@link CuentaEmpresarial}
 */
class CuentaBancariaTest {

    // Lista que contendrá diferentes tipos de cuentas bancarias para probar su comportamiento común
    private List<CuentaBancaria> cuentas;

    /**
     * Método que se ejecuta antes de cada prueba.
     * <p>
     * Crea un cliente de ejemplo y le asocia tres tipos de cuentas diferentes:
     * ahorro, corriente y empresarial.
     */
    @BeforeEach
    void setUp(){
        // Se crea una persona natural con información básica
        PersonaNatural p = new PersonaNatural("Pepito", "Pérez", "pepe@gmail.com"
        , "1234", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "1231",
                "313123123", "Colombia", "Cali");
        // Se encapsula la persona dentro de un cliente del banc
        Cliente cliente = new Cliente(p);
        // Se crean las tres cuentas que se probarán
        cuentas = List.of(
                new CuentaAhorro(cliente),
                new CuentaCorriente(cliente),
                new CuentaEmpresarial(cliente)
        );
    }

    /**
     *  Prueba 1: Verifica que cada tipo de cuenta tenga un retiro mínimo válido y positivo.
     * <p>
     * Esta prueba garantiza que el método {@code getRetiroMinimo()} retorna un valor coherente,
     * ya que cada clase concreta de cuenta puede definir su propio monto mínimo de retiro.
     */
    @Test
    void testRetiroMinimoPorTipo() {
        for(CuentaBancaria cuenta : cuentas){
            double minimo = cuenta.getRetiroMinimo();
            System.out.println(cuenta.getClass().getSimpleName() + " -> mínimo: " + minimo);
            // Se valida que el retiro mínimo sea mayor a 0
            assertTrue(minimo > 0, "El retiro mínimo debe de ser positivo");
            // Se verifica que el valor sea consistente entre llamada
            assertEquals(minimo, cuenta.getRetiroMinimo());
        }
    }

    /**
     *  Prueba 2: Válida las operaciones de depósito y retiro.
     * <p>
     * Se deposita un monto inicial en cada cuenta y luego se realiza un retiro válido.
     * Finalmente, se comprueba que el saldo se haya actualizado correctamente.
     */
    @Test
    void testDepositoYRetiro(){
        for(CuentaBancaria cuenta : cuentas){
            // Se deposita un monto inicial en la cuenta
            cuenta.depositar(200000.0, false);
            double saldoInicial = cuenta.getSaldo();
            // Se calcula un monto de retiro mayor al mínimo permitido
            double montoRetiro = cuenta.getRetiroMinimo() + 1000;
            // Se realiza el retiro
            cuenta.retirar(montoRetiro, false);
            // Se muestra el saldo resultante para depuración
            System.out.println(cuenta.getSaldo());
            // Se verifica que el nuevo saldo sea el esperado
            assertEquals(saldoInicial - montoRetiro, cuenta.getSaldo(), 0.001);
        }
    }

    /**
     *  Prueba 3: Comprueba que cada cuenta tenga un código y un nombre de tipo válidos.
     * <p>
     * Se asegura que los métodos {@code getCodigoTipoCuenta()} y {@code getNombreTipoCuenta()}
     * devuelvan información coherente y no nula.
     */
    @Test
    void testCodigoTipoCuentaYNombre() {
        for (CuentaBancaria cuenta : cuentas) {
            String codigo = cuenta.getCodigoTipoCuenta();
            String nombre = cuenta.getNombreTipoCuenta();
            // Validaciones de no nulidad
            assertNotNull(codigo);
            assertNotNull(nombre);
            // Muestra el resultado en consola
            System.out.println(nombre + " -> Código: " + codigo);
        }
    }

}