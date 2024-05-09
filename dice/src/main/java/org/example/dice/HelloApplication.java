package org.example.dice;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class HelloApplication extends Application {

    public TextField players_playing;
    boolean against_bot;
    public int player_count = 0;
    public Label winner;
    private final GridPane naseptavac = new GridPane();
    private final StackPane root = new StackPane();
    public AnchorPane anchor = new AnchorPane();
    public static Button end_turn = new Button("end turn");
    public static Button but = new Button("roll");
    public static TranslateTransition[] transitionT = new TranslateTransition[6];
    public static RotateTransition[] transitionR = new RotateTransition[6];
    public String[] player_names = new String[10];
    public Text[] player_scores = new Text[10];
    public int[] total_score = new int[10];
    int size = 50;
    static Stage stage_true;
    static Text combination_text = new Text(" possible\n combinations:");
    public TextField singleplayer_name;
    private int remaining_cubes = 6;
    public RadioButton gamemode1;
    @FXML
    CheckBox assist;

    static CuboidMesh[] boxes = new CuboidMesh[6];

    public int offset_times = 0;
    public boolean can_roll = true;


    @Override
    public void start(Stage stage) throws IOException {
        stage_true = stage;
        scene_manager(1, 0);
    }

    public static void main(String[] args) {
        launch();
    }

    public void scene_manager(int scene, int player) throws IOException {
        write_winner();
        switch (scene) {
            case 1:
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start-screen.fxml"));
                Scene start_screen = new Scene(fxmlLoader.load(), 320, 240);
                stage_true.setScene(start_screen);
                stage_true.show();
                break;
            case 2:
                if (gamemode1.isSelected()) {
                    against_bot = true;
                } else {
                    against_bot = false;
                }
                if (against_bot) {
                    player_count = 2;
                } else {
                    player_count = Integer.parseInt(players_playing.getText());
                }
                Text[] texts = new Text[player_count];
                TextField[] textAreas = new TextField[player_count + 1];
                StackPane text_holder = new StackPane();
                VBox vbox = new VBox();
                for (int i = 0; i < player_count-1; i++) {
                    texts[i] = new Text();
                    textAreas[i] = new TextField();
                    textAreas[i].setId(String.valueOf(i));
                    texts[i].setText("player" + i);
                        VBox box = new VBox(texts[i],textAreas[i]);
                        box.setAlignment(Pos.TOP_CENTER);
                    vbox.getChildren().add(box);
                }
                VBox but_v = getvBox(textAreas);
                vbox.getChildren().add(but_v);
                but_v.setAlignment(Pos.BOTTOM_CENTER);
                text_holder.getChildren().addAll(vbox);
                Scene scene6 = new Scene(text_holder, 1500, 1000);
                stage_true.setScene(scene6);
                stage_true.show();
                break;
            case 4:
                player_names[1] = "bot";
                player_count = 2;
                assist_1 = assist.isSelected();
                for (int i = 0; i < 3; i++) {
                    total_score[i] = 0;
                    player_scores[i] = new Text();
                }
                against_bot = true;

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
                            if (locked_dice[i] == 0) {
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
                System.out.println(player_count);
                AnchorPane[] player_box = new AnchorPane[player_count];
                VBox players = new VBox();
                players.setMaxSize(60, 60);
                ScrollPane leader_board = new ScrollPane();
                leader_board.setMinSize(200, 200);
                leader_board.setMaxSize(2000, 200);
                leader_board.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
                leader_board.setTranslateX(800);
                leader_board.setTranslateY(0);
                leader_board.setStyle("-fx-background: grey; -fx-background-insets: 0; -fx-padding: 0;");
                leader_board.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));

                for (int i = 0; i < 2; i++) {
                    player_scores[i] = new Text();
                    player_scores[i].setText("player");
                    player_scores[i].setTranslateY(25);
                    player_scores[i].setTranslateX(5);
                    player_box[i] = new AnchorPane();
                    player_box[i].setMinSize(150, 50);
                    player_box[i].setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
                    player_box[i].getChildren().add(player_scores[i]);
                    players.getChildren().add(player_box[i]);
                }

                leader_board.setContent(players);

                anchor = new AnchorPane(boxes[0], boxes[1], boxes[2], boxes[3], boxes[4], boxes[5], but, end_turn);
                end_turn.setOnMouseClicked(mouseEvent -> {
                    try {
                        setEnd_turn(dice_p_arr);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });

                end_turn.setTranslateY(20);
                naseptavac.setMinSize(200, 200);
                anchor.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                naseptavac.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
                naseptavac.setTranslateX(800);
                naseptavac.setTranslateY(200);
                if (assist.isSelected()) {
                    naseptavac.getChildren().add(combination_text);
                }

                anchor.getChildren().addAll(naseptavac, leader_board);
                root.getChildren().add(anchor);

                Scene scene4 = new Scene(root, 1500, 1000);
                stage_true.setScene(scene4);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();

                for (CuboidMesh mesh : boxes) {
                    mesh.setOnMouseClicked(mouseEvent -> {
                        try {
                            disable_dice(mesh, 1);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                break;
            case 5:

                for (int i = 0; i < player_count; i++) {
                    total_score[i] = 0;
                    player_scores[i] = new Text();
                }

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
                            if (locked_dice[i] == 0) {
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
                System.out.println(player_count);
                player_box = new AnchorPane[player_count];
                players = new VBox();
                players.setMaxSize(60, 60);
                leader_board = new ScrollPane();
                leader_board.setMinSize(200, 200);
                leader_board.setMaxSize(2000, 200);
                leader_board.setTranslateX(800);
                leader_board.setTranslateY(0);
                leader_board.setStyle("-fx-background: grey; -fx-background-insets: 0; -fx-padding: 0;");

                for (int i = 0; i < player_count; i++) {
                    player_scores[i] = new Text();
                    player_scores[i].setText("player");
                    player_scores[i].setTranslateY(25);
                    player_scores[i].setTranslateX(5);
                    player_box[i] = new AnchorPane();
                    player_box[i].setMinSize(150, 50);
                    player_box[i].setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
                    player_box[i].getChildren().add(player_scores[i]);
                    players.getChildren().add(player_box[i]);
                }

                leader_board.setContent(players);

                anchor = new AnchorPane(boxes[0], boxes[1], boxes[2], boxes[3], boxes[4], boxes[5], but, end_turn);
                end_turn.setOnMouseClicked(mouseEvent -> {
                    try {
                        setEnd_turn(dice_p_arr);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

                end_turn.setTranslateY(20);
                naseptavac.setMinSize(200, 200);
                anchor.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                naseptavac.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
                naseptavac.setTranslateX(800);
                naseptavac.setTranslateY(200);
                if (assist.isSelected()) {
                    naseptavac.getChildren().add(combination_text);
                }

                anchor.getChildren().addAll(naseptavac, leader_board);
                root.getChildren().add(anchor);

                Scene scene5 = new Scene(root, 1500, 1000);
                stage_true.setScene(scene5);
                stage_true.setTitle("JavaFX 3D Example");
                stage_true.show();

                for (CuboidMesh mesh : boxes) {
                    mesh.setOnMouseClicked(mouseEvent -> {
                        try {
                            disable_dice(mesh, 1);
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
                winner.setText(player_names[player]);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + scene);
        }
    }

    private VBox getvBox(TextField[] textAreas) {
        Button name_but = new Button();
        name_but.setStyle("-fx-background:#b5b5b5;-fx-background-radius:10");
        name_but.setOnMouseClicked(mouseEvent -> {
            if (against_bot){
                player_names[0]= textAreas[0].getText();
                player_names[1] = "bot";
            }else {
                for (int i = 0; i < player_count; i++) {
                    player_names[i] = textAreas[i].getText();
                }
            }
                          if (against_bot) {
                              try {
                                  single_player();
                              } catch (IOException e) {
                                  throw new RuntimeException(e);
                              }
                          }else {
                              try {
                                  multi_player();
                              } catch (IOException e) {
                                  throw new RuntimeException(e);
                              }
                          }
        });
        name_but.setText("start game");
        VBox but_v = new VBox(name_but);
        return but_v;
    }

    public void start_button() throws IOException {
        scene_manager(2, 0);
    }

    @FXML
    public void single_player() throws IOException {
        scene_manager(4, 0);
    }

    public void multi_player() throws IOException {
        scene_manager(5, 0);
    }

    public void start_game() throws IOException {
        scene_manager(4, 0);
    }

    public void end_game(int player) throws IOException {
        scene_manager(6, player);
    }


    private void write_winner() throws IOException {
        FileWriter fw = new FileWriter("leaderboard.csv", true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Spain");
        bw.newLine();
        bw.close();
    }

    private void disable_dice(CuboidMesh mesh, int lock_value) throws IOException, InterruptedException {
        int cube = Integer.parseInt(mesh.getId());
        if (locked_dice[cube] == 0) {
            can_roll = true;
            remaining_cubes -= 1;
            offset_times += 1;
            mesh.setTranslateX(50 + 50 * offset_times);
            mesh.setTranslateY(600);
            lock_dice(cube, lock_value);
        } else if (locked_dice[cube] == 1) {
            remaining_cubes += 1;
            offset_times -= 1;
            locked_dice[cube] = 0;
            mesh.setTranslateY(90 + 70 * cube);
            mesh.setTranslateX(40);
            dice_p_arr[results[cube] - 1] -= 1;
        }
        System.out.println(remaining_cubes);
    }

    public void show_total_score() throws IOException {

        for (int i = 0; i < player_count; i++) {
            if (total_score[i] > 10000) {
                end_game(i);
            }
            player_scores[i].setText(player_names[i] + "  total score: " + total_score[i]);
        }
    }

    public void change_combination_text(String s) {
        combination_text.setText(s);
    }

    public void reset_cubes() throws IOException, InterruptedException {
        remaining_cubes = 6;
        for (int i = 0; i < boxes.length; i++) {
            locked_dice[i] = 0;
            boxes[i].setTranslateY(90 + 70 * i);
            boxes[i].setTranslateX(40);
            offset_times = 0;
            for (CuboidMesh box : boxes) {
                box.setDisable(false);
                for (int h : locked_dice) {
                    h = 0;
                }
            }

        }

        if (player == 1 && against_bot) {
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
                if (locked_dice[i] == 0) {
                    transitionR[i].play();
                    transitionT[i].play();
                }
            }
            rot.play();
            setEnd_turn(dice_values);
            player = 0;
        }
    }

    public int[] dice_values = new int[6];
    public int[] results = new int[6];
    public int player = 0;
    public int[] dice_p_arr = new int[6];
    public int[] locked_dice = new int[6];
    public boolean assist_1;
    public int turn_score = 0;
    public boolean has_remaining_values;

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

    public int[] positionsY = new int[6];
    public int[] positionsX = new int[6];

    public void generate_values(CuboidMesh[] ar_box, RotateTransition[] transitionRX, TranslateTransition[] transitionT, boolean assist) throws IOException, InterruptedException {
        assist_1 = assist;
        dice_values = new int[6];
        for (int i = 0; i < ar_box.length; i++) {
            if (locked_dice[i] == 0) {
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
            if (locked_dice[i] == 0) {
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

            if (locked_dice[i] == 0) {

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
                int randomY = random.nextInt(500) + 30;
                int randomX = random.nextInt(600) + 30;
                boolean is_overlaping = true;
                while (is_overlaping) {
                    is_overlaping = false;
                    for (int j = 0; j < 6; j++) {
                        if (positionsY[j] + 50 < randomY + 50 && positionsY[j] + 50 > randomY - 50 ||
                                positionsY[j] - 50 > randomY - 50 && positionsY[j] - 50 < randomY + 50 &&
                                        positionsX[j] + 50 < randomX + 50 && positionsX[j] + 50 > randomX - 50 ||
                                positionsX[j] - 50 > randomX - 50 && positionsX[j] - 50 < randomX + 50) {
                            randomY = random.nextInt(500) + 30;
                            randomX = random.nextInt(600) + 30;
                            is_overlaping = true;
                        }
                    }
                }

                positionsY[i] = randomY;
                positionsX[i] = randomX;
                transitionT[i].setToY(randomY);
                transitionT[i].setToX(randomX);

            }
        }

        for (int value : dice_values) {
            System.out.println(value);
        }
        print_combination(dice_values, false, true);
        //evaluate_trow(dice_values);


    }

    public static int tmp_score;

    private void evaluate_throw(int[] dice_values) {
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
            dice_values[0] = 0;
        }
        //50 points
        if (dice_values[4] > 0) {
            tmp_score += 50 * dice_values[4];
            dice_values[4] = 0;
        }
        for (int value : dice_values) {
            if (value != 0) {
                has_remaining_values = true;
                break;
            }else has_remaining_values = false;
        }
        System.out.println("final score: " + tmp_score);

    }

    private void print_combination(int[] dice_values, boolean determines_roll, boolean end_turn) throws InterruptedException {
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

        if (assist_1) {
            change_combination_text(combinations.toString());
        }

    }

    public void setEnd_turn(int[] array) throws IOException, InterruptedException {
        evaluate_throw(array);
        if (!has_remaining_values) {
            total_score[player] += tmp_score;

            if (tmp_score == 0) {
                total_score[player] -= turn_score;
            }
            turn_score += tmp_score;
            show_total_score();

            if (remaining_cubes != 0) {
                if (against_bot) {
                    if (player == 0) {
                        player = 1;
                    } else player = 0;
                } else {
                    if (player < player_count - 1) {
                        player += 1;
                    } else {
                        player = 0;
                    }
                }
            }
            turn_score = 0;
            tmp_score = 0;
            reset_cubes();
        } else System.out.println("has remaining values");

    }

    public void tmp_dice_value() throws IOException {
        evaluate_throw(dice_p_arr);
        if (has_remaining_values) {
            total_score[player] += tmp_score;
            turn_score += tmp_score;
            show_total_score();

        }

    }

    public void lock_dice(int i, int disable_value) throws IOException, InterruptedException {
        locked_dice[i] = disable_value;
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

    public void point_loss(int points) {
        total_score[player] -= points;
    }
}