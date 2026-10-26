package hu.nye.progtech.rushhour;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

/**
 * A konzol karakterkódolásának meghatározása. A játék nem kényszerít rá UTF-8-at a konzolra, hanem azt
 * a kódolást használja, amelyet a JVM a konzolhoz talált (Windowson ez a konzol kódlapja, pl. cp852),
 * így az ékezetes betűk parancssorban és az IDE-ben is jól jelennek meg.
 */
public final class ConsoleEncoding {

    private static final List<String> INPUT_PROPERTIES = List.of("stdin.encoding", "sun.stdin.encoding");

    private ConsoleEncoding() {
        // csak statikus metódusok
    }

    /**
     * A konzolról olvasáshoz használt kódolás: a JVM {@code stdin.encoding} tulajdonsága, ha van és
     * érvényes, különben a megadott tartalék (a kimenet kódolása, ami a konzolon ugyanaz a kódlap).
     */
    public static Charset inputCharset(Properties properties, Charset fallback) {
        for (String key : INPUT_PROPERTIES) {
            String name = properties.getProperty(key);
            if (name != null && isSupported(name)) {
                return Charset.forName(name);
            }
        }
        return fallback;
    }

    private static boolean isSupported(String name) {
        try {
            return Charset.isSupported(name);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
