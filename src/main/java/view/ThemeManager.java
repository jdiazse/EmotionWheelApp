package view;

import java.awt.Color;

public class ThemeManager {
    public static final Color DARK_BACKGROUND = new Color(10, 10, 40);
    public static final Color DARK_FOREGROUND = Color.WHITE;
    public static final Color DARK_BUTTON_BACKGROUND = new Color(30, 30, 70);
    public static final Color DARK_BUTTON_FOREGROUND = Color.WHITE;

    public static final Color LIGHT_BACKGROUND = Color.WHITE;
    public static final Color LIGHT_FOREGROUND = Color.BLACK;
    public static final Color LIGHT_BUTTON_BACKGROUND = new Color(240, 240, 240);
    public static final Color LIGHT_BUTTON_FOREGROUND = Color.BLACK;

    private static boolean isDarkTheme = true;

    public static boolean isDarkTheme() {
        return isDarkTheme;
    }

    public static void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    public static Color getBackgroundColor() {
        return isDarkTheme ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    public static Color getForegroundColor() {
        return isDarkTheme ? DARK_FOREGROUND : LIGHT_FOREGROUND;
    }

    public static Color getButtonBackgroundColor() {
        return isDarkTheme ? DARK_BUTTON_BACKGROUND : LIGHT_BUTTON_BACKGROUND;
    }

    public static Color getButtonForegroundColor() {
        return isDarkTheme ? DARK_BUTTON_FOREGROUND : LIGHT_BUTTON_FOREGROUND;
    }
}