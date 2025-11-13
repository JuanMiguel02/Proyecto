package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.TransaccionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/*
*Clase que representa una Cuenta de Banco
*/
public abstract class CuentaBancaria {

    private static final HashSet<String> numerosExistentes = new HashSet<>();
    private static final int CODIGO_BANCO = 666;
    private final ArrayList<Transaccion> historialTransacciones;
    private String numeroCuenta;
    private double saldo;
    private final LocalDate fechaApertura;
    private final Cliente propietario;
    private static final double DEPOSITO_MINIMO = 10_000;

    public CuentaBancaria(Cliente propietario) {
        this.propietario = propietario;
        this.numeroCuenta = generarNumeroCuenta();
        this.saldo = 0.0;
        this.fechaApertura = LocalDate.now();
        this.historialTransacciones = new ArrayList<>();
    }

    public CuentaBancaria(Cliente propietario, String numeroCuenta, double saldo) {
        this.propietario = propietario;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.fechaApertura = LocalDate.now();
        this.historialTransacciones = new ArrayList<>();

        cargarTransaccionesDesdeArchivo();
    }

    //Metodo para generar un numero de cuenta
    private String generarNumeroCuenta() {
        String numero;
        do {
            //Crea una secuencia aleatoria de 5 caracteres entre 0 y 9999, luego formatea con ceros a la izquieda si es necesario
            String secuencia = String.format("%05d", ThreadLocalRandom.current().nextInt(0, 10000));
            //Codigo del banco + codigo de la cuenta + secuencia generada
            numero = CODIGO_BANCO + getCodigoTipoCuenta() + secuencia;
            //Se le añade el digito verificador al de la cuenta
            numero += calcularDigitoVerificador(numero);

        } while (numerosExistentes.contains(numero));
        numerosExistentes.add(numero);
        return numero;
    }

    //Recorre la secuencia creada, suma los digitos modulo 10 garantizando que sea un numero entre 0-9
    private String calcularDigitoVerificador(String numeroParcial) {
        int suma = 0;
        for (char c : numeroParcial.toCharArray()) {
            suma += Character.getNumericValue(c);
        }
        int digito = suma % 10;
        return String.valueOf(digito);
    }

    public abstract String getCodigoTipoCuenta();

    @Override
    public String toString() {
        return String.format(
                "%s - %s - %s",
                numeroCuenta,
                propietario.getNombre(),
                fechaApertura
        );
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public double getSaldo() {
        return this.saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Cliente getPropietario() {
        return this.propietario;
    }

    public abstract double getRetiroMinimo();

    public abstract void retirar(Double monto, boolean esTransferencia);

    public void depositar(Double monto, boolean esTransferencia) {
        if(monto <= 0) {
            throw new IllegalArgumentException("El monto debe de ser mayor a 0");
        }

        if (!esTransferencia && monto < DEPOSITO_MINIMO) throw new IllegalArgumentException("El déposito mínimo es de $" + DEPOSITO_MINIMO);
        saldo += monto;
    }

    public String getNombreTipoCuenta() {
        return switch (getCodigoTipoCuenta()) {
            case "1" -> "Cuenta de Ahorro";
            case "2" -> "Cuenta Corriente";
            case "3" -> "Cuenta Empresarial";
            default -> "Tipo desconocido";
        };
    }

    public ArrayList<Transaccion> getHistorialTransacciones() {
        return historialTransacciones;
    }

    private void cargarTransaccionesDesdeArchivo() {
        List<Transaccion> transaccionesDeEstaCuenta =
                TransaccionRepository.getInstancia().getPorCuenta(this.numeroCuenta);

        this.historialTransacciones.clear();
        this.historialTransacciones.addAll(transaccionesDeEstaCuenta);

        System.out.println(" Cargadas " + transaccionesDeEstaCuenta.size() +
                " transacciones para cuenta " + this.numeroCuenta);
    }
}

