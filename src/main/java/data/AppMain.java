package data;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import common.MongoUtils;
import picocli.CommandLine;

public class AppMain {

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(AppMain.class);
    
    private AppConfig config;

    public static void main(String[] args) throws Exception {

        var config = CommandLine.populateCommand(new AppConfig(), args);
        if (config.isUsageHelpRequested()) {
            CommandLine.usage(config, System.out);
            return;
        }

        var app = new AppMain(config);
        app.start();
        System.exit(0);
    }

    public AppMain(AppConfig config) {
        this.config = config;
    }

    private void start() throws Exception {
        MongoDatabase database = MongoUtils.mongoDatabase();

        if (config.update()) {
            var binance =
                    BinanceApiClientFactory.newInstance().newRestClient();
            var updaterFactory =
                    new UpdaterFactory(binance, database, config);
            List<Updater> updaters = updaterFactory.getUpdaters();
            for (Updater updater : updaters) {
                update(updater, config);
            }
        }

        if (config.export()) {
            List<Exporter> exporters = createExporters(database, config);
            for (Exporter exporter : exporters) {
                exporter.export(config.decimalseparator());
                LOG.info("{}. export finished.", config.ticker());
            }
        }
    }

    private void update(Updater updater, AppConfig config) {
        var atDay = LocalDate.now();

        boolean doNext = true;
        while (doNext) {
            atDay = atDay.minusDays(1);
            if (config.check()) {
                updater.checkAndClean(atDay);
            }
            doNext = updater.update(atDay);
        }

        LOG.info("{}. update finished.", config.ticker());
    }

    private List<Exporter> createExporters(MongoDatabase mongoDatabase, AppConfig config) {
        MongoCollection<CandleCsv> data =
                mongoDatabase.getCollection(config.ticker() + "1m", CandleCsv.class);

        return List.of(new Exporter1m(data, config.ticker()));
    }

    private static void pause() {
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
