package triplej.banco.Models.Cuentas;

import triplej.banco.Repositories.TransaccionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Transaccion {
    private String id;
    private LocalDateTime fecha;
    private String tipo;
    private double monto;
    private String descripcion;
    private String cuentaOrigen;
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

    public boolean esSospechosa(){
        if(monto > 10_000_000) return true;
        if(cuentaOrigen != null && cuentaOrigen.equals(cuentaDestino)) return true;

        TransaccionRepository repo = TransaccionRepository.getInstance();
        List<Transaccion> historial = repo.getPorCuenta(cuentaOrigen);

        LocalDateTime ahora = LocalDateTime.now();
        long recientes = historial.stream()
                .filter(t -> !t.equals(this))
                .filter(t -> t.getFecha().isAfter(ahora.minusMinutes(10)))
                .count();

        if(recientes >= 5) return true;

        int hora = fecha.getHour();
        if(hora >=0 && hora <= 4) return true;

        return false;
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
}
