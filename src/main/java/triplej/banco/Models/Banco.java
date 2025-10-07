package triplej.banco.Models;

import triplej.banco.Repositories.UsuarioRepository;

public class Banco {

    private static Banco instancia;

    //Los bancos suelen tener un prefijo establecido para identificar sus transacciones
    private static final String CODIGO = "666";
    private final UsuarioRepository usuarios;

    private Banco() {
        this.usuarios = new UsuarioRepository();
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
}
