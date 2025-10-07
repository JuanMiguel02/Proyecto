package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBancaria;

public class PersonaJuridica extends Cliente{
    private String razonSocial;
    private String representanteLegal;
    private String tipoEmpresa;

    public PersonaJuridica(String razonSocial, String representanteLegal, String tipoEmpresa, TipoDocumento documento, String numeroDocumento, String telefono, String email, String contrasenia, String pais, String ciudad){
        super(razonSocial, documento, numeroDocumento, telefono, email, contrasenia, pais, ciudad);
        this.representanteLegal = representanteLegal;
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }
}
