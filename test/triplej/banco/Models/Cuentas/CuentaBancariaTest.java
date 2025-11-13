package triplej.banco.Models.Cuentas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.TipoDocumento;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CuentaBancariaTest {

    private List<CuentaBancaria> cuentas;

    @BeforeEach
    void setUp(){
        PersonaNatural p = new PersonaNatural("Pepito", "Pérez", "pepe@gmail.com"
        , "1234", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "1231",
                "313123123", "Colombia", "Cali");
        Cliente cliente = new Cliente(p);
        cuentas = List.of(
                new CuentaAhorro(cliente),
                new CuentaCorriente(cliente),
                new CuentaEmpresarial(cliente)
        );
    }

    @Test
    void testRetiroMinimoPorTipo() {
        for(CuentaBancaria cuenta : cuentas){
            double minimo = cuenta.getRetiroMinimo();
            System.out.println(cuenta.getClass().getSimpleName() + " -> mínimo: " + minimo);
            assertTrue(minimo > 0, "El retiro mínimo debe de ser positivo");
            assertEquals(minimo, cuenta.getRetiroMinimo());
        }
    }

    @Test
    void testDepositoYRetiro(){
        for(CuentaBancaria cuenta : cuentas){
            cuenta.depositar(200000.0, false);
            double saldoInicial = cuenta.getSaldo();

            double montoRetiro = cuenta.getRetiroMinimo() + 1000;
            cuenta.retirar(montoRetiro, false);
            System.out.println(cuenta.getSaldo());

            assertEquals(saldoInicial - montoRetiro, cuenta.getSaldo(), 0.001);
        }
    }

    @Test
    void testCodigoTipoCuentaYNombre() {
        for (CuentaBancaria cuenta : cuentas) {
            String codigo = cuenta.getCodigoTipoCuenta();
            String nombre = cuenta.getNombreTipoCuenta();
            assertNotNull(codigo);
            assertNotNull(nombre);
            System.out.println(nombre + " -> Código: " + codigo);
        }
    }

}