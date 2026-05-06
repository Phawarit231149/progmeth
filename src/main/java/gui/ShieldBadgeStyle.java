package gui;

/** Shared inline JavaFX style strings for the shield badge label. */
public final class ShieldBadgeStyle {

    private ShieldBadgeStyle() {}

    public static final String BASE =
            "-fx-font-size: 11px; -fx-font-weight: bold; " +
                    "-fx-min-width: 18px; -fx-min-height: 18px; " +
                    "-fx-background-radius: 9; -fx-alignment: center; " +
                    "-fx-padding: 0 3 0 3;";

    public static final String ACTIVE =
            "-fx-background-color: #43a047; -fx-text-fill: white; " + BASE;

    public static final String INACTIVE =
            "-fx-background-color: #e53935; -fx-text-fill: white; " + BASE;
}