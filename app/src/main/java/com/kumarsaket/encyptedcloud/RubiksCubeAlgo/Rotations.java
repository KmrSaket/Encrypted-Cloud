package com.kumarsaket.encyptedcloud.RubiksCubeAlgo;

public class Rotations {

    public static int gcd(int a, int b) {
        if (b == 0)
            return a;
        else
            return gcd(b, a % b);
    }

    public static void HorizontalRotation(int[][] arr, int shiftNumber, int size, int fixed_row) {
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

    public static void VerticalRotation(int[][] arr, int shiftNumber, int size, int fixed_col) {
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

}
