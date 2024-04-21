package org.example.dice;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.io.IOException;


public class HelloApplication extends Application {
    static Stage stage_true;
    CuboidMesh box1 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box2 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box3 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box4 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box5 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box6 = new CuboidMesh(50f,50f,50f);
    RotateTransition tb1 = new RotateTransition(Duration.millis(2000),box1);
    @Override
    public void start(Stage stage) throws IOException {
        stage_true = stage;
        scene_manager(1);
    }
public Box dice1;
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
                Button but = new Button();
                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {
                    generate_transition();
                });
                HBox hbox = new HBox(box1,box2,box3,box4,box5,box6,but);
                box1.setTextureModeImage(getClass().getResource("pic.png").toExternalForm());
                box2.setTextureModeImage(getClass().getResource("pic.png").toExternalForm());
                box3.setTextureModeImage(getClass().getResource("pic.png").toExternalForm());
                box4.setTextureModeImage(getClass().getResource("pic.png").toExternalForm());
                box5.setTextureModeImage(getClass().getResource("pic.png").toExternalForm());
                box6.setTextureModeImage(getClass().getResource("pic.png").toExternalForm());

                Group root = new Group(hbox);

                Scene scene4 = new Scene(root, 600, 400, Color.GREEN);
                stage_true.setScene(scene4);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();
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

    public void generate_transition(){
        tb1.setNode(box1);
        tb1.setDuration(Duration.millis(2000));
        tb1.setAxis(Point3D.ZERO.add(0,1,0));
        tb1.setByAngle(120);
        tb1.play();
    }

}