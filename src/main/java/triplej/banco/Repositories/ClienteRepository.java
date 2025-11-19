package triplej.banco.Repositories;

import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.CuentaCorriente;
import triplej.banco.Models.Cuentas.CuentaEmpresarial;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Utils.CuentaFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de gestionar los datos de los clientes del banco.
 * <p>
 * Administra tanto la carga inicial desde archivos como la creación, actualización
 * y persistencia de los datos relacionados con los clientes y sus cuentas bancarias.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Cargar clientes y cuentas desde archivos persistentes.</li>
 *   <li>Guardar nuevos clientes y cuentas en el sistema.</li>
 *   <li>Actualizar saldos o reescribir información cuando cambian los datos.</li>
 *   <li>Mantener la coherencia con los repositorios de usuarios y transacciones.</li>
 * </ul>
 *
 * <h3>Patrón aplicado:</h3>
 * Implementa el patrón <b>Singleton</b> para garantizar una única instancia
 * compartida en toda la aplicación.
 */
public class ClienteRepository {

    /**
     * Instancia única del repositorio (Singleton).
     */
    private static ClienteRepository instancia;

    /**
     * Lista de clientes en memoria.
     */
    private final ArrayList<Cliente> clientes;

    /**
     * Repositorio de usuarios, utilizado para asociar y persistir los datos personales.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Repositorio de transacciones, necesario para cargar movimientos previos.
     */
    private final TransaccionRepository transaccionRepository;

    /**
     * Constructor privado del repositorio.
     * <p>
     * Durante la inicialización:
     * <ol>
     *   <li>Verifica si existen los archivos <code>Usuarios.txt</code> y <code>Cuentas.txt</code>.</li>
     *   <li>Si existen, carga los datos desde archivo.</li>
     *   <li>Si no existen, genera datos de ejemplo por primera vez.</li>
     * </ol>
     * </p>
     */
    private ClienteRepository() {
        this.clientes = new ArrayList<>();
        this.usuarioRepository = UsuarioRepository.getInstancia();
        this.transaccionRepository = TransaccionRepository.getInstancia();

        Path rutaUsuarios = Paths.get("Banco", "Datos", "Usuarios.txt");
        Path rutaCuentas = Paths.get("Banco", "Datos", "Cuentas.txt");

        if (Files.exists(rutaUsuarios) && Files.exists(rutaCuentas)) {
            System.out.println("Cargando clientes y cuentas desde archivos existentes...");
            transaccionRepository.cargarDatos();
            cargarDesdeArchivo();
        } else {
            System.out.println(" Primera ejecución: creando datos de ejemplo de clientes...");
            cargarDatosEjemplo();
            transaccionRepository.cargarDatos();
        }
    }

    /**
     * Retorna la instancia única del repositorio (Singleton).
     */
    public static ClienteRepository getInstancia() {
        if (instancia == null) {
            instancia = new ClienteRepository();
        }
        return instancia;
    }

    /**
     * Guarda la información de un cliente en memoria y en archivo.
     * <p>
     * Este método realiza tres pasos principales:
     * <ol>
     *   <li>Guarda el usuario asociado en el archivo <code>Usuarios.txt</code>.</li>
     *   <li>Agrega el cliente a la lista en memoria si aún no está registrado.</li>
     *   <li>Guarda o actualiza cada cuenta del cliente en <code>Cuentas.txt</code>.</li>
     * </ol>
     * </p>
     *
     * @param cliente Cliente a guardar.
     */
    public void guardar(Cliente cliente) {
        usuarioRepository.guardarUsuario(cliente.getPersonaAsociada());

        if (!clientes.contains(cliente) && cliente.getPersonaAsociada().getRolUsuario() == RolUsuario.CLIENTE) {
            clientes.add(cliente);
        }

        for (CuentaBancaria cuenta : cliente.getCuentas()) {
            // Verificar si la cuenta ya existe en archivo
            if (cuentaExisteEnArchivo(cuenta.getNumeroCuenta())) {
                actualizarSaldoEnArchivo(cuenta); // Actualizar saldo
            } else {
                guardarCuentaEnArchivo(cuenta); // Guardar nueva cuenta
            }
        }
    }

