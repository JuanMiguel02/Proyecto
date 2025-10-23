package triplej.banco.Models.Cajero;

import triplej.banco.Models.Banco;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.ReporteCliente;
import triplej.banco.Models.Reportes.ReporteGenerado;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.CuentaFactory;

import java.util.Optional;

public class Cajero {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public Cajero() {
        this.usuarioRepository = Banco.getInstancia().getUsuarioRepository();
        this.clienteRepository = Banco.getInstancia().getClienteRepository();
    }

    public Cliente registrarCliente(Usuario usuario, String tipoCuenta) {
        if(usuarioRepository.buscarUsuarioPorEmail(usuario.getCorreo()).isPresent()) {
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

        Cliente cliente = new Cliente(usuario);
        CuentaBancaria cuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);
        cliente.agregarCuenta(cuenta);
        clienteRepository.guardar(cliente);
        return cliente;
    }

    public void agregarCuentaACliente(Cliente cliente, String tipoCuenta){
        if(cliente == null){
            throw new IllegalArgumentException("El cliente no puede estar nulo");
        }
        CuentaBancaria nuevaCuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);
        cliente.agregarCuenta(nuevaCuenta);
        System.out.println("Cuenta " + nuevaCuenta.getNumeroCuenta() + " agregada ");
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
            System.out.println("Error al realizar deposito");
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






