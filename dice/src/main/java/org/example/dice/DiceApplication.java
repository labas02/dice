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

import java.io.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

public class DiceApplication extends Application {

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
    public String[] player_names = new String[99];
    public Text[] player_scores = new Text[99];
    public int[] total_score = new int[99];
    int size = 50;
    static Stage stage_true;
    static Text combination_text = new Text(" possible\n combinations:");
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
        scene_manager(1);
    }

    public static void main(String[] args) {
        launch();
    }

    public void scene_manager(int scene) throws IOException {
        switch (scene) {
            case 1:
                FXMLLoader fxmlLoader = new FXMLLoader(DiceApplication.class.getResource("start-screen.fxml"));
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
                if (!against_bot) {
                    for (int i = 0; i < player_count; i++) {
                        texts[i] = new Text();
                        textAreas[i] = new TextField();
                        textAreas[i].setId(String.valueOf(i));
                        texts[i].setText("player" + i);
                        VBox box = new VBox(texts[i], textAreas[i]);
                        box.setAlignment(Pos.TOP_CENTER);
                        vbox.getChildren().add(box);
                    }
                }else for (int i = 0; i < 2; i++) {
                    texts[i] = new Text();
                    textAreas[i] = new TextField();
                    textAreas[i].setId(String.valueOf(i));
                    texts[i].setText("player" + i);
                    VBox box = new VBox(texts[i], textAreas[i]);
                    box.setAlignment(Pos.TOP_CENTER);
                    vbox.getChildren().add(box);
                }
                VBox but_v = getvBox(textAreas);
                vbox.getChildren().add(but_v);
                but_v.setAlignment(Pos.BOTTOM_CENTER);
                text_holder.getChildren().addAll(vbox);
                Scene scene6 = new Scene(text_holder);
                stage_true.setScene(scene6);
                stage_true.show();
                break;
            case 3:

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
                    if (can_roll && remaining_cubes != 0) {
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
                    evaluate_throw(dice_values,2);
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
                        setEnd_turn();
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
                            disable_dice(mesh);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                break;
            case 5:
                assist_1 = assist.isSelected();
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
                    if (can_roll && remaining_cubes != 0) {
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
                    evaluate_throw(dice_values,2);
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
                player_box = new AnchorPane[player_count];
                players = new VBox();
                players.setMaxSize(60, 60);
                leader_board = new ScrollPane();
                leader_board.setMinSize(200, 200);
                leader_board.setMaxSize(2000, 200);
                leader_board.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
                leader_board.setTranslateX(800);
                leader_board.setTranslateY(0);
                leader_board.setStyle("-fx-background: grey; -fx-background-insets: 0; -fx-padding: 0;");
                leader_board.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));

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
                        setEnd_turn();
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
                            disable_dice(mesh);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                break;
            case 6:
                Label winner = new Label();
                Label winner_name = new Label();
                Button main_menu = new Button();
                main_menu.setOnMouseClicked(mouseEvent -> {
                    try {
                        scene_manager(1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                main_menu.setTranslateY(100);
                main_menu.setText("main menu");
                winner.setText("winner:   ");
                winner.setStyle("-fx-font-size:22");
                winner_name.setTranslateX(50);
                winner_name.setText(player_names[player]);
                StackPane root = new StackPane(winner,winner_name,main_menu);
                Scene end_screen = new Scene(root, 320, 240);
                stage_true.setScene(end_screen);
                stage_true.show();
                break;
            case 7:
                int i = 0;
                VBox main_box = new VBox();
                StackPane history_root = new StackPane();
                Label history_label = new Label();
                Button exit_button = new Button();
                exit_button.setText("back");
                exit_button.setStyle("-fx-background-color:#b5b5b5;-fx-font-size:22");
                history_label.setStyle("-fx-font-size:22");
                exit_button.setOnMouseClicked(mouseEvent -> {
                    try {
                        scene_manager(1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                HBox v = new HBox(exit_button,history_label);
                v.setAlignment(Pos.CENTER);
                main_box.getChildren().add(v);
                String csvFile = "history.csv";
                String line;
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    while ((line = br.readLine()) != null) {
                        i++;
                        if (i>2) {
                            i++;
                            Label l1 = new Label();
                            String[] parts = line.split(",");
                            l1.setText("winner: " + parts[1] + " other players: " + parts[2] + " date: " + parts[3]);
                            l1.setAlignment(Pos.CENTER);
                            l1.setStyle("-fx-font-size:20");
                            VBox tmp_box = new VBox(l1);
                            tmp_box.setMinHeight(50);
                            tmp_box.setAlignment(Pos.CENTER);
                            tmp_box.setStyle("-fx-background-color:#b5b5b5;");

                            main_box.getChildren().add(tmp_box);
                        }
                    }
                }
                ScrollPane scroll = new ScrollPane(main_box);
                main_box.setStyle("-fx-background-color:grey");

                scroll.setFitToHeight(true);
                scroll.setFitToWidth(true);


                history_label.setText("history of matches");
                history_root.getChildren().add(scroll);
                history_root.setStyle("-fx-background-color:grey");
                Scene history = new Scene(history_root);
                stage_true.setScene(history);
                stage_true.show();
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
        return new VBox(name_but);
    }

    public void start_button() throws IOException {
        scene_manager(2);
    }

    @FXML
    public void single_player() throws IOException {
        scene_manager(4);
    }

    public void multi_player() throws IOException {
        scene_manager(5);
    }

    public void start_game() throws IOException {
        scene_manager(4);
    }

    public void end_game() throws IOException {
        scene_manager(6);
    }
    public void history() throws IOException {
        scene_manager(7);
    }


    private void disable_dice(CuboidMesh mesh) throws IOException, InterruptedException {
        int cube = Integer.parseInt(mesh.getId());
        if (locked_dice[cube] == 0) {
            can_roll = true;
            remaining_cubes -= 1;
            offset_times += 1;
            mesh.setTranslateX(50 + 50 * offset_times);
            mesh.setTranslateY(600);
            lock_dice(cube, 1);
        } else if (locked_dice[cube] == 1) {
            remaining_cubes += 1;
            offset_times -= 1;
            locked_dice[cube] = 0;
            mesh.setTranslateY(90 + 70 * cube);
            mesh.setTranslateX(40);
            dice_p_arr[results[cube] - 1] -= 1;
        }
    }

    public void show_total_score() throws IOException {

        for (int i = 0; i < player_count; i++) {
            if (total_score[i] >= 1000) {
                against_bot = false;
                write_to_csv();
                end_game();
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
                for (CuboidMesh mesh:boxes){
                    try {
                        disable_dice(mesh);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                evaluate_throw(dice_p_arr,1);
                total_score[player] += tmp_score;
                tmp_score = 0;
                turn_score = 0;

                try {
                    show_total_score();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
               change_player();
                try {
                    reset_cubes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
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
        }
    }

    public int[] dice_values = new int[6];
    public int[] results = new int[6];
    public int player = 0;
    public int[] dice_p_arr = new int[6];
    public int[] locked_dice = new int[6];
    public boolean assist_1;
    public int turn_score = 0;

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
    }

    public int tmp_score;

    private void evaluate_throw(int[] dice_values,int mode) {

        StringBuilder combinations = new StringBuilder();
        int doubles = 0;
        int[] doubles_position = new int[6];
        //3000 points
        if (dice_values[0] == 1 && dice_values[1] == 1 && dice_values[2] == 1 && dice_values[3] == 1 && dice_values[4] == 1 && dice_values[5] == 1) {
            switch(mode){
                case 1:
                    tmp_score += 3000;
                    for (int value : dice_values) {
                        value = 0;
                    }
                    break;
                case 2:
                    combinations.append("1 + 2 + 3 + 4 + 5 + 6: 3000\n");
                    break;
                case 3:
                    break;
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
            switch(mode){
                case 1:
                    tmp_score += 1500;
                    for (int i = 0; i < doubles_position.length; i++) {
                        dice_values[i] -= 2;
                        doubles_position[i] -= 2;
                    }
                    break;
                case 2:
                    combinations.append("3 doubles: 1500\n");
                    break;
                case 3:
                    break;
            }

        }
        if (dice_values[0] == 3) {
            switch(mode){
                case 1:
                    tmp_score += 1000;
                    dice_values[0] -= 3;
                    break;
                case 2:
                    combinations.append("1 + 1 + 1: 1000\n");
                    break;
                case 3:
                    break;
            }

        }
        //100*dice points
        for (int i = 0; i < dice_values.length; i++) {
            switch(mode){
                case 1:
                    if (i != 0) {
                        if (dice_values[i] == 6) {
                            tmp_score += 200 * (i + 1);
                            dice_values[i] = 0;
                        } else if (dice_values[i] >= 3) {
                            tmp_score += 100 * (i + 1);
                            dice_values[i] -= 3;
                        }
                    }
                    break;
                case 2:
                    if (i != 0) {
                    if (dice_values[i] == 6) {
                        combinations.append("2 three numbers:").append(200 * (i + 1)).append("\n");
                    } else if (dice_values[i] >= 3) {
                        combinations.append("three numbers:").append(100 * (i + 1)).append("\n");

                    }
                }
                    break;
                case 3:
                    break;
            }

        }
        //100
        if (dice_values[0] > 0) {
            switch(mode){
                case 1:
                    tmp_score += 100 * dice_values[0];
                    dice_values[0] = 0;
                    break;
                case 2:
                    combinations.append("number 1:").append(100 * dice_values[0]).append("\n");
                    break;
                case 3:
                    break;
            }

        }
        //50 points
        if (dice_values[4] > 0) {
            switch(mode){
                case 1:
                    tmp_score += 50 * dice_values[4];
                    dice_values[4] = 0;
                    break;
                case 2:
                    combinations.append("number 5:").append(50 * dice_values[4]).append("\n");
                    break;
                case 3:
                    break;
            }

        }
        if (assist.isSelected()) {
            change_combination_text("combinations: "+combinations);
        }

    }

    public void setEnd_turn() throws IOException, InterruptedException {
        generate_values(boxes, transitionR, transitionT, assist.isSelected());
        evaluate_throw(dice_p_arr,1);
        turn_score += tmp_score;
        total_score[player] += tmp_score;
        if (turn_score < 400 || tmp_score == 0){
            total_score[player] -= turn_score;
        }
        if (remaining_cubes != 0||tmp_score == 0){
            change_player();
            turn_score = 0;
        }
        reset_cubes();
        show_total_score();
        tmp_score = 0;
        dice_p_arr = new int[6];
        can_roll = true;
    }

    public void tmp_dice_value() throws IOException {
        evaluate_throw(dice_p_arr,1);
            turn_score += tmp_score;
            total_score[player] += tmp_score;
            tmp_score = 0;
        show_total_score();
    }

    public void lock_dice(int i, int disable_value) {
        locked_dice[i] = disable_value;
        switch (results[i]) {
            case 1:
                dice_p_arr[0] += 1;
                break;
            case 2:
                dice_p_arr[1] += 1;
                break;
            case 3 :
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

    }

    public void change_player(){
        player += 1;
        if (player == player_count){
            player = 0;
        }
    }

    public void write_to_csv() throws IOException {
        String other_players = "";
        for (int i = 0; i < player_count; i++) {
            if (player != i) {
                other_players += player_names[i]+";";
            }
        }
        String id = get_from_csv();
        FileWriter fw = new FileWriter("history.csv", true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.newLine();
        id= String.valueOf(Integer.parseInt(id)+1);
        bw.write(id+",");
        bw.write(player_names[player]+",");
        bw.write(other_players+",");
        bw.write(String.valueOf(LocalDateTime.now()));
        bw.close();
    }

    public String get_from_csv() throws IOException {
        String csvFile = "history.csv";
        String line;
        String lastLine = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                lastLine = line; // Store the last line
            }

            if (lastLine != null) {
                // Split the last line by comma
                String[] parts = lastLine.split(",");

                // Extracting the first column (index 0)
                String firstColumn = parts[0];

                return firstColumn;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
