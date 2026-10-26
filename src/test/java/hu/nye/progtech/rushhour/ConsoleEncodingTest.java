package hu.nye.progtech.rushhour;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class ConsoleEncodingTest {

    private static final Charset FALLBACK = StandardCharsets.ISO_8859_1;

    @Test
    void usesTheStdinEncodingWhenItIsSet() {
        Properties properties = new Properties();
        properties.setProperty("stdin.encoding", "IBM852");

        assertEquals(Charset.forName("IBM852"), ConsoleEncoding.inputCharset(properties, FALLBACK));
    }

    @Test
    void usesTheLegacyPropertyWhenTheNewOneIsMissing() {
        Properties properties = new Properties();
        properties.setProperty("sun.stdin.encoding", "UTF-8");

        assertEquals(StandardCharsets.UTF_8, ConsoleEncoding.inputCharset(properties, FALLBACK));
    }

    @Test
    void skipsUnsupportedAndIllegalNames() {
        Properties properties = new Properties();
        properties.setProperty("stdin.encoding", "nincs-ilyen-kodolas");
        properties.setProperty("sun.stdin.encoding", "!!");

        assertEquals(FALLBACK, ConsoleEncoding.inputCharset(properties, FALLBACK));
    }

    @Test
    void fallsBackWhenNothingIsSet() {
        assertEquals(FALLBACK, ConsoleEncoding.inputCharset(new Properties(), FALLBACK));
    }
}
