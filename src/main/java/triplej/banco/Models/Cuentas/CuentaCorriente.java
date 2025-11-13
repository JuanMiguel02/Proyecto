package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

/**
 * Representa una cuenta corriente dentro del sistema bancario.
 * <p>
 * Este tipo de cuenta permite realizar sobregiros dentro de un límite establecido.
 * Además, aplica una comisión cuando el saldo cae por debajo de cero.
 * <p>
 * Hereda de {@link CuentaBancaria} y redefine el comportamiento de retiro
 * para incluir la lógica del sobregiro y su comisión correspondiente.
 */
public class CuentaCorriente extends CuentaBancaria {
    /** Límite mínimo permitido para el sobregiro. */
    private static final double SOBREGIRO_MINIMO = 200_000;

    /** Límite máximo permitido para el sobregiro. */
    private static final double SOBREGIRO_MAXIMO = 5_000_000;

    /** Monto máximo que puede sobregirarse la cuenta. */
    private double limiteSobregiro;

    /** Porcentaje de comisión aplicada al valor sobregirado. */
    private double tasaComisionSobregiro;

    /** Indica si la cuenta actualmente se encuentra sobregirada (saldo negativo). */
    private boolean sobregirada;      // indica si actualmente está en negativo

    /**
     * Crea una cuenta corriente con el límite de sobregiro mínimo por defecto.
     *
     * @param propietario Cliente propietario de la cuenta.
     */
    public CuentaCorriente(Cliente propietario) {
        this(propietario, generarLimiteSobregiroPorDefecto());
    }

    /**
     * Crea una cuenta corriente especificando el límite de sobregiro.
     *
     * @param propietario       Cliente propietario de la cuenta.
     * @param limiteSobregiro   Límite máximo permitido para sobregiro.
     */
    public CuentaCorriente(Cliente propietario, double limiteSobregiro){
        super(propietario);
        setLimiteSobregiro(limiteSobregiro);
        this.tasaComisionSobregiro = 0.02; // 2%
        this.sobregirada = false;
    }

    /**
     * Crea una cuenta corriente especificando número, saldo inicial y límite de sobregiro.
     *
     * @param propietario       Cliente propietario de la cuenta.
     * @param numeroCuenta      Número de cuenta asignado.
     * @param saldo             Saldo inicial.
     * @param limiteSobregiro   Límite máximo permitido para sobregiro.
     */
    public CuentaCorriente (Cliente propietario, String numeroCuenta, double saldo, double limiteSobregiro){
        super(propietario,numeroCuenta,saldo);
        setLimiteSobregiro(limiteSobregiro);
        this.tasaComisionSobregiro = 0.02;  // 2% de comisión por sobregiro
        this.sobregirada = saldo < 0;
    }

    /**
     * Retorna el código identificador de este tipo de cuenta.
     *
     * @return "2" correspondiente a una cuenta corriente.
     */
    @Override
    public String getCodigoTipoCuenta() {
        return "2";
    }

    /**
     * Realiza un retiro de la cuenta corriente, considerando el límite de sobregiro.
     *
     * <p>Válida que:</p>
     * <ul>
     *     <li>El monto sea mayor a cero.</li>
     *     <li>El monto no sea menor al retiro mínimo permitido.</li>
     *     <li>No se supere el límite de sobregiro configurado.</li>
     * </ul>
     *
     * <p>Si el saldo resultante es negativo, aplica una comisión equivalente al
     * porcentaje definido en {@code tasaComisionSobregiro}.</p>
     *
     * @param monto             Monto a retirar.
     * @param esTransferencia   Indica si el retiro proviene de una transferencia (omite validaciones de retiro mínimo).
     * @throws IllegalArgumentException si el monto es inválido o excede el límite de sobregiro.
     */
    @Override
    public void retirar(Double monto, boolean esTransferencia) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe de ser mayor a 0");

        if (!esTransferencia && monto < getRetiroMinimo()) throw new IllegalArgumentException("El retiro mínimo para cuentas corrientes es de $" + getRetiroMinimo());

        if (monto > getSaldo()) throw new IllegalArgumentException("Fondos insuficientes");

        double nuevoSaldo = getSaldo() - monto;

        // Verificar si supera el límite permitido
        if (nuevoSaldo < -limiteSobregiro)
            throw new IllegalArgumentException("Límite de sobregiro alcanzado. Máximo permitido: $" + limiteSobregiro);

        // Si entra en sobregiro, aplicar comisión
        if (nuevoSaldo < 0) {
            double comision = Math.abs(nuevoSaldo) * tasaComisionSobregiro;
            nuevoSaldo -= comision;
            sobregirada = true;
        } else {
            sobregirada = false;
        }

        setSaldo(nuevoSaldo);
    }

    /**
     * Genera el límite de sobregiro por defecto asignado por el banco.
     *
     * @return El valor mínimo permitido para sobregiro.
     */
    private static double generarLimiteSobregiroPorDefecto() {
        return SOBREGIRO_MINIMO; // por defecto el banco asigna el mínimo
    }

    /**
     * Establece el límite de sobregiro verificando que esté dentro del rango permitido.
     *
     * @param limiteSobregiro Límite deseado para el sobregiro.
     * @throws IllegalArgumentException si el valor está fuera del rango permitido.
     */
    public void setLimiteSobregiro(double limiteSobregiro) {
        if (limiteSobregiro < SOBREGIRO_MINIMO || limiteSobregiro > SOBREGIRO_MAXIMO) {
            throw new IllegalArgumentException(
                    "El sobregiro debe estar entre $" + SOBREGIRO_MINIMO + " y $" + SOBREGIRO_MAXIMO
            );
        }
        this.limiteSobregiro = limiteSobregiro;
    }

    /**
     * Retorna el monto mínimo permitido para realizar un retiro.
     *
     * @return 20,000 pesos.
     */
    @Override
    public double getRetiroMinimo() {
        return 20000;
    }

    public double getLimiteSobregiro() { return limiteSobregiro; }
    public double getComisionSobregiro() { return tasaComisionSobregiro; }
    public void setComisionSobregiro(double comisionSobregiro) { this.tasaComisionSobregiro = comisionSobregiro; }
    public boolean isSobregirada() { return sobregirada; }

}
