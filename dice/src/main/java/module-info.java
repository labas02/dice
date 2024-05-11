module org.example.dice {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxyz3d.core;
    requires java.desktop;
    requires javafx.media;
    requires json.simple;
    requires com.opencsv;


    opens org.example.dice to javafx.fxml;
    exports org.example.dice;
}