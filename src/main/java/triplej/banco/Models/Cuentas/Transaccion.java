package triplej.banco.Models.Cuentas;

import java.time.LocalDate;

public class Transaccion {
    private String id;
    private LocalDate fecha;
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
        this.fecha = LocalDate.now();
        this.tipo = tipo;
        this.monto = monto;
        this.exitosa = false;
    }

    public String getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
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
