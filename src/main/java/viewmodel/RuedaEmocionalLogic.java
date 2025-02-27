package viewmodel;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class RuedaEmocionalLogic {

    public static final String[][] EMOTIONS = {
        {"Éxtasis", "Alegría", "Serenidad"},
        {"Pasmo", "Confianza", "Aceptación"},
        {"Terror", "Miedo", "Temor"},
        {"Asombro", "Sorpresa", "Distracción"},
        {"Pena", "Tristeza", "Melancolía"},
        {"Odio", "Aversión", "Tedio"},
        {"Furia", "Ira", "Enfado"},
        {"Vigilancia", "Anticipación", "Interés"}
    };

    private static final Map<String, Color> EMOTION_COLORS = new HashMap<>();
    static {
        EMOTION_COLORS.put("Éxtasis", Color.YELLOW);
        EMOTION_COLORS.put("Pasmo", new Color(144, 238, 144)); // Verde claro
        EMOTION_COLORS.put("Terror", new Color(0, 100, 0)); // Verde oscuro
        EMOTION_COLORS.put("Asombro", new Color(173, 216, 230)); // Azul claro
        EMOTION_COLORS.put("Pena", new Color(0, 0, 128)); // Azul oscuro
        EMOTION_COLORS.put("Odio", new Color(255, 182, 193)); // Rosado
        EMOTION_COLORS.put("Furia", Color.RED);
        EMOTION_COLORS.put("Vigilancia", new Color(255, 140, 0)); // Naranja
    }

    public static Color getBaseColor(String emotion) {
        return EMOTION_COLORS.get(emotion);
    }

    public static Color adjustBrightness(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() * factor));
        int g = Math.min(255, (int) (color.getGreen() * factor));
        int b = Math.min(255, (int) (color.getBlue() * factor));
        return new Color(r, g, b);
    }
}