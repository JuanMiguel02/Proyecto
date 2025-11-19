# Proyecto Final Programación I
## Sistema de Gestión Bancaria "UQBank"

>  Proyecto desarrollado por Juan Miguel Henao, Jerónimo Delgado y Juan Camilo Agudelo.

Aplicación de escritorio desarrollada en **Java** con **JavaFX** que simula el funcionamiento básico de un sistema bancario.  
El proyecto permite la gestión de empleados, clientes y transacciones financieras, diferenciando las funciones del **Administrador** y del **Cajero**.

---

##  Características principales

### 1. Roles del sistema

####  Administrador
- Registra, edita y elimina **empleados** (personas naturales).
- Administra la información general del sistema.

#### Cajero
- Registra **clientes** (tanto personas naturales como jurídicas).
- Gestiona operaciones de:
    - Apertura de cuentas
    - Depósitos
    - Retiros
    - Transferencias
    - Consultas de saldo o movimientos

---

###  2. Tipos de clientes

- **Persona Natural:**  
  Incluye nombre, apellido, tipo y número de documento, teléfono, país y ciudad.

- **Persona Jurídica:**  
  Registra razón social, representante legal, tipo de empresa y demás datos de contacto.

>  Ambos tipos de cliente se almacenan en el archivo `Usuarios.txt`, con diferenciación de columnas según el tipo de persona.

---

###  3. Persistencia de datos

El sistema utiliza archivos de texto en la carpeta `Banco/Datos` para guardar la información:

| Archivo             | Contenido                                                       |
|---------------------|-----------------------------------------------------------------|
| `Usuarios.txt`      | Todos los usuarios del sistema                                  |
| `Empleados.txt`     | Empleados registrados por el administrador                      |
| `Clientes.txt`      | Información de clientes y cuentas bancarias                     |
| `Transacciones.txt` | Historial de transacciones (depósitos, retiros, transferencias) |

Los repositorios (`Repositories`) se encargan de leer, guardar y reescribir estos archivos usando clases como:
- `UsuarioRepository`
- `ClienteRepository`
- `EmpleadoRepository`
- `TransaccionRepository`

Cada archivo se actualiza automáticamente con las operaciones del sistema.

Los reportes (`Reportes`) pueden guardarse en una archivo txt
- `Reportes Admin`
- `Reportes Clieentes`

---

### Usuario predefinidos del sistema
| Rol                    | Nombre completo | Correo         | Contraseña | Descripción                                                                                          |
|------------------------|-----------------|----------------|------------|------------------------------------------------------------------------------------------------------|
| ️ **Administrador**    | Sancho Panza    | sancho@uqbank  | **123456** | Tiene acceso total al sistema. Puede crear, editar o eliminar empleados, y monitorear transacciones. |
| **Cajero**             | Paco Jones      | paco@gmail     | **123456** | Puede registrar clientes, abrir cuentas y procesar depósitos, retiros o transferencias.              |
| **Cliente (Natural)**  | Kepo John       | kepo@gmail     | **12345**  | Cliente con una cuenta de ahorro activa. Puede consultar su saldo e historial.                       |
| **Cliente (Jurídico)** | Empresa X       | empresax@gmail | **123456** | Cliente empresarial con cuenta corriente. Puede realizar transacciones y revisar reportes.           |


###  4. Interfaz gráfica (JavaFX)

Construida con **JavaFX** y archivos **FXML**, organizados por vistas:

- `AdminViews` → vistas relacionadas con el administrador (gestión de empleados).
- `CajeroViews` → vistas del cajero (registro de clientes, apertura de cuentas, operaciones).

#### Vistas principales:
- `Login-view.fxml`: pantalla de inicio de sesión.
- `Cajero-view.fxml`: panel principal del cajero.
- `FormularioCliente-view.fxml`: formulario de registro de clientes.
- `FormularioNuevaCuenta-view.fxml`: apertura de nuevas cuentas.

---

###  5. Interfaz gráfica (JavaFX)
Las pruebas se encuentran en el directorio src/test/java/triplej/banco/, organizadas por capa:

- ModelsTest → Lógica de negocio.

- RepositoriesTest → Persistencia y CRUD.

- ServicesTest → Casos de uso y reglas de negocio.

### 6. Estructura del proyecto


```
BancoProyecto/
├── src/
│   ├── main/
│   │   ├── java/triplej/banco/
│   │   │   ├── Controllers/
│   │   │   │   ├── VistaAdmin/                                                     #Controladores de la vista del Admin
│   │   │   │   │   ├── AdminController.java
│   │   │   │   │   ├── FormularioEmpleadoController.java
│   │   │   │   │   ├── MonitoreoTransaccionesController.java
│   │   │   │   │   └── TablaEmpleadosController.java
│   │   │   │   ├── VistaCajero/                                                    #Controladores de la vista del Cajero
│   │   │   │   │   ├── ClienteController.java
│   │   │   │   │   └── LoginController.java
│   │   │   │   └── ...
│   │   │   ├── Models/                                                             #Todos los modelos del sistema
│   │   │   │   ├── Cuentas/                                                        #Clases relacionadas a las cuentas bancarias
│   │   │   │   │   ├── CuentaBancaria.java
│   │   │   │   │   ├── CuentaAhorro.java
│   │   │   │   │   ├── CuentaCorriente.java
│   │   │   │   │   ├── CuentaEmpresarial.java
│   │   │   │   │   └── Transaccion.java
│   │   │   │   ├── Reportes/                                                       #Clases relacionadas a los reportes
│   │   │   │   │   ├── Reporte.java
│   │   │   │   │   ├── ReporteAdmin.java
│   │   │   │   │   ├── ReporteCliente.java
│   │   │   │   │   └── ReporteGenerado.java
│   │   │   │   ├── Usuarios/                                                       #Clases relacionadas a los usuarios
│   │   │   │   │   ├── Usuario.java
│   │   │   │   │   ├── Persona.java
│   │   │   │   │   ├── PersonaNatural.java
│   │   │   │   │   ├── PersonaJuridica.java
│   │   │   │   │   ├── Cliente.java
│   │   │   │   │   ├── Empleado.java
│   │   │   │   │   ├── RolUsuario.java
│   │   │   │   │   └── TipoDocumento.java
│   │   │   ├── Repositories/                                                       #Repositorios para persistencia de datos
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── ClienteRepository.java
│   │   │   │   ├── EmpleadoRepository.java
│   │   │   │   └── TransaccionRepository.java
│   │   │   ├── Services/                                                           #Capas de servicio
│   │   │   │   ├── AdminService.java
│   │   │   │   └── CajeroService.java
│   │   │   ├── Utils/                                                              #Clases utilitarias
│   │   │   │   ├── AlertHelper.java
│   │   │   │   └── ...
│   │   ├── Launcher.java                                                           #Compilación del programa
│   │   │   └── module-info.java
│   │   └── resources/
│   │       ├── triplej/banco/                                                      #Agrupa toda la información relacionada a las vistas
│   │       │   ├── Images/
│   │       │   ├── Styles/                                                         #Estilos del programa
│   │       │   │   ├── admin.css
│   │       │   │   ├── cajero.css
│   │       │   │   └── login.css
│   │       │   └── Views/
│   │       │       ├── AdminViews/                                                 #Vistas del admin
│   │       │       │   ├── Admin-view.fxml
│   │       │       │   ├── TablaEmpleados-view.fxml
│   │       │       │   ├── MonitoreoTransacciones-view.fxml
│   │       │       │   └── FormularioEmpleado-view.fxml
│   │       │       └── CajeroViews/                                                #Vistas del cajero
│   │       │           ├── Cajero-view.fxml
│   │       │           ├── Login-view.fxml
│   │       │           ├── Cliente-view.fxml
│   │       │           ├── Deposito-view.fxml
│   │       │           ├── Retiro-view.fxml
│   │       │           └── Transferencia-view.fxml
│   ├── test/                                                                       #Pruebas unitarias
│   │   └── java/triplej/banco/
│   │       ├── Models/
│   │       │   ├── Cuentas/CuentaBancariaTest.java
│   │       │   └── Reportes/ReporteAdminTest.java
│   │       ├── Repositories/
│   │       │   ├── ClienteRepositoryTest.java
│   │       │   ├── EmpleadoRepositoryTest.java
│   │       │   ├── TransaccionRepositoryTest.java
│   │       │   └── UsuarioRepositoryTest.java
│   │       ├── Services/
│   │       │   ├── AdminServiceTest.java
│   │       │   └── CajeroServiceTest.java
│   │       └── Utils/
│   │           └── CuentaFactoryTest.java
├── .gitignore
├── pom.xml / mvnw
└── README.md
```

## Ejecución

Clonar el repositorio.
```
bash 
https://github.com/JuanMiguel02/Proyecto.git
```

1. Abrir el proyecto en **IntelliJ IDEA** o cualquier IDE compatible con Maven y JavaFX.
2. Cargar las dependencias de `Maven`
3. Compilar y ejecutar el `Launcher` principal.
4. Los datos se crearán automáticamente en `Banco/Datos/` al ejecutar por primera vez.

---

## Tecnologías utilizadas

- **Java 17+**
- **JavaFX**
- **Maven**
- **Arquitectura MVC**
- **Persistencia con archivos de texto**

---


