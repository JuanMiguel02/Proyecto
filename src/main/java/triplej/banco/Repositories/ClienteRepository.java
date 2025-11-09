package triplej.banco.Repositories;

import triplej.banco.Models.Cuentas.CuentaBancaria;
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

public class ClienteRepository {
    private static ClienteRepository instancia;
    private final ArrayList<Cliente> clientes;
    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;

    private ClienteRepository() {
        this.clientes = new ArrayList<>();
        this.usuarioRepository = UsuarioRepository.getInstancia();
        this.transaccionRepository = TransaccionRepository.getInstance();

        Path rutaUsuarios = Paths.get("Banco", "Datos", "Usuarios.txt");
        Path rutaCuentas = Paths.get("Banco", "Datos", "Cuentas.txt");

        if (Files.exists(rutaUsuarios) && Files.exists(rutaCuentas)) {
            System.out.println("Cargando clientes y cuentas desde archivos existentes...");
            transaccionRepository.cargarDatos();
            cargarDesdeArchivo();
        } else {
            System.out.println(" Primera ejecución: creando datos de ejemplo de clientes...");
            cargarDatosEjemplo();
        }
    }


    public static ClienteRepository getInstancia() {
        if (instancia == null) {
            instancia = new ClienteRepository();
        }
        return instancia;
    }

    public void guardar(Cliente cliente) {
        usuarioRepository.guardar(cliente.getUsuarioAsociado());

        if (!clientes.contains(cliente) && cliente.getUsuarioAsociado().getRolUsuario() == RolUsuario.CLIENTE) {
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

    public void actualizarCliente(Cliente clienteActualizado) {
        for (Cliente clienteActual : clientes) {
            //  Comparar por documento, accediendo desde PersonaNatural
            if (clienteActual.getUsuarioAsociado().getId().equals(clienteActualizado.getUsuarioAsociado().getId())) {
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
     * Verifica si una cuenta ya existe en el archivo
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
                .filter(c -> c.getUsuarioAsociado().getCorreo().equals(email))
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

    private void cargarDatosEjemplo() {
        PersonaNatural juan = new PersonaNatural(
                "Juan", "Henao", "juancho@gmail", "12345", RolUsuario.CLIENTE,
                TipoDocumento.CEDULACIUDADANIA, "1232190", "2132141", "Colombia", "Bogotá");

        PersonaNatural paco = new PersonaNatural(
                "Paco", "Jones", "pacojones@gmail", "123456", RolUsuario.CLIENTE,
                TipoDocumento.CEDULACIUDADANIA, "123345", "21341", "Colombia", "Bogotá");

        Cliente cliente1 = new Cliente(juan);
        Cliente cliente2 = new Cliente(paco);

        clientes.add(cliente1);
        clientes.add(cliente2);

        guardar(cliente1);
        guardar(cliente2);
    }

    public int contarClientes() {
        return clientes.size();
    }

    /**
     * Carga los clientes y sus cuentas desde archivo.
     * Si el cliente no existía en memoria, se reconstruye desde UsuarioRepository.
     */
    public void cargarDesdeArchivo() {

        Path ruta = Paths.get("Banco", "Datos", "Cuentas.txt");
        if (!Files.exists(ruta)) return;

        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            lector.readLine(); // saltar encabezado
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split("\t");
                if (datos.length < 4) continue;

                String numeroCuenta = datos[0];
                double saldo = Double.parseDouble(datos[1].replace(",", "."));
                String tipo = datos[2];
                String correo = datos[3];

                // Busca el cliente por correo
                Cliente cliente = buscarPorCorreo(correo).orElse(null);
                if (cliente == null) {
                    // Si no existe en memoria, se reconstruye a partir del usuario asociado
                    Usuario usuario = usuarioRepository.buscarUsuarioPorCorreo(correo).orElse(null);
                    if (usuario == null) continue;

                    cliente = new Cliente((Persona) usuario);
                    clientes.add(cliente);
                }

                // Evitar duplicar cuentas ya cargadas
                boolean yaExiste = cliente.getCuentas().stream()
                        .anyMatch(c -> c.getNumeroCuenta().equals(numeroCuenta));
                if (yaExiste) continue;

                // Crear cuenta con datos persistentes (sin generar nuevo número)
                CuentaBancaria cuenta = CuentaFactory.crearCuentaConDatos(tipo, cliente, numeroCuenta, saldo);

                cliente.agregarCuenta(cuenta);
                if (cliente.getCuentaActiva() == null) {
                    cliente.setCuentaActiva(cuenta);

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Guarda una cuenta en el archivo, evitando duplicados por número de cuenta.
     */
    private void guardarCuentaEnArchivo(CuentaBancaria cuenta) {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Cuentas.txt");
            if (ruta.getParent() != null) {
                Files.createDirectories(ruta.getParent());
            }

            if (!Files.exists(ruta)) {
                Files.writeString(ruta, "NumeroCuenta\tSaldo\tTipoCuenta\tCorreoCliente\n");
            }

            String contenido = Files.readString(ruta);
            if (contenido.contains(cuenta.getNumeroCuenta())) {
                return; // ya está registrada
            }

            String linea = String.format(
                    "%s\t%.2f\t%s\t%s%n",
                    cuenta.getNumeroCuenta(),
                    cuenta.getSaldo(),
                    cuenta.getCodigoTipoCuenta(),
                    cuenta.getPropietario().getCorreo()
            );

            Files.writeString(ruta, linea, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Actualiza el saldo de una cuenta en el archivo
     */
    public void actualizarSaldoEnArchivo(CuentaBancaria cuentaActualizada) {
        try {
            Path ruta = Paths.get("Banco", "Datos", "Cuentas.txt");
            if (!Files.exists(ruta)) return;

            // Leer todas las líneas
            List<String> lineas = Files.readAllLines(ruta);
            List<String> nuevasLineas = new ArrayList<>();

            // Conservar el encabezado
            if (!lineas.isEmpty()) {
                nuevasLineas.add(lineas.getFirst());
            }

            // Actualizar la línea de la cuenta específica
            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i);
                String[] datos = linea.split("\t");

                if (datos.length >= 4 && datos[0].equals(cuentaActualizada.getNumeroCuenta())) {
                    // Reemplazar con los nuevos datos
                    String nuevaLinea = String.format(
                            "%s\t%.2f\t%s\t%s",
                            cuentaActualizada.getNumeroCuenta(),
                            cuentaActualizada.getSaldo(),
                            cuentaActualizada.getCodigoTipoCuenta(),
                            cuentaActualizada.getPropietario().getCorreo()
                    );
                    nuevasLineas.add(nuevaLinea);
                    System.out.println(" Actualizando saldo de cuenta " + cuentaActualizada.getNumeroCuenta() +
                            " a: " + cuentaActualizada.getSaldo());
                } else {
                    nuevasLineas.add(linea);
                }
            }

            // Reescribir el archivo completo
            Files.write(ruta, nuevasLineas, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar el archivo de cuentas: " + e.getMessage(), e);
        }
    }
}
