package org.example.dice;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.util.Random;

public class HelloController {
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
        n.setByAngle(360*(1+ random.nextInt(5))+Math.toDegrees(d));
    }

    public static void generate_values(CuboidMesh[] ar_box, RotateTransition[] transitionRX, TranslateTransition[] transitionT,boolean assist){
        for (CuboidMesh mesh : ar_box){
            mesh.setRotationAxis(Rotate.Y_AXIS);
            mesh.setRotate(0);
            mesh.setRotationAxis(Rotate.Z_AXIS);
            mesh.setRotate(0);
            mesh.setRotationAxis(Rotate.X_AXIS);
            mesh.setRotate(0);
        }
        int[] results = new int[6];
        for (int i = 0; i < 6; i++) {

            Random random = new Random();
            int random1 = 1 + random. nextInt((6 - 1) + 1);
            results[i] = random1;
            //System.out.println(results[i]);
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
                    //matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(360),Math.toRadians(360));
                    transitionRX[i].setAxis(Point3D.ZERO.add(0,1,1));
                    transitionRX[i].setByAngle(360*(1+random.nextInt(5)));
                    break;
                case 5:
                    matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(270),Math.toRadians(0));
                    break;
                case 6:
                    matrixRotateNode(transitionRX[i], Math.toRadians(0),Math.toRadians(180),Math.toRadians(90));
                    break;
            }



        }
        int[] dice_values = new int[6];
    for (int i = 0;i<results.length;i++) {
        Random random = new Random();
        switch(results[i]) {
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
    for (int value: dice_values) {
        System.out.println(value);
    }
    //vypne našeptávač
if(assist) {
    evaluate_trow(dice_values);
}
}

    private static void evaluate_trow(int[] dice_values) {
        int tmp_score = 0;
        int doubles = 0;
        int[] doubles_position = new int[6];
        //3000 points
        if (dice_values[0] == 1&&dice_values[1] == 1&&dice_values[2] == 1&&dice_values[3] == 1&&dice_values[4] == 1&&dice_values[5] == 1) {
            tmp_score += 3000;
            for (int value: dice_values){
                value = 0;
            }
        }
        //counts double values
            for (int i = 0;i<dice_values.length;i++){
                if (dice_values[i]>=2){
                    doubles += 1;
                    doubles_position[i] += 1;
                }
            }
            //1500 points
            if (doubles ==3){
                tmp_score += 1500;
                for (int i = 0;i<doubles_position.length;i++){
                    dice_values[i] -= 2;
                    doubles_position[i] -= 2;
                }
            }
            if (dice_values[0]==3){
                tmp_score += 1000;
                dice_values[0]-=3;
            }
            //100*dice points
            for (int i = 0; i < dice_values.length; i++) {
                if (i!=0) {
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
        if (dice_values[0]>0){
            tmp_score += 100*dice_values[0];
        }
            //50 points
        if (dice_values[4]>0){
            tmp_score += 50*dice_values[4];
        }


        System.out.println("final score: "+tmp_score);
    }

}