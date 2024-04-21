module org.example.dice {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxyz3d.core;


    opens org.example.dice to javafx.fxml;
    exports org.example.dice;
}