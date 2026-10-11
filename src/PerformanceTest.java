import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PerformanceTest {
    private static final int[] SIZES = {100, 1_000, 10_000};
    private static final int WARMUP_RUNS = 5;
    private static final int MEASURED_RUNS = 20;
    private static final long RANDOM_SEED = 20261010L;
    private static final int KEY_MULTIPLIER = 0x9E3779B9;
    private static volatile long blackhole;

    private record Times(long insertNanos, long searchNanos, long deleteNanos) {
    }

    private record Result(int size, String structure, double insertNanos,
                          double searchNanos, double deleteNanos) {
    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        List<Result> results = new ArrayList<>();

        for (int size : SIZES) {
            warmUp(size);
            results.add(measureBst(size));
            results.add(measureHashTable(size));
        }

        String report = buildTextReport(results);
        String csv = buildCsv(results);

        Path resultsDirectory = Path.of("results");
        Files.createDirectories(resultsDirectory);
        Files.writeString(resultsDirectory.resolve("benchmark_output.txt"), report);
        Files.writeString(resultsDirectory.resolve("benchmark_results.csv"), csv);

        System.out.print(report);
        System.out.println("CSV saved to results/benchmark_results.csv");
        System.out.println("Text report saved to results/benchmark_output.txt");
    }

    private static void warmUp(int size) {
        for (int run = 0; run < WARMUP_RUNS; run++) {
            int[] insertKeys = shuffledKeys(size, RANDOM_SEED + size + run);
            int[] searchKeys = mixedSearchKeys(size, RANDOM_SEED + 10_000 + run);
            int[] deleteKeys = shuffledCopy(insertKeys, RANDOM_SEED + 20_000 + run);
            runBst(insertKeys, searchKeys, deleteKeys);
            runHashTable(insertKeys, searchKeys, deleteKeys);
        }
    }

    private static Result measureBst(int size) {
        long insertTotal = 0;
        long searchTotal = 0;
        long deleteTotal = 0;

        for (int run = 0; run < MEASURED_RUNS; run++) {
            int[] insertKeys = shuffledKeys(size, RANDOM_SEED + size * 100L + run);
            int[] searchKeys = mixedSearchKeys(size, RANDOM_SEED + 30_000 + run);
            int[] deleteKeys = shuffledCopy(insertKeys, RANDOM_SEED + 40_000 + run);
            Times times = runBst(insertKeys, searchKeys, deleteKeys);
            insertTotal += times.insertNanos;
            searchTotal += times.searchNanos;
            deleteTotal += times.deleteNanos;
        }

        return new Result(size, "BST",
                insertTotal / (double) MEASURED_RUNS,
                searchTotal / (double) MEASURED_RUNS,
                deleteTotal / (double) MEASURED_RUNS);
    }

    private static Result measureHashTable(int size) {
        long insertTotal = 0;
        long searchTotal = 0;
        long deleteTotal = 0;

        for (int run = 0; run < MEASURED_RUNS; run++) {
            int[] insertKeys = shuffledKeys(size, RANDOM_SEED + size * 100L + run);
            int[] searchKeys = mixedSearchKeys(size, RANDOM_SEED + 30_000 + run);
            int[] deleteKeys = shuffledCopy(insertKeys, RANDOM_SEED + 40_000 + run);
            Times times = runHashTable(insertKeys, searchKeys, deleteKeys);
            insertTotal += times.insertNanos;
            searchTotal += times.searchNanos;
            deleteTotal += times.deleteNanos;
        }

        return new Result(size, "Hash Table",
                insertTotal / (double) MEASURED_RUNS,
                searchTotal / (double) MEASURED_RUNS,
                deleteTotal / (double) MEASURED_RUNS);
    }

    private static Times runBst(int[] insertKeys, int[] searchKeys, int[] deleteKeys) {
        BinarySearchTree tree = new BinarySearchTree();

        long start = System.nanoTime();
        for (int key : insertKeys) {
            tree.insert(key);
        }
        long insertTime = System.nanoTime() - start;

        int found = 0;
        start = System.nanoTime();
        for (int key : searchKeys) {
            if (tree.contains(key)) {
                found++;
            }
        }
        long searchTime = System.nanoTime() - start;

        int deleted = 0;
        start = System.nanoTime();
        for (int key : deleteKeys) {
            if (tree.delete(key)) {
                deleted++;
            }
        }
        long deleteTime = System.nanoTime() - start;

        verifyResults(insertKeys.length / 2, insertKeys.length, found, deleted, tree.size());
        blackhole += found + deleted;
        return new Times(insertTime, searchTime, deleteTime);
    }

    private static Times runHashTable(int[] insertKeys, int[] searchKeys, int[] deleteKeys) {
        SimpleHashTable table = new SimpleHashTable();

        long start = System.nanoTime();
        for (int key : insertKeys) {
            table.insert(key);
        }
        long insertTime = System.nanoTime() - start;

        int found = 0;
        start = System.nanoTime();
        for (int key : searchKeys) {
            if (table.contains(key)) {
                found++;
            }
        }
        long searchTime = System.nanoTime() - start;

        int deleted = 0;
        start = System.nanoTime();
        for (int key : deleteKeys) {
            if (table.delete(key)) {
                deleted++;
            }
        }
        long deleteTime = System.nanoTime() - start;

        verifyResults(insertKeys.length / 2, insertKeys.length, found, deleted, table.size());
        blackhole += found + deleted;
        return new Times(insertTime, searchTime, deleteTime);
    }

    private static void verifyResults(int expectedFound, int expectedDeleted,
                                      int found, int deleted, int finalSize) {
        if (found != expectedFound || deleted != expectedDeleted || finalSize != 0) {
            throw new IllegalStateException("A data structure returned an incorrect result");
        }
    }

    private static int[] shuffledKeys(int size, long seed) {
        List<Integer> values = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            values.add(i * KEY_MULTIPLIER);
        }
        Collections.shuffle(values, new Random(seed));
        return values.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int[] shuffledCopy(int[] source, long seed) {
        int[] copy = source.clone();
        Random random = new Random(seed);
        for (int i = copy.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temporary = copy[i];
            copy[i] = copy[j];
            copy[j] = temporary;
        }
        return copy;
    }

    private static int[] mixedSearchKeys(int size, long seed) {
        int[] keys = new int[size];
        for (int i = 0; i < size / 2; i++) {
            keys[i] = i * KEY_MULTIPLIER; // present
            keys[i + size / 2] = (size + i) * KEY_MULTIPLIER; // absent
        }
        return shuffledCopy(keys, seed);
    }

    private static String buildCsv(List<Result> results) {
        StringBuilder text = new StringBuilder();
        text.append("elements,structure,operation,average_total_ms,average_ns_per_operation\n");
        for (Result result : results) {
            appendCsvRow(text, result.size, result.structure, "insert", result.insertNanos);
            appendCsvRow(text, result.size, result.structure, "search", result.searchNanos);
            appendCsvRow(text, result.size, result.structure, "delete", result.deleteNanos);
        }
        return text.toString();
    }

    private static void appendCsvRow(StringBuilder text, int size, String structure,
                                     String operation, double totalNanos) {
        text.append(String.format(Locale.US, "%d,%s,%s,%.6f,%.2f%n",
                size, structure, operation, totalNanos / 1_000_000.0, totalNanos / size));
    }

    private static String buildTextReport(List<Result> results) {
        StringBuilder text = new StringBuilder();
        text.append("BST and Hash Table benchmark\n");
        text.append("Random seed: ").append(RANDOM_SEED).append('\n');
        text.append("Warm-up runs: ").append(WARMUP_RUNS).append('\n');
        text.append("Measured runs: ").append(MEASURED_RUNS).append(" (reported value is the mean)\n\n");
        text.append(String.format("%-8s %-12s %12s %12s %12s%n",
                "Elements", "Structure", "Insert ms", "Search ms", "Delete ms"));
        text.append("------------------------------------------------------------------\n");
        for (Result result : results) {
            text.append(String.format(Locale.US, "%-8d %-12s %12.6f %12.6f %12.6f%n",
                    result.size, result.structure,
                    result.insertNanos / 1_000_000.0,
                    result.searchNanos / 1_000_000.0,
                    result.deleteNanos / 1_000_000.0));
        }
        return text.toString();
    }
}
