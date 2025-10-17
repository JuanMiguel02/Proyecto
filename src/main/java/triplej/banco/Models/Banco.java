package triplej.banco.Models;

import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

public class Banco {

    private static Banco instancia;

    //Los bancos suelen tener un prefijo establecido para identificar sus transacciones
    private static final String CODIGO = "666";
    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final EmpleadoRepository empleados;

    private Banco() {
        this.usuarios = UsuarioRepository.getInstancia();
        this.clientes = ClienteRepository.getInstancia();
        this.empleados = EmpleadoRepository.getInstance();
    }

    public static String getCodigo(){
        return CODIGO;
    }

    public static Banco getInstancia(){
        if(instancia == null){
            instancia = new Banco();
        }
        return instancia;
    }

    public UsuarioRepository getUsuarioRepository() {
        return usuarios;
    }

    public ClienteRepository getClienteRepository(){
        return clientes;
    }

    public EmpleadoRepository getEmpleadoRepository(){ return empleados;}
}
