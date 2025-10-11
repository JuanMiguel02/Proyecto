package triplej.banco.Models.Usuarios;

import triplej.banco.Models.Cuentas.CuentaBancaria;

public class PersonaJuridica extends Persona{

    private String razonSocial;
    private String representanteLegal;
    private String tipoEmpresa;

    public PersonaJuridica(String razonSocial, String representanteLegal, String tipoEmpresa, String correo, String contrasenia, RolUsuario rolUsuario, TipoDocumento tipoDocumento, String numeroDocumento, String telefono, String pais, String ciudad){
        super(correo, contrasenia, rolUsuario, tipoDocumento, numeroDocumento, telefono, pais, ciudad);
        this.razonSocial = razonSocial;
        this.representanteLegal = representanteLegal;
        this.tipoEmpresa = tipoEmpresa;
    }

    @Override
    public String getNombreCompleto(){
        return this.razonSocial;
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
