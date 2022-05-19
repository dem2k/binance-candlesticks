package tester;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class CandleStreamer implements Iterator<Candle> {

    private Candle next;
    private boolean hasNext;
    BufferedReader reader;
    private Utils utils;
    private double startPrice = 0;

    public CandleStreamer(String csvFile, Utils utils) throws IOException {
        reader = Files.newBufferedReader(Path.of(csvFile));
        this.utils = utils;
//        // skip sep and header
//        reader.readLine();
//        reader.readLine();
        // init first next
        this.next = Candle.fromCsv(reader.readLine(),utils);
        this.hasNext = this.next != null;
        this.startPrice = next().hlc3();
    }

    @Override
    public Candle next() {
        if (!hasNext) {
            throw new RuntimeException("Next element missed");
        }
        Candle result = next;
        next = Candle.fromCsv(readNextLine(),utils);
        hasNext = next != null;
        return result;
    }
    
    @Override
    public boolean hasNext() {
        return hasNext;
    }

    private String readNextLine() {
        String newNext = null;
        try {
            newNext = reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newNext;
    }

    public double startPrice() {
        return startPrice;
    }
}
