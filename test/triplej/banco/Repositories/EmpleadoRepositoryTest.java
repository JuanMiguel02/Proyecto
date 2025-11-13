package triplej.banco.Repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplej.banco.Models.Usuarios.Empleado;
import triplej.banco.Models.Usuarios.PersonaNatural;
import triplej.banco.Models.Usuarios.RolUsuario;
import triplej.banco.Models.Usuarios.TipoDocumento;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoRepositoryTest {

    private final EmpleadoRepository empleadoRepository = EmpleadoRepository.getInstancia();

    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Empleados.txt");
        if(Files.exists(ruta)) {
            Files.delete(ruta);
        }
        empleadoRepository.getEmpleados().clear();
    }

    @Test
    void agregarEmpleado() {
        PersonaNatural persona = new PersonaNatural("Camilo", "Agudelo",
                "camilo@test.com", "1234",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Pueblo Tapao");
        Empleado empleado = new Empleado(persona, "Jefe", 2000.0, "Sistemas");

        empleadoRepository.agregarEmpleado(empleado);
        assertTrue(empleadoRepository.getEmpleados().contains(empleado));
        assertEquals(1,  empleadoRepository.getEmpleados().size());
    }

    @Test
    void eliminarEmpleado() {
        PersonaNatural persona = new PersonaNatural("Jerónimo", "Delgado",
                "jeronimo@test.com", "1234",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Calarcá");
        Empleado empleado = new Empleado(persona, "Jefe", 2000.0, "Sistemas");

        empleadoRepository.agregarEmpleado(empleado);
        assertTrue(empleadoRepository.getEmpleados().contains(empleado));
        assertEquals(1,  empleadoRepository.getEmpleados().size());

        empleadoRepository.eliminarEmpleado(empleado);
        assertFalse(empleadoRepository.getEmpleados().contains(empleado));
        assertEquals(0,  empleadoRepository.getEmpleados().size());
    }

    @Test
    void buscarPorCorreo() {
        PersonaNatural persona = new PersonaNatural("Juan", "Henao",
                "juan@test.com", "1234",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Armenia");
        Empleado empleado = new Empleado(persona, "Jefe", 2000.0, "Sistemas");

        empleadoRepository.agregarEmpleado(empleado);
        assertTrue(empleadoRepository.buscarPorCorreo("juan@test.com").isPresent());
    }

    @Test
    void existeEmpleadoConCorreo() {
        PersonaNatural persona = new PersonaNatural("Jacobo", "Valencia",
                "jacobo@test.com", "1234",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Circasia");
        Empleado empleado = new Empleado(persona, "Jefe", 2000.0, "Sistemas");

        empleadoRepository.agregarEmpleado(empleado);
        assertTrue(empleadoRepository.existeEmpleadoConCorreo("jacobo@test.com"));
    }

    @Test
    void actualizarEmpleado() {
        PersonaNatural persona = new PersonaNatural("Jacobo", "Valencia",
                "jacobo@test.com", "1234",
                RolUsuario.EMPLEADO, TipoDocumento.CEDULACIUDADANIA, "1021",
                "2414", "Colombia", "Circasia");
        Empleado empleado = new Empleado(persona, "Jefe", 2000.0, "Sistemas");
        empleadoRepository.agregarEmpleado(empleado);

        assertEquals(2000.0, empleado.getSalario());
        assertTrue(empleadoRepository.existeEmpleadoConCorreo("jacobo@test.com"));

        empleado.setSalario(5000.0);
        persona.setCorreo("jacobo@gmail.com");
        empleadoRepository.actualizarEmpleado(empleado);

        assertEquals(5000.0, empleado.getSalario());
        assertTrue(empleadoRepository.existeEmpleadoConCorreo("jacobo@gmail.com"));
    }
}