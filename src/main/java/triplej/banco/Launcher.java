package triplej.banco;

import triplej.banco.Models.Cajero.Cajero;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.TipoDocumento;
import triplej.banco.Repositories.UsuarioRepository;

public class Launcher {
    public static void main(String[] args) {
        // El Cajero obtendrá el repositorio que necesita por sí mismo internamente.
        Cajero cajero = new Cajero();

        // --- Ejemplo de registro de un cliente ---
        System.out.println("Registrando un nuevo cliente...");
        Cliente c1 = new PersonaNatural("Juan", "Henao", TipoDocumento.CEDULACIUDADANIA, "213214", "21314","dasd@","1414124", "colombia", "armenia");
        cajero.registrarCliente(c1, "AHORRO");
        System.out.println("Cliente registrado con éxito.");

        // --- Ejemplo de operaciones ---
        String numeroCuenta = c1.getCuentas().get(0).getNumeroCuenta();
        System.out.println("El saldo inicial es: " + c1.getCuentas().get(0).getSaldo());
        cajero.realizarDeposito(numeroCuenta, 20000, "Depósito de nómina");
        System.out.println("El saldo después del depósito es: " + c1.getCuentas().get(0).getSaldo());
        System.out.println("Historial de la cuenta: " + c1.getCuentas().get(0).getHistorial());

        // --- CÓMO SABER CUÁNTOS CLIENTES TIENE EL BANCO ---
        // 1. Obtener la instancia del repositorio de usuarios.
        UsuarioRepository repo = UsuarioRepository.getInstancia();

        // 2. Llamar al nuevo método para contar los clientes.
        long numeroDeClientes = repo.contarClientes();

        // 3. Imprimir el resultado.
        System.out.println("=============================================");
        System.out.println("El número total de clientes en el banco es: " + numeroDeClientes);
        System.out.println("=============================================");

        // --- Ejemplo de agregar una segunda cuenta ---
        System.out.println("\nAgregando una segunda cuenta al cliente...");
        cajero.agregarCuentaACliente(c1, "CORRIENTE");
        System.out.println("El cliente ahora tiene " + c1.getCuentas().size() + " cuentas.");

        // --- Volver a contar los clientes ---
        // El número no debería cambiar, porque sigue siendo el mismo cliente.
        System.out.println("El número total de clientes en el banco sigue siendo: " + repo.contarClientes());
    }
}
