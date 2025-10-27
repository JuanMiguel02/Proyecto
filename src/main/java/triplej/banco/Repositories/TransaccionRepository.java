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

public class TransaccionRepository {
    private static TransaccionRepository instance;
    private final List<Transaccion> transacciones;

    private TransaccionRepository() {
        transacciones  = new ArrayList<>();

        Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
        if (Files.exists(ruta)) {
            // Si ya existe el archivo, cargamos las transacciones persistidas
            cargarDesdeArchivo();
        } else {
            // Si es la primera ejecución, cargamos datos de ejemplo
            System.out.println("⚙️ Primera ejecución: creando transacciones de ejemplo...");
            cargarDatosEjemplo();
        }
    }

    public static TransaccionRepository getInstance() {
        if (instance == null) {
            instance = new TransaccionRepository();
        }
        return instance;
    }

    public void agregar(Transaccion transaccion){
        transacciones.add(transaccion);
        guardarEnArchivo(transaccion);
    }

    public List<Transaccion> getTodasTransacciones(){
        return transacciones;
    }

    public List<Transaccion> getPorCuenta(String numeroCuenta){
        return transacciones.stream()
                .filter(t -> numeroCuenta.equals(t.getCuentaOrigen()) || numeroCuenta.equals(t.getCuentaDestino()))
                .collect(Collectors.toList());
    }

    private void cargarDatosEjemplo() {
       Transaccion t1 = new Transaccion("123","Retiro", 20000,"12345", "213213");
        agregar(t1);

        Transaccion t2 = new Transaccion("321","Deposito", 150000,"12341", "213413");
        agregar(t2);

        Transaccion t3 = new Transaccion("321","Deposito", 15000000,"12341", "213413");
        agregar(t3);

    }

    private void guardarEnArchivo(Transaccion trans){
        try{
            Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
            if(ruta.getParent() != null){
                Files.createDirectories(ruta.getParent());
            }

            if(!Files.exists(ruta)){
                Files.writeString(ruta,"ID\tTipo\tMonto\tCuentaOrigen\tCuentaDestino\tFecha\n");
            }

            String linea = String.format(
                    "%s\t%s\t%.2f\t%s\t%s\t%s%n",
                   trans.getId(),
                   trans.getTipo(),
                   trans.getMonto(),
                   trans.getCuentaOrigen(),
                   trans.getCuentaDestino(),
                   trans.getFecha()
            );
            Files.writeString(ruta, linea, StandardOpenOption.APPEND);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar transacción: " + e.getMessage(), e);
        }

    }

    private void cargarDesdeArchivo(){
        Path ruta = Paths.get("Banco", "Datos", "Transacciones.txt");
        if(!Files.exists(ruta)) return;

        try(BufferedReader lector = Files.newBufferedReader(ruta)){
            lector.readLine(); // saltar encabezado
            String linea;
            while((linea = lector.readLine()) != null){
                String[] datos = linea.split("\t");
                if(datos.length < 6) continue;

                String id = datos[0];
                String tipo = datos[1];
                double monto = Double.parseDouble(datos[2].replace(",", "."));
                String cuentaOrigen = datos[3];
                String cuentaDestino = datos[4];
                LocalDateTime fecha = LocalDateTime.parse(datos[5]);

                Transaccion trans = new Transaccion(id, tipo, monto, cuentaOrigen, cuentaDestino);
                trans.setFecha(fecha);
                transacciones.add(trans);

            }

        } catch (Exception e) {
            throw new RuntimeException("Error al cargar  las transacciones: " + e.getMessage(), e);
        }
    }

}
