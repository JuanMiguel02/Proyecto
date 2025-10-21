package triplej.banco.Repositories;

import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;

import java.util.ArrayList;
import java.util.Optional;

public class ClienteRepository {
    private static ClienteRepository instancia;
    private final ArrayList<Cliente> clientes;
    private final UsuarioRepository usuarioRepository;

    private ClienteRepository(){
        this.clientes = new ArrayList<>();
        this.usuarioRepository = UsuarioRepository.getInstancia();
    }

    public static synchronized ClienteRepository getInstancia(){
        if(instancia == null){
            instancia = new ClienteRepository();
        }
        return instancia;
    }

    public void guardar(Cliente cliente){
        usuarioRepository.guardar(cliente.getUsuarioAsociado());
        clientes.add(cliente);
    }

    public Optional<Cliente> buscarPorEmail(String email){
        return clientes.stream()
                .filter(c -> c.getUsuarioAsociado().getCorreo().equals(email))
                .findFirst();
    }

    public Optional<CuentaBancaria> buscarCuentaPorNumero(String numeroCuenta){
        return clientes.stream()
                .flatMap(cliente -> cliente.getCuentas().stream())
                .filter(cuentaBancaria -> cuentaBancaria.getNumeroCuenta().equals(numeroCuenta))
                .findFirst();
    }

    public Optional<Cliente> buscarClientePorCuenta(String numeroCuenta){
        return clientes.stream()
                .filter(c -> c.getCuentas().stream()
                        .anyMatch(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta)))
                        .findFirst();
    }

    public int contarClientes(){
        return clientes.size();
    }
}
