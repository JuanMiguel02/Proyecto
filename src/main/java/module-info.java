module triplej.banco {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens triplej.banco to javafx.fxml;
    opens triplej.banco.Controllers to javafx.fxml;
    opens triplej.banco.Models to javafx.fxml;
    opens triplej.banco.Models.Usuarios to javafx.base;
    opens triplej.banco.Models.Cuentas to javafx.base;


    exports triplej.banco;
    exports triplej.banco.Controllers;
}