package org.example.dice;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

public class HelloApplication extends Application {
    public int player_count;
    public Label winner;
    private final GridPane score_counter = new GridPane();
    private final StackPane root = new StackPane();
    public AnchorPane anchor = new AnchorPane();
    public static Button end_turn = new Button("end turn");
    public static Button but = new Button("roll");
    public static TranslateTransition[] transitionT = new TranslateTransition[6];
    public static RotateTransition[] transitionR = new RotateTransition[6];
    int size = 50;
    static Stage stage_true;
    static Text combination_text = new Text(" possible\n combinations:");
    @FXML
    CheckBox assist;
    static CuboidMesh[] boxes = new CuboidMesh[6];
    static Text score_text_1 = new Text();
    static Text score_text_2 = new Text();
    public static int offset_times = 0;
    public static boolean can_roll = true;

    @Override
    public void start(Stage stage) throws IOException {
        stage_true = stage;
        scene_manager(1,0);
    }

    public static void main(String[] args) {
        launch();
    }

    public void scene_manager(int scene,int player) throws IOException {
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

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i] = new CuboidMesh(size, size, size);
                }
                transitionR = new RotateTransition[6];
                for (int i = 0; i < transitionR.length; i++) {
                    transitionR[i] = new RotateTransition(Duration.millis(2000), boxes[i]);
                }
                transitionT = new TranslateTransition[6];
                for (int i = 0; i < transitionT.length; i++) {
                    transitionT[i] = new TranslateTransition(Duration.millis(2000), boxes[i]);
                }

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setId(String.valueOf(i));
                }

                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {
                    if (can_roll) {
                        can_roll = false;
                        try {
                            generate_values(boxes, transitionR, transitionT, assist.isSelected());
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        for (int i = 0; i < 6; i++) {
                            if (!locked_dice[i]) {
                                transitionR[i].play();
                                transitionT[i].play();
                            }
                        }

                        MediaPlayer mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("stone.wav")).toString()));
                        mediaPlayer.isAutoPlay();
                        mediaPlayer.play();
                        try {
                            tmp_dice_value();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
                anchor = new AnchorPane(boxes[0], boxes[1], boxes[2], boxes[3], boxes[4], boxes[5], but, end_turn);
                end_turn.setOnMouseClicked(mouseEvent -> {
                    try {
                        setEnd_turn(dice_p_arr);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                end_turn.setTranslateY(20);
                score_text_1.setText("total score: 0");
                score_text_1.setStyle("-fx-font:15 arial;");
                score_text_2.setText("total score: 0");
                score_text_2.setStyle("-fx-font:15 arial;");
                score_text_2.setTranslateX(110);
                score_counter.getChildren().addAll(score_text_1, score_text_2, combination_text);

                score_counter.setMinSize(350, 400);
                anchor.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                score_counter.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
                score_counter.setTranslateX(800);
                score_counter.setTranslateY(50);

                anchor.getChildren().add(score_counter);
                root.getChildren().add(anchor);

                Scene scene4 = new Scene(root, 1500, 1000);
                stage_true.setScene(scene4);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();
                for (CuboidMesh mesh : boxes) {
                    mesh.setOnMouseClicked(mouseEvent -> {
                        try {
                            lock_dice(mesh);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                break;
            case 5:


                for (int i = 0; i < boxes.length; i++) {
                    boxes[i] = new CuboidMesh(size, size, size);
                }
                transitionR = new RotateTransition[6];
                for (int i = 0; i < transitionR.length; i++) {
                    transitionR[i] = new RotateTransition(Duration.millis(2000), boxes[i]);
                }
                transitionT = new TranslateTransition[6];
                for (int i = 0; i < transitionT.length; i++) {
                    transitionT[i] = new TranslateTransition(Duration.millis(2000), boxes[i]);
                }

                for (int i = 0; i < boxes.length; i++) {
                    boxes[i].setId(String.valueOf(i));
                }

                but.setText("roll");
                but.setOnMouseClicked(mouseEvent -> {
                    if (can_roll) {
                        can_roll = false;
                        try {
                            generate_values(boxes, transitionR, transitionT, assist.isSelected());
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        for (int i = 0; i < 6; i++) {
                            if (!locked_dice[i]) {
                                transitionR[i].play();
                                transitionT[i].play();
                            }
                        }

                        MediaPlayer mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("stone.wav")).toString()));
                        mediaPlayer.isAutoPlay();
                        mediaPlayer.play();
                        try {
                            tmp_dice_value();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
                anchor = new AnchorPane(boxes[0], boxes[1], boxes[2], boxes[3], boxes[4], boxes[5], but, end_turn);
                end_turn.setOnMouseClicked(mouseEvent -> {
                    try {
                        setEnd_turn(dice_p_arr);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                end_turn.setTranslateY(20);
                score_text_1.setText("total score: 0");
                score_text_1.setStyle("-fx-font:15 arial;");
                score_text_2.setText("total score: 0");
                score_text_2.setStyle("-fx-font:15 arial;");
                score_text_2.setTranslateX(110);
                score_counter.getChildren().addAll(score_text_1, score_text_2, combination_text);

                score_counter.setMinSize(350, 400);
                anchor.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                score_counter.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
                score_counter.setTranslateX(800);
                score_counter.setTranslateY(50);

                anchor.getChildren().add(score_counter);
                root.getChildren().add(anchor);

                Scene scene5 = new Scene(root, 1500, 1000);
                stage_true.setScene(scene5);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();
                for (CuboidMesh mesh : boxes) {
                    mesh.setOnMouseClicked(mouseEvent -> {
                        try {
                            lock_dice(mesh);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                break;
            case 6:
                FXMLLoader fxmlLoader3 = new FXMLLoader(HelloApplication.class.getResource("end-screen.fxml"));
                Scene end_screen;
                end_screen = new Scene(fxmlLoader3.load(), 320, 240);
                stage_true.setScene(end_screen);
                stage_true.show();
                winner.setText("winner is player" + player);
                break;
            case 7:
                FXMLLoader fxmlLoader4 = new FXMLLoader(HelloApplication.class.getResource("option-multiplayer.fxml"));
                Scene multiplayer_options = new Scene(fxmlLoader4.load(), 320, 240);
                stage_true.setScene(multiplayer_options);
                stage_true.show();

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + scene);
        }
    }

    public void start_button() throws IOException {
        scene_manager(2,0);

    }
    public void set_player_count(){
        player_count = 10;
    }
    @FXML
    public void single_player() throws IOException {
        scene_manager(3,0);
    }

    public void multi_player() throws IOException {
        scene_manager(5,0);
    }

    public void start_game() throws IOException {
        scene_manager(4,0);
    }
    public void end_game(int player) throws  IOException{
        scene_manager(6,player);
    }
    public void multi_player_options() throws IOException {
        scene_manager(7,0);
    }

    private void lock_dice(CuboidMesh mesh) throws IOException, InterruptedException {
        offset_times += 1;
        mesh.setDisable(true);
        mesh.setTranslateX(50 + 50 * offset_times);
        mesh.setTranslateY(600);
        lock_dice(Integer.parseInt(mesh.getId()));
    }

    public void show_total_score(int value_1, int value_2) throws IOException {
        if(value_1>10000){
            end_game(1);
        } else if (value_2>10000) {
            end_game(2);
        }
        score_text_1.setText("total Score: " + value_1);
        score_text_2.setText("total Score: " + value_2);
    }

    public void change_combination_text(String s) {
        combination_text.setText(s);
    }

    public void reset_cubes() throws IOException, InterruptedException {
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setTranslateY(90 + 70 * i);
            boxes[i].setTranslateX(40);
            offset_times = 0;
            for (CuboidMesh box : boxes) {
                box.setDisable(false);
                locked_dice = new boolean[6];
            }

        }

        if (player == 2){
            generate_values(boxes, transitionR, transitionT, assist.isSelected());
            RotateTransition rot = new RotateTransition();
            rot.setDuration(Duration.millis(2000));
            rot.setNode(boxes[1]);
            rot.setOnFinished(actionEvent -> {
                try {
                    reset_cubes();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        for (int i = 0; i < 6; i++) {
            if (!locked_dice[i]) {
                transitionR[i].play();
                transitionT[i].play();
            }
        }
rot.play();
       setEnd_turn(dice_values);
        }
    }

    public  int[] dice_values = new int[6];
    public  int[] results = new int[6];
    public  int total_score_1 = 0;
    public  int total_score_2 = 0;
    public  int player = 1;
    public  int[] dice_p_arr = new int[6];
    public  boolean[] locked_dice = new boolean[6];
    public  boolean assist_1;
    public  int turn_score = 0;

    public static void matrixRotateNode(RotateTransition n, double alf, double bet, double gam) {
        Random random = new Random();

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
        n.setByAngle(360 * (1 + random.nextInt(5)) + Math.toDegrees(d));
    }

    public void generate_values(CuboidMesh[] ar_box, RotateTransition[] transitionRX, TranslateTransition[] transitionT, boolean assist) throws IOException, InterruptedException {
        assist_1 = assist;
        dice_values = new int[6];
        for (int i = 0; i < ar_box.length; i++) {
            if (!locked_dice[i]) {
                ar_box[i].setRotationAxis(Rotate.Y_AXIS);
                ar_box[i].setRotate(0);
                ar_box[i].setRotationAxis(Rotate.Z_AXIS);
                ar_box[i].setRotate(0);
                ar_box[i].setRotationAxis(Rotate.X_AXIS);
                ar_box[i].setRotate(0);
            }
        }
        results = new int[6];
        for (int i = 0; i < 6; i++) {
            if (!locked_dice[i]) {
                Random random = new Random();
                int random1 = 1 + random.nextInt((6 - 1) + 1);
                results[i] = random1;
                //System.out.println(results[i]);
                switch (random1) {
                    case 1:
                        matrixRotateNode(transitionRX[i], Math.toRadians(0), Math.toRadians(180), Math.toRadians(270));
                        break;
                    case 2:
                        matrixRotateNode(transitionRX[i], Math.toRadians(0), Math.toRadians(90), Math.toRadians(0));
                        break;
                    case 3:
                        matrixRotateNode(transitionRX[i], Math.toRadians(0), Math.toRadians(0), Math.toRadians(180));
                        break;
                    case 4:
                        //matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(360),Math.toRadians(360));
                        transitionRX[i].setAxis(Point3D.ZERO.add(0, 1, 1));
                        transitionRX[i].setByAngle(360 * (1 + random.nextInt(5)));
                        break;
                    case 5:
                        matrixRotateNode(transitionRX[i], Math.toRadians(0), Math.toRadians(270), Math.toRadians(0));
                        break;
                    case 6:
                        matrixRotateNode(transitionRX[i], Math.toRadians(0), Math.toRadians(180), Math.toRadians(90));
                        break;
                }
            }
        }

        for (int i = 0; i < results.length; i++) {
            if (!locked_dice[i]) {

                Random random = new Random();
                switch (results[i]) {
                    case 1:
                        dice_values[0] += 1;
                        break;
                    case 2:
                        dice_values[1] += 1;
                        break;
                    case 3:
                        dice_values[2] += 1;
                        break;
                    case 4:
                        dice_values[3] += 1;
                        break;
                    case 5:
                        dice_values[4] += 1;
                        break;
                    case 6:
                        dice_values[5] += 1;
                        break;
                }
                transitionT[i].setToY(random.nextInt(500));
                transitionT[i].setToX(random.nextInt(600));
            }
        }

        for (int value : dice_values) {
            System.out.println(value);
        }
        print_combination(dice_values, false, true);
        //evaluate_trow(dice_values);


    }

    public static int tmp_score;

    private static void evaluate_trow(int[] dice_values) {
        tmp_score = 0;
        int doubles = 0;
        int[] doubles_position = new int[6];
        //3000 points
        if (dice_values[0] == 1 && dice_values[1] == 1 && dice_values[2] == 1 && dice_values[3] == 1 && dice_values[4] == 1 && dice_values[5] == 1) {
            tmp_score += 3000;
            for (int value : dice_values) {
                value = 0;
            }
        }
        //counts double values
        for (int i = 0; i < dice_values.length; i++) {
            if (dice_values[i] >= 2) {
                doubles += 1;
                doubles_position[i] += 1;
            }
        }
        //1500 points
        if (doubles == 3) {
            tmp_score += 1500;
            for (int i = 0; i < doubles_position.length; i++) {
                dice_values[i] -= 2;
                doubles_position[i] -= 2;
            }
        }
        if (dice_values[0] == 3) {
            tmp_score += 1000;
            dice_values[0] -= 3;
        }
        //100*dice points
        for (int i = 0; i < dice_values.length; i++) {
            if (i != 0) {
                if (dice_values[i] == 6) {
                    tmp_score += 200 * (i + 1);
                    dice_values[i] = 0;
                } else if (dice_values[i] >= 3) {
                    tmp_score += 100 * (i + 1);
                    dice_values[i] -= 3;
                }
            }
        }
        //100
        if (dice_values[0] > 0) {
            tmp_score += 100 * dice_values[0];
        }
        //50 points
        if (dice_values[4] > 0) {
            tmp_score += 50 * dice_values[4];
        }

        if (tmp_score == 0) {
            HelloApplication.can_roll = false;
        } else {
            HelloApplication.can_roll = true;
        }
        System.out.println("final score: " + tmp_score);

    }

    private void print_combination(int[] dice_values, boolean determines_roll, boolean end_turn) throws IOException, InterruptedException {
        StringBuilder combinations = new StringBuilder();
        int doubles = 0;
        int[] doubles_position = new int[6];
        //3000 points
        if (dice_values[0] == 1 && dice_values[1] == 1 && dice_values[2] == 1 && dice_values[3] == 1 && dice_values[4] == 1 && dice_values[5] == 1) {
            combinations.append("1 + 2 + 3 + 4 + 5 + 6: 3000\n");
        }
        //counts double values
        for (int i = 0; i < dice_values.length; i++) {
            if (dice_values[i] >= 2) {
                doubles += 1;
                doubles_position[i] += 1;
            }
        }
        //1500 points
        if (doubles == 3) {
            combinations.append("3 doubles: 1500\n");

        }
        if (dice_values[0] == 3) {
            combinations.append("1 + 1 + 1: 1000\n");
        }
        //100*dice points
        for (int i = 0; i < dice_values.length; i++) {
            if (i != 0) {
                if (dice_values[i] == 6) {
                    combinations.append("2 three numbers:").append(200 * (i + 1)).append("\n");
                } else if (dice_values[i] >= 3) {
                    combinations.append("three numbers:").append(100 * (i + 1)).append("\n");

                }
            }
        }
        //100
        if (dice_values[0] > 0) {
            combinations.append("number 1:").append(100 * dice_values[0]).append("\n");
        }
        //50 points
        if (dice_values[4] > 0) {
            combinations.append("number 5:").append(50 * dice_values[4]).append("\n");
        }

        if (assist_1 && !determines_roll) {
            change_combination_text(combinations.toString());
        }
        if (determines_roll && combinations.toString() != "") {
            can_roll = true;
        } else {
            can_roll = false;
        }
        if (!determines_roll && combinations.toString() == "" && end_turn) {
            setEnd_turn(dice_p_arr);
        }

    }

    public void setEnd_turn(int[] array) throws IOException, InterruptedException {
        evaluate_trow(array);
        if (player == 1) {
            total_score_1 += tmp_score;
        } else {
            total_score_2 += tmp_score;
        }
        if (tmp_score == 0){
            point_loss(turn_score);
        }
        turn_score += tmp_score;
        show_total_score(total_score_1, total_score_2);
        dice_p_arr = new int[6];

        if (player == 1) {
            player = 2;
        } else player = 1;
        turn_score = 0;
        reset_cubes();
        HelloApplication.can_roll = true;
    }

    public void tmp_dice_value() throws IOException {
        evaluate_trow(dice_p_arr);
        if (player == 1) {
            total_score_1 += tmp_score;
        } else {
            total_score_2 += tmp_score;
        }
        turn_score += tmp_score;
        show_total_score(total_score_1, total_score_2);
        dice_p_arr = new int[6];
    }

    public void lock_dice(int i) throws IOException, InterruptedException {
        locked_dice[i] = true;
        switch (results[i]) {
            case 1:
                dice_p_arr[0] += 1;
                break;
            case 2:
                dice_p_arr[1] += 1;
                break;
            case 3:
                dice_p_arr[2] += 1;
                break;
            case 4:
                dice_p_arr[3] += 1;
                break;
            case 5:
                dice_p_arr[4] += 1;
                break;
            case 6:
                dice_p_arr[5] += 1;
                break;
        }

        for (int value1 : dice_p_arr) {
            System.out.println(value1);
        }
        print_combination(dice_p_arr, true, false);

    }
    public void point_loss(int points){
        if (player == 1){
            total_score_1 -= points;
        }else {
            total_score_2 -= points;
        }
    }

}