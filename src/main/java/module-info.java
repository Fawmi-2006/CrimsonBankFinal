module com.crimsonbank {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.crimsonbank.controllers to javafx.fxml;
    opens com.crimsonbank.models to javafx.base;

    exports com.crimsonbank;
}