    /**
     * Actualiza un cliente y sus cuentas en el archivo.
     * <p>
     * Si una cuenta ya existe, se actualiza su saldo;
     * si no, se agrega una nueva línea al archivo.
     * Además, se reescribe el archivo de usuarios para mantener la consistencia.
     * </p>
     */
    public void actualizarCliente(Cliente clienteActualizado) {
        for (Cliente clienteActual : clientes) {
            //  Comparar por documento, accediendo desde PersonaNatural
            if (clienteActual.getPersonaAsociada().getId().equals(clienteActualizado.getPersonaAsociada().getId())) {
                for (CuentaBancaria cuenta : clienteActualizado.getCuentas()) {
                    if (cuentaExisteEnArchivo(cuenta.getNumeroCuenta())) {
                        actualizarSaldoEnArchivo(cuenta);
                    } else {
                        guardarCuentaEnArchivo(cuenta);
                    }
                }

                // Reescribir los usuarios para mantener consistencia
                usuarioRepository.reescribirArchivo();
                System.out.println("Cliente y cuentas actualizados correctamente en archivo.");
                break;
            }
        }
    }

    public Optional<Cliente> buscarPorDocumento(String documento) {
        return clientes.stream()
                .filter(c -> c.getDocumento().equalsIgnoreCase(documento))
                .findFirst();
    }

    public ArrayList<Cliente> getClientes() {
        return this.clientes;
    }

    /**
     * Métodos de búsqueda
     */
    private boolean cuentaExisteEnArchivo(String numeroCuenta) {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Cuentas.txt");
            if (!Files.exists(ruta)) return false;

            return Files.lines(ruta)
                    .anyMatch(linea -> linea.startsWith(numeroCuenta + "\t"));
        } catch (IOException e) {
            return false;
        }
    }

    public Optional<Cliente> buscarPorCorreo(String email) {
        return clientes.stream()
                .filter(c -> c.getPersonaAsociada().getCorreo().equals(email))
                .findFirst();
    }

    public List<CuentaBancaria> buscarCuentasDeCliente(Cliente cliente) {
        if (cliente == null) return List.of(); // Evita null pointer

        return cliente.getCuentas(); // devuelve la lista directamente
    }

    public Optional<Cliente> buscarClientePorCuenta(String numeroCuenta) {
        return clientes.stream()
                .filter(c -> c.getCuentas().stream()
                        .anyMatch(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta)))
                .findFirst();
    }

    public Optional<CuentaBancaria> buscarCuentaPorNumero(String numeroCuenta) {
        return clientes.stream()
                .flatMap(c -> c.getCuentas().stream())
                .filter(cuenta -> cuenta.getNumeroCuenta().equalsIgnoreCase(numeroCuenta))
                .findFirst();
    }

    /**
     * Carga datos de ejemplo para la primera ejecución.
     * Crea dos clientes (uno natural y uno jurídico) con una cuenta de ahorro cada uno.
     */
    private void cargarDatosEjemplo() {
        PersonaNatural juan = new PersonaNatural(
                "Kepo", "John", "kepo@gmail", "12345", RolUsuario.CLIENTE,
                TipoDocumento.CEDULACIUDADANIA, "1232190", "2132141", "Colombia", "Bogotá");

        PersonaJuridica paco = new PersonaJuridica(
                "Empresa X", "Nory Navas", "Privada", "empresax@gmail", "123456", RolUsuario.CLIENTE,
                TipoDocumento.NIT, "123345", "21341", "Colombia", "Armenia");

        Cliente cliente1 = new Cliente(juan);
        Cliente cliente2 = new Cliente(paco);

        CuentaBancaria cuenta1 = new CuentaAhorro(cliente1);
        CuentaBancaria cuenta2 = new CuentaEmpresarial(cliente2);

        cliente1.agregarCuenta(cuenta1);
        cliente2.agregarCuenta(cuenta2);

        clientes.add(cliente1);
        clientes.add(cliente2);

        guardar(cliente1);
        guardar(cliente2);
    }

    //Métodos de persistencia de datos

    /**
     * Carga los clientes y sus cuentas desde el archivo “Cuentas.txt”.
     * <p>
     * Si el cliente no existía en memoria, lo reconstruye usando la información guardada.
     * Cada línea del archivo representa una cuenta bancaria con sus datos.
     */
    public void cargarDesdeArchivo() {
        // Se define la ruta del archivo de cuentas
        Path ruta = Paths.get("Banco", "Datos", "Cuentas.txt");

        // Si el archivo no existe, no hay nada que cargar y el método termina
        if (!Files.exists(ruta)) return;

        // Se abre el archivo con un lector de texto que se cerrará automáticamente al final (try-with-resources)
        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            // Se lee la primera línea del archivo (encabezado) y se ignora
            lector.readLine();
            String linea;
            // Se recorre cada línea del archivo mientras no sea nula
            while ((linea = lector.readLine()) != null) {
                // Se separan los datos usando tabulaciones como delimitadores
                String[] datos = linea.split("\t");
                // Si hay menos de 4 datos, la línea está incompleta y se salta
                if (datos.length < 4) continue;

                // Se extraen los valores de cada columna
                String numeroCuenta = datos[0];
                double saldo = Double.parseDouble(datos[1].replace(",", "."));
                String tipo = datos[2];
                String correo = datos[3];
                // Si existe, se lee el sobregiro; de lo contrario, se usa un valor por defecto
                double sobregiro = (datos.length >= 5) ? Double.parseDouble(datos[4].replace(",", ".")) : 500000.0;

                // Se busca si el cliente ya existe en memoria usando su correo
                Cliente cliente = buscarPorCorreo(correo).orElse(null);
                // Si no está en memoria, se intenta reconstruir desde el repositorio de usuarios
                if (cliente == null) {
                    Usuario usuario = usuarioRepository.buscarUsuarioPorCorreo(correo).orElse(null);
                    if (usuario == null) continue;

                    // Se crea un nuevo cliente a partir del usuario encontrado
                    cliente = new Cliente((Persona) usuario);
                    clientes.add(cliente);
                }

                // Se verifica que el cliente no tenga ya registrada esta cuenta
                boolean yaExiste = cliente.getCuentas().stream()
                        .anyMatch(c -> c.getNumeroCuenta().equals(numeroCuenta));
                if (yaExiste) continue;

                // Se crea el objeto CuentaBancaria correspondiente
                CuentaBancaria cuenta;

                //  se usa la fábrica de cuentas (CuentaFactory). Si es cuenta corriente, usa el sobregiro leído
                if ("2".equals(tipo)) {
                    cuenta = CuentaFactory.crearCuentaConDatos("2", cliente, numeroCuenta, saldo, sobregiro);
                } else {
                    // Para cualquier otro tipo
                    cuenta = CuentaFactory.crearCuentaConDatos(tipo, cliente, numeroCuenta, saldo, sobregiro);
                }
                // Se agrega la cuenta al cliente
                cliente.agregarCuenta(cuenta);
                // Si el cliente no tiene una cuenta activa aún, se establece esta como activa
                if (cliente.getCuentaPorNumero() == null) {
                    cliente.setCuentaActiva(cuenta);
                }
            }
        } catch (IOException e) {
            // Si ocurre un error de lectura, se lanza una excepción en tiempo de ejecución
            throw new RuntimeException(e);
        }
    }

    /**
     * Guarda una cuenta nueva en el archivo “Cuentas.txt”.
     * <p>
     * Este método escribe los datos de una cuenta bancaria en el archivo de texto,
     * asegurándose de que el archivo y las carpetas existan, de que no se repita el número
     * de cuenta, y de que el formato sea correcto.
     */
    private void guardarCuentaEnArchivo(CuentaBancaria cuenta) {
        try {
            // Aquí se define la ruta del archivo donde se almacenarán las cuentas.
            Path ruta = Paths.get("Banco", "Datos", "Cuentas.txt");
            // Verifica si la carpeta “Banco/Datos” existe; si no, la crea.
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }
            // Si el archivo no existe todavía, lo crea y le agrega una línea de encabezado.
            if (!Files.exists(ruta)) {
                Files.writeString(ruta, "NumeroCuenta\tSaldo\tTipoCuenta\tCorreoCliente\tSobregiro\n");
            }
            // Si el archivo no existe todavía, lo crea y le agrega una línea de encabezado.
            String contenido = Files.readString(ruta);
            // Si el número de cuenta ya está en el archivo, se detiene para evitar duplicados.
            if (contenido.contains(cuenta.getNumeroCuenta())) {
                return;
            }
            // Variable para almacenar la línea que se escribirá en el archivo.
            String linea;

            // Si la cuenta es de tipo corriente, también guarda el sobregiro permitido.
            if (cuenta instanceof CuentaCorriente corriente) {
                linea = String.format(
                        "%s\t%.2f\t%s\t%s\t%.2f%n",
                        cuenta.getNumeroCuenta(),                // Número de la cuenta
                        cuenta.getSaldo(),                       // Saldo disponible
                        cuenta.getCodigoTipoCuenta(),            // Código del tipo de cuenta (por ejemplo 1 o 2)
                        cuenta.getPropietario().getCorreo(),     // Correo del propietario
                        corriente.getLimiteSobregiro()          // Monto del sobregiro (solo para cuenta corriente)
                );
            } else {
                // Si no es corriente (por ejemplo, ahorro), no se incluye el sobregiro.
                linea = String.format(
                        "%s\t%.2f\t%s\t%s%n",
                        cuenta.getNumeroCuenta(),
                        cuenta.getSaldo(),
                        cuenta.getCodigoTipoCuenta(),
                        cuenta.getPropietario().getCorreo()
                );
            }

            // Finalmente, escribe la línea en el archivo. Si no existe, lo crea;
            // si existe, la agrega al final (APPEND).
            Files.writeString(ruta, linea, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            // Si ocurre un error (por ejemplo, el archivo no puede abrirse o escribirse),
            // se lanza una excepción en tiempo de ejecución con un mensaje descriptivo.
            throw new RuntimeException("Error al guardar en archivo" + e);
        }
    }

    /**
     * Actualiza el saldo de una cuenta bancaria en el archivo “Cuentas.txt”.
     * <p>
     * Relee todas las líneas del archivo, reemplaza la línea correspondiente
     * a la cuenta modificada, y luego sobrescribe el archivo con los nuevos datos.
     */
    public void actualizarSaldoEnArchivo(CuentaBancaria cuentaActualizada) {
        try {
            // Se define la ruta del archivo de cuentas
            Path ruta = Paths.get("Banco", "Datos", "Cuentas.txt");
            // Si el archivo no existe, no hay nada que actualizar
            if (!Files.exists(ruta)) return;
            // Se leen todas las líneas del archivo en una lista
            List<String> lineas = Files.readAllLines(ruta);
            // Se crea una nueva lista para almacenar las líneas actualizadas
            List<String> nuevasLineas = new ArrayList<>();

            // Si el archivo no está vacío, se conserva la primera línea (el encabezado)
            if (!lineas.isEmpty()) {
                nuevasLineas.add(lineas.getFirst());
            }
            // Se recorren todas las líneas, comenzando desde la segunda (índice 1)
            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i);
                String[] datos = linea.split("\t");

                // Si la línea pertenece a la cuenta que queremos actualizar
                if (datos.length >= 4 && datos[0].equals(cuentaActualizada.getNumeroCuenta())) {
                    // Se crea una nueva línea con el saldo actualizado
                    String nuevaLinea;
                    // Si es una cuenta corriente, también se guarda el sobregiro
                    if (cuentaActualizada instanceof CuentaCorriente corriente) {
                        nuevaLinea = String.format(
                                "%s\t%.2f\t%s\t%s\t%.2f",
                                cuentaActualizada.getNumeroCuenta(),
                                cuentaActualizada.getSaldo(),
                                cuentaActualizada.getCodigoTipoCuenta(),
                                cuentaActualizada.getPropietario().getCorreo(),
                                corriente.getLimiteSobregiro()
                        );
                    } else {
                        // Si no es corriente, no se incluye el sobregiro
                        nuevaLinea = String.format(
                                "%s\t%.2f\t%s\t%s",
                                cuentaActualizada.getNumeroCuenta(),
                                cuentaActualizada.getSaldo(),
                                cuentaActualizada.getCodigoTipoCuenta(),
                                cuentaActualizada.getPropietario().getCorreo()
                        );
                    }
                    // Se agrega la línea actualizada a la lista nueva
                    nuevasLineas.add(nuevaLinea);
                } else {
                    // Si no corresponde a la cuenta modificada, se conserva tal cual
                    nuevasLineas.add(linea);
                }
            }
            // Finalmente, se sobrescribe el archivo con las líneas actualizadas.
            Files.write(ruta, nuevasLineas, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

        } catch (IOException e) {
            // Si ocurre un error durante la lectura o escritura del archivo,
            // se lanza una excepción detallando el problema.
            throw new RuntimeException("Error al actualizar el archivo de cuentas: " + e.getMessage(), e);
        }
    }
}
