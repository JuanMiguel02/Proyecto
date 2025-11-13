package triplej.banco.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Usuarios.*;
import triplej.banco.Repositories.EmpleadoRepository;
import triplej.banco.Repositories.UsuarioRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private AdminService adminService;
    private EmpleadoRepository empleadoRepository;
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        empleadoRepository = mock(EmpleadoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);

        adminService = new AdminService();

        try{
            var campoEmpleado = AdminService.class.getDeclaredField("empleadoRepository");
            campoEmpleado.setAccessible(true);
            campoEmpleado.set(adminService, empleadoRepository);

            var campoUsuario = AdminService.class.getDeclaredField("usuarioRepository");
            campoUsuario.setAccessible(true);
            campoUsuario.set(adminService, usuarioRepository);


        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void registrarEmpleadoFotoDefecto() {
        PersonaNatural persona = new PersonaNatural(
                "Pepito", "Pérez", "pepito@gmail", "123", RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA,
                "123", "313123123", "Colombia", "Armenia"
        );

        File imagenSeleccionada = null;

        Empleado empleado = adminService.registrarEmpleado(persona, "Cajero", 250000, "Atención al cliente", imagenSeleccionada);

        assertNotNull(empleadoRepository);
        assertEquals("Cajero", empleado.getCargo());
        assertEquals("/triplej/banco/Images/avatar.png", persona.getFoto());
        verify(empleadoRepository).agregarEmpleado(any(Empleado.class));
    }

    @Test
    void registrarEmpleadoFoto() throws IOException {
        PersonaNatural persona = new PersonaNatural(
                "Pepito", "Pérez", "pepito@gmail", "123", RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA,
                "123", "313123123", "Colombia", "Armenia"
        );

        File archivoTemporal = File.createTempFile("fotoPrueba", ".jpg");
        Files.writeString(archivoTemporal.toPath(), "imagen_falsa");

        doNothing().when(empleadoRepository).agregarEmpleado(any(Empleado.class));

        Empleado nuevo = adminService.registrarEmpleado(persona, "Admin", 400000, "TI", archivoTemporal);

        assertNotNull(nuevo);
        assertEquals("Admin", nuevo.getCargo());
        assertTrue(persona.getFoto().endsWith("123.jpg"));
        verify(empleadoRepository, times(1)).agregarEmpleado(any(Empleado.class));
    }

    @Test
    void correoYaExiste() {
        when(empleadoRepository.existeEmpleadoConCorreo("ana@correo.com")).thenReturn(true);
        when(usuarioRepository.existeUsuarioConCorreo("ana@correo.com")).thenReturn(false);

        boolean existe = adminService.correoYaExiste("Ana@Correo.com ");

        assertTrue(existe);
        verify(empleadoRepository, times(1)).existeEmpleadoConCorreo("ana@correo.com");
    }

    @Test
    void obtenerEmpleados() {
        when(empleadoRepository.getEmpleados()).thenReturn(List.of(mock(Empleado.class)));

        var lista = adminService.obtenerEmpleados();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        verify(empleadoRepository, times(1)).getEmpleados();
    }

    @Test
    void eliminarEmpleado() {
        assertFalse(adminService.eliminarEmpleado(null));

        Empleado e = mock(Empleado.class);
        doNothing().when(empleadoRepository).eliminarEmpleado(e);
        assertTrue(adminService.eliminarEmpleado(e));
        verify(empleadoRepository, times(1)).eliminarEmpleado(e);
    }

    @Test
    void actualizarEmpleado() {
        PersonaNatural persona = new PersonaNatural(
                "Juan", "Pérez", "juan@correo.com", "123",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "12345",
                "3100000000", "Colombia", "Armenia"
        );

        Empleado empleado = new Empleado(persona, "Cajero", 2_000_000, "Atención");

        // Datos nuevos
        String nuevoNombre = "Carlos";
        String nuevoApellido = "Hernández";
        String nuevoCorreo = "carlos@correo.com";
        String nuevoTelefono = "3201111111";
        String nuevaCiudad = "Bogotá";
        String nuevoCargo = "Administrador";
        String nuevoDepartamento = "Operaciones";
        double nuevoSalario = 3_000_000;
        String nuevaContrasenia = "nueva123";
        RolUsuario nuevoRol = RolUsuario.ADMIN;

        // --- ACT ---
        adminService.actualizarEmpleado(
                empleado,
                nuevoNombre, nuevoApellido, nuevoCorreo, nuevoTelefono, nuevaCiudad,
                nuevoCargo, nuevoDepartamento, nuevoSalario, nuevaContrasenia, nuevoRol
        );

        // --- ASSERT ---
        assertEquals(nuevoNombre, empleado.getPersona().getNombre());
        assertEquals(nuevoApellido, empleado.getPersona().getApellido());
        assertEquals(nuevoCorreo, empleado.getPersona().getCorreo());
        assertEquals(nuevoTelefono, empleado.getPersona().getTelefono());
        assertEquals(nuevaCiudad, empleado.getPersona().getCiudad());
        assertEquals(nuevoRol, empleado.getPersona().getRolUsuario());

        assertEquals(nuevoCargo, empleado.getCargo());
        assertEquals(nuevoDepartamento, empleado.getDepartamento());
        assertEquals(nuevoSalario, empleado.getSalario());
        assertEquals(nuevaContrasenia, empleado.getPersona().getContrasenia());

        // --- VERIFY ---
        verify(usuarioRepository, times(1)).actualizarUsuario(empleado.getPersona());
        verify(empleadoRepository, times(1)).actualizarEmpleado(empleado);
    }

    @Test
    void determinarRolPorCargo() {
        assertEquals(RolUsuario.ADMIN, adminService.determinarRolPorCargo("Admin"));
        assertEquals(RolUsuario.CAJERO, adminService.determinarRolPorCargo("Cajero"));
        assertEquals(RolUsuario.EMPLEADO, adminService.determinarRolPorCargo("Celador"));

    }
}