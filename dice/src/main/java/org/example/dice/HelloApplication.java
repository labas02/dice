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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.shapes.primitives.CuboidMesh;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class HelloApplication extends Application {
    static Stage stage_true;
    @FXML
    CheckBox assist;

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
                FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("options-singleplayer.fxml"));
                Scene select_options;
                select_options = new Scene(fxmlLoader2.load(), 320, 240);
                stage_true.setScene(select_options);
                stage_true.show();
            break;
                case 4:
                Image backgroundImage = new Image(getClass().getResource("background.png").toExternalForm(),1000,2000,true,true);
                ImageView backgroundImageView = new ImageView(backgroundImage);

                backgroundImageView.setSmooth(true);
                backgroundImageView.setFitWidth(1920);
                backgroundImageView.setFitHeight(1080);
                CuboidMesh[] boxes = new CuboidMesh[6];
                int size = 50;

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i] = new CuboidMesh(size, size, size);
                }
                RotateTransition[] transitionR = new RotateTransition[6];
                for (int i = 0;i<transitionR.length;i++){
                    transitionR[i] =new RotateTransition(Duration.millis(2000),boxes[i]);
                }
                TranslateTransition[] transitionT =new TranslateTransition[6];
                for (int i = 0; i < transitionT.length; i++) {
                    transitionT[i] = new TranslateTransition(Duration.millis(2000),boxes[i]);
                }

                TranslateTransition tb1 = new TranslateTransition(Duration.millis(2000),boxes[1]);
                Button but = new Button();

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setId(String.valueOf(i));
                }

                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {

                    HelloController.generate_values(boxes,transitionR,transitionT,assist.isSelected());
                    ParallelTransition transition = new ParallelTransition(transitionR[0],transitionR[1],transitionR[2],transitionR[3],transitionR[4],transitionR[5],transitionT[0],transitionT[1],transitionT[2],transitionT[3],transitionT[4],transitionT[5]);
                    transition.play();
                    MediaPlayer mediaPlayer = new MediaPlayer(new Media(getClass().getResource("stone.wav").toString()));
                    mediaPlayer.isAutoPlay();
                    mediaPlayer.play();
                });
                AnchorPane anchor = new AnchorPane(boxes[0],boxes[1],boxes[2],boxes[3],boxes[4],boxes[5],but);

                HBox hbox = new HBox(anchor);
                for (CuboidMesh mesh : boxes) {
                    mesh.setTextureModeImage(Objects.requireNonNull(getClass().getResource("dice.png")).toExternalForm());
                }

                for(int i = 0;i<boxes.length;i++){
                    boxes[i].setTranslateY(90+70*i);
                    boxes[i].setTranslateX(40);
                }

                StackPane root = new StackPane();
                GridPane score_counter = new GridPane();
                score_counter.setMinSize(100,100);
                score_counter.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
                score_counter.setTranslateX(500);
                score_counter.setTranslateY(50);
                root.getChildren().add(backgroundImageView);
                root.getChildren().add(hbox);
                anchor.getChildren().add(score_counter);
                Scene scene4 = new Scene(root, 1000, 1000);
                stage_true.setScene(scene4);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();
                for (CuboidMesh mesh :boxes){
                    mesh.setOnMouseClicked(mouseEvent -> lock_dice(mesh));
                }
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

    public void start_game() throws IOException {
        scene_manager(4);
    }

private void lock_dice(CuboidMesh mesh){
mesh.setVisible(false);
}


}