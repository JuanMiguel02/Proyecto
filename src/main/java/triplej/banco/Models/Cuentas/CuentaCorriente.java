package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.TransaccionRepository;

public class CuentaCorriente extends CuentaBancaria {
    private double limiteSobregiro;
    private double comisionSobregiro; // comisión o interés por usar sobregiro
    private boolean sobregirada;      // indica si actualmente está en negativo

    public CuentaCorriente(Cliente propietario){
        super(propietario);
        this.limiteSobregiro = 500000; // Permite sobregiro hasta -500,000
        this.comisionSobregiro = 0.02;  // 2% de comisión por sobregiro
        this.sobregirada = false;
    }

    public CuentaCorriente (Cliente propietario, String numeroCuenta, double saldo){
        super(propietario,numeroCuenta,saldo);
        this.limiteSobregiro = -500000; // Permite sobregiro hasta -500,000
        this.comisionSobregiro = 0.02;  // 2% de comisión por sobregiro
        this.sobregirada = false;
    }

    @Override
    public String getCodigoTipoCuenta() {
        return "2";
    }

    @Override
    public void retirar(Double monto) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser positivo");

        double nuevoSaldo = getSaldo() - monto;

        if (nuevoSaldo < -limiteSobregiro)
            throw new IllegalArgumentException("Límite de sobregiro alcanzado");

        // Aplica comisión si entra en sobregiro
        if (nuevoSaldo < 0) {
            double comision = monto * comisionSobregiro;
            nuevoSaldo -= comision;
            sobregirada = true;
        } else {
            sobregirada = false;
        }

        setSaldo(nuevoSaldo);

        Transaccion trans = new Transaccion(
                Transaccion.generarIdTransaccion(),
                "retiro",
                monto,
                getNumeroCuenta(),
                getNumeroCuenta()
        );
        trans.setDescripcion("Retiro cuenta corriente. Sobregirada: " + sobregirada);
        trans.setExitosa(true);
        TransaccionRepository.getInstance().agregar(trans);
        getHistorial().add(trans);
    }

    // Getters y setters
    public double getLimiteSobregiro() { return limiteSobregiro; }
    public void setLimiteSobregiro(double limiteSobregiro) { this.limiteSobregiro = limiteSobregiro; }
    public double getComisionSobregiro() { return comisionSobregiro; }
    public void setComisionSobregiro(double comisionSobregiro) { this.comisionSobregiro = comisionSobregiro; }
    public boolean isSobregirada() { return sobregirada; }


}
