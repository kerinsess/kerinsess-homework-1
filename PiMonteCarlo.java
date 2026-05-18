import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * ДЗ 1. Лінійне обчислення числа Пі за методом Монте-Карло
 *
 * Запуск:
 *   javac PiMonteCarlo.java
 *   java PiMonteCarlo
 *
 * Результати: results/pi_monte_carlo_results.csv
 */
public class PiMonteCarlo {

    static final double PI_TRUE = Math.PI;

    static final long[] N_VALUES = {
        1_000_000L,
        10_000_000L,
        100_000_000L,
        1_000_000_000L,
        10_000_000_000L,
        100_000_000_000L
    };

    static final String OUTPUT_DIR  = "results";
    static final String OUTPUT_FILE = OUTPUT_DIR + "/pi_monte_carlo_results.csv";

    // ---------------------------------------------------------------

    /**
     * Лінійний алгоритм Монте-Карло.
     * Генеруємо точки (x, y) у квадраті [0,1]x[0,1].
     * Точка в чверті кола: x^2 + y^2 <= 1
     * π ≈ 4 * inside / n
     */
    static double[] monteCarloPi(long n) {
        Random rnd = new Random();
        long inside = 0;

        long startNs = System.nanoTime();

        for (long i = 0; i < n; i++) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            if (x * x + y * y <= 1.0) {
                inside++;
            }
        }

        long endNs = System.nanoTime();

        double piEst   = 4.0 * inside / n;
        double elapsed = (endNs - startNs) / 1_000_000_000.0; // секунди
        return new double[]{ piEst, elapsed };
    }

    // ---------------------------------------------------------------

    static String formatN(long n) {
        // 1000000 -> "1_000_000"
        String s = String.valueOf(n);
        StringBuilder sb = new StringBuilder();
        int rem = s.length() % 3;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i - rem) % 3 == 0) sb.append('_');
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------

    public static void main(String[] args) throws IOException {

        Files.createDirectories(Paths.get(OUTPUT_DIR));

        System.out.println("=".repeat(76));
        System.out.println("  Метод Монте-Карло — обчислення числа π  (лінійний алгоритм, Java)");
        System.out.printf("  Справжнє π = %.15f%n", PI_TRUE);
        System.out.println("=".repeat(76));
        System.out.printf("  %-20s  %-16s  %-14s  %-12s  %-18s%n",
                "N", "π оцінка", "точність", "час (с)", "time_per_point");
        System.out.println("-".repeat(76));

        try (PrintWriter csv = new PrintWriter(new FileWriter(OUTPUT_FILE))) {

            csv.println("N,pi_estimated,accuracy,execution_time_sec,time_per_point");

            for (long n : N_VALUES) {
                System.out.printf("  %-20s  обчислення...%n", formatN(n));

                double[] res       = monteCarloPi(n);
                double piEst       = res[0];
                double elapsed     = res[1];
                double accuracy    = Math.abs(piEst - PI_TRUE);
                double timePerPt   = elapsed / n;

                // Консоль
                System.out.printf("\033[1A\033[2K"); // перезаписати попередній рядок
                System.out.printf("  %-20s  %-16.10f  %-14.10f  %-12.3f  %-18.2e%n",
                        formatN(n), piEst, accuracy, elapsed, timePerPt);

                // CSV
                csv.printf("%d,%.10f,%.10f,%.6f,%.2e%n",
                        n, piEst, accuracy, elapsed, timePerPt);
            }
        }

        System.out.println("=".repeat(76));
        System.out.println("\n  Результати збережено у: " + OUTPUT_FILE);
    }
}
