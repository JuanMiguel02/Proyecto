package triplej.banco.Repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.Transaccion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransaccionRepositoryTest {
    private final TransaccionRepository transaccionRepository = TransaccionRepository.getInstancia();

    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
        if(Files.exists(ruta)) {
            Files.delete(ruta);
        }
        transaccionRepository.getTodasTransacciones().clear();
    }

    @Test
    void agregar() {
        Transaccion trans = new Transaccion("1234", "Retiro", 5000.0,"1223", "12345");
        transaccionRepository.agregar(trans);

        assertTrue(transaccionRepository.getTodasTransacciones().contains(trans));
        assertEquals(1, transaccionRepository.getTodasTransacciones().size());
    }


    @Test
    void getPorCuenta() {
        CuentaBancaria cuenta = mock(CuentaBancaria.class);
        when(cuenta.getNumeroCuenta()).thenReturn("1234567890");

        Transaccion trans = new Transaccion("123", "Retiro", 5000.0,"1234567890", "12345");
        transaccionRepository.agregar(trans);

        List<Transaccion> resultado = transaccionRepository.getPorCuenta("1234567890");

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("123", resultado.getFirst().getId());
        assertEquals("1234567890", resultado.getFirst().getCuentaOrigen());
    }
}