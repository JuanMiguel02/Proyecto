package triplej.banco.Services;

import triplej.banco.Models.Banco;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.Transaccion;
import triplej.banco.Models.Reportes.ReporteCliente;
import triplej.banco.Models.Reportes.Reporte;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;
import triplej.banco.Repositories.TransaccionRepository;
import triplej.banco.Repositories.UsuarioRepository;
import triplej.banco.Utils.CuentaFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static triplej.banco.Utils.AlertHelper.mostrarAlerta;


/**
 * Servicio que gestiona las operaciones relacionadas con los clientes desde la vista del cajero.
 * Se encarga de registrar nuevos clientes, crear cuentas bancarias, manejar imágenes,
 * realizar depósitos, consultar saldos y generar reportes.
 */
public class CajeroService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final TransaccionRepository transaccionRepository;

    private static final String RUTA_IMAGENES =
            System.getProperty("user.home") + File.separator + "UQBank" + File.separator + "imagenes";
    private static final String IMAGEN_POR_DEFECTO = "/triplej/banco/Images/avatar.png";

    public CajeroService() {
        this.usuarioRepository = Banco.getInstancia().getUsuarioRepository();
        this.clienteRepository = Banco.getInstancia().getClienteRepository();
        this.transaccionRepository = Banco.getInstancia().getTransaccionRepository();
    }

    /**
     * Registra una persona natural como cliente con una cuenta y saldo inicial.
     */
    public void registrarPersonaNatural(
            String nombre, String apellido, String correo, String contrasenia,
            TipoDocumento tipoDocumento, String numDocumento, String telefono,
            String pais, String ciudad, String tipoCuenta, double saldo, Double sobregiro, File imagenSeleccionada
    ) {
        PersonaNatural persona = new PersonaNatural(
                nombre, apellido, correo, contrasenia, RolUsuario.CLIENTE,
                tipoDocumento, numDocumento, telefono, pais, ciudad
        );

        String rutaFoto = guardarImagenCliente(imagenSeleccionada, numDocumento);
        persona.setFoto(rutaFoto);

        registrarCliente(persona, tipoCuenta, saldo, sobregiro);
    }

    /**
     * Registra una persona jurídica como cliente con una cuenta y saldo inicial.
     */
    public void registrarPersonaJuridica(
            String razonSocial, String representante, String tipoEmpresa,
            String correo, String contrasenia, TipoDocumento tipoDocumento,
            String numDocumento, String telefono, String pais, String ciudad,
            String tipoCuenta, double saldo, Double sobregiro, File imagenSeleccionada
    ) {
        PersonaJuridica persona = new PersonaJuridica(
                razonSocial, representante, tipoEmpresa, correo, contrasenia,
                RolUsuario.CLIENTE, tipoDocumento, numDocumento, telefono, pais, ciudad
        );

        String rutaFoto = guardarImagenCliente(imagenSeleccionada, numDocumento);
        persona.setFoto(rutaFoto);

        registrarCliente(persona, tipoCuenta, saldo, sobregiro);
    }

    /**
     * Registra un nuevo cliente y crea su cuenta bancaria correspondiente.
     */
    public Cliente registrarCliente(Usuario usuario, String tipoCuenta) {
        if (usuarioRepository.buscarUsuarioPorCorreo(usuario.getCorreo()).isPresent()) {
            mostrarAlerta("El correo ya está registrado: " + usuario.getCorreo());
            throw new IllegalArgumentException(
                    "El correo ya está registrado: " + usuario.getCorreo()
            );
        }

        if (usuario.getRolUsuario() != RolUsuario.CLIENTE) {
            throw new IllegalArgumentException(
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

    public void registrarCliente(Usuario usuario, String tipoCuenta, double saldo, double sobregiro) {
        if (usuarioRepository.buscarUsuarioPorCorreo(usuario.getCorreo()).isPresent()) {
            mostrarAlerta("El correo ya está registrado: " + usuario.getCorreo());
            throw new IllegalArgumentException(
                    "El correo ya está registrado: " + usuario.getCorreo()
            );
        }

        if (usuario.getRolUsuario() != RolUsuario.CLIENTE) {
            throw new IllegalArgumentException(
                    "Las credenciales son erroneas!"
            );
        }
        usuario.setRolUsuario(RolUsuario.CLIENTE);

        Cliente cliente = new Cliente((Persona) usuario);

        CuentaBancaria cuenta = "CORRIENTE".equalsIgnoreCase(tipoCuenta)
                ? CuentaFactory.crearCuenta(tipoCuenta, cliente, sobregiro)
                : CuentaFactory.crearCuenta(tipoCuenta, cliente);

        cliente.agregarCuenta(cuenta);
        cuenta.setSaldo(saldo);
        clienteRepository.guardar(cliente);
    }

    /**
     * Guarda una imagen asociada al cliente. Si no se selecciona una imagen, se usa una por defecto.
     */
    public String guardarImagenCliente(File archivo, String numeroDocumento) {
        try {
            Path carpeta = Paths.get(RUTA_IMAGENES);
            Files.createDirectories(carpeta);

            if (archivo != null) {
                String nombreArchivo = numeroDocumento + ".jpg";
                Path destino = carpeta.resolve(nombreArchivo);
                Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
                return destino.toString();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar imagen: " + e.getMessage());
        }
        return IMAGEN_POR_DEFECTO;
    }

    public CuentaBancaria agregarCuentaACliente(Cliente cliente, String tipoCuenta) {
        if (cliente == null) {
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
        nuevaCuenta.setSaldo(saldoInicial);

        // Agregar al cliente y guardar
        cliente.agregarCuenta(nuevaCuenta);
        clienteRepository.actualizarCliente(cliente);

        System.out.println("Cuenta " + nuevaCuenta.getNumeroCuenta() + " agregada con saldo inicial: " + saldoInicial);
        return nuevaCuenta;
    }

    /**
     * Realiza un depósito en la cuenta especificada.
     */
    public void realizarDeposito(CuentaBancaria cuenta, double monto) {
        if (cuenta == null) {
            System.out.println("No se encontró");
            return;
        }
        try {
            cuenta.depositar(monto, false);
            registrarTransaccion(
                    "DÉPOSITO",
                    monto,
                    cuenta.getNumeroCuenta(),
                    cuenta.getNumeroCuenta(),
                    "Retiro realizado correctamente"
            );
            clienteRepository.actualizarCliente(cuenta.getPropietario());
            System.out.println("Deposito de " + monto + " realizado");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Realiza un retiro en la cuenta especificada.
     */
    public void realizarRetiro(CuentaBancaria cuenta, double monto) {
        if (cuenta == null) {
            System.out.println("No se encontró");
            return;
        }
        try {
            cuenta.retirar(monto, false);
            registrarTransaccion(
                    "RETIRO",
                    monto,
                    cuenta.getNumeroCuenta(),
                    cuenta.getNumeroCuenta(),
                    "Retiro realizado correctamente"
            );
            clienteRepository.actualizarCliente(cuenta.getPropietario());
            System.out.println("Retiro de " + monto + " realizado");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void realizarTransferencia(CuentaBancaria origen, CuentaBancaria destino, double monto) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Debe seleccionar ambas cuentas.");
        }

        if (origen.equals(destino)) {
            throw new IllegalArgumentException("No puede transferir a la misma cuenta.");
        }

        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo.");
        }

        try {
            origen.retirar(monto, true);
            destino.depositar(monto, true);

            registrarTransaccion(
                    "TRANSFERENCIA",
                    monto,
                    origen.getNumeroCuenta(),
                    destino.getNumeroCuenta(),
                    "Transferencia realizada correctamente"
            );

            clienteRepository.actualizarCliente(origen.getPropietario());
            clienteRepository.actualizarCliente(destino.getPropietario());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Consulta el saldo actual de una cuenta.
     */
    public double consultarSaldo(CuentaBancaria cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.getSaldo();
    }

    /**
     * Genera un reporte PDF con la información del cliente y sus transacciones.
     */
    public Reporte generarReporteCliente(CuentaBancaria cuenta) {
        ReporteCliente reporte = new ReporteCliente(cuenta);
        return reporte.generarReporte();
    }

    private void registrarTransaccion(String tipo, double monto, String origen, String destino,
                                      String descripcion) {
        Transaccion trans = new Transaccion(
                Transaccion.generarIdTransaccion(),
                tipo,
                monto,
                origen,
                destino
        );
        trans.setDescripcion(descripcion);
        trans.setExitosa(true);

        transaccionRepository.agregar(trans);

        clienteRepository.buscarClientePorCuenta(origen).ifPresent(c -> {
            CuentaBancaria cuentaOrigen = c.getCuentaPorNumero(origen);
            if (cuentaOrigen != null) {
                cuentaOrigen.getHistorial().add(trans);
            }
        });

        if (!origen.equals(destino)) {
            clienteRepository.buscarClientePorCuenta(destino).ifPresent(c -> {
                CuentaBancaria cuentaDestino = c.getCuentaPorNumero(destino);
                if (cuentaDestino != null) {
                    cuentaDestino.getHistorial().add(trans);
                }
            });
        }
    }
}
