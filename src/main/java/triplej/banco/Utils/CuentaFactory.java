package triplej.banco.Utils;

import triplej.banco.Models.Cuentas.CuentaAhorro;
import triplej.banco.Models.Cuentas.CuentaBancaria;
import triplej.banco.Models.Cuentas.CuentaCorriente;
import triplej.banco.Models.Cuentas.CuentaEmpresarial;
import triplej.banco.Models.Usuarios.Cliente;

/**
 * Clase de utilidad que implementa el patrón de diseño Factory (Fábrica).
 * Su función es centralizar la creación de objetos del tipo {@link CuentaBancaria}
 * según el tipo de cuenta solicitado.
 * <p>
 * Esto evita el uso de múltiples condicionales dispersos en el código,
 * y facilita la extensión del sistema al añadir nuevos tipos de cuentas.
 */
public class CuentaFactory {

    /**
     * Crea una cuenta bancaria del tipo especificado para el cliente dado.
     *
     * @param tipoCuenta tipo de cuenta (por ejemplo, "AHORRO", "CORRIENTE", "EMPRESARIAL")
     * @param propietario instancia del cliente propietario de la cuenta
     * @return una nueva instancia de {@link CuentaBancaria} correspondiente al tipo indicado
     * @throws IllegalArgumentException si el tipo de cuenta no es válido
     */
    public static CuentaBancaria crearCuenta(String tipoCuenta, Cliente propietario){
        // Convierte el tipo de cuenta a mayúsculas para evitar errores por diferencia de mayúsculas/minúsculas.
        return switch (tipoCuenta.toUpperCase()){
            case "AHORRO" -> new CuentaAhorro(propietario);
            case "CORRIENTE" -> new CuentaCorriente(propietario);
            case "EMPRESARIAL" -> new CuentaEmpresarial(propietario);
            default -> throw new IllegalArgumentException("Tipo de cuenta no valido");
        };
    }

    /**
     * Variante del método anterior que permite definir un valor de sobregiro
     * (solo aplicable a las cuentas corrientes).
     *
     * @param tipoCuenta tipo de cuenta ("AHORRO", "CORRIENTE", "EMPRESARIAL")
     * @param propietario cliente al que pertenece la cuenta
     * @param sobregiro límite de sobregiro opcional (solo se usa para cuentas corrientes)
     * @return una instancia de la cuenta solicitada configurada con los parámetros adecuados
     */
    public static CuentaBancaria crearCuenta(String tipoCuenta, Cliente propietario, Double sobregiro) {
        return switch (tipoCuenta.toUpperCase()) {
            case "AHORRO" -> new CuentaAhorro(propietario);
            case "CORRIENTE" -> {
                // Crea una cuenta corriente y asigna el sobregiro si se ha indicado un valor válido.
                CuentaCorriente cta = new CuentaCorriente(propietario);
                if (sobregiro != null && sobregiro > 0) {
                    cta.setLimiteSobregiro(sobregiro);
                }
                yield cta; //Devuelve la cuenta configurada
            }
            case "EMPRESARIAL" -> new CuentaEmpresarial(propietario);
            default -> throw new IllegalArgumentException("Tipo de cuenta no válido: " + tipoCuenta);
        };
    }

    /**
     * Crea una cuenta bancaria a partir de datos ya existentes (por ejemplo, al cargar desde archivo).
     * Esta versión permite especificar número de cuenta, saldo y sobregiro, según el tipo.
     *
     * @param tipo código del tipo de cuenta: "1"=Ahorro, "2"=Corriente, "3"=Empresarial
     * @param cliente propietario de la cuenta
     * @param numeroCuenta número único asociado a la cuenta
     * @param saldo saldo inicial de la cuenta
     * @param sobregiro límite de sobregiro en caso de ser cuenta corriente
     * @return una cuenta bancaria configurada con los datos indicados
     * @throws IllegalArgumentException si el tipo de cuenta no coincide con ninguno de los códigos válidos
     */
    public static CuentaBancaria crearCuentaConDatos(String tipo, Cliente cliente, String numeroCuenta, double saldo, Double sobregiro) {
        if (tipo.equals("1")) {
            // Crea una cuenta de ahorro con saldo inicial.
            return new CuentaAhorro(cliente, numeroCuenta, saldo);
        } else if (tipo.equals("2")) {
            // Crea una cuenta corriente incluyendo sobregiro (si aplica).
            return new CuentaCorriente(cliente, numeroCuenta, saldo, sobregiro);
        } else if(tipo.equals("3")) {
            // Crea una cuenta empresarial con saldo inicial.
            return new CuentaEmpresarial(cliente, numeroCuenta, saldo);
        }else{
            // Si el tipo no coincide con ninguno, lanza un error.
            throw new IllegalArgumentException("Tipo de cuenta desconocido: " + tipo);
        }
    }

}
