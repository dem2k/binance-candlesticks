package tester;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import common.Utils;

public class CandleGnr {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");

    private String ticker, frame, time;
    private double open, high, low, close, volume, hlc3;

    public static CandleGnr from(String csvLine, Utils utils) {
        if (csvLine == null) {
            return null;
        }

        String[] values = csvLine.split(";");
        CandleGnr candle = new CandleGnr();
        // TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
        candle.ticker = values[0];
        candle.frame = values[1];
        candle.time = values[2];
        candle.open = utils.truncateTickSize((Double.parseDouble(values[3])));
        candle.high = utils.truncateTickSize(Double.parseDouble(values[4]));
        candle.low = utils.truncateTickSize(Double.parseDouble(values[5]));
        candle.close = utils.truncateTickSize(Double.parseDouble(values[6]));
        candle.volume = Double.parseDouble(values[7]);
        candle.hlc3 = utils.truncateTickSize((candle.high + candle.low + candle.close) / 3);

        return candle;
    }

    private CandleGnr() {
    }

    public String ticker() {
        return ticker;
    }

    public String frame() {
        return frame;
    }

    public String time() {
        return time;
    }

    public double open() {
        return open;
    }

    public double high() {
        return high;
    }

    public double low() {
        return low;
    }

    public double close() {
        return close;
    }

    public double volume() {
        return volume;
    }

    public double hlc3() {
        return hlc3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CandleGnr candle)) {
            return false;
        }
        return Double.compare(candle.open, open) == 0 && Double.compare(candle.high, high) == 0 && Double.compare(candle.low, low) == 0 && Double.compare(candle.close, close) == 0 && Double.compare(candle.volume, volume) == 0 && Objects.equals(ticker, candle.ticker) && Objects.equals(frame, candle.frame) && Objects.equals(time, candle.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, frame, time, open, high, low, close, volume);
    }

    @Override
    public String toString() {
        return "tester.Candle{" +
                "ticker='" + ticker + '\'' +
                ", frame='" + frame + '\'' +
                ", time='" + time + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }

    public long unixTime() {
        return LocalDateTime.parse(this.time(), dtf)
                .atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
