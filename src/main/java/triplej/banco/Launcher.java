package triplej.banco;

import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaCorriente;
import triplej.banco.Models.Cuentas.CuentaEmpresarial;
import triplej.banco.Models.Usuarios.Cliente;
import triplej.banco.Models.Usuarios.TipoDocumento;


public class Launcher {
    public static void main(String[] args) {
        Cliente c1 = new Cliente("Juan", TipoDocumento.CEDULACIUDADANIA, "111111","313312312", "juan@gmail.com", "Pepe123", "Colombia", "Armenia");

        CuentaAhorro ch = new CuentaAhorro(c1,2000);
        CuentaCorriente ch1 = new CuentaCorriente(c1,20010);
        CuentaEmpresarial ch2 = new CuentaEmpresarial(c1,20020);
        System.out.println(ch);
        System.out.println(ch1);
        System.out.println(ch2);


    }
}
