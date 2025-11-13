package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

public class CuentaCorriente extends CuentaBancaria {
    private static final double SOBREGIRO_MINIMO = 200_000;
    private static final double SOBREGIRO_MAXIMO = 5_000_000;

    private double limiteSobregiro;
    private double tasaComisionSobregiro; // porcentaje de comisión
    private boolean sobregirada;      // indica si actualmente está en negativo

    public CuentaCorriente(Cliente propietario) {
        this(propietario, generarLimiteSobregiroPorDefecto());
    }

    public CuentaCorriente(Cliente propietario, double limiteSobregiro){
        super(propietario);
        setLimiteSobregiro(limiteSobregiro);
        this.tasaComisionSobregiro = 0.02; // 2%
        this.sobregirada = false;
    }

    public CuentaCorriente (Cliente propietario, String numeroCuenta, double saldo, double limiteSobregiro){
        super(propietario,numeroCuenta,saldo);
        setLimiteSobregiro(limiteSobregiro);
        this.tasaComisionSobregiro = 0.02;  // 2% de comisión por sobregiro
        this.sobregirada = saldo < 0;
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "2";
    }

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

    private static double generarLimiteSobregiroPorDefecto() {
        return SOBREGIRO_MINIMO; // por defecto el banco asigna el mínimo
    }

    // Getters y setters
    public void setLimiteSobregiro(double limiteSobregiro) {
        if (limiteSobregiro < SOBREGIRO_MINIMO || limiteSobregiro > SOBREGIRO_MAXIMO) {
            throw new IllegalArgumentException(
                    "El sobregiro debe estar entre $" + SOBREGIRO_MINIMO + " y $" + SOBREGIRO_MAXIMO
            );
        }
        this.limiteSobregiro = limiteSobregiro;
    }

    @Override
    public double getRetiroMinimo() {
        return 20000;
    }

    public double getLimiteSobregiro() { return limiteSobregiro; }
    public double getComisionSobregiro() { return tasaComisionSobregiro; }
    public void setComisionSobregiro(double comisionSobregiro) { this.tasaComisionSobregiro = comisionSobregiro; }
    public boolean isSobregirada() { return sobregirada; }

}
