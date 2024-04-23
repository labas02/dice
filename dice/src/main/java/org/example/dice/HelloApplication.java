package org.example.dice;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.io.IOException;
import java.util.Objects;


public class HelloApplication extends Application {
    static Stage stage_true;
    @Override
    public void start(Stage stage) throws IOException {
        stage_true = stage;
        scene_manager(1);
    }

    public static void main(String[] args) {
        launch();
    }
    public void scene_manager(int scene) throws IOException {
        switch (scene){
            case 1:
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start-screen.fxml"));
                Scene start_screen = new Scene(fxmlLoader.load(), 320, 240);
                stage_true.setScene(start_screen);
                stage_true.show();
                break;
            case 2:
                FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("select-screen.fxml"));
                Scene select_menu;
                select_menu = new Scene(fxmlLoader1.load(), 320, 240);
                stage_true.setScene(select_menu);
                stage_true.show();
                break;
            case 3:
                CuboidMesh[] boxes = new CuboidMesh[6];
                int size = 50;

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i] = new CuboidMesh(size, size, size);
                }
                RotateTransition[] transitionR = new RotateTransition[6];
                for (int i = 0;i<transitionR.length;i++){
                    transitionR[i] =new RotateTransition(Duration.millis(2000),boxes[i]);
                }

                TranslateTransition tb1 = new TranslateTransition(Duration.millis(2000),boxes[1]);
                Button but = new Button();
                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setId(String.valueOf(i));
                }
                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {
                    HelloController.generate_values(boxes,transitionR);
                    ParallelTransition transition = new ParallelTransition(transitionR[0],transitionR[1],transitionR[2],transitionR[3],transitionR[4],transitionR[5]);
                    transition.play();
                    tb1.play();
                });
                AnchorPane anchor = new AnchorPane(boxes[0],boxes[1],boxes[2],boxes[3],boxes[4],boxes[5],but);
                HBox hbox = new HBox(anchor);
                for (CuboidMesh mesh : boxes) {
                    mesh.setTextureModeImage(Objects.requireNonNull(getClass().getResource("dice.png")).toExternalForm());
                }

                for(int i = 0;i<boxes.length;i++){
                    boxes[i].setTranslateY(50+70*i);
                    boxes[i].setTranslateX(40);
                }

                Group root = new Group(hbox);

                Scene scene4 = new Scene(root, 600, 400, Color.GREEN);
                stage_true.setScene(scene4);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();
                for (CuboidMesh mesh :boxes){
                    mesh.setOnMouseClicked(mouseEvent -> lock_dice(mesh));
                }
                break;
            case 4:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + scene);
        }
    }
    public void start_button() throws IOException {
        scene_manager(2);

    }
    @FXML
    public void single_player() throws IOException {
        scene_manager(3);
    }





private void lock_dice(CuboidMesh mesh){
mesh.getId();
mesh.setVisible(false);
}


}