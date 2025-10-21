package triplej.banco.Models.Reportes;

import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.Transaccion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteCliente implements Reporte {
    private final CuentaBancaria cuentaBancaria;

    public ReporteCliente(CuentaBancaria cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    @Override
    public ReporteGenerado generarReporte() {
        List<String> contenido = new ArrayList<>();

        contenido.add("Reporte de movimientos de la cuenta: " + cuentaBancaria.getNumeroCuenta());
        contenido.add("Titular: " + cuentaBancaria.getPropietario());
        contenido.add("Saldo actual: $" + String.format("%.2f", cuentaBancaria.getSaldo()));
        contenido.add("Fecha de generación: " + LocalDateTime.now());
        contenido.add("-----------------------------------------------------");

        for(Transaccion t : cuentaBancaria.getHistorial()){
            String tipoMovimiento;

            if(cuentaBancaria.getNumeroCuenta().equals(t.getCuentaOrigen())){
                tipoMovimiento = "Enviada a: " + t.getCuentaDestino();
            }
            else if(cuentaBancaria.getNumeroCuenta().equals(t.getCuentaDestino())){
                tipoMovimiento = "Recibida de : " + t.getCuentaDestino();
            }
            else{
                tipoMovimiento = t.getTipo();
            }
            String linea = String.format(
                    "ID: %s | %s | %s | Monto: $%.2f | %s",
                    t.getId(),
                    t.getFecha(),
                    tipoMovimiento,
                    t.getMonto(),
                    t.getDescripcion()
            );
            contenido.add(linea);
        }

        if(contenido.size() == 5){
            contenido.add("No se encontraron transacciones registradas");
        }

        return new ReporteGenerado(
                "Historial de movimientos - Cuenta: " + cuentaBancaria.getNumeroCuenta(),
                LocalDateTime.now(),
                contenido
        );
    }
}
