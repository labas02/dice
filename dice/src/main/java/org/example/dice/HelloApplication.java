package org.example.dice;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    static Stage stage_true;
    static Text combination_text = new Text(" possible\n combinations:");
    @FXML
    CheckBox assist;
    static CuboidMesh[] boxes = new CuboidMesh[6];
    static Text score_text = new Text();
    public static int offset_times = 0;
    public static boolean can_roll = true;

    @Override
    public void start(Stage stage) throws IOException {
        stage_true = stage;
        scene_manager(1);
    }

    public static void main(String[] args) {
        launch();
    }

    public void scene_manager(int scene) throws IOException {
        switch (scene) {
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
                Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResource("background.png")).toExternalForm(), 1000, 2000, true, true);
                ImageView backgroundImageView = new ImageView(backgroundImage);
                backgroundImageView.setSmooth(true);
                backgroundImageView.setFitWidth(1920);
                backgroundImageView.setFitHeight(1080);
                int size = 50;

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i] = new CuboidMesh(size, size, size);
                }
                RotateTransition[] transitionR = new RotateTransition[6];
                for (int i = 0; i < transitionR.length; i++) {
                    transitionR[i] = new RotateTransition(Duration.millis(2000), boxes[i]);
                }
                TranslateTransition[] transitionT = new TranslateTransition[6];
                for (int i = 0; i < transitionT.length; i++) {
                    transitionT[i] = new TranslateTransition(Duration.millis(2000), boxes[i]);
                }

                TranslateTransition tb1 = new TranslateTransition(Duration.millis(2000), boxes[1]);
                Button but = new Button();

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setId(String.valueOf(i));
                }

                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {
                    if (can_roll) {
                        can_roll = false;
                        HelloController.generate_values(boxes, transitionR, transitionT, assist.isSelected());
                        for (int i = 0; i < 6; i++) {
                            if (HelloController.locked_dice[i] == false) {
                                transitionR[i].play();
                                transitionT[i].play();
                            }
                        }

                        MediaPlayer mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("stone.wav")).toString()));
                        mediaPlayer.isAutoPlay();
                        mediaPlayer.play();
                        HelloController.tmp_dice_value();
                    }
                });

                for (CuboidMesh mesh : boxes) {
                    mesh.setTextureModeImage(Objects.requireNonNull(getClass().getResource("dice.png")).toExternalForm());
                }

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setTranslateY(90 + 70 * i);
                    boxes[i].setTranslateX(40);
                }


                combination_text.setTranslateX(20);
                combination_text.setTranslateY(50);
                AnchorPane anchor = new AnchorPane(boxes[0], boxes[1], boxes[2], boxes[3], boxes[4], boxes[5], but);
                Button end_turn = new Button("end turn");
                end_turn.setOnMouseClicked(mouseEvent -> HelloController.setEnd_turn());
                end_turn.setTranslateY(20);
                score_text.setText("total score: 0");
                score_text.setStyle("-fx-font:25 arial;");
                StackPane root = new StackPane();
                GridPane score_counter = new GridPane();
                score_counter.getChildren().addAll(score_text, combination_text, end_turn);

                score_counter.setMaxSize(250, 300);
                score_counter.setMinSize(200, 200);
                anchor.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                score_counter.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
                score_counter.setTranslateX(800);
                score_counter.setTranslateY(50);

                anchor.getChildren().add(score_counter);
                root.getChildren().add(anchor);

                Scene scene4 = new Scene(root, 1200, 800);
                stage_true.setScene(scene4);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();
                for (CuboidMesh mesh : boxes) {
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

    private void lock_dice(CuboidMesh mesh) {
        offset_times += 1;
        mesh.setDisable(true);
        mesh.setTranslateX(50 + 50 * offset_times);
        mesh.setTranslateY(600);
        HelloController.lock_dice(Integer.parseInt(mesh.getId()), 1);
    }

    public static void show_total_score(int value) {
        score_text.setText("total Score: " + value);
    }

    public static void change_combination_text(String s) {
        combination_text.setText(s);
    }

    public static void reset_cubes() {
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setTranslateY(90 + 70 * i);
            boxes[i].setTranslateX(40);
            offset_times = 0;
            for (CuboidMesh box : boxes) {
                box.setDisable(false);
                HelloController.locked_dice = new boolean[6];
            }

        }
    }
}