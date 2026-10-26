package hu.nye.progtech.rushhour.model;

/** A jármű rögzített, megváltoztathatatlan tengelye. */
public enum Orientation {
    HORIZONTAL("vízszintesen"),
    VERTICAL("függőlegesen");

    private final String label;

    Orientation(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
