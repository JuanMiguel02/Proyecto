package triplej.banco.Repositories;

import triplej.banco.Models.Cuentas.Transaccion;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositorio encargado de gestionar las transacciones bancarias.
 * <p>
 * Se encarga de almacenar, cargar, y persistir todas las transacciones del sistema.
 * Implementa el patrón Singleton para asegurar que solo exista una instancia.
 * </p>
 */
public class TransaccionRepository {
    // Instancia única del repositorio (patrón Singleton)
    private static TransaccionRepository instance;

    // Lista que almacena todas las transacciones en memoria
    private final List<Transaccion> transacciones;

    // Constructor privado para evitar instanciación externa
    private TransaccionRepository() {
        transacciones  = new ArrayList<>();
    }

    /**
     * Método encargado de cargar los datos del archivo o generar datos de ejemplo si no existe.
     */
    public void cargarDatos() {
            // Se define la ruta del archivo donde se guardan las transacciones
            Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
            if (Files.exists(ruta)) {
                // Si el archivo existe, se cargan las transacciones desde el archivo
                System.out.println("Cargando transacciones desde archivo...");
                cargarDesdeArchivo();
            } else {
                // Si no existe, se cargan algunos datos de ejemplo
                cargarDatosEjemplo();
                System.out.println(" Primera ejecución: creando transacciones de ejemplo...");
            }
    }

    /**
     * Retorna la única instancia de TransaccionRepository (Singleton)
     */
    public static TransaccionRepository getInstancia() {
        if (instance == null) {
            instance = new TransaccionRepository();
        }
        return instance;
    }

    /**
     * Agrega una transacción a la lista y la guarda en el archivo.
     */
    public void agregar(Transaccion transaccion){
        transacciones.add(transaccion);
        guardarEnArchivo(transaccion);
    }

    /**
     * Retorna todas las transacciones registradas.
     */
    public List<Transaccion> getTodasTransacciones(){
        return transacciones;
    }

    /**
     * Retorna las transacciones asociadas a una cuenta específica (como origen o destino).
     */
    public List<Transaccion> getPorCuenta(String numeroCuenta){
        return transacciones.stream()
                .filter(t -> numeroCuenta.equals(t.getCuentaOrigen()) || numeroCuenta.equals(t.getCuentaDestino()))
                .collect(Collectors.toList());
    }

    /**
     * Carga transacciones de ejemplo para pruebas o primera ejecución.
     */
    private void cargarDatosEjemplo() {
        Transaccion t1 = new Transaccion("123","Retiro", 20000,"12345", "213213");
        t1.setExitosa(true);
        agregar(t1);

        Transaccion t2 = new Transaccion("321","Deposito", 150000,"12341", "213413");
        t2.setExitosa(true);
        agregar(t2);

        Transaccion t3 = new Transaccion("321","Deposito", 15000000,"12341", "213413");
        t3.setExitosa(true);
        agregar(t3);

    }

    /**
     * Guarda una transacción en el archivo “Transacciones.txt”.
     */
    private void guardarEnArchivo(Transaccion trans){
        try{
            // Se abre la ruta del archivo donde se guardan las transacciones
            Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
            // Si la carpeta contenedora no existe, se crea
            if(ruta.getParent() != null){
                Files.createDirectories(ruta.getParent());
            }
            // Si el archivo no existe, se crea junto con su encabezado
            if(!Files.exists(ruta)){
                Files.writeString(ruta, String.format("ID\tTipo\tMonto\tCuentaOrigen\tCuentaDestino\tFecha\tDescripción\tExitosa%n"));
            }
            // Se construye la línea que representa la transacción
            String linea = String.format(
                    "%s\t%s\t%.2f\t%s\t%s\t%s\t%s\t%s%n",
                    trans.getId(),
                    trans.getTipo(),
                    trans.getMonto(),
                    trans.getCuentaOrigen(),
                    trans.getCuentaDestino(),
                    trans.getFecha(),
                    trans.getDescripcion(),
                    trans.isExitosa()
            );
            // Se escribe la línea al final del archivo
            Files.writeString(ruta, linea, StandardOpenOption.APPEND);

        } catch (IOException e) {
            // En caso de error, se lanza una excepción con el detalle
            throw new RuntimeException("Error al guardar transacción: " + e.getMessage(), e);
        }

    }

    /**
     * Carga todas las transacciones desde el archivo “Transacciones.txt”.
     */
    private void cargarDesdeArchivo(){
        // Se define la ruta del archivo
        Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
        // Si el archivo no existe, no hay nada que cargar
        if(!Files.exists(ruta)) return;

        // Se intenta abrir el archivo y leerlo línea por línea
        try(BufferedReader lector = Files.newBufferedReader(ruta)){
            lector.readLine(); // saltar encabezado
            String linea;
            // Mientras haya líneas por leer
            while((linea = lector.readLine()) != null){
                // Se separan los datos usando tabuladores como delimitador
                String[] datos = linea.split("\t");
                // Se valida que tenga todos los campos requeridos
                if(datos.length < 8) continue;
                // Se crea una transacción a partir de los datos leídos
                Transaccion trans = getTransaccion(datos);
                // Se agrega a la lista en memoria
                transacciones.add(trans);

            }

        } catch (Exception e) {
            throw new RuntimeException("Error al cargar  las transacciones: " + e.getMessage(), e);
        }
    }

    /**
     * Crea una transacción a partir de los datos leídos de una línea del archivo.
     */
    private static Transaccion getTransaccion(String[] datos) {
        // Se extraen los campos desde el arreglo de datos
        String id = datos[0];
        String tipo = datos[1];
        double monto = Double.parseDouble(datos[2].replace(",", "."));
        String cuentaOrigen = datos[3];
        String cuentaDestino = datos[4];
        LocalDateTime fecha = LocalDateTime.parse(datos[5]);
        String descripcion = datos[6];
        boolean exitosa = Boolean.parseBoolean(datos[7]);

        // Se crea un nuevo objeto Transaccion con los valores leídos
        Transaccion trans = new Transaccion(id, tipo, monto, cuentaOrigen, cuentaDestino);
        trans.setFecha(fecha);
        trans.setExitosa(exitosa);
        trans.setDescripcion(descripcion);
        return trans;
    }

}
