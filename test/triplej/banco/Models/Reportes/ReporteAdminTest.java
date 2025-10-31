package triplej.banco.Models.Reportes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Cuentas.Transaccion;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.Usuario;
import triplej.banco.Repositories.TransaccionRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReporteAdminTest {
    private TransaccionRepository transaccionRepository;
    private UsuarioRepository usuarioRepository;
    private ReporteAdmin reporteAdmin;

    @BeforeEach
    void setUp() {
        transaccionRepository = mock(TransaccionRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);

        reporteAdmin = new ReporteAdmin(transaccionRepository, usuarioRepository);
    }

    @Test
    void generarReporte() {

        ObservableList<Usuario> usuarios = FXCollections.observableArrayList();

        // Crear el mock del usuario
        Usuario usuario = mock(Usuario.class);

        when(usuario.getNombreCompleto()).thenReturn("Aquiles Tengo");
        when(usuario.getRolUsuario()).thenReturn(RolUsuario.CLIENTE);
        when(usuario.isActivo()).thenReturn(true);

        // Agregar el usuario a la lista antes de devolverla
        usuarios.add(usuario);

        // Crear el mock de transacción
        Transaccion transaccion = mock(Transaccion.class);
        when(transaccion.getId()).thenReturn("T001");
        when(transaccion.getTipo()).thenReturn("Depósito");
        when(transaccion.getCuentaDestino()).thenReturn("123");
        when(transaccion.getCuentaOrigen()).thenReturn("456");
        when(transaccion.getMonto()).thenReturn(5000.0);
        when(transaccion.getFechaFormateada()).thenReturn(LocalDate.now().toString());
        when(transaccion.esSospechosa()).thenReturn(false);

        // Mockear repositorios
        when(usuarioRepository.getUsuarios()).thenReturn(usuarios);
        when(transaccionRepository.getTodasTransacciones()).thenReturn(List.of(transaccion));

        // Generar el reporte
        ReporteGenerado reporte = reporteAdmin.generarReporte();

        System.out.println("==== CONTENIDO DEL REPORTE ====");
        reporte.getContenido().forEach(System.out::println);

        // Aserciones
        assertNotNull(reporte);
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("Total de usuarios")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("Total de transacciones")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("Aquiles Tengo")));
        assertTrue(reporte.getContenido().stream().anyMatch(linea -> linea.contains("T001")));


    }
}