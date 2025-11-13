package triplej.banco.Models.Usuarios;

public class Empleado{

    private String cargo;
    private double salario;
    private PersonaNatural persona;
    private String departamento;

    public Empleado(PersonaNatural persona, String cargo, double salario, String departamento){
        this.cargo = cargo;
        this.salario = salario;
        this.persona = persona;
        this.departamento = departamento;
    }

    public PersonaNatural getPersona() {
        return persona;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getNombre(){
        return this.persona.getNombre();
    }

    public String getApellido(){
        return this.persona.getApellido();
    }

    public String getFoto(){
        return this.persona.getFoto();
    }

    public String getNombreCompleto(){
        return this.persona.getNombreUsuario();
    }

    public String getCorreo(){
        return this.persona.getCorreo();
    }

    public String getTelefono(){
        return this.persona.getTelefono();
    }

    public String getCiudad(){
        return this.persona.getCiudad();
    }

    public String getDocumento(){
        return this.persona.getNumeroDocumento();
    }

    public String getDepartamento() {
        return this.departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setContrasenia(String contrasenia) {
        this.persona.setContrasenia(contrasenia);
    }
}
