package triplej.banco;

import triplej.banco.Models.Cajero.Cajero;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.TipoDocumento;

public class Launcher {
    public static void main(String[] args) {
        // El Cajero obtendrá el repositorio que necesita por sí mismo internamente.
        Cajero cajero = new Cajero();

        // --- Ejemplo de registro de un cliente ---
        System.out.println("Registrando un nuevo cliente...");
        PersonaNatural c1 = new PersonaNatural("Juan", "Henao", "juanm@gf", "12451", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "213214", "2132414", "colombia", "armenia");
        PersonaNatural c2 = new PersonaNatural("Juan", "Ho", "juanm@f", "12451", RolUsuario.CLIENTE, TipoDocumento.CEDULACIUDADANIA, "213214", "2132414", "colombia", "armenia");

        Cliente cliente = cajero.registrarCliente(c1, "AHORRO");
        Cliente cliente2 = cajero.registrarCliente(c2, "CORRIENTE");

        cajero.agregarCuentaACliente(cliente, "CORRIENTE");

        CuentaBancaria cuenta = cliente.getCuentas().getFirst();

        cajero.realizarDeposito(cuenta, 20000, "");

        System.out.println("Saldo: " + cajero.consultarSaldo(cuenta));
        System.out.println(cliente.getCuentas());
        System.out.println(cliente.getCuentas().getFirst().getHistorial());

    }
}
