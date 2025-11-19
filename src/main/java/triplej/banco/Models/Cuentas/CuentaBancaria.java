package triplej.banco.Models.Cuentas;

import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.TransaccionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Representa una cuenta bancaria genérica dentro del sistema.
 * <p>
 * Esta clase abstracta define los atributos y comportamientos comunes
 * a todos los tipos de cuentas bancarias, incluyendo:
 * <p>
 * - Generación automática y validada de números de cuenta.
 * - Control de saldo y operaciones básicas (depósito y retiro).
 * - Registro y carga del historial de transacciones.
 * <p>
 * Las clases concretas (como {@link CuentaAhorro} o CuentaCorriente)
 * deben implementar los métodos abstractos que definen el tipo de cuenta
 * y las condiciones específicas de retiro.
 *
 */
public abstract class CuentaBancaria {

    /** Conjunto de números de cuenta existentes, usado para evitar duplicados. */
    private static final HashSet<String> numerosExistentes = new HashSet<>();

    /** Código identificador del banco utilizado en la generación de cuentas. */
    private static final int CODIGO_BANCO = 666;

    /** Historial de transacciones asociadas a la cuenta. */
    private final ArrayList<Transaccion> historialTransacciones;

    /** Número único de cuenta. */
    private String numeroCuenta;

    /** Saldo actual de la cuenta. */
    private double saldo;

    /** Fecha en la que se abrió la cuenta. */
    private final LocalDate fechaApertura;

    /** Cliente propietario de la cuenta. */
    private final Cliente propietario;

    /** Monto mínimo permitido para realizar depósitos. */
    private static final double DEPOSITO_MINIMO = 10_000;

    /**
     * Constructor que crea una nueva cuenta bancaria con un número de cuenta generado automáticamente.
     *
     * @param propietario Cliente propietario de la cuenta.
     */
    public CuentaBancaria(Cliente propietario) {
        this.propietario = propietario;
        this.numeroCuenta = generarNumeroCuenta();
        this.saldo = 0.0;
        this.fechaApertura = LocalDate.now();
        this.historialTransacciones = new ArrayList<>();
    }

    /**
     * Constructor que permite crear una cuenta con datos previamente definidos,
     * por ejemplo, al cargar cuentas desde un archivo.
     *
     * @param propietario Cliente propietario de la cuenta.
     * @param numeroCuenta Número único de cuenta.
     * @param saldo Saldo inicial de la cuenta.
     */
    public CuentaBancaria(Cliente propietario, String numeroCuenta, double saldo) {
        this.propietario = propietario;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.fechaApertura = LocalDate.now();
        this.historialTransacciones = new ArrayList<>();

        cargarTransaccionesDesdeArchivo();
    }

    /**
     * Genera un número de cuenta único utilizando un patrón basado en:
     * <p>
     * - Código del banco (fijo).
     * - Código del tipo de cuenta (definido por cada subclase).
     * - Secuencia aleatoria de 5 dígitos.
     * - Dígito verificador calculado con el módulo 10.
     * <p>
     * El método asegura que no existan duplicados en el sistema.
     *
     * @return Número de cuenta generado.
     */
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

    /**
     * Calcula el dígito verificador para un número parcial de cuenta.
     * <p>
     * Recorre los dígitos del número generado, suma sus valores numéricos
     * y obtiene el módulo 10 del total, asegurando que el resultado sea
     * un número entre 0 y 9.
     *
     * @param numeroParcial Número de cuenta sin el dígito verificador.
     * @return Dígito verificador calculado.
     */
    private String calcularDigitoVerificador(String numeroParcial) {
        int suma = 0;
        for (char c : numeroParcial.toCharArray()) {
            suma += Character.getNumericValue(c);
        }
        int digito = suma % 10;
        return String.valueOf(digito);
    }

/**
 * Retorna el código que identifica el tipo de cuenta.
 * <p>
 * Este método debe ser implementado por cada subclase para
 * definir su propio identificador.
 *
 * @return Código de tipo de cuenta.
 */
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

    /**
     * Retorna el monto mínimo que se puede retirar en esta cuenta.
     *
     * @return Monto mínimo de retiro.
     */
    public abstract double getRetiroMinimo();

    /**
     * Realiza un retiro del saldo disponible en la cuenta.
     * <p>
     * Este método es abstracto y debe ser implementado por las subclases,
     * ya que las condiciones de retiro varían según el tipo de cuenta.
     *
     * @param monto Monto a retirar.
     * @param esTransferencia Indica si el retiro proviene de una transferencia.
     */
    public abstract void retirar(Double monto, boolean esTransferencia);

    /**
     * Permite realizar un depósito en la cuenta.
     * <p>
     * Este método valida que el monto sea mayor que cero y cumpla con el
     * depósito mínimo permitido cuando no se trata de una transferencia.
     * <p>
     * Si las validaciones son correctas, el monto se suma al saldo actual.
     *
     * @param monto Monto a depositar.
     * @param esTransferencia Indica si el depósito proviene de una transferencia.
     * @throws IllegalArgumentException Si el monto es inválido o menor al depósito mínimo.
     */
    public void depositar(Double monto, boolean esTransferencia) {
        if(monto <= 0) {
            throw new IllegalArgumentException("El monto debe de ser mayor a 0");
        }

        if (!esTransferencia && monto < DEPOSITO_MINIMO) throw new IllegalArgumentException("El déposito mínimo es de $" + DEPOSITO_MINIMO);
        saldo += monto;
    }

    /**
     * Devuelve el nombre descriptivo del tipo de cuenta, basado en el código.
     *
     * @return Nombre del tipo de cuenta.
     */
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

    /**
     * Carga las transacciones asociadas a esta cuenta desde el repositorio.
     * <p>
     * Este método consulta al {@link TransaccionRepository} por todas las
     * transacciones que coincidan con el número de cuenta actual y las
     * almacena en el historial interno de la cuenta.
     */
    private void cargarTransaccionesDesdeArchivo() {
        List<Transaccion> transaccionesDeEstaCuenta =
                TransaccionRepository.getInstancia().getPorCuenta(this.numeroCuenta);

        this.historialTransacciones.clear();
        this.historialTransacciones.addAll(transaccionesDeEstaCuenta);

        System.out.println(" Cargadas " + transaccionesDeEstaCuenta.size() +
                " transacciones para cuenta " + this.numeroCuenta);
    }
}

