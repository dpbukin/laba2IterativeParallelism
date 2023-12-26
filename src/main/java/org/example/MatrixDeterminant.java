package org.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MatrixDeterminant {

    public static void main(String[] args) {
        int n = 11;// Установка размера матрицы
        int[][] matrix = identityMatrix(n); // Создание единичной матрицы

        // Однопоточное выполнение
        long startTimeSingle = System.currentTimeMillis();
        long determinantSingle = calcDetSingle(matrix);
        long singleTime = System.currentTimeMillis() - startTimeSingle;

        // Многопоточное выполнение
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        long startTime = System.currentTimeMillis();

        Long determinant = forkJoinPool.invoke(new DeterminantTask(matrix, 0, n - 1));

        long parallelTime = System.currentTimeMillis() - startTime;

        System.out.println("Время однопоточного выполнения: " + singleTime + " мс");
        System.out.println("Результат однопоточного выполнения: " + determinantSingle);
        System.out.println("Время параллельного выполнения: " + parallelTime + " мс");
        System.out.println("Результат параллельного выполнения: " + determinant);
    }

    // Создание единичной матрицы
    private static int[][] identityMatrix(int n) {
        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = 1;
            }
        }

        return matrix;
    }

    // Расчет определителя для однопоточного выполнения
    private static long calcDetSingle(int[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        }

        int determinant = 0;
        int sign = 1;

        for (int i = 0; i < n; i++) {
            // Вычисление минора матрицы
            int[][] minor = minor(matrix, 0, i);

            // Рекурсивный вызов для расчета определителя минора
            determinant += sign * matrix[0][i] * calcDetSingle(minor);

            // Изменение знака перед следующим слагаемым
            sign = -sign;
        }

        return determinant;
    }

    private static class DeterminantTask extends RecursiveTask<Long> {
        private final int[][] matrix;
        private final int start;
        private final int end;
        private static final int MAX_DEPTH = 50; // Максимальная глубина рекурсии

        public DeterminantTask(int[][] matrix, int startRow, int endRow) {
            this.matrix = matrix;
            this.start = startRow;
            this.end = endRow;
        }

        @Override
        protected Long compute() {

            int size = end - start + 1;

            if (size == 1 || size == 2 || size >= MAX_DEPTH) {
                return computeSequentially();
            } else {
                // Выполняем подзадачи асинхронно
                DeterminantTask leftTask = new DeterminantTask(matrix, start, start + size / 2 - 1);
                DeterminantTask rightTask = new DeterminantTask(matrix, start + size / 2, end);

                invokeAll(leftTask, rightTask);

                // Ожидаем завершения подзадач и объединяем результаты
                long leftResult = leftTask.join();
                long rightResult = rightTask.join();

                // Используем миноры для рекурсивных вызовов
                long determinant = 0;
                int sign = 1;

                for (int i = 0; i < size; i++) {
                    int[][] minor = minor(matrix, start, i);
                    determinant += sign * matrix[start][i] * computeWithDepth(minor, 0, size - 2);
                    sign = -sign;
                }

                return (size % 2 == 0) ? leftResult + rightResult : leftResult - rightResult;

            }
        }

        private Long computeWithDepth(int[][] matrix, int depth, int size) {
            if (size == 1 || size == 2 || depth >= MAX_DEPTH) {
                return computeSequentially(matrix, size);
            } else {
                long determinant = 0;
                int sign = 1;

                for (int i = 0; i < size; i++) {
                    int[][] minor = minor(matrix, 0, i);
                    determinant += sign * matrix[0][i] * computeWithDepth(minor, depth + 1, size - 2);
                    sign = -sign;
                }

                return determinant;
            }
        }

        private Long computeSequentially() {
            return computeSequentially(matrix, end - start + 1);
        }

        private Long computeSequentially(int[][] matrix, int size) {
            int determinant = 0;
            int sign = 1;

            for (int i = 0; i < size; i++) {
                // Вычисление минора матрицы
                int[][] minor = minor(matrix, start, i);

                // Рекурсивный вызов для расчета определителя минора
                determinant += sign * matrix[start][i] * calcDetSingle(minor);

                // Изменение знака перед следующим слагаемым
                sign = -sign;
            }

            return (long) determinant;
        }
    }

    // Создание минора матрицы
    private static int[][] minor(int[][] matrix, int row, int col) {
        int n = matrix.length;
        int[][] minor = new int[n - 1][n - 1];
        int minorRow = 0;
        int minorCol;

        for (int i = 0; i < n; i++) {
            if (i != row) {
                minorCol = 0;
                for (int j = 0; j < n; j++) {
                    if (j != col) {
                        minor[minorRow][minorCol++] = matrix[i][j];
                    }
                }
                minorRow++;
            }
        }

        return minor;
    }
}



