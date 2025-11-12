package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.TransaccionRepository;

public class CuentaEmpresarial extends CuentaBancaria {
    private double saldoMinimo;         // Saldo mínimo requerido
    private double comisionTransaccion; // Comisión fija por transacción
    private double topeTransferencia;   // Límite máximo por retiro o transferencia


    public CuentaEmpresarial(Cliente propietario){
        super(propietario);
        this.saldoMinimo = 10000;
        this.comisionTransaccion = 10000;
        this.topeTransferencia = 20000000; // 20 millones
    }

    public CuentaEmpresarial (Cliente propietario, String numeroCuenta, double saldo){
        super(propietario,numeroCuenta,saldo);
        this.saldoMinimo = 10000;
        this.comisionTransaccion = 10000;
        this.topeTransferencia = 20000000; // 20 millones
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "3";
    }

    @Override
    public void retirar(Double monto) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser positivo");
        if (monto > topeTransferencia)
            throw new IllegalArgumentException("El monto supera el límite de retiro por transacción (" + topeTransferencia + ")");

        double total = monto + comisionTransaccion;

        if (getSaldo() - total < saldoMinimo)
            throw new IllegalArgumentException("Debe mantener un saldo mínimo de " + saldoMinimo);

        setSaldo(getSaldo() - total);

        Transaccion trans = new Transaccion(
                Transaccion.generarIdTransaccion(),
                "retiro",
                monto,
                getNumeroCuenta(),
                getNumeroCuenta()
        );
        trans.setDescripcion("Retiro cuenta empresarial (Comisión: " + comisionTransaccion + ")");
        trans.setExitosa(true);
        TransaccionRepository.getInstance().agregar(trans);
        getHistorial().add(trans);
    }

    // Getters y setters
    public double getSaldoMinimo() { return saldoMinimo; }
    public void setSaldoMinimo(double saldoMinimo) { this.saldoMinimo = saldoMinimo; }
    public double getComisionTransaccion() { return comisionTransaccion; }
    public void setComisionTransaccion(double comisionTransaccion) { this.comisionTransaccion = comisionTransaccion; }
    public double getTopeTransferencia() { return topeTransferencia; }
    public void setTopeTransferencia(double topeTransferencia) { this.topeTransferencia = topeTransferencia; }

}
