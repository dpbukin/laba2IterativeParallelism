package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class MatrixDeterminant {

    public static void main(String[] args) {
        int n = 11;
        int[][] matrix = generateMatrix(n);

        // Однопоточное выполнение
        long startTime = System.currentTimeMillis();
        int determinantSingle = computeDeterminantSingle(matrix);
        long elapsedTimeSingle = System.currentTimeMillis() - startTime;

        System.out.println("Однопоточный режим:");
        System.out.println("Определитель матрицы: " + determinantSingle);
        System.out.println("Время выполнения: " + elapsedTimeSingle + " мс");

        // Многопоточное выполнение
        startTime = System.currentTimeMillis();
        int determinantParallel = computeDeterminantParallel(matrix);
        long elapsedTimeParallel = System.currentTimeMillis() - startTime;

        System.out.println("\nМногопоточный режим:");
        System.out.println("Определитель матрицы: " + determinantParallel);
        System.out.println("Время выполнения: " + elapsedTimeParallel + " мс");
    }

    public static int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];
        // Заполняем матрицу единицами (может быть любое другое заполнение)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = 1;
            }
        }
        return matrix;
    }

    public static int computeDeterminantSingle(int[][] matrix) {
        int n = matrix.length;

        // Создаем задачу для вычисления определителя
        DeterminantTask determinantTask = new DeterminantTask(matrix);

        // Вычисляем определитель в однопоточном режиме
        return determinantTask.compute();
    }

    public static int computeDeterminantParallel(int[][] matrix) {
        int n = matrix.length;

        // Создаем объект ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // Создаем задачу для вычисления определителя
        DeterminantTask determinantTask = new DeterminantTask(matrix);

        // Запускаем задачу и получаем результат
        int determinant = forkJoinPool.invoke(determinantTask);

        // Завершаем работу ForkJoinPool
        forkJoinPool.shutdown();

        return determinant;
    }

    static class DeterminantTask extends RecursiveTask<Integer> {
        private final int[][] matrix;

        public DeterminantTask(int[][] matrix) {
            this.matrix = matrix;
        }

        @Override
        protected Integer compute() {
            int n = matrix.length;

            if (n == 1) {
                return matrix[0][0]; // Определитель матрицы 1x1
            }

            if (n == 2) {
                return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]; // Определитель матрицы 2x2
            }

            int determinant = 0;

            for (int j = 0; j < n; j++) {
                int[][] subMatrix = getSubMatrix(matrix, 0, j);
                int sign = (int) Math.pow(-1, j);
                determinant += sign * matrix[0][j] * computeSubDeterminant(subMatrix);
            }

            return determinant;
        }

        private int[][] getSubMatrix(int[][] matrix, int rowToRemove, int colToRemove) {
            int n = matrix.length - 1;
            int[][] subMatrix = new int[n][n];
            int rowIndex = 0, colIndex;

            for (int i = 0; i < matrix.length; i++) {
                if (i == rowToRemove) continue;

                colIndex = 0;
                for (int j = 0; j < matrix.length; j++) {
                    if (j == colToRemove) continue;

                    subMatrix[rowIndex][colIndex] = matrix[i][j];
                    colIndex++;
                }

                rowIndex++;
            }

            return subMatrix;
        }

        private int computeSubDeterminant(int[][] subMatrix) {
            DeterminantTask subTask = new DeterminantTask(subMatrix);
            return subTask.fork().join();
        }
    }
}

