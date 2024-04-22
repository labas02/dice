package org.example.dice;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;


public class HelloApplication extends Application {
    static Stage stage_true;
    CuboidMesh box1 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box2 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box3 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box4 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box5 = new CuboidMesh(50f,50f,50f);
    CuboidMesh box6 = new CuboidMesh(50f,50f,50f);
    CuboidMesh[] ar_box = {box1,box2,box3,box4,box5,box6};
    RotateTransition rb1x = new RotateTransition(Duration.millis(2000),box1);
    RotateTransition rb2x = new RotateTransition(Duration.millis(2000),box2);
    RotateTransition rb3x = new RotateTransition(Duration.millis(2000),box3);
    RotateTransition rb4x = new RotateTransition(Duration.millis(2000),box4);
    RotateTransition rb5x = new RotateTransition(Duration.millis(2000),box5);
    RotateTransition rb6x = new RotateTransition(Duration.millis(2000),box6);

    RotateTransition[] transitionRX = {rb1x,rb2x,rb3x,rb4x,rb5x,rb6x};


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
                Button but = new Button();
                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {
                    generate_values();
                    ParallelTransition transition = new ParallelTransition(rb1x,rb2x,rb3x,rb4x,rb5x,rb6x);
                    transition.play();

                });
                AnchorPane anchor = new AnchorPane(box1,box2,box3,box4,box5,box6,but);
                HBox hbox = new HBox(anchor);
                for (CuboidMesh mesh : ar_box) {
                    mesh.setTextureModeImage(Objects.requireNonNull(getClass().getResource("pic.png")).toExternalForm());
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

public void generate_values(){
    for (CuboidMesh mesh : ar_box){
        mesh.setRotationAxis(Rotate.Y_AXIS);
        mesh.setRotate(0);
        mesh.setRotationAxis(Rotate.Z_AXIS);
        mesh.setRotate(0);
        mesh.setRotationAxis(Rotate.X_AXIS);
        mesh.setRotate(0);
    }
    for (int i = 0; i < 6; i++) {

        Random random = new Random();
        int random1 = 1+random.nextInt(6);
        System.out.println(random1);

        switch(random1) {
            case 1:
 matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(180),Math.toRadians(270));

                break;
            case 2:
matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(90),Math.toRadians(0));

                break;
            case 3:
matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(0),Math.toRadians(180));

                break;
            case 4:
                matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(180),Math.toRadians(180));
                break;
            case 5:
matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(270),Math.toRadians(0));

                break;
            case 6:
  matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(180),Math.toRadians(90));
                break;
        }



        }


    }

    private void matrixRotateNode(RotateTransition n, double alf, double bet, double gam) {
        double A11 = Math.cos(alf) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alf);
        double A22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        double den = 2d * Math.sin(d);
        Point3D p = new Point3D((A32 - A23) / den, (A13 - A31) / den, (A21 - A12) / den);
        n.setAxis(p);
        Random random = new Random();
        n.setByAngle((360 * (random.nextInt(10)+4))+Math.toDegrees(d));
    }




}