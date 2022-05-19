package tester;

import org.junit.Test;

import tester.Strategy;
import tester.Wallet;

public class StrategyTest {

    @Test
    public void testFinischScreen() {
        Wallet wallet = new Wallet("BTC", "USD");
        Strategy strategy = new Strategy(null, wallet, null, null);
        strategy.finish();
    }
}
