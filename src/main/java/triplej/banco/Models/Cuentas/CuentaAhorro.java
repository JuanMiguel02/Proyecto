package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;

public class CuentaAhorro extends CuentaBancaria {

    private double tasaInteres;
    private int retirosMensuales;
    private int limiteRetirosMensuales;

    public CuentaAhorro(Cliente propietario){
        super(propietario);
        this.tasaInteres = 0.04; // 4% mensual
        this.limiteRetirosMensuales = 3;
        this.retirosMensuales = 0;
    }

    public CuentaAhorro(Cliente propietario, String numeroCuenta, double saldo){
        super(propietario,numeroCuenta,saldo);
        this.tasaInteres = 0.04; // 4% mensual
        this.limiteRetirosMensuales = 3;
        this.retirosMensuales = 0;
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "1";
    }

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

    @Override
    public double getRetiroMinimo() {
        return 10000;
    }

    /**
     * Aplica intereses sobre el saldo actual.
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
