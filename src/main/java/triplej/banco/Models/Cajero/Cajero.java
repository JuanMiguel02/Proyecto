package triplej.banco.Models.Cajero;

import triplej.banco.Models.Banco;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.CuentaFactory;

import java.util.Optional;

public class Cajero {
    private final UsuarioRepository usuarioRepository;

    public Cajero() {
        this.usuarioRepository = Banco.getInstancia().getUsuarioRepository();

    }

    public void registrarCliente(Cliente cliente, String tipoCuenta) {
        if (usuarioRepository.buscarPorEmail(cliente.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("El correo ya existe");
        }
        CuentaBancaria cuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);
        cliente.agregarCuenta(cuenta);

        usuarioRepository.guardar(cliente);
    }

    public void agregarCuentaACliente(Cliente cliente, String tipoCuenta){
        try{
            CuentaBancaria nuevaCuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);
            cliente.agregarCuenta(nuevaCuenta);
        } catch (IllegalArgumentException ignored){
        }
    }

    public void realizarDeposito(String numeroCuentaOrigen, double monto, String descripcion) {
        try {

            Optional<CuentaBancaria> cuentaOpt = usuarioRepository.buscarCuentaPorNumero(numeroCuentaOrigen);
            if (cuentaOpt.isPresent()) {
                cuentaOpt.get().depositar(monto, descripcion);
                return;
            }
            System.out.println("Cuenta no encontrada");

        } catch (IllegalArgumentException e) {
            System.out.println("Error al realizar deposito");
        }
    }

    public double consultarSaldo(CuentaBancaria cuenta) {
      return cuenta.getSaldo();
    }

}






