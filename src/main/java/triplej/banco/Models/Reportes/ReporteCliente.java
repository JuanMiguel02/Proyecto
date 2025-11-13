package triplej.banco.Models.Reportes;

import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.Transaccion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReporteCliente implements ReporteGenerado {
    private final CuentaBancaria cuentaBancaria;

    public ReporteCliente(CuentaBancaria cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

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
