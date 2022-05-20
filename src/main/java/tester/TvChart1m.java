package tester;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import common.CommonUtils;

public class TvChart1m implements TvChart {

    private CommonUtils utils;

    private List<String> markers = new ArrayList<>();
    private List<String> gridLines = new ArrayList<>();

    private Set<String> series = new TreeSet<>();

    private String last = "";

    public TvChart1m(final Grid grid, final CommonUtils utils) {
        this.utils = utils;
        gridLines.addAll(grid.orders().stream()
                .sorted(Comparator.comparing(Order::price))
                .map(order -> utils.truncateTickSize(order.price()))
                .map(Object::toString)
                .toList());
    }

    @Override
    public void update(CandleGnr candle) {

        if (last.equals(candle.time())) {
            return;
        }
        last = candle.time();

        String data = String.format("{ time: %s, value: %s }, \t// %s%n",
                candle.unixTime(), candle.hlc3(), candle.time()
        );

        series.add(data);
    }

    @Override
    public void update(Order order, CandleGnr candle) {
        String template = "{ time: %s, position: '%s', color: '%s', shape: '%s' },\n";
        long time = candle.unixTime();
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
                                
                const series = chart.addLineSeries({priceFormat:{ type: 'price', precision: %s, minMove: %s }});
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
        series.forEach(buffer::append);
        buffer.append("]);\n");
        return buffer.toString();
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
