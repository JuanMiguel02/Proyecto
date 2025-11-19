package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;
/**
 * Representa una cuenta de ahorro dentro del sistema bancario.
 * <p>
 * Esta clase extiende la funcionalidad básica de {@link CuentaBancaria},
 * agregando características propias de las cuentas de ahorro, tales como:
 * <p>
 * - Tasa de interés mensual aplicada al saldo.
 * - Límite de retiros mensuales sin comisión.
 * - Comisión por retiro adicional al superar el límite establecido.
 * <p>
 * Los métodos de esta clase incluyen validaciones específicas sobre
 * el monto de retiro, número de retiros y aplicación de intereses.
 *
 */

public class CuentaAhorro extends CuentaBancaria {

    /** Tasa de interés mensual aplicada al saldo (por defecto 4%). */
    private double tasaInteres;

    /** Cantidad de retiros realizados durante el mes. */
    private int retirosMensuales;

    /** Número máximo de retiros mensuales sin comisión. */
    private int limiteRetirosMensuales;

    /**
     * Crea una nueva cuenta de ahorro asociada a un cliente,
     * con valores iniciales por defecto para la tasa de interés
     * y el límite de retiros mensuales.
     *
     * @param propietario Cliente propietario de la cuenta.
     */
    public CuentaAhorro(Cliente propietario){
        super(propietario);
        this.tasaInteres = 0.04; // 4% mensual
        this.limiteRetirosMensuales = 5;
        this.retirosMensuales = 0;
    }

    /**
     * Crea una cuenta de ahorro con número de cuenta y saldo inicial definidos.
     *
     * @param propietario Cliente propietario de la cuenta.
     * @param numeroCuenta Número único asignado a la cuenta.
     * @param saldo Saldo inicial de la cuenta.
     */
    public CuentaAhorro(Cliente propietario, String numeroCuenta, double saldo){
        super(propietario,numeroCuenta,saldo);
        this.tasaInteres = 0.04; // 4% mensual
       this.limiteRetirosMensuales = 5;
        this.retirosMensuales = 0;
    }

    /**
     * Devuelve el código identificador para el tipo de cuenta ahorro.
     *
     * @return Código tipo de cuenta, en este caso "1".
     */
    @Override
    public String getCodigoTipoCuenta() {
        return "1";
    }

    /**
     * Permite realizar un retiro de dinero de la cuenta de ahorro.
     * <p>
     * Este método valida que el monto sea positivo, que cumpla con el mínimo
     * requerido (cuando no se trata de una transferencia) y que haya fondos suficientes.
     * <p>
     * Además, lleva el conteo de los retiros mensuales y aplica una comisión del 1%
     * cuando el cliente supera el límite de retiros gratuitos establecidos por mes.
     *
     * @param monto Monto a retirar.
     * @param esTransferencia Indica si el retiro se realiza como parte de una transferencia.
     *
     * @throws IllegalArgumentException Si el monto es inválido o no hay fondos suficientes.
     */
    @Override
    public void retirar(Double monto, boolean esTransferencia) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe de ser mayor a 0");

        if (!esTransferencia && monto <  getRetiroMinimo()) throw new IllegalArgumentException("El retiro mínimo para cuentas de ahorro es de $" +  getRetiroMinimo());

        if (monto > getSaldo()) throw new IllegalArgumentException("Fondos insuficientes");

        double comision = 0.0;
        retirosMensuales++;

        // Si supera el límite de retiros gratuitos, cobra comisión
        if (retirosMensuales > limiteRetirosMensuales) {
            comision = monto * 0.01; // 1% adicional
        }

        double total = monto + comision;
        if (total > getSaldo()) throw new IllegalArgumentException("Saldo insuficiente para cubrir el retiro y la comisión");

        setSaldo(getSaldo() - total);

    }

    /**
     * Devuelve el monto mínimo permitido para realizar un retiro.
     *
     * @return Monto mínimo de retiro (10,000 pesos).
     */
    @Override
    public double getRetiroMinimo() {
        return 10000;
    }

    /**
     * Aplica el interés mensual sobre el saldo actual de la cuenta.
     * <p>
     * El cálculo se realiza multiplicando el saldo actual por la tasa de interés,
     * y posteriormente sumando el resultado al saldo total.
     */
    public void aplicarInteres() {
        double interes = getSaldo() * tasaInteres;
        setSaldo(getSaldo() + interes);
    }

    // Getters y setters
    public double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(double tasaInteres) { this.tasaInteres = tasaInteres; }
    public int getRetirosMensuales() { return retirosMensuales; }
    public void setRetirosMensuales(int retirosMensuales) { this.retirosMensuales = retirosMensuales; }
    public int getLimiteRetirosMensuales() { return limiteRetirosMensuales; }
    public void setLimiteRetirosMensuales(int limiteRetirosMensuales) { this.limiteRetirosMensuales = limiteRetirosMensuales; }
}
