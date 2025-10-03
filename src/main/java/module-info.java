module triplej.banco {
    requires javafx.controls;
    requires javafx.fxml;


    opens triplej.banco to javafx.fxml;
    exports triplej.banco;
    exports triplej.banco.Controllers;
    opens triplej.banco.Controllers to javafx.fxml;
}