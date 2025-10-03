package triplej.banco.Models.Usuarios;

public enum TipoDocumento {
    CEDULACIUDADANIA("Cédula de Ciudadanía"),
    PASAPORTE("Pasaporte"),
    CEDULAEXTRANJERIA("Cédula de Extranjería"),
    REGISTROCIVIL("Registro Civil"),
    NIT("NIT");


    private final String documento;

    TipoDocumento(String documento) {
        this.documento = documento;
    }

    public String toString(){
        return this.documento;
    }
}
