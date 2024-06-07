import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.*;

public class ConcurrentFileSearch {
    private static final int NUMBER_OF_THREADS = 10;
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

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!attrs.isDirectory()) {
                    try {
                        fileQueue.put(file);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            executor.execute(new FileSearchTask());
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private static class FileSearchTask implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Path file = fileQueue.poll(1, TimeUnit.SECONDS);
                    if (file == null) {
                        break;
                    }
                    searchFile(file);
                }
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void searchFile(Path file) throws IOException {
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(keyword)) {
                        System.out.println("Found in file: " + file.toString());
                        break;
                    }
                }
            }
        }
    }
}
