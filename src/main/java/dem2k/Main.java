package dem2k;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.IOException;
import java.time.LocalDate;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import picocli.CommandLine;

public class Main {

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(Main.class);

    public static final String MONGO_DATABASE = "binance";

    public static void main(String[] args) throws IOException {

        var config = CommandLine.populateCommand(new Config(), args);
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
        MongoCollection<TfCandle> mongoCollection =
                mongoDatabase.getCollection(config.ticker(), TfCandle.class);

        Updater updater =
                new Updater5m(binanceRestClient, mongoCollection, config.ticker());

        if (config.update()) {
            runUpdate(config, updater);
        }

        if (config.export()) {
            runExport(config, updater);
        }

        pause();
        System.exit(0);
    }

    private static void runUpdate(Config config, Updater updater) {
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

    private static void runExport(Config config, Updater updater) throws IOException {
        updater.export();
        LOG.info("{}. export finished.", config.ticker());
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
