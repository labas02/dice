module org.example.dice {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.dice to javafx.fxml;
    exports org.example.dice;
}