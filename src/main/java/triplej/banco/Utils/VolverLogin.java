package triplej.banco.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Clase utilitaria que permite regresar desde cualquier ventana o vista
 * al menú de inicio de sesión (Login) dentro de la aplicación.
 * <p>
 * Esta clase se utiliza comúnmente cuando el usuario cierra sesión,
 * o cuando se requiere reiniciar la interfaz al estado inicial.
 * <p>
 * Al ser abstracta, no puede instanciarse, ya que su propósito es
 * ofrecer un método estático reutilizable en cualquier parte del sistema.
 */
public abstract class VolverLogin {

    /**
     * Regresa la aplicación a la ventana de inicio de sesión.
     * <p>
     * Este método cierra la ventana actual (si existe) y abre
     * una nueva ventana que carga la vista del archivo FXML del login.
     *
     * @param ventanaActual la ventana que se encuentra abierta actualmente.
     *                      Puede ser {@code null} si no hay una ventana previa.
     */
    public static void volverLogin(Stage ventanaActual) {
        try {
            // Carga el archivo FXML correspondiente a la vista de inicio de sesión.
            FXMLLoader loader = new FXMLLoader(VolverLogin.class.getResource("/triplej/banco/Views/Login-view.fxml")
            );
            // Carga el árbol de nodos (interfaz) definido en el FXML.
            Parent root = loader.load();

            // Crea una nueva escena a partir del contenido cargado.
            Scene scene = new Scene(root);
            // Aplica la hoja de estilos CSS para mantener el diseño visual coherente.
            scene.getStylesheets().add(
                    Objects.requireNonNull(VolverLogin.class.getResource("/triplej/banco/Styles/login.css")).toExternalForm()
            );

            // Crea una nueva ventana (Stage) para mostrar el login.
            Stage loginStage = new Stage();
            loginStage.setTitle("Inicio UQ BANK");
            loginStage.setScene(scene);
            loginStage.setMaximized(true);
            loginStage.show();

            // Si existe una ventana actual, se cierra para evitar duplicados.
            if (ventanaActual != null) {
                ventanaActual.close();
            }

        } catch (IOException e) {
            // Si ocurre un error al cargar el archivo FXML o CSS,
            // se lanza una excepción más descriptiva.
            throw new RuntimeException("Error al volver al login: " + e.getMessage(), e);
        }
    }
}
