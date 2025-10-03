package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Banco;
import triplej.banco.Models.Usuarios.Cliente;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
/*
*Clase que representa una Cuenta de Banco
*/
public abstract class CuentaBanco {

    private static final HashSet<String> numerosExistentes = new HashSet<>();

    private Cliente propietario;
    private String numeroCuenta;
    private double saldo;
    private LocalDate fechaApertura;


    public CuentaBanco(Cliente propietario, double saldo) {
        this.propietario = propietario;
        this.numeroCuenta = generarNumeroCuenta();
        this.saldo = saldo;
        this.fechaApertura = LocalDate.now();
    }

    //Metodo para generar un numero de cuenta
    private String generarNumeroCuenta(){
        String numero;
        do{
            //Crea una secuencia aleatoria de 5 caracteres entre 0 y 9999, luego formatea con ceros a la izquieda si es necesario
            String secuencia = String.format("%05d", ThreadLocalRandom.current().nextInt(0,10000));
            //Codigo del banco + codigo de la cuenta + secuencia generada
            numero = Banco.getCodigo() + getCodigoTipoCuenta() + secuencia;
            //Se le añade el digito verificador al de la cuenta
            numero+= calcularDigitoVerificador(numero);

        }while (numerosExistentes.contains(numero));
        numerosExistentes.add(numero);
        return numero;
    }

    //Recorre la secuencia creada, suma los digitos modulo 10 garantizando que sea un numero entre 0-9
    private String calcularDigitoVerificador(String numeroParcial){
        int suma = 0;
        for(char c: numeroParcial.toCharArray()){
            suma+= Character.getNumericValue(c);
        }
        int digito = suma % 10;
        return String.valueOf(digito);
    }

    public abstract String getCodigoTipoCuenta();

    @Override
    public String toString() {
        return "CuentaBanco{" +
                "propietario=" + propietario +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", saldo=" + saldo +
                ", fechaApertura=" + fechaApertura +
                '}';
    }

    public Cliente getPropietario() {
        return propietario;
    }

    public void setPropietario(Cliente propietario) {
        this.propietario = propietario;
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


    public void depositar(Double monto){
        if(monto <=0) throw new IllegalArgumentException("El monto debe ser positivo");
        saldo += monto;
    };

}
