package tester;

public interface TvChart {

    void update(CandleGnr candle);

    void update(Order order, CandleGnr candle);

    void save(String fileName) throws Exception;
}
