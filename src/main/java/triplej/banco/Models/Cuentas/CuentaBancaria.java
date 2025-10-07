package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Banco;
import triplej.banco.Models.Usuarios.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
/*
*Clase que representa una Cuenta de Banco
*/
public abstract class CuentaBancaria {

    private static final HashSet<String> numerosExistentes = new HashSet<>();
    private ArrayList<Transaccion> historial = new ArrayList<>();
    private String numeroCuenta;
    private double saldo;
    private LocalDate fechaApertura;
    private Cliente propietario;

    public CuentaBancaria(Cliente propietario) {
        this.propietario = propietario;
        this.numeroCuenta = generarNumeroCuenta();
        this.saldo = 0.0;
        this.fechaApertura = LocalDate.now();
    }

    //Metodo para generar un numero de cuenta
    private String generarNumeroCuenta() {
        String numero;
        do {
            //Crea una secuencia aleatoria de 5 caracteres entre 0 y 9999, luego formatea con ceros a la izquieda si es necesario
            String secuencia = String.format("%05d", ThreadLocalRandom.current().nextInt(0, 10000));
            //Codigo del banco + codigo de la cuenta + secuencia generada
            numero = Banco.getCodigo() + getCodigoTipoCuenta() + secuencia;
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
        return "CuentaBanco{" +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", saldo=" + saldo +
                ", fechaApertura=" + fechaApertura +
                '}';
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

    public abstract void retirar(Double monto);


    public void depositar(Double monto, String descripcion) {
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser positivo");
        saldo += monto;

        Transaccion trans = new Transaccion(
              generarIdTransaccion(),
                "DEPÓSITO",
                monto,
                this.numeroCuenta,
                this.numeroCuenta
        );

        trans.setDescripcion(descripcion);
        trans.setExitosa(true);

        historial.add(trans);
    }


    private String generarIdTransaccion(){
        return "TXN-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);

    }
    public ArrayList<Transaccion> getHistorial() {
        return historial;
    }
}

