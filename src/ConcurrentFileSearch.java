import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConcurrentFileSearch {
    private static final int NUMBER_OF_THREADS = 10; //default automate based on system resources
    private static final BlockingQueue<Path> fileQueue = new LinkedBlockingQueue<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static String keyword;

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println("Usage: java ConcurrentFileSearch <start-directory> <keyword>");
            return;
        }

        Path startPath = Paths.get(args[0]);
        keyword = args[1];

        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            System.err.println("The provided path does not exist or is not a directory: " + startPath);
            return;
        }

        Files.walk(startPath)
                .filter(Files::isRegularFile)
                .forEach(fileQueue::add);

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            executor.execute(new FileSearchTask());
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private static class FileSearchTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Path file = fileQueue.poll(1, TimeUnit.SECONDS);
                    if (file == null) {
                        break;
                    }
                    if (Files.isDirectory(file)) {
                        continue; // Skip directories
                    }
                    searchFile(file);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private void searchFile(Path file) {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                boolean found = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(keyword)) {
                        System.out.println("Found in file: " + file.toString());
                        found = true;
                        break;
                    }
                }
                if (!found) {
//                    System.out.println("Keyword not found in file: " + file.toString());
                }
            } catch (IOException e) {
//                System.err.println("Error reading file: " + file.toString() + ", " + e.getMessage());
            }
        }

    }
}
