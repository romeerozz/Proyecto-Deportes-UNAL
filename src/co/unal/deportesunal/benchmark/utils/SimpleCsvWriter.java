package co.unal.deportesunal.benchmark.utils;

import co.unal.deportesunal.persistence.FileConstant;

import java.io.*;

public class SimpleCsvWriter implements CsvWriter {

    private final BufferedWriter writer;

    public SimpleCsvWriter() throws IOException {
        this(FileConstant.INDEX_BENCHMARK_RESULTS, false);
    }

    public SimpleCsvWriter(String filePath) throws IOException {
        this(filePath, false);
    }

    public SimpleCsvWriter(String filePath, boolean append) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV file path cannot be null or empty.");
        }

        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Could not create directory: " + parentDir.getAbsolutePath());
            }
        }

        this.writer = new BufferedWriter(new FileWriter(file, append));
    }

    @Override
    public void writeHeader(String... cols) {
        writeRow(cols);
    }

    @Override
    public void writeRow(String... values) {
        try {
            if (values == null || values.length == 0) {
                writer.newLine();
                return;
            }

            for (int i = 0; i < values.length; i++) {
                if (i > 0) writer.write(",");
                writer.write(escape(values[i]));
            }

            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV row.", e);
        }
    }

    private String escape(String value) {
        if (value == null) return "";

        boolean mustQuote =
                value.contains(",") ||
                        value.contains("\"") ||
                        value.contains("\n") ||
                        value.contains("\r");

        String escaped = value.replace("\"", "\"\"");

        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}