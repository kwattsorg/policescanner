package net.kwatts.android.NetScanner;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getRedirectUrl() {
        String inputUrl = "http://relay.broadcastify.com/zmr5pk7qxvycs10";
        String outUrl = Util.getRedirectUrl(inputUrl);
    }
}