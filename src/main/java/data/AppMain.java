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
import picocli.CommandLine;

public class AppMain {

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(AppMain.class);

    public static final String MONGO_DATABASE = "binance";

    public static void main(String[] args) throws IOException {

        var config = CommandLine.populateCommand(new AppConfig(), args);
        if (config.isUsageHelpRequested()) {
            CommandLine.usage(config, System.out);
            return;
        }

        BinanceApiClientFactory binanceClientFactory =
                BinanceApiClientFactory.newInstance();
        BinanceApiRestClient binanceRestClient = binanceClientFactory.newRestClient();

        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry).build();
        MongoDatabase mongoDatabase =
                MongoClients.create(mongoClientSettings).getDatabase(MONGO_DATABASE);

        UpdaterFactory updaterFactory =
                new UpdaterFactory(binanceRestClient, mongoDatabase, config);
        List<Updater> updaters = updaterFactory.getUpdaters();

        for (Updater updater : updaters) {
            if (config.update()) {
                update(updater, config);
            }
        }

        List<Exporter> exporters = createExporters(mongoDatabase,config);

        for (Exporter exporter : exporters) {
            if (config.export()) {
                export(exporter, config);
            }

        }

        pause();
        System.exit(0);
    }

    private static void update(Updater updater, AppConfig config) {
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

    private static void export(Exporter exporter, AppConfig config) throws IOException {
        exporter.export(config.decimalseparator());
        LOG.info("{}. export finished.", config.ticker());
    }

    private static List<Exporter> createExporters(MongoDatabase mongoDatabase, AppConfig config) {
        MongoCollection<CandleCsv> data =
                mongoDatabase.getCollection(config.ticker() + "1m", CandleCsv.class);

        return List.of(new Exporter1m(data,config.ticker()));
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
