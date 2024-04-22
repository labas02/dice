package org.example.dice;

import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import org.fxyz3d.shapes.primitives.CuboidMesh;

import java.util.Random;

public class HelloController {
    public static void matrixRotateNode(RotateTransition n, double alf, double bet, double gam) {
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
        n.setByAngle((360 * (random.nextInt(5)+4))+Math.toDegrees(d));
    }

    public static void generate_values(CuboidMesh[] ar_box, RotateTransition[] transitionRX){
        for (CuboidMesh mesh : ar_box){
            mesh.setRotationAxis(Rotate.Y_AXIS);
            mesh.setRotate(0);
            mesh.setRotationAxis(Rotate.Z_AXIS);
            mesh.setRotate(0);
            mesh.setRotationAxis(Rotate.X_AXIS);
            mesh.setRotate(0);
        }
        int[] results = new int[10];
        for (int i = 0; i < 6; i++) {

            Random random = new Random();
            int random1 = 1+random.nextInt(6);
            results[i] = random1;
            System.out.println(results[i]);
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


}