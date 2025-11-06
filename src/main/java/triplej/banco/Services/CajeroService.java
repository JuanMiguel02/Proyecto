package triplej.banco.Services;

import triplej.banco.Models.Banco;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.ReporteCliente;
import triplej.banco.Models.Reportes.ReporteGenerado;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.Persona;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.CuentaFactory;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;


public class CajeroService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public CajeroService() {
        this.usuarioRepository = Banco.getInstancia().getUsuarioRepository();
        this.clienteRepository = Banco.getInstancia().getClienteRepository();
    }

    public Cliente registrarCliente(Usuario usuario, String tipoCuenta) {
        if(usuarioRepository.buscarUsuarioPorCorreo(usuario.getCorreo()).isPresent()) {
            mostrarAlerta("El correo ya está registrado: " + usuario.getCorreo());
            throw  new IllegalArgumentException(
                    "El correo ya está registrado: " + usuario.getCorreo()
            );
        }

        if(usuario.getRolUsuario() != RolUsuario.CLIENTE) {
            throw  new IllegalArgumentException(
                    "Las credenciales son erroneas!"
            );
        }
        usuario.setRolUsuario(RolUsuario.CLIENTE);

        Cliente cliente = new Cliente((Persona) usuario);
        CuentaBancaria cuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);
        cliente.agregarCuenta(cuenta);
        clienteRepository.guardar(cliente);
        return cliente;
    }
    public Cliente registrarCliente(Usuario usuario, String tipoCuenta, double saldo) {
        if(usuarioRepository.buscarUsuarioPorCorreo(usuario.getCorreo()).isPresent()) {
            mostrarAlerta("El correo ya está registrado: " + usuario.getCorreo());
            throw  new IllegalArgumentException(
                    "El correo ya está registrado: " + usuario.getCorreo()
            );
        }

        if(usuario.getRolUsuario() != RolUsuario.CLIENTE) {
            throw  new IllegalArgumentException(
                    "Las credenciales son erroneas!"
            );
        }
        usuario.setRolUsuario(RolUsuario.CLIENTE);

        Cliente cliente = new Cliente((Persona) usuario);
        CuentaBancaria cuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);
        cliente.agregarCuenta(cuenta);
        cuenta.depositar(saldo);
        clienteRepository.guardar(cliente);
        return cliente;
    }

    public CuentaBancaria agregarCuentaACliente(Cliente cliente, String tipoCuenta){
        if(cliente == null){
            throw new IllegalArgumentException("El cliente no puede estar nulo");
        }
        CuentaBancaria nuevaCuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);
        cliente.agregarCuenta(nuevaCuenta);
        clienteRepository.actualizarCliente(cliente);
        System.out.println("Cuenta " + nuevaCuenta.getNumeroCuenta() + " agregada ");

        return nuevaCuenta;
    }

    public CuentaBancaria agregarCuentaACliente(Cliente cliente, String tipoCuenta, double saldoInicial) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede estar nulo");
        }

        // Crear cuenta con datos iniciales
        CuentaBancaria nuevaCuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);

        // Asignar saldo inicial
        nuevaCuenta.depositar(saldoInicial);

        // Agregar al cliente y guardar
        cliente.agregarCuenta(nuevaCuenta);
        clienteRepository.actualizarCliente(cliente);

        System.out.println("Cuenta " + nuevaCuenta.getNumeroCuenta() + " agregada con saldo inicial: " + saldoInicial);
        return nuevaCuenta;
    }

    public void realizarDeposito(CuentaBancaria cuenta, double monto, String descripcion) {
        if( cuenta == null){
            System.out.println("No se encontró");
            return;
        }
        if(descripcion == null || descripcion.isBlank()){
            descripcion = "Deposito realizado";
        }
        try{
            cuenta.depositar(monto);
            System.out.println("Deposito de " + monto + " realizado");
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public double consultarSaldo(CuentaBancaria cuenta){
        if(cuenta == null){
            throw  new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.getSaldo();
    }

    public ReporteGenerado generarReporteCliente(CuentaBancaria cuenta){
        ReporteCliente reporte = new ReporteCliente(cuenta);
        return reporte.generarReporte();
    }
}
