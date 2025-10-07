package triplej.banco.Repositories;

import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsuarioRepository {
    private static UsuarioRepository instancia;
    // Unica lista para todos los usuarios.
    private final ArrayList<Usuario> usuarios;

    public UsuarioRepository() {
        usuarios = new ArrayList<>();
    }

    public static synchronized UsuarioRepository getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioRepository();
        }
        return instancia;
    }

    // Unico metodo para guardar cualquier tipo de usuario.
    public void guardar(Usuario usuario) {
        if(buscarPorEmail(usuario.getCorreo()).isPresent()){
            throw  new IllegalArgumentException("Advertencia: Se intentó guardar un usuario con un email que ya existe: " + usuario.getCorreo());
        }
        usuarios.add(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    public List<Cliente> getClientes() {
        return usuarios.stream()
                .filter(u -> u instanceof Cliente)
                .map(u -> (Cliente) u)
                .collect(Collectors.toList());
    }

    public Optional<CuentaBancaria> buscarCuentaPorNumero(String numeroCuenta) {
        return usuarios.stream()
                .filter(u -> u instanceof Cliente)
                .map(u -> (Cliente) u)
                .flatMap(c -> c.getCuentas().stream())
                .filter(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta))
                .findFirst();
    }

    public Optional<Cliente> buscarClientePorCuenta(String numeroCuenta){
        return getClientes().stream()
                         .filter(c -> c.getCuentas().stream()
                        .anyMatch(cta -> cta.getNumeroCuenta().equals(numeroCuenta)))
                        .findFirst();
    }

    /**
     * Cuenta el numero total de usuarios que son de tipo Cliente.
     * @return el numero de clientes registrados.
     */
    public long contarClientes() {
        return usuarios.stream()
                .filter(u -> u instanceof Cliente)
                .count();
    }
}
