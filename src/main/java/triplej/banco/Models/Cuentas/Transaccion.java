package triplej.banco.Models.Cuentas;

import triplej.banco.Repositories.TransaccionRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Representa una transacción bancaria dentro del sistema.
 *
 * <p>
 * Cada transacción almacena información sobre el tipo de operación realizada
 * (por ejemplo: depósito, retiro, transferencia), la cuenta de origen,
 * la cuenta de destino (si aplica), el monto involucrado, la fecha y hora,
 * y si fue o no exitosa.
 * </p>
 *
 * <p>
 * Además, la clase incluye lógica para identificar operaciones sospechosas
 * según criterios de negocio (por ejemplo, montos elevados o frecuencia de transacciones).
 * </p>
 */
public class Transaccion {
    private final String id;                    //ID de la transacción
    private LocalDateTime fecha;                //Fecha de la transacción
    private final String tipo;                  //Tipo de transacción
    private final double monto;                 //Monto de la transacción
    private String descripcion;                 //Descripción de la transacción
    private final String cuentaOrigen;          //Cuenta de origen de la transacción
    private String cuentaDestino;               //Cuenta de destino de la transacción
    private boolean exitosa;                    //Determina su exito

    /**
     * Crea una transacción con cuenta de origen y cuenta de destino.
     *
     * @param id            Identificador único de la transacción.
     * @param tipo          Tipo de transacción (ej. "Transferencia", "Depósito").
     * @param monto         Monto involucrado en la operación.
     * @param cuentaOrigen  Número de cuenta de origen.
     * @param cuentaDestino Número de cuenta de destino.
     */
    public Transaccion(String id, String tipo, double monto, String cuentaOrigen, String cuentaDestino){
        this.id = id;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.fecha = LocalDateTime.now();
        this.tipo = tipo;
        this.monto = monto;
        this.exitosa = false;
    }

    /**
     * Crea una transacción que solo tiene cuenta de origen (por ejemplo, un retiro o depósito).
     *
     * @param id           Identificador único de la transacción.
     * @param tipo         Tipo de transacción (ej. "Depósito", "Retiro").
     * @param monto        Monto involucrado en la operación.
     * @param cuentaOrigen Número de cuenta de origen.
     */
    public Transaccion(String id, String tipo, double monto, String cuentaOrigen){
        this.id = id;
        this.cuentaOrigen = cuentaOrigen;
        this.fecha = LocalDateTime.now();
        this.tipo = tipo;
        this.monto = monto;
        this.exitosa = false;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Devuelve la fecha de la transacción formateada en formato legible.
     *
     * @return Fecha y hora en formato "dd/MM/yyyy HH:mm:ss".
     */
    public String getFechaFormateada(){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(formato);
    }

    public String getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public boolean isExitosa() {
        return exitosa;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setExitosa(boolean exitosa) {
        this.exitosa = exitosa;
    }

    public void setFecha(LocalDateTime fecha){
        this.fecha = fecha;
    }

    /**
     * Determina si una transacción debe considerarse sospechosa.
     *
     * <p>Se aplica cualquiera de las siguientes condiciones:</p>
     * <ul>
     *     <li>El monto supera los 10 millones de pesos.</li>
     *     <li>La cuenta ha realizado más de 5 transacciones en los últimos 10 minutos.</li>
     *     <li>La operación se realiza entre la medianoche y las 4 a. m.</li>
     * </ul>
     *
     * @return {@code true} si la transacción es sospechosa; {@code false} en caso contrario.
     */
    public boolean esSospechosa(){
        if(monto > 10_000_000) return true;

        TransaccionRepository repo = TransaccionRepository.getInstancia();
        List<Transaccion> historial = repo.getPorCuenta(cuentaOrigen);

        LocalDateTime ahora = LocalDateTime.now();
        long recientes = historial.stream()
                .filter(t -> !t.equals(this))
                .filter(t -> t.getFecha().isAfter(ahora.minusMinutes(10)))
                .count();

        if(recientes >= 5) return true;

        int hora = fecha.getHour();
        return hora <= 4;
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "id='" + id + '\'' +
                ", fecha=" + fecha +
                ", tipo='" + tipo + '\'' +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                ", cuentaOrigen='" + cuentaOrigen + '\'' +
                ", cuentaDestino='" + cuentaDestino + '\'' +
                ", exitosa=" + exitosa +
                '}';
    }

    /**
     * Genera un identificador único para una nueva transacción.
     * <p>
     * El formato es: {@code TXN-[timestamp]-[número aleatorio de 4 dígitos]}.
     * </p>
     *
     * @return Identificador único de transacción.
     */
    public static String generarIdTransaccion(){
        return "TXN-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }
}
