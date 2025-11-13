package triplej.banco.Models.Reportes;

import triplej.banco.Models.Cuentas.Transaccion;
import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Repositories.TransaccionRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera reportes administrativos con información global del sistema bancario.
 * <p>
 * Este reporte consolida datos de usuarios y transacciones almacenadas en los
 * repositorios, incluyendo:
 * </p>
 * <ul>
 *     <li>Total de usuarios registrados.</li>
 *     <li>Total de transacciones procesadas.</li>
 *     <li>Listado de usuarios activos.</li>
 *     <li>Resumen de transacciones realizadas.</li>
 *     <li>Transacciones marcadas como sospechosas.</li>
 * </ul>
 *
 * <p>
 * Implementa la interfaz {@link ReporteGenerado}, lo que permite integrarla con
 * otros tipos de reportes dentro del módulo de reportes del sistema.
 * </p>
 *
 * <p>
 * La clase puede operar tanto con repositorios reales (por defecto) como con
 * repositorios inyectados manualmente, facilitando las pruebas unitarias.
 * </p>
 */
public  class ReporteAdmin implements ReporteGenerado {

    /** Repositorio encargado de gestionar las transacciones bancarias. */
    private final TransaccionRepository transaccionRepository;

    /** Repositorio encargado de gestionar los usuarios registrados en el sistema. */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor por defecto.
     * <p>
     * Obtiene las instancias únicas (singleton) de los repositorios de transacciones
     * y usuarios. Ideal para su uso en ejecución normal del sistema.
     * </p>
     */
    public ReporteAdmin(){
        this.transaccionRepository = TransaccionRepository.getInstancia();
        this.usuarioRepository = UsuarioRepository.getInstancia();
    }

    /**
     * Constructor alternativo que permite inyectar los repositorios.
     * <p>
     * Este constructor se utiliza principalmente para pruebas unitarias o
     * escenarios personalizados donde se requiere un repositorio simulado (mock).
     * </p>
     *
     * @param transaccionRepository Repositorio de transacciones.
     * @param usuarioRepository     Repositorio de usuarios.
     */
    public ReporteAdmin(TransaccionRepository transaccionRepository, UsuarioRepository usuarioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Genera el reporte administrativo completo.
     * <p>
     * El reporte incluye encabezado, conteos globales, listados de usuarios,
     * transacciones y una sección especial con transacciones sospechosas.
     * </p>
     *
     * @return Objeto {@link Reporte} que contiene toda la información generada.
     */
    @Override
    public Reporte generarReporte() {
        List<String> contenido = new ArrayList<>();
        contenido.add("-----------------REPORTE ADMINISTRATIVO---------------");
        contenido.add("Fecha de generación: " + LocalDateTime.now());
        contenido.add("Total de usuarios: " + usuarioRepository.getUsuarios().size());
        contenido.add("Total de transacciones: " + transaccionRepository.getTodasTransacciones().size());
        contenido.add("===============");

        contenido.addAll(generarResumenUsuarios());
        contenido.addAll(generarResumenTransacciones());
        contenido.addAll(generarTransaccionesSospechosas());

        return new Reporte("Reporte general del banco - ", LocalDateTime.now(), contenido);
    }

    /**
     * Genera una sección con el resumen de todas las transacciones registradas.
     *
     * @return Lista de líneas con información resumida de cada transacción.
     */
    private List<String> generarResumenTransacciones(){
        List<String> seccion = new ArrayList<>();
        seccion.add("TRANSACCIONES GENERALES");
        for(Transaccion t : transaccionRepository.getTodasTransacciones()){
            seccion.add(String.format("- [%s] |%s | %s -> %s | $%.2f | %s",
                    t.getId(),
                    t.getTipo(),
                    t.getCuentaOrigen(),
                    t.getCuentaDestino(),
                    t.getMonto(),
                    t.getFechaFormateada()));
        }
        seccion.add("");
        return seccion;
    }

    /**
     * Genera una sección con los usuarios activos registrados en el sistema.
     *
     * @return Lista de líneas con información básica de los usuarios.
     */
    private List<String> generarResumenUsuarios(){
        List<String> seccion = new ArrayList<>();
        seccion.add("USUARIOS ACTIVOS: ");
            for(Usuario u : usuarioRepository.getUsuarios()){
                seccion.add(String.format("- %s | %s | Estado: %s",
                u.getNombreUsuario(),
                u.getRolUsuario(),
                u.isActivo() ? "Activo" : "Inactivo"));
            }

        seccion.add("");
        return seccion;
    }

    /**
     * Genera una sección con las transacciones marcadas como sospechosas.
     * <p>
     * Las transacciones se consideran sospechosas si cumplen condiciones definidas
     * en el método {@link Transaccion#esSospechosa()}.
     * </p>
     *
     * @return Lista de líneas describiendo las transacciones sospechosas detectadas.
     */
    private List<String> generarTransaccionesSospechosas(){
        List<String> seccion = new ArrayList<>();
        seccion.add("TRANSACCIONES SOSPECHOSAS: ");
        var sospechosas = transaccionRepository.getTodasTransacciones()
                .stream()
                .filter(Transaccion::esSospechosa)
                .toList();

        if(sospechosas.isEmpty()){
            seccion.add("No se detectaron transacciones sospechosas");
        }else{
            for(Transaccion t : sospechosas){
                seccion.add(String.format("%s | Monto: $%.2f | Tipo: %s | Cuentas %s -> %s | Fecha: %s",
                        t.getId(), t.getMonto(), t.getTipo(), t.getCuentaOrigen(), t.getCuentaDestino(), t.getFechaFormateada()));
            }
        }
        return seccion;
    }
}
