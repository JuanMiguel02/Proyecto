package triplej.banco.Repositories;

import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

public class ClienteRepository {
    private static ClienteRepository instancia;
    private final ArrayList<Cliente> clientes;
    private final UsuarioRepository usuarioRepository;

    private ClienteRepository(){
        this.clientes = new ArrayList<>();
        this.usuarioRepository = UsuarioRepository.getInstancia();

        Path ruta = Paths.get("Banco", "Datos", "Usuario");

        if (Files.exists(ruta)) {
            System.out.println("✅ Cargando clientes desde archivo existente...");
            cargarDesdeUsuarios();
        } else {
            System.out.println("⚙️ Primera ejecución: creando datos de ejemplo de clientes...");
            cargarDatosEjemplo();
        }

    }

    private void cargarDesdeUsuarios() {
        usuarioRepository.obtenerPorRol(RolUsuario.CLIENTE)
                .forEach(usuario -> clientes.add(new Cliente((PersonaNatural) usuario)));
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

    private void cargarDatosEjemplo() {

        PersonaNatural juan = new PersonaNatural(
                "Juan", "Henao", "juancho@gmail", "12345", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA,
                "123213", "2132141", "Colombia", "Bogotá");
        guardar(new Cliente(juan));

        PersonaNatural paco = new PersonaNatural(
                "Paco", "Jones", "pacojones@gmail", "123456", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA,
                "1233", "21341", "Colombia", "Bogotá");
        guardar(new Cliente(paco));


    }

    public int contarClientes(){
        return clientes.size();
    }
}
