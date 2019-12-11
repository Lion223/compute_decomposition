package App;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class App {
    static ExecutorService es = Executors.newCachedThreadPool();
    static boolean random = true;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        System.out.println("Enter n:");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        System.out.println("Random data? (1 - yes, 2 - no):");
        scanner = new Scanner(System.in);
        int r = scanner.nextInt();

        if(r == 2)
            random = false;

        Callable<int[][]> c = () -> generateMatrix(n);

        int[][] A = es.submit(c).get();

        if(!random){
            System.out.println("Enter coefficients of matrix A:");
            A = enterMatrix(n);
        }

        Callable<int[]> c2 = () -> {
            int[] a = generateArray(n);
            for(int i = 1; i <= a.length; i++){
                if (i%2 == 0) {
                    a[i - 1] = 22*i;
                } else{
                    a[i - 1] = 22;
                }
            }
            return a;
        };

        int[] b = es.submit(c2).get();

        int y1[] = mul(A, b);

        Callable<int[][]> c3 = () -> generateMatrix(n);

        int[][] A1 = es.submit(c3).get();

        if(!random){
            System.out.println("Enter coefficients of matrix A1:");
            A1 = enterMatrix(n);
        }

        Callable<int[]> c4 = () -> generateArray(n);

        int[] b1 = es.submit(c4).get();

        Callable<int[]> c5 = () -> generateArray(n);

        int[] c1 = es.submit(c5).get();

        int y2[] = mul(A1, sub(b1, mul(21, c1)));

        Callable<int[][]> c6 = () -> generateMatrix(n);

        int A2[][] = es.submit(c6).get();

        if(!random){
            System.out.println("Enter coefficients of matrix A2:");
            A2 = enterMatrix(n);
        }

        Callable<int[][]> c7 = () -> generateMatrix(n);

        int B2[][] = es.submit(c7).get();

        if(!random){
            System.out.println("Enter coefficients of matrix B2:");
            B2 = enterMatrix(n);
        }

        Callable<int[][]> c8 = new Callable<int[][]>() {
            @Override
            public int[][] call() throws Exception {
                int a[][] = generateMatrix(n);
                for(int i = 0; i < a.length; i++){
                    for(int j = 0; j < a[i].length; j++){
                        try{
                            a[i][j] =  22 / (i + j);
                        }catch(ArithmeticException e){
                            a[i][j] =  0;
                        }
                    }
                }
                return a;
            }
        };

        int C2[][] = es.submit(c8).get();

        int[][] y3 = mul(A2, sub(B2, C2));

        int[] res = sum(sum(y2,y1), mul(sum(mul(y3, mul(y1,sum(mul(y3,y1),y2))), mul(y2,y2)),y2));

        System.out.println("Result: " + Arrays.toString(res));
    }

    private static int[] enterArray(int size){
        int[] array = new int[size];
        Scanner s = new Scanner(System.in);
        for (int i = 1; i <= array.length; i++) {
            array[i - 1] = s.nextInt();
        }
        return array;
    }

    private static int[] generateArray(int size) {
        int[] array = new int[size];
        Random r = new Random();
        for (int i = 1; i <= array.length; i++) {
            array[i - 1] = r.nextInt(100)+1;
        }
        return array;
    }

    private static int[][] enterMatrix(int size){
        int[][] matrix = new int[size][size];
        Scanner s = new Scanner(System.in);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = s.nextInt();
            }
        }
        return matrix;
    }

    private static int[][] generateMatrix(int size) {
        int[][] matrix = new int[size][size];
        Random r = new Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = r.nextInt(100)+1;
            }
        }
        return matrix;
    }

    private static int[] mul(int[][] matrix, int[] vector) {
        return Arrays.stream(matrix)
                .mapToInt(row ->
                        IntStream.range(0, row.length)
                                .map(col -> row[col] * vector[col])
                                .sum()
                ).toArray();
    }

    private static int[] mul(int[] v1, int[] v2) {
        int[] v = new int[v1.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = v1[i] * v2[i];
        }
        return v;
    }

    private static int[] mul(int coef, int[] array) {
        Arrays.stream(array).forEach(e -> e *= coef);
        return array;
    }

    private static int[][] mul(int a[][], int b[][]) {
        if (a.length == 0) return new int[0][0];
        if (a[0].length != b.length) return null;

        int n = a[0].length;
        int m = a.length;
        int p = b[0].length;
        int ans[][] = new int[m][p];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    ans[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return ans;
    }

    private static int[] sum(int[] v1, int[] v2) {
        if (v1.length != v2.length) {
            throw new RuntimeException("Different dimension of vectors");
        }
        int v[] = new int[v1.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = v1[i] + v2[i];
        }
        return v;
    }

    private static int[] sub(int[] v1, int[] v2) {
        int v[] = new int[v1.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = v1[i] - v2[i];
        }
        return v;
    }

    private static int[][] sub(int[][] m1, int[][] m2) {
        if (m1.length != m2.length) {
            throw new RuntimeException("Different dimension of matrices");
        }
        int m[][] = new int[m1.length][m1.length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                m[i][j] = m1[i][j] - m2[i][j];
            }
        }
        return m;
    }
}