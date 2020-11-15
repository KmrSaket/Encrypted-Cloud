package com.kumarsaket.encyptedcloud.RubiksCubeAlgo;

import java.util.Random;

public class Encryption extends Rotations {


    int[][] PixelMatrix;
    int M;  //height
    int N; //width
    int alpha = 23;
    int setA_maxValue = (int) Math.pow(2, alpha) - 1;
    int[] Kr;
    int[] Kc;
    Random random = new Random();


    public Encryption(int[][] Pixelmatrix, int width, int height) {
        this.PixelMatrix = Pixelmatrix;
        this.M = height;
        this.N = width;
        this.Kr = new int[M];
        this.Kc = new int[N];

        // initializing Kr
        for (int i = 0; i < M; i++)
            this.Kr[i] = random.nextInt(setA_maxValue);
//            this.Kr[i] = 5000;


        // initializing Kc
        for (int i = 0; i < N; i++)
            this.Kc[i] = random.nextInt(setA_maxValue);

//            this.Kc[i] = 7352525;


    }


    public int[] getKr() {
        return Kr;
    }

    public int[] getKc() {
        return Kc;
    }

    public int[][] PixelMatrix() {
        return PixelMatrix;
    }

    public void doEnc() {
        step4(PixelMatrix, M, N, Kr);
        step5(PixelMatrix, M, N, Kc);
        step6(PixelMatrix, M, N, Kc);
        step7(PixelMatrix, M, N, Kr);
    }


    public static void step4(int[][] b, int M, int N, int[] Kr) {
        int ai, Mai;
        for (int i = 0; i < M; i++) {
            ai = 0;
            for (int j = 0; j < N; j++) {
                ai += b[i][j];
            }
            Mai = ai % 2;
            if (Mai == 1) {
                // System.out.println("left " + Kr[i] +" times");
                HorizontalRotation(b, Kr[i], N, i);
                // N is row size of b
                // i is the fixed_row
                // left shift row b[i][j] by kr[i] times
            } else {
                // System.out.println("right " + Kr[i] +" times");
                HorizontalRotation(b, N - (Kr[i] % N), N, i);
                // right shift b[i][j] by kr[i] times
            }
        }
    }

    public static void step5(int[][] b, int M, int N, int[] Kc) {
        int ai, Mai;
        for (int i = 0; i < N; i++) {
            ai = 0;
            for (int j = 0; j < M; j++) {
                ai += b[j][i];
            }
            Mai = ai % 2;
            if (Mai == 1) {
                // System.out.println("down " + Kc[i] +" times");

                VerticalRotation(b, M - (Kc[i] % M), M, i);
                // M is row size of b
                // i is the fixed_col
                // down shift b[j][i] by kc[i] times
            } else {
                // System.out.println("up " + Kc[i] +" times");
                VerticalRotation(b, Kc[i], M, i);
                // up shift b[j][i] by kc[i] times
            }
        }
    }

    public static void step6(int[][] b, int M, int N, int[] Kc) {
        boolean even = true;
        for (int i = 0; i < M; i++) {
            if (even) {
                for (int j = 0, k = N - 1; k >= 0; j++, k--) {
                    b[i][j] = b[i][j] ^ Kc[k];
                }
            } else {
                for (int j = 0; j < N; j++) {
                    b[i][j] = b[i][j] ^ Kc[j];
                }
            }
            even = !even;
        }
    }

    public static void step7(int[][] b, int M, int N, int[] Kr) {
        boolean even = true;
        for (int i = 0; i < N; i++) {
            if (even) {
                for (int j = 0, k = M - 1; j < M; j++, k--) {
                    b[j][i] = b[j][i] ^ Kr[k];
                }
            } else {
                for (int j = 0; j < M; j++) {
                    b[j][i] = b[j][i] ^ Kr[j];
                }
            }
            even = !even;
        }
    }


}
