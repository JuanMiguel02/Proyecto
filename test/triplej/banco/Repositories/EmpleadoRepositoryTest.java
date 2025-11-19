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

/**
 * Esta clase realiza pruebas unitarias para el repositorio de empleados.
 * Se encarga de verificar que las operaciones CRUD (crear, leer, actualizar, eliminar)
 * sobre los empleados funcionen correctamente dentro de la clase {@link EmpleadoRepository}.
 */
class EmpleadoRepositoryTest {
    // Repositorio bajo prueba (patrón Singleton)
    private final EmpleadoRepository empleadoRepository = EmpleadoRepository.getInstancia();


    /**
     * Método que se ejecuta antes de cada prueba (@BeforeEach).
     * Su objetivo es limpiar el archivo de datos y reiniciar la lista interna de empleados
     * para que cada test empiece con un entorno controlado y vacío.
     */
    @BeforeEach
    void setUp() throws IOException {
        //Limpia la carpeta antes de cada test
        Path ruta = Paths.get("Banco", "Datos", "Empleados.txt");
        if(Files.exists(ruta)) {
            Files.delete(ruta);
        }
        empleadoRepository.getEmpleados().clear();
    }

    /**
     * Prueba 1 para el método {@link EmpleadoRepository#agregarEmpleado(Empleado)}.
     * Verifica que al agregar un nuevo empleado, este se almacene correctamente
     * en la lista interna del repositorio.
     */
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

    /**
     * Prueba 2 para el método {@link EmpleadoRepository#eliminarEmpleado(Empleado)}.
     * Comprueba que un empleado existente se elimine correctamente de la lista del repositorio.
     */
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

    /**
     * Prueba 3 para {@link EmpleadoRepository#buscarPorCorreo(String)}.
     * Verifica que el método encuentre correctamente un empleado
     * según su correo electrónico.
     */
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

    /**
     * Prueba 4 para {@link EmpleadoRepository#existeEmpleadoConCorreo(String)}.
     * Evalúa que el método detecte correctamente si un correo ya está registrado.
     */
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

    /**
     * Prueba 5 para {@link EmpleadoRepository#actualizarEmpleado(Empleado)}.
     * Comprueba que los cambios realizados a un empleado (como el salario o el correo)
     * se actualicen correctamente dentro del repositorio.
     */
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