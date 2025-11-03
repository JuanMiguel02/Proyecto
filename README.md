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

### 🖥 4. Interfaz gráfica (JavaFX)

Construida con **JavaFX** y archivos **FXML**, organizados por vistas:

- `AdminViews` → vistas relacionadas con el administrador (gestión de empleados).
- `CajeroViews` → vistas del cajero (registro de clientes, apertura de cuentas, operaciones).

#### Vistas principales:
- `Login-view.fxml`: pantalla de inicio de sesión.
- `Cajero-view.fxml`: panel principal del cajero.
- `FormularioCliente-view.fxml`: formulario de registro de clientes.
- `FormularioNuevaCuenta-view.fxml`: apertura de nuevas cuentas.

---

### 5. Estructura del proyecto


```
BancoProyecto/
├── Banco/
│   └── Datos/
│       ├── Cuentas.txt               # Información de las cuentas bancarias
│       ├── Empleados.txt             # Datos de empleados
│       ├── Transacciones.txt         # Registro de transacciones
│       └── Usuarios.txt              # Clientes naturales y jurídicos
│
├── src/
│   └── main/
│       ├── java/
│       │   └── triplej/banco/
│       │       ├── App.java                          # Clase principal
│       │       ├── Controllers/
│       │       │   ├── VistaAdmin/                   # Controladores de vistas del administrador
│       │       │   └── VistaCajero/                  # Controladores de vistas del cajero
│       │       │   └── LoginController               # Controlador de la vista del login
│       │       ├── Models/
│       │       │   ├── Cuentas/                      # Modelos de cuentas bancarias
│       │       │   ├── Reportes/                     # Modelos para generación de reportes
│       │       │   └── Usuarios/                     # Modelos de empleados, clientes y roles
│       │       ├── Repositories/                     # Manejo de persistencia (archivos TXT)
│       │       └── Utils/                            # Clases auxiliares o utilitarias
│       │       └── Launcher                          # Ejecución de la aplicación
│       │
│       └── resources/
│           └── triplej/banco/Views/
│           │       ├── AdminViews/                       # Vistas del administrador (FXML)
│           │       ├── CajeroViews/                      # Vistas del cajero (FXML)
│           │       ├── Cliente-view.fxml                 # Vista principal de cliente
│           │       ├── Login-view.fxml                   # Vista de inicio de sesión
│           └── triplej/banco/Styles/
│               ├── 
│               └── triplej/banco/Images/
│               ├── 
│
├── pom.xml                                           # Configuración del proyecto Maven
└── README.md                                         # Documentación del proyecto
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


