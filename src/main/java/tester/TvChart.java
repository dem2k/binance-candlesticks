package tester;

public interface TvChart {

    void update(Candle candle);

    void update(Order order, Candle candle);

    void save(String fileName) throws Exception;
}
