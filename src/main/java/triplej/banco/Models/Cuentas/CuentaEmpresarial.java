package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

/**
 * Representa una cuenta empresarial dentro del sistema bancario.
 * <p>
 * Este tipo de cuenta está orientado a empresas o negocios que requieren
 * manejar montos elevados, mantener un saldo mínimo obligatorio y pagar una
 * comisión por ciertas transacciones.
 * <p>
 * Hereda de {@link CuentaBancaria} y redefine las operaciones de retiro
 * para incluir reglas adicionales sobre el saldo mínimo, las comisiones
 * y los topes de transferencia permitidos.
 */
public class CuentaEmpresarial extends CuentaBancaria {
    private double saldoMinimo;         // Saldo mínimo requerido
    private double comisionTransaccion; // Comisión fija por transacción
    private double topeTransferencia;   // Límite máximo por retiro o transferencia

    /**
     * Crea una nueva cuenta empresarial con valores predeterminados.
     *
     * @param propietario Cliente propietario de la cuenta.
     */
    public CuentaEmpresarial(Cliente propietario){
        super(propietario);
        this.saldoMinimo = 100000;
        this.comisionTransaccion = 10000;
        this.topeTransferencia = 20000000; // 20 millones
    }

    /**
     * Crea una cuenta empresarial con número y saldo inicial definidos.
     *
     * @param propietario   Cliente propietario de la cuenta.
     * @param numeroCuenta  Número de cuenta asignado.
     * @param saldo         Saldo inicial.
     */
    public CuentaEmpresarial (Cliente propietario, String numeroCuenta, double saldo){
        super(propietario,numeroCuenta,saldo);
        this.saldoMinimo = 10000;
        this.comisionTransaccion = 10000;
        this.topeTransferencia = 20000000; // 20 millones
    }

    /**
     * Retorna el código identificador de este tipo de cuenta.
     *
     * @return "3" correspondiente a una cuenta empresarial.
     */
    @Override
    public String getCodigoTipoCuenta() {
        return "3";
    }

    /**
     * Realiza un retiro de la cuenta empresarial, aplicando las reglas de negocio correspondientes.
     *
     * <p>Válida que:</p>
     * <ul>
     *     <li>El monto a retirar sea mayor que cero.</li>
     *     <li>El monto no sea menor al retiro mínimo permitido.</li>
     *     <li>El monto no supere el saldo disponible.</li>
     *     <li>El monto no exceda el tope máximo permitido por transacción.</li>
     *     <li>El saldo resultante no quede por debajo del saldo mínimo requerido.</li>
     * </ul>
     *
     * <p>Si la operación es una transferencia, se aplica además la comisión definida
     * en {@code comisionTransaccion}.</p>
     *
     * @param monto             Monto a retirar.
     * @param esTransferencia   Indica si el retiro proviene de una transferencia (aplica comisión adicional).
     * @throws IllegalArgumentException si alguna condición de las reglas de negocio no se cumple.
     */
    @Override
    public void retirar(Double monto, boolean esTransferencia) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe de ser mayor a 0");

        if (!esTransferencia && monto < getRetiroMinimo()) throw new IllegalArgumentException("El retiro mínimo para cuentas empresariales es de $" + getRetiroMinimo());

        if (monto > getSaldo()) throw new IllegalArgumentException("Fondos insuficientes");

        if (monto > topeTransferencia)
            throw new IllegalArgumentException("El monto supera el límite de retiro por transacción (" + topeTransferencia + ")");

        double total = monto + (esTransferencia ? comisionTransaccion : 0);

        if (getSaldo() - total < saldoMinimo)
            throw new IllegalArgumentException("Debe mantener un saldo mínimo de " + saldoMinimo);

        setSaldo(getSaldo() - total);

    }

    /**
     * Retorna el monto mínimo permitido para realizar un retiro.
     *
     * @return 50,000 pesos.
     */
    @Override
    public double getRetiroMinimo() {
        return 50000;
    }

    // Getters y setters
    public double getSaldoMinimo() { return saldoMinimo; }
    public void setSaldoMinimo(double saldoMinimo) { this.saldoMinimo = saldoMinimo; }
    public double getComisionTransaccion() { return comisionTransaccion; }
    public void setComisionTransaccion(double comisionTransaccion) { this.comisionTransaccion = comisionTransaccion; }
    public double getTopeTransferencia() { return topeTransferencia; }
    public void setTopeTransferencia(double topeTransferencia) { this.topeTransferencia = topeTransferencia; }

}
