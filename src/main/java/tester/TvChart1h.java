package tester;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvChart1h implements TvChart {

    private Utils utils;

    private List<String> markers = new ArrayList<>();
    private List<String> gridLines = new ArrayList<>();

    private Map<String, List<Candle>> series = new HashMap<>();

    private String last = "";

    public TvChart1h(final Grid grid, final Utils utils) {
        this.utils = utils;
        gridLines.addAll(grid.orders().stream()
                .sorted(Comparator.comparing(Order::price))
                .map(order -> utils.truncateTickSize(order.price()))
                .map(Object::toString)
                .toList());
    }

    @Override
    public void update(Candle candle) {

        if (last.equals(candle.time())) {
            return;
        }
        last = candle.time();

        // time format: 2021-03-17T08-22
        String key = candle.time().substring(0, 13);
        merge(series, key, candle);
    }

    @Override
    public void update(Order order, Candle candle) {
        String template = "{ time: %s, position: '%s', color: '%s', shape: '%s' },\n";
        long time = truncate1h(candle.time().substring(0, 13));
        String pos = order.side() == OrderType.BUY ? "belowBar" : "aboveBar";
        String color = order.side() == OrderType.BUY ? "Green" : "Red";
        String shape = order.side() == OrderType.BUY ? "arrowUp" : "arrowDown";
        this.markers.add(String.format(template, time, pos, color, shape));
    }

    @Override
    public void save(String fileName) throws Exception {
        BufferedWriter writer = Files.newBufferedWriter(Path.of(fileName),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        writer.write(header());
        writer.write(gridLines());
        writer.write(markers());
        writer.write(series());
        writer.write(footer());
        writer.close();
    }

    private long truncate1h(String time) {
        return LocalDateTime.parse(time, dtf)
                .atZone(ZoneId.systemDefault()).toEpochSecond();

    }

    private void merge(Map<String, List<Candle>> map, String key, Candle candle) {
        List<Candle> candles =
                map.computeIfAbsent(key, k -> new ArrayList<>());
        candles.add(candle);
    }

    private String header() {
        String template = """
                <html>
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                </head>
                <body>
                	<script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
                	<script>
                const chart = LightweightCharts.createChart(document.body);
                chart.applyOptions({grid:{vertLines:{visible:false}, horzLines:{visible:false}}, crosshair:{mode:0}});
                                
                chart.timeScale().fitContent();
                chart.timeScale().applyOptions({timeVisible:true});
                                
                const series = chart.addCandlestickSeries({priceFormat:{ type: 'price', precision: %s, minMove: %s }});
                series.applyOptions({lastValueVisible:false,priceLineVisible:false});
                """;
        return String.format(template, utils.tickSize(), utils.tickSizeOrigin());
    }

    private String footer() {
        String footer = """
                	</script>
                </body></html>
                """;
        return footer;
    }

    private String series() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("series.setData([\n");
        candles1mTo1h().forEach(buffer::append);
        buffer.append("]);\n");
        return buffer.toString();
    }

    private List<String> candles1mTo1h() {
        return series.keySet().stream().sorted()
                .map(this::toStringOfSeries)
                .toList();
    }

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    private String toStringOfSeries(String key) {
        long time = truncate1h(key);
        List<Candle> candles = series.get(key);
        Double open = candles.stream().min(Comparator.comparing(Candle::time)).map(Candle::open).orElse(0d);
        Double high = candles.stream().max(Comparator.comparing(Candle::high)).map(Candle::high).orElse(0d);
        Double low = candles.stream().min(Comparator.comparing(Candle::low)).map(Candle::low).orElse(0d);
        Double close = candles.stream().max(Comparator.comparing(Candle::time)).map(Candle::close).orElse(0d);

        return String.format("{ time: %s, open: %s, high: %s, low: %s, close: %s },\n",
                time, open, high, low, close);
    }

    private String gridLines() {
        String template = """
                series.createPriceLine({price: %s, color: 'Silver', lineStyle: LightweightCharts.LineStyle.LargeDashed});
                """;
        StringBuilder buffer = new StringBuilder();
        gridLines.forEach(price -> buffer.append(String.format(template, price)));
        return buffer.toString();
    }

    private String markers() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("series.setMarkers([\n");
        markers.forEach(buffer::append);
        buffer.append("]);\n");
        return buffer.toString();
    }
}
