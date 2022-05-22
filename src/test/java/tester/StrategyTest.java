package tester;

import org.junit.Test;

public class StrategyTest {

    @Test
    public void testFinischScreen() {
        Wallet wallet = new Wallet("BTC", "USD", null);
        Strategy strategy = new Strategy(null, wallet, null, null);
        strategy.finish();
    }
}
