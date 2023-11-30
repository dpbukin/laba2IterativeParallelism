package org.example;

import java.util.Arrays;
import java.util.Random;

public class MatrixDeterminant {

    private static int[][] matrix;
    private static int n;

    public static void main(String[] args) {
        n = 11; // Пример размера матрицы (можно использовать любой другой размер)
        matrix = repetitiveRowsMatrix(n);

        long startTime = System.currentTimeMillis();
        int determinantSingle = calculateDeterminant(matrix);
        long singleTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int determinantParallel = calculateDeterminantParallel(matrix);
        long parallelTime = System.currentTimeMillis() - startTime;

        System.out.println("Время однопоточного выполнения: " + singleTime + " мс");
        System.out.println("Время параллельного выполнения: " + parallelTime + " мс");

        System.out.println("Результат однопоточного выполнения: " + determinantSingle);
        System.out.println("Результат параллельного выполнения: " + determinantParallel);
    }

    public static int[][] repetitiveRowsMatrix(int n) {
        int[][] repetitiveMatrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(repetitiveMatrix[i], 1);
        }

        return repetitiveMatrix;
    }

    public static int calculateDeterminant(int[][] matrix) {
        int n = matrix.length;

        if (n == 1) {
            return matrix[0][0];
        }

        int determinant = 0;

        for (int i = 0; i < n; i++) {
            determinant += Math.pow(-1, i) * matrix[0][i] * calculateDeterminant(minor(matrix, 0, i));
        }

        return determinant;
    }

    public static int calculateDeterminantParallel(int[][] matrix) {
        DeterminantThread[] threads = new DeterminantThread[n];
        int[] determinants = new int[n];

        for (int i = 0; i < n; i++) {
            threads[i] = new DeterminantThread(i);
            threads[i].start();
        }

        for (int i = 0; i < n; i++) {
            try {
                threads[i].join();
                determinants[i] = threads[i].getResult();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int determinant = 0;
        for (int i = 0; i < n; i++) {
            determinant += Math.pow(-1, i) * matrix[0][i] * determinants[i];
        }

        return determinant;
    }

    private static int[][] minor(int[][] matrix, int row, int col) {
        int n = matrix.length;
        int[][] minorMatrix = new int[n - 1][n - 1];

        for (int i = 0, k = 0; i < n; i++) {
            if (i == row) continue;

            for (int j = 0, l = 0; j < n; j++) {
                if (j == col) continue;

                minorMatrix[k][l] = matrix[i][j];
                l++;
            }

            k++;
        }

        return minorMatrix;
    }

    static class DeterminantThread extends Thread {
        private final int rowIndex;
        private int result;

        DeterminantThread(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        @Override
        public void run() {
            result = calculateDeterminant(minor(matrix, rowIndex, 0));
        }

        public int getResult() {
            return result;
        }
    }
}

