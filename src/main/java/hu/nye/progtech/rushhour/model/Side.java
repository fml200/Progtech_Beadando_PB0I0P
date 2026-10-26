package hu.nye.progtech.rushhour.model;

/** A két játékos. Mindegyikhez egy rögzített azonosítójú céljármű tartozik. */
public enum Side {
    HUMAN('A', "Játékos"),
    MACHINE('B', "Gép");

    private final char heroId;
    private final String label;

    Side(char heroId, String label) {
        this.heroId = heroId;
        this.label = label;
    }

    public char getHeroId() {
        return heroId;
    }

    public String getLabel() {
        return label;
    }

    public Side opponent() {
        return this == HUMAN ? MACHINE : HUMAN;
    }
}
