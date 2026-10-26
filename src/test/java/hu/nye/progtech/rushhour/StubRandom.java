package hu.nye.progtech.rushhour;

import java.util.Random;

/**
 * Előre megadott számokat visszaadó véletlenszám-generátor a tesztekhez. Ha a megadott számok
 * elfogynak, mindig 0-t ad. Ellenőrzi, hogy a kért érték a megadott korláton belül van-e.
 */
public final class StubRandom extends Random {

    private static final long serialVersionUID = 1L;

    private final transient int[] values;
    private int next;

    public StubRandom(int... values) {
        this.values = values.clone();
    }

    @Override
    public int nextInt(int bound) {
        int value = next < values.length ? values[next] : 0;
        next++;
        if (value < 0 || value >= bound) {
            throw new IllegalStateException("A(z) " + value + " érték nem esik a [0, " + bound + ") tartományba.");
        }
        return value;
    }
}
