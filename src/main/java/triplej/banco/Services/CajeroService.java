package triplej.banco.Services;

import triplej.banco.Models.Banco;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Reportes.ReporteCliente;
import triplej.banco.Models.Reportes.ReporteGenerado;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.ClienteRepository;
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
    private static final String RUTA_IMAGENES =
            System.getProperty("user.home") + File.separator + "UQBank" + File.separator + "imagenes";
    private static final String IMAGEN_POR_DEFECTO = "/triplej/banco/Images/avatar.png";

    public CajeroService() {
        this.usuarioRepository = Banco.getInstancia().getUsuarioRepository();
        this.clienteRepository = Banco.getInstancia().getClienteRepository();
    }

    /**
     * Registra una persona natural como cliente con una cuenta y saldo inicial.
     */
    public void registrarPersonaNatural(
            String nombre, String apellido, String correo, String contrasenia,
            TipoDocumento tipoDocumento, String numDocumento, String telefono,
            String pais, String ciudad, String tipoCuenta, double saldo, File imagenSeleccionada
    ) {
        PersonaNatural persona = new PersonaNatural(
                nombre, apellido, correo, contrasenia, RolUsuario.CLIENTE,
                tipoDocumento, numDocumento, telefono, pais, ciudad
        );

        String rutaFoto = guardarImagenCliente(imagenSeleccionada, numDocumento);
        persona.setFoto(rutaFoto);

        registrarCliente(persona, tipoCuenta, saldo);
    }

    /**
     * Registra una persona jurídica como cliente con una cuenta y saldo inicial.
     */
    public void registrarPersonaJuridica(
            String razonSocial, String representante, String tipoEmpresa,
            String correo, String contrasenia, TipoDocumento tipoDocumento,
            String numDocumento, String telefono, String pais, String ciudad,
            String tipoCuenta, double saldo, File imagenSeleccionada
    ) {
        PersonaJuridica persona = new PersonaJuridica(
                razonSocial, representante, tipoEmpresa, correo, contrasenia,
                RolUsuario.CLIENTE, tipoDocumento, numDocumento, telefono, pais, ciudad
        );

        String rutaFoto = guardarImagenCliente(imagenSeleccionada, numDocumento);
        persona.setFoto(rutaFoto);

        registrarCliente(persona, tipoCuenta, saldo);
    }

    /**
     * Registra un nuevo cliente y crea su cuenta bancaria correspondiente.
     */
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

    public void registrarCliente(Usuario usuario, String tipoCuenta, double saldo) {
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

    /**
     * Realiza un depósito en la cuenta especificada.
     */
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

    /**
     * Consulta el saldo actual de una cuenta.
     */
    public double consultarSaldo(CuentaBancaria cuenta){
        if(cuenta == null){
            throw  new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.getSaldo();
    }

    /**
     * Genera un reporte PDF con la información del cliente y sus transacciones.
     */
    public ReporteGenerado generarReporteCliente(CuentaBancaria cuenta){
        ReporteCliente reporte = new ReporteCliente(cuenta);
        return reporte.generarReporte();
    }
}
