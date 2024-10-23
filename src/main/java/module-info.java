module se233.astroboy {
    requires javafx.controls;
    requires javafx.fxml;


    opens se233.astroboy to javafx.fxml;
    exports se233.astroboy;
}