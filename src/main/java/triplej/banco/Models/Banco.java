package triplej.banco.Models;

import triplej.banco.Repositories.ClienteRepository;

public class Banco {

    //Los bancos suelen tener un prefijo establecido para identificar sus transacciones
    private static final String CODIGO = "666";
    private ClienteRepository clientes;

    public static String getCodigo(){
        return CODIGO;
    }


}
