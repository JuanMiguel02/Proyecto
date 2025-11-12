package triplej.banco.Models.Reportes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.Transaccion;
import triplej.banco.Models.Usuarios.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReporteClienteTest {
    private ReporteCliente reporteCliente;
    private CuentaBancaria cuentaBancaria;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cuentaBancaria = mock(CuentaBancaria.class);
        cliente = mock(Cliente.class);

        reporteCliente = new ReporteCliente(cuentaBancaria);
    }

    @Test
    void generarReporte() {

        //Mock de cliente
        when(cliente.getNombre()).thenReturn("Aquiles Tengo");

        //Mock de cuenta
        when(cuentaBancaria.getNumeroCuenta()).thenReturn("1234567890");
        when(cuentaBancaria.getPropietario()).thenReturn(cliente);
        when(cuentaBancaria.getSaldo()).thenReturn(2000.0);

        // Crear el mock de transacción
        Transaccion transaccion = mock(Transaccion.class);
        when(transaccion.getId()).thenReturn("T001");
        when(transaccion.getTipo()).thenReturn("Depósito");
        when(transaccion.getCuentaDestino()).thenReturn("123");
        when(transaccion.getCuentaOrigen()).thenReturn("456");
        when(transaccion.getMonto()).thenReturn(2000.0);
        when(transaccion.getFechaFormateada()).thenReturn(LocalDate.now().toString());
        when(transaccion.esSospechosa()).thenReturn(false);

        //Agregar la transaccion a la lista de transacciones
        ArrayList<Transaccion> transacciones = new ArrayList<>();
        transacciones.add(transaccion);

        when(cuentaBancaria.getHistorial()).thenReturn(transacciones);

        //Generar el reporte
        Reporte reporte = reporteCliente.generarReporte();

        // Aserciones
        assertNotNull(reporte);
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("Reporte de movimientos de la cuenta:")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("Titular")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("1234567890")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("2000")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("Aquiles Tengo")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("T001")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("Depósito")));
    }
}