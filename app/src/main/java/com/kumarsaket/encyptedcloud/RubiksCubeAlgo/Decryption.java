package com.kumarsaket.encyptedcloud.RubiksCubeAlgo;

public class Decryption {


    public int[][] PixelMatrix() {
        return PixelMatrix;
    }

    int [][] PixelMatrix;
    int M;  //height
    int N; //width
    int [] Kr;
    int [] Kc;

    public Decryption(int [][] Pixelmatrix, int width, int height, int [] Kr, int [] Kc) {
        this.PixelMatrix = Pixelmatrix;
        this.M = height;
        this.N = width;
        this.Kr = Kr;
        this.Kc = Kc;
    }

    public void doDecrypt(){
        step7(PixelMatrix,M,N,Kr);
        step6(PixelMatrix,M,N,Kc);
        step8(PixelMatrix,M,N,Kc);
        step9(PixelMatrix,M,N,Kr);
    }

    public static void HorizontalRotation(int [][] arr, int shiftNumber, int size, int fixed_row){
        shiftNumber = shiftNumber % size;
        int i, j, k, temp;
        int g_c_d = gcd(shiftNumber, size);
        for (i = 0; i < g_c_d; i++) {
            /* move i-th values of blocks */
            temp = arr[fixed_row][i];
            j = i;
            while (true) {
                k = j + shiftNumber;
                if (k >= size)
                    k = k - size;
                if (k == i)
                    break;
                arr[fixed_row][j] = arr[fixed_row][k];
                j = k;
            }
            arr[fixed_row][j] = temp;
        }

        // for (int x=0;x<size;x++ ) {
        //   System.out.print(arr[x]+"\t");
        // }
    }

    public static void VerticalRotation(int [][] arr, int shiftNumber, int size, int fixed_col){
        shiftNumber = shiftNumber % size;
        int i, j, k, temp;
        int g_c_d = gcd(shiftNumber, size);
        for (i = 0; i < g_c_d; i++) {
            /* move i-th values of blocks */
            temp = arr[i][fixed_col];
            j = i;
            while (true) {
                k = j + shiftNumber;
                if (k >= size)
                    k = k - size;
                if (k == i)
                    break;
                arr[j][fixed_col] = arr[k][fixed_col];
                j = k;
            }
            arr[j][fixed_col] = temp;
        }

        // for (int x=0;x<size;x++ ) {
        //   System.out.print(arr[x]+"\t");
        // }
    }




    public static void step6(int [][] b, int M, int N, int[] Kc){
        boolean even = true;
        for (int i=0;i<M ;i++ ) {
            if (even) {
                for (int j=0,k=N-1;j<N ;j++,k-- ) {
                    b[i][j] = b[i][j]^Kc[k];
                }
            }else {
                for (int j=0;j<N ;j++ ) {
                    b[i][j] = b[i][j]^Kc[j];
                }
            }
            even=!even;
        }
    }

    public static void step7(int [][] b, int M, int N, int[] Kr){
        boolean even = true;
        for (int i=0;i<N ;i++) {
            if (even) {
                for (int j=0,k=M-1;j<M ;j++,k-- ) {
                    b[j][i] = b[j][i]^Kr[k];
                }
            }else {
                for (int j=0;j<M ;j++ ) {
                    b[j][i] = b[j][i]^Kr[j];
                }
            }
            even=!even;
        }
    }


    public static void step8(int [][] b,int M,  int N,int []Kc){
        int ai , Mai;
        for (int i=0;i<N;i++ ) {
            ai = 0;
            for (int j=0;j<M;j++ ) {
                ai+=b[j][i];
            }
            Mai = ai % 2;
            if (Mai == 1) {
                // System.out.println("up " + Kc[i] +" times");

                VerticalRotation(b,Kc[i],M,i);
                // M is row size of b
                // i is the fixed_col
                // up shift b[j][i] by kc[i] times
            }else {
                // System.out.println("down " + Kc[i] +" times");
                VerticalRotation(b,M-(Kc[i]%M),M,i);

                // down shift b[j][i] by kc[i] times
            }
        }
    }

    public static void step9(int [][] b,int M,  int N,int [] Kr){
        int ai , Mai;
        for (int i=0;i<M;i++ ) {
            ai = 0;
            for (int j=0;j<N;j++ ) {
                ai+=b[i][j];
            }
            Mai = ai % 2;
            if (Mai == 1) {
                // System.out.println("right " + Kr[i] +" times");
                HorizontalRotation(b,N - (Kr[i]%N),N,i);
                // N is row size of b
                // i is the fixed_row
                // right shift row b[i][j] by kr[i] times
            }else {
                // System.out.println("left " + Kr[i] +" times");
                HorizontalRotation(b,Kr[i],N,i);

                // left shift b[i][j] by kr[i] times
            }
        }
    }

    public static int gcd(int a, int b){
        if (b == 0)
            return a;
        else
            return gcd(b, a % b);
    }
}
