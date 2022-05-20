package tester;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WalletTest {

    @Test
    public void testBase() {
        Wallet wallet = new Wallet("BTC", "USD");
        double result = wallet.putBase(100);
        assertEquals(100, result, 0);
        double rem = wallet.removeBase(40);
        assertEquals(60, rem, 0);
    }

    @Test
    public void testQuote() {
        Wallet wallet = new Wallet("BTC", "USD");
        double result = wallet.putQuote(100);
        assertEquals(100, result, 0);
        double rem = wallet.removeQuote(40);
        assertEquals(60, rem, 0);
    }

    @Test
    public void testRemoveBase() {
        Wallet wallet = new Wallet("BTC", "USD");
        wallet.putQuote(100);
        double result = wallet.payFeeOf(25);
        assertEquals(99.975, result, 0);

    }


}
