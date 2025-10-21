package triplej.banco.Repositories;

import triplej.banco.Models.Cuentas.Transaccion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransaccionRepository {
    private static TransaccionRepository instance;
    private final List<Transaccion> transacciones;

    private TransaccionRepository() {
        transacciones  = new ArrayList<>();
        cargarDatosEjemplo();
    }

    public static TransaccionRepository getInstance() {
        if (instance == null) {
            instance = new TransaccionRepository();
        }
        return instance;
    }

    public void agregar(Transaccion transaccion){
        transacciones.add(transaccion);
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

        Transaccion t2 = new Transaccion("321","Déposito", 150000,"12341", "213413");
        agregar(t2);

        Transaccion t3 = new Transaccion("321","Déposito", 15000000,"12341", "213413");
        agregar(t3);

    }

}
