package triplej.banco.Models.Cuentas;

import triplej.banco.Repositories.TransaccionRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Transaccion {
    private String id;
    private LocalDateTime fecha;
    private final String tipo;
    private final double monto;
    private String descripcion;
    private final String cuentaOrigen;
    private String cuentaDestino;
    private boolean exitosa;

    public Transaccion(String id, String tipo, double monto, String cuentaOrigen, String cuentaDestino){
        this.id = id;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.fecha = LocalDateTime.now();
        this.tipo = tipo;
        this.monto = monto;
        this.exitosa = false;
    }

    public Transaccion(String id, String tipo, double monto, String cuentaOrigen){
        this.id = id;
        this.cuentaOrigen = cuentaOrigen;
        this.fecha = LocalDateTime.now();
        this.tipo = tipo;
        this.monto = monto;
        this.exitosa = false;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

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


    public static String generarIdTransaccion(){
        return "TXN-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }
}
