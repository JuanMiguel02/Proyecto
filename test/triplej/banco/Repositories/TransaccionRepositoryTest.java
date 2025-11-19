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

/**
 * Clase de pruebas unitarias para {@link TransaccionRepository}.
 * Evalúa las operaciones de almacenamiento y consulta de transacciones bancarias,
 * garantizando que el repositorio mantenga correctamente los datos en memoria
 * y en los archivos del sistema.
 */
class TransaccionRepositoryTest {
    // Repositorio bajo prueba (patrón Singleton)
    private final TransaccionRepository transaccionRepository = TransaccionRepository.getInstancia();

    /**
     * Se ejecuta antes de cada prueba para preparar un entorno limpio.
     * Elimina el archivo de transacciones y limpia la lista en memoria.
     * Esto asegura que cada test sea independiente y no herede datos previos.
     */
    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
        if(Files.exists(ruta)) {
            Files.delete(ruta);
        }
        transaccionRepository.getTodasTransacciones().clear();
    }

    /**
     * Prueba 2 para {@link TransaccionRepository#agregar(Transaccion)}.
     * Verifica que una transacción nueva se registre correctamente
     * dentro del repositorio.
     */
    @Test
    void agregar() {
        Transaccion trans = new Transaccion("1234", "Retiro", 5000.0,"1223", "12345");
        transaccionRepository.agregar(trans);

        assertTrue(transaccionRepository.getTodasTransacciones().contains(trans));
        assertEquals(1, transaccionRepository.getTodasTransacciones().size());
    }

    /**
     * Prueba 1 para {@link TransaccionRepository#getPorCuenta(String)}.
     * Comprueba que el repositorio devuelva correctamente las transacciones
     * asociadas a una cuenta bancaria específica.
     */
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