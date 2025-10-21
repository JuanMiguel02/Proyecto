package triplej.banco.Models.Reportes;

import triplej.banco.Models.Cuentas.Transaccion;
import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Repositories.TransaccionRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public  class ReporteAdmin implements Reporte{

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteAdmin(){
        this.transaccionRepository = TransaccionRepository.getInstance();
        this.usuarioRepository = UsuarioRepository.getInstancia();
    }

    @Override
    public ReporteGenerado generarReporte() {
        List<String> contenido = new ArrayList<>();
        contenido.add("-----------------REPORTE ADMINISTRATIVO---------------");
        contenido.add("Fecha de generación: " + LocalDateTime.now());
        contenido.add("Total de usuarios: " + usuarioRepository.getUsuarios().size());
        contenido.add("Total de transacciones: " + transaccionRepository.getTodasTransacciones().size());
        contenido.add("===============");

        contenido.addAll(generarResumenUsuarios());
        contenido.addAll(generarResumenTransacciones());
        contenido.addAll(generarTransaccionesSospechosas());

        return new ReporteGenerado("Reporte avanzado del sistema", LocalDateTime.now(), contenido);
    }

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


    private List<String> generarResumenUsuarios(){
        List<String> seccion = new ArrayList<>();
        seccion.add("USUARIOS ACTIVOS: ");
            for(Usuario u : usuarioRepository.getUsuarios()){
                seccion.add(String.format("- %s | %s | Estado: %s",
                u.getNombreCompleto(),
                u.getRolUsuario(),
                u.isActivo() ? "Activo" : "Inactivo"));
            }

        seccion.add("");
        return seccion;
    }

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
