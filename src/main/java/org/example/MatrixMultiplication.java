package org.example;

import java.util.Random;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class MatrixMultiplication {
    public static void main(String[] args) {
        int n = 1000;
        int[][] matrixA = randomMatrix(n);
        int[][] matrixB = edMatrix(n);

        long startTime = System.currentTimeMillis();
        int[][] resultSingle = multiplyMatrices(matrixA, matrixB, n);
        long singleTime = System.currentTimeMillis() - startTime;


        startTime = System.currentTimeMillis();
        int[][] resultParallel = multiplyMatricesParallel(matrixA, matrixB, n);
        long parallelTime = System.currentTimeMillis() - startTime;


        System.out.println("Время однопоточного выполнения: " + singleTime + " мс");
        System.out.println("Время параллельного выполнения: " + parallelTime + " мс");


//        System.out.println("Результат однопоточного выполнения:");
//
//        for (int i = 0; i < resultSingle.length; i++) {
//            System.out.println(" ");
//            for (int j = 0; j < resultSingle.length; j++) {
//                System.out.print(resultSingle[i][j] + " ");
//            }
//        }
//
//        System.out.println();
//        System.out.println("Результат многопоточного выполнения:");
//
//        for (int i = 0; i < resultParallel.length; i++) {
//            System.out.println(" ");
//            for (int j = 0; j < resultParallel.length; j++) {
//                System.out.print(resultParallel[i][j] + " ");
//            }
//        }
    }

    public static int[][] randomMatrix(int n) {

        int[][] randomMatrix = new int[n][n];
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                randomMatrix[i][j] = random.nextInt(100);
            }
        }

        return randomMatrix;
    }

    public static int[][] edMatrix(int n) {
        int[][] edMatrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Если индекс строки (i) равен индексу столбца (j), то устанавливаем элемент (i, j) в 1 (главная диагональ),
                // иначе устанавливаем его в 0 (элементы вне главной диагонали).
                if (i == j) {
                    edMatrix[i][j] = 1;
                } else {
                    edMatrix[i][j] = 0;
                };
            }
        }

        return edMatrix;
    }

    public static int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB, int n) {
        int[][] result = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    result[i][j] = result[i][j] + (matrixA[i][k] * matrixB[k][j]);
                }
            }
        }
        return result;
    }
    public static int[][] multiplyMatricesParallel(int[][] matrixA, int[][] matrixB, int n) {

        // Получаем количество доступных процессорных ядер
        int numThreads = Runtime.getRuntime().availableProcessors();

        // Создаем пул потоков с количеством потоков, равным количеству ядер
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        Future<int[]>[] futures = new Future[n];

        for (int i = 0; i < n; i++) {
            final int x = i; // Текущая строка, которую будет обрабатывать поток
            futures[i] = executor.submit(() -> {
                int[] partialResult = new int[n];

                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        partialResult[j] = partialResult[j] + (matrixA[x][k] * matrixB[k][j]);
                    }
                }
                return partialResult; // Возвращаем частичный результат как матрицу int[][]
            });
        }

        int[][] result = new int[n][n];

        // Собираем результаты из объектов Future и объединяем их в общую матрицу-результат
        for (int i = 0; i < n; i++) {
            try {
                result[i] = futures[i].get();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        // Завершаем работу пула потоков
        executor.shutdown();

        return result;
    }
}
