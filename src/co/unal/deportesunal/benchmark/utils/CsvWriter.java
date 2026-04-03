package co.unal.deportesunal.benchmark.utils;

import java.io.Closeable;

public interface CsvWriter extends Closeable {
    void writeHeader(String... cols);
    void writeRow(String... values);
}
