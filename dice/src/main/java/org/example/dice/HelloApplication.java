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
                CuboidMesh box1 = new CuboidMesh(50,50,50);
                CuboidMesh box2 = new CuboidMesh(50,50,50);
                CuboidMesh box3 = new CuboidMesh(50,50,50);
                CuboidMesh box4 = new CuboidMesh(50,50,50);
                CuboidMesh box5 = new CuboidMesh(50,50,50);
                CuboidMesh box6 = new CuboidMesh(50,50,50);
                CuboidMesh[] ar_box = {box1,box2,box3,box4,box5,box6};
                RotateTransition rb1x = new RotateTransition(Duration.millis(2000),box1);
                RotateTransition rb2x = new RotateTransition(Duration.millis(2000),box2);
                RotateTransition rb3x = new RotateTransition(Duration.millis(2000),box3);
                RotateTransition rb4x = new RotateTransition(Duration.millis(2000),box4);
                RotateTransition rb5x = new RotateTransition(Duration.millis(2000),box5);
                RotateTransition rb6x = new RotateTransition(Duration.millis(2000),box6);
                RotateTransition[] transitionR = {rb1x,rb2x,rb3x,rb4x,rb5x,rb6x};
                TranslateTransition tb1 = new TranslateTransition(Duration.millis(2000),box1);
                Button but = new Button();
                for (int i = 0; i < ar_box.length; i++) {
                    ar_box[i].setId(String.valueOf(i));
                }
                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {
                    HelloController.generate_values(ar_box,transitionR);
                    ParallelTransition transition = new ParallelTransition(rb1x,rb2x,rb3x,rb4x,rb5x,rb6x);
                    transition.play();
                    tb1.play();
                });
                AnchorPane anchor = new AnchorPane(box1,box2,box3,box4,box5,box6,but);
                HBox hbox = new HBox(anchor);
                for (CuboidMesh mesh : ar_box) {
                    mesh.setTextureModeImage(Objects.requireNonNull(getClass().getResource("dice.png")).toExternalForm());
                }

                for(int i = 0;i<ar_box.length;i++){
                    ar_box[i].setTranslateY(50+65*i);
                    ar_box[i].setTranslateX(30);
                }

                Group root = new Group(hbox);

                Scene scene4 = new Scene(root, 600, 400, Color.GREEN);
                stage_true.setScene(scene4);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();
                for (CuboidMesh mesh :ar_box){
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