package triplej.banco.Models.Reportes;

import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.Transaccion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera reportes personalizados de movimientos para un cliente específico.
 * <p>
 * Este reporte se enfoca en mostrar el historial de transacciones asociadas a una cuenta
 * bancaria determinada, incluyendo transferencias, depósitos y retiros.
 * </p>
 *
 * <p>
 * Implementa la interfaz {@link ReporteGenerado}, lo que permite integrarla con otros tipos
 * de reportes dentro del sistema, como reportes administrativos o globales.
 * </p>
 *
 * <p>
 * Cada reporte incluye información sobre el titular, tipo de cuenta, saldo actual y
 * un listado detallado de los movimientos realizados.
 * </p>
 */
public class ReporteCliente implements ReporteGenerado {

    /** Cuenta bancaria asociada al reporte. */
    private final CuentaBancaria cuentaBancaria;

    /**
     * Crea un nuevo generador de reportes para una cuenta bancaria específica.
     *
     * @param cuentaBancaria La cuenta del cliente sobre la cual se generará el reporte.
     */
    public ReporteCliente(CuentaBancaria cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    /**
     * Genera el reporte completo de movimientos de la cuenta.
     * <p>
     * El reporte contiene:
     * </p>
     * <ul>
     *     <li>Encabezado con la información del titular y la cuenta.</li>
     *     <li>Saldo actual.</li>
     *     <li>Listado detallado de transacciones realizadas (depósitos, retiros o transferencias).</li>
     * </ul>
     *
     * <p>
     * Si la cuenta no tiene movimientos registrados, el reporte lo indicará explícitamente.
     * </p>
     *
     * @return Objeto {@link Reporte} que contiene toda la información formateada.
     */
    @Override
    public Reporte generarReporte() {
        List<String> contenido = new ArrayList<>();

        contenido.add("Reporte de movimientos de la cuenta: " + cuentaBancaria.getNumeroCuenta() + " ( " + cuentaBancaria.getNombreTipoCuenta() + " )");
        contenido.add("Titular: " + cuentaBancaria.getPropietario().getNombre());
        contenido.add("Saldo actual: $" + String.format("%.2f", cuentaBancaria.getSaldo()));
        contenido.add("Fecha de generación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        contenido.add("-----------------------------------------------------");

        for (Transaccion t : cuentaBancaria.getHistorialTransacciones()) {
            String tipoMovimiento;
            String detalle;

            // Caso 1: Transferencias (origen y destino distintos)
            if (!t.getCuentaOrigen().equals(t.getCuentaDestino())) {
                if (cuentaBancaria.getNumeroCuenta().equals(t.getCuentaOrigen())) {
                    tipoMovimiento = "Transferencia enviada";
                    detalle = "A cuenta: " + t.getCuentaDestino();
                } else if (cuentaBancaria.getNumeroCuenta().equals(t.getCuentaDestino())) {
                    tipoMovimiento = "Transferencia recibida";
                    detalle = "Desde cuenta: " + t.getCuentaOrigen();
                } else {
                    tipoMovimiento = "Movimiento externo";
                    detalle = "N/A";
                }
            }
            // Caso 2: Depósitos o retiros (origen == destino)
            else {
                tipoMovimiento = t.getTipo().toUpperCase();
                detalle = "";
            }
            //Línea formateada del movimiento
            String linea = String.format(
                    "ID: %s | %s | %-22s | Monto: $%.2f | %s %s | %s%n",
                    t.getId(),
                    t.getFecha(),
                    tipoMovimiento,
                    t.getMonto(),
                    detalle,
                    t.getDescripcion() != null ? "| " + t.getDescripcion() : "",
                    t.isExitosa() ? "Exitosa" : "Fallida"
            );

            contenido.add(linea);
        }
        //Si no hay transacciones
        if (contenido.size() == 5) {
            contenido.add("No se encontraron transacciones registradas.");
        }

        return new Reporte(
                "Historial de movimientos - Cuenta: " + cuentaBancaria.getNumeroCuenta(),
                LocalDateTime.now(),
                contenido
        );
    }
}
