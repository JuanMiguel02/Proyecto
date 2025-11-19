package triplej.banco.Services;

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
    // Repositorios usados para manejar los datos de usuarios, clientes y transacciones.
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final TransaccionRepository transaccionRepository;

    // Ruta base donde se guardan las imágenes de los clientes
    private static final String RUTA_IMAGENES =
            System.getProperty("user.home") + File.separator + "UQBank" + File.separator + "imagenes";
    // Imagen por defecto en caso de que no se seleccione una personalizada.
    private static final String IMAGEN_POR_DEFECTO = "/triplej/banco/Images/avatar.png";

    /**
     * Constructor del servicio del cajero.
     * Inicializa las referencias a los repositorios necesarios.
     */
    public CajeroService() {
        this.usuarioRepository = UsuarioRepository.getInstancia();
        this.clienteRepository = ClienteRepository.getInstancia();
        this.transaccionRepository = TransaccionRepository.getInstancia();
    }

    /**
     * Registra una persona natural como cliente y crea su cuenta bancaria correspondiente.
     *
     * <p>El método crea un objeto {@link PersonaNatural}, guarda su imagen de perfil
     * (si se selecciona una) y luego llama internamente a {@link #registrarCliente(Usuario, String, double, Double)}
     * para asociar una cuenta bancaria con el cliente.</p>
     *
     * @param nombre nombre del cliente.
     * @param apellido apellido del cliente.
     * @param correo correo electrónico único.
     * @param contrasenia contraseña para el acceso al sistema.
     * @param tipoDocumento tipo de documento (CC, TI, NIT, etc.).
     * @param numDocumento número del documento de identidad.
     * @param telefono número de contacto.
     * @param pais país de residencia.
     * @param ciudad ciudad de residencia.
     * @param tipoCuenta tipo de cuenta (ahorro o corriente).
     * @param saldo saldo inicial.
     * @param sobregiro límite de sobregiro (solo aplica a cuentas corrientes).
     * @param imagenSeleccionada imagen de perfil del cliente (puede ser {@code null}).
     * @return la {@link CuentaBancaria} creada y asociada al cliente.
     */
    public CuentaBancaria registrarPersonaNatural(
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

        return registrarCliente(persona, tipoCuenta, saldo, sobregiro);

    }

    /**
     * Registra una persona jurídica como cliente y crea su cuenta bancaria correspondiente.
     *
     * @param razonSocial nombre de la empresa.
     * @param representante representante legal de la empresa.
     * @param tipoEmpresa tipo de empresa (S.A., S.A.S., etc.).
     * @param correo correo electrónico de contacto.
     * @param contrasenia contraseña de acceso.
     * @param tipoDocumento tipo de documento (NIT, RUT, etc.).
     * @param numDocumento número del documento de identificación empresarial.
     * @param telefono teléfono de contacto.
     * @param pais país donde opera.
     * @param ciudad ciudad donde se encuentra la sede principal.
     * @param tipoCuenta tipo de cuenta (ahorro o corriente).
     * @param saldo saldo inicial.
     * @param sobregiro límite de sobregiro (solo aplica a cuentas corrientes).
     * @param imagenSeleccionada imagen asociada a la empresa (puede ser {@code null}).
     * @return la {@link CuentaBancaria} creada y asociada al cliente jurídico.
     */
    public CuentaBancaria registrarPersonaJuridica(
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

        return registrarCliente(persona, tipoCuenta, saldo, sobregiro);
    }

    /**
     * Registra un nuevo cliente (ya sea persona natural o jurídica) con una cuenta bancaria.
     * <p>Válida que el correo no esté registrado previamente y asigna el rol de cliente.</p>
     *
     * @param usuario usuario a registrar.
     * @param tipoCuenta tipo de cuenta bancaria a crear.
     * @return el objeto {@link Cliente} registrado con su cuenta asociada.
     * @throws IllegalArgumentException si el correo ya existe o el rol no es válido.
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

    /**
     * Variante sobrecargada que también permite definir el saldo inicial y el sobregiro.
     */
    private CuentaBancaria registrarCliente(Usuario usuario, String tipoCuenta, double saldo, Double sobregiro) {
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
        return cuenta;
   }

    /**
     * Guarda la imagen de un cliente en la carpeta local del sistema.
     * <p>Si no se selecciona una imagen, se asigna la predeterminada.</p>
     *
     * @param archivo archivo de imagen (puede ser {@code null}).
     * @param numeroDocumento número del documento del cliente (usado como nombre del archivo).
     * @return ruta donde se almacenó la imagen.
     */
    private String guardarImagenCliente(File archivo, String numeroDocumento) {
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
            System.err.println("Error al guardarUsuario imagen: " + e.getMessage());
        }
        return IMAGEN_POR_DEFECTO;
    }

    /**
     * Agrega una nueva cuenta a un cliente existente.
     *
     * @param cliente cliente al que se le agregará la cuenta.
     * @param tipoCuenta tipo de cuenta (ahorro o corriente).
     * @param saldoInicial saldo inicial.
     * @return la nueva {@link CuentaBancaria} creada.
     */
    public CuentaBancaria agregarCuentaACliente(Cliente cliente, String tipoCuenta, double saldoInicial) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede estar nulo");
        }

        // Crear cuenta con datos iniciales
        CuentaBancaria nuevaCuenta = CuentaFactory.crearCuenta(tipoCuenta.toUpperCase(), cliente);

        // Asignar saldo inicial
        nuevaCuenta.setSaldo(saldoInicial);

        // Agregar al cliente y guardarUsuario
        cliente.agregarCuenta(nuevaCuenta);
        clienteRepository.actualizarCliente(cliente);

        System.out.println("Cuenta " + nuevaCuenta.getNumeroCuenta() + " agregada con saldo inicial: " + saldoInicial);
        return nuevaCuenta;
    }

    /**
     * Realiza un depósito en una cuenta.
     *
     * @param cuenta cuenta bancaria destino.
     * @param monto monto a depositar.
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
     * Realiza un retiro en una cuenta.
     *
     * @param cuenta cuenta bancaria origen.
     * @param monto monto a retirar.
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

    /**
     * Realiza una transferencia entre dos cuentas bancarias.
     *
     * @param origen cuenta de origen.
     * @param destino cuenta destino.
     * @param monto monto a transferir.
     * @throws IllegalArgumentException si las cuentas son iguales o el monto no es válido.
     */
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
     *
     * @param cuenta cuenta bancaria.
     * @return saldo actual.
     */
    public double consultarSaldo(CuentaBancaria cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.getSaldo();
    }

    /**
     * Genera un reporte con la información del cliente y su historial de transacciones.
     *
     * @param cuenta cuenta bancaria del cliente.
     * @return un objeto {@link Reporte} con la información detallada.
     */
    public Reporte generarReporteCliente(CuentaBancaria cuenta) {
        ReporteCliente reporte = new ReporteCliente(cuenta);
        return reporte.generarReporte();
    }

    /**
     * Registra una transacción bancaria (depósito, retiro o transferencia) en los repositorios correspondientes.
     *
     * @param tipo tipo de transacción (ej. “DEPÓSITO”, “RETIRO”, “TRANSFERENCIA”).
     * @param monto monto involucrado.
     * @param origen número de cuenta de origen.
     * @param destino número de cuenta destino.
     * @param descripcion descripción adicional de la transacción.
     */
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

        // Asociar la transacción al historial del cliente origen
        clienteRepository.buscarClientePorCuenta(origen).ifPresent(c -> {
            CuentaBancaria cuentaOrigen = c.getCuentaPorNumero(origen);
            if (cuentaOrigen != null) {
                cuentaOrigen.getHistorialTransacciones().add(trans);
            }
        });

        // Asociar al cliente destino si es distinto
        if (!origen.equals(destino)) {
            clienteRepository.buscarClientePorCuenta(destino).ifPresent(c -> {
                CuentaBancaria cuentaDestino = c.getCuentaPorNumero(destino);
                if (cuentaDestino != null) {
                    cuentaDestino.getHistorialTransacciones().add(trans);
                }
            });
        }
    }
}
