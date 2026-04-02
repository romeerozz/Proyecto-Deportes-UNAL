package co.unal.deportesunal.domain;

public class SportCount {
    private final SportEnum sport;
    private int count;

    public SportCount(SportEnum sport) {
        this.sport = sport;
        this.count = 0;
    }

    public SportEnum getSport() { return sport; }
    public int getCount() { return count; }

    public void increment() { count++; }
    public void reset() { count = 0; }
}
