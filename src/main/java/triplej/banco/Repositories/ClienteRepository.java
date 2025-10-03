package triplej.banco.Repositories;

import triplej.banco.Models.Usuarios.Cliente;

import java.util.ArrayList;

public class ClienteRepository {
    private static ClienteRepository instancia;
    private ArrayList<Cliente> clientes;

    private ClienteRepository(){
        clientes = new ArrayList<>();
    }

    public static ClienteRepository getInstancia(){
        if(instancia == null){
            instancia = new ClienteRepository();
        }
        return instancia;
    }

    public ArrayList<Cliente> getClientes(){
        return clientes;
    }

    public void agregarCliente(Cliente cliente){
        clientes.add(cliente);
    }
}
