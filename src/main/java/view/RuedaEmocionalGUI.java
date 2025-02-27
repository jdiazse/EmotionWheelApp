package view;

import model.FirebaseManager;
import model.OpenAIAPI;
import viewmodel.RuedaEmocionalLogic;

import javax.swing.*;
import java.awt.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;

public class RuedaEmocionalGUI {

    private static String userId;
    private static JFrame frame;
    private static JPanel wheelPanel;
    private static JPanel rightButtonPanel;
    private static JPanel leftButtonPanel;
    private static JMenuBar menuBar;

    public static void launchWheel(String loggedInUserId) {
        userId = loggedInUserId;

        frame = new JFrame("EmotionWheel desktop app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        wheelPanel = new JPanel(null);
        wheelPanel.setPreferredSize(new Dimension(800, 800));
        mainPanel.add(wheelPanel, BorderLayout.CENTER);

        rightButtonPanel = new JPanel();
        rightButtonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        rightButtonPanel.setPreferredSize(new Dimension(150, 250));

        JButton viewLogsButton = new RoundedButton("Ver Logs");
        JButton editLogButton = new RoundedButton("Editar Log");
        JButton deleteLogButton = new RoundedButton("Eliminar Log");
        JButton deleteAllLogsButton = new RoundedButton("Eliminar Todos");

        rightButtonPanel.add(viewLogsButton);
        rightButtonPanel.add(editLogButton);
        rightButtonPanel.add(deleteLogButton);
        rightButtonPanel.add(deleteAllLogsButton);

        leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        leftButtonPanel.setPreferredSize(new Dimension(150, 150));

        JButton provisionalDiagnosisButton = new RoundedButton("Diagnóstico provisional");
        JButton professionalDiagnosisButton = new RoundedButton("Diagnóstico profesional");

        leftButtonPanel.add(provisionalDiagnosisButton);
        leftButtonPanel.add(professionalDiagnosisButton);

        menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Tema");
        themeMenu.setForeground(ThemeManager.getForegroundColor());
        JMenuItem lightThemeItem = new JMenuItem("Tema Claro");
        JMenuItem darkThemeItem = new JMenuItem("Tema Oscuro");

        lightThemeItem.addActionListener(e -> {
            ThemeManager.setDarkTheme(false);
            applyTheme();
        });

        darkThemeItem.addActionListener(e -> {
            ThemeManager.setDarkTheme(true);
            applyTheme();
        });

        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);
        menuBar.add(themeMenu);

        frame.setJMenuBar(menuBar);

        mainPanel.add(rightButtonPanel, BorderLayout.EAST);
        mainPanel.add(leftButtonPanel, BorderLayout.WEST);

        applyTheme();
        createWheel();

        viewLogsButton.addActionListener(e -> viewLogs());
        editLogButton.addActionListener(e -> editLog());
        deleteLogButton.addActionListener(e -> deleteLog());
        deleteAllLogsButton.addActionListener(e -> deleteAllLogs());
        provisionalDiagnosisButton.addActionListener(e -> showProvisionalDiagnosis());
        professionalDiagnosisButton.addActionListener(e -> showProfessionalDiagnosis());

        frame.setVisible(true);
    }

    private static void createWheel() {
        int centerX = 470;
        int centerY = 330;
        int radius = 300;
        int buttonSize = 2 * radius;

        Color[] emotionColors = {
            new Color(252, 177, 3),
            new Color(144, 238, 144),
            new Color(0, 100, 0),
            new Color(173, 216, 230),
            Color.BLUE,
            Color.PINK,
            Color.RED,
            Color.ORANGE
        };

        for (int i = 0; i < RuedaEmocionalLogic.EMOTIONS.length; i++) {
            double startAngle = 45 * i;
            double arcAngle = 45;

            for (int j = 0; j < RuedaEmocionalLogic.EMOTIONS[i].length; j++) {
                double innerRadius = radius * j / RuedaEmocionalLogic.EMOTIONS[i].length;
                double outerRadius = radius * (j + 1) / RuedaEmocionalLogic.EMOTIONS[i].length;

                SectorButton button = new SectorButton(
                    RuedaEmocionalLogic.EMOTIONS[i][j], 
                    startAngle, 
                    arcAngle, 
                    (int) innerRadius, 
                    (int) outerRadius, 
                    buttonSize
                );

                float intensityFactor = 1.0f - (j * 0.2f);
                Color baseColor = emotionColors[i];
                Color adjustedColor = RuedaEmocionalLogic.adjustBrightness(baseColor, intensityFactor);

                button.setBackground(adjustedColor);
                button.setForeground(ThemeManager.getForegroundColor());
                button.setBounds(centerX - radius, centerY - radius, buttonSize, buttonSize);

                button.addActionListener(e -> {
                    String selectedEmotion = ((SectorButton) e.getSource()).getText();
                    showCommentDialog(selectedEmotion);
                });

                wheelPanel.add(button);
            }
        }
    }

    private static void showCommentDialog(String selectedEmotion) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBackground(ThemeManager.getBackgroundColor());

        JLabel label = new JLabel("Comentarios? " + selectedEmotion + ":");
        label.setForeground(ThemeManager.getForegroundColor());
        JTextField textField = new JTextField(20);
        textField.setBackground(ThemeManager.getButtonBackgroundColor());
        textField.setForeground(ThemeManager.getButtonForegroundColor());

        panel.add(label);
        panel.add(textField);

        applyThemeToDialogs();

        int result = JOptionPane.showConfirmDialog(
            frame,
            panel,
            "Comentario",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String comment = textField.getText();
            if (comment != null && !comment.trim().isEmpty()) {
                FirebaseManager.saveEmotionToFirebase(userId, selectedEmotion, comment);
                JOptionPane.showMessageDialog(frame, "Comentario guardado exitosamente!");
            }
        }
    }

    private static void applyTheme() {
        Color bgColor = ThemeManager.getBackgroundColor();
        Color fgColor = ThemeManager.getForegroundColor();
        Color btnBgColor = ThemeManager.getButtonBackgroundColor();
        Color btnFgColor = ThemeManager.getButtonForegroundColor();

        frame.getContentPane().setBackground(bgColor);
        wheelPanel.setBackground(bgColor);
        rightButtonPanel.setBackground(bgColor);
        leftButtonPanel.setBackground(bgColor);

        for (Component comp : wheelPanel.getComponents()) {
            if (comp instanceof SectorButton) {
                ((SectorButton) comp).setForeground(fgColor);
            }
        }

        updateButtonColors(rightButtonPanel, btnBgColor, btnFgColor);
        updateButtonColors(leftButtonPanel, btnBgColor, btnFgColor);

        menuBar.setBackground(bgColor);
        menuBar.setForeground(fgColor);
        ((JMenu) menuBar.getComponent(0)).setForeground(fgColor);

        applyThemeToDialogs();
    }

    private static void applyThemeToDialogs() {
        UIManager.put("OptionPane.background", ThemeManager.getBackgroundColor());
        UIManager.put("Panel.background", ThemeManager.getBackgroundColor());
        UIManager.put("OptionPane.messageForeground", ThemeManager.getForegroundColor());
        UIManager.put("OptionPane.buttonBackground", ThemeManager.getButtonBackgroundColor());
        UIManager.put("OptionPane.buttonForeground", ThemeManager.getButtonForegroundColor());
        UIManager.put("TextField.background", ThemeManager.getButtonBackgroundColor());
        UIManager.put("TextField.foreground", ThemeManager.getButtonForegroundColor());
    }

    private static void updateButtonColors(JPanel panel, Color bgColor, Color fgColor) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(bgColor);
                btn.setForeground(fgColor);
            }
        }
    }

    private static void toggleTheme() {
        ThemeManager.setDarkTheme(!ThemeManager.isDarkTheme());
        applyTheme();
        wheelPanel.repaint();
    }

    private static void viewLogs() {
        FirebaseManager.fetchEmotionLogs(userId, new FirebaseManager.EmotionLogsListener() {
            @Override
            public void onLogsFetched(List<Map<String, Object>> logs) {
                StringBuilder sb = new StringBuilder();
                for (Map<String, Object> log : logs) {
                    sb.append("Emoción: ").append(log.get("emotion"))
                      .append("\nComentario: ").append(log.get("comment"))
                      .append("\n\n");
                }
                applyThemeToDialogs();
                JOptionPane.showMessageDialog(frame, sb.toString(), "Registros", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onLogsFetchFailed(String error) {
                applyThemeToDialogs();
                JOptionPane.showMessageDialog(frame, "Error: " + error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void editLog() {
        FirebaseManager.fetchEmotionLogs(userId, new FirebaseManager.EmotionLogsListener() {
            @Override
            public void onLogsFetched(List<Map<String, Object>> logs) {
                String[] options = logs.stream()
                    .map(log -> "Emoción: " + log.get("emotion") + " - Comentario: " + log.get("comment"))
                    .toArray(String[]::new);

                applyThemeToDialogs();
                String selected = (String) JOptionPane.showInputDialog(
                    frame,
                    "Seleccione el registro a editar:",
                    "Editar",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
                );

                if (selected != null) {
                    int index = java.util.Arrays.asList(options).indexOf(selected);
                    String logId = (String) logs.get(index).get("id");
                    String newComment = JOptionPane.showInputDialog(frame, "Nuevo comentario:");
                    if (newComment != null && !newComment.trim().isEmpty()) {
                        FirebaseManager.editEmotionLog(userId, logId, newComment);
                    }
                }
            }

            @Override
            public void onLogsFetchFailed(String error) {
                applyThemeToDialogs();
                JOptionPane.showMessageDialog(frame, "Error: " + error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void deleteLog() {
        FirebaseManager.fetchEmotionLogs(userId, new FirebaseManager.EmotionLogsListener() {
            @Override
            public void onLogsFetched(List<Map<String, Object>> logs) {
                String[] options = logs.stream()
                    .map(log -> "Emoción: " + log.get("emotion") + " - Comentario: " + log.get("comment"))
                    .toArray(String[]::new);

                applyThemeToDialogs();
                String selected = (String) JOptionPane.showInputDialog(
                    frame,
                    "Seleccione el registro a eliminar:",
                    "Eliminar",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
                );

                if (selected != null) {
                    int index = java.util.Arrays.asList(options).indexOf(selected);
                    String logId = (String) logs.get(index).get("id");
                    FirebaseManager.deleteEmotionLog(userId, logId);
                }
            }

            @Override
            public void onLogsFetchFailed(String error) {
                applyThemeToDialogs();
                JOptionPane.showMessageDialog(frame, "Error: " + error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void deleteAllLogs() {
        applyThemeToDialogs();
        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "¿Está seguro de eliminar TODOS los registros?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            FirebaseManager.deleteAllEmotionLogs(userId);
        }
    }

    private static void showProvisionalDiagnosis() {
        FirebaseManager.fetchEmotionLogs(userId, new FirebaseManager.EmotionLogsListener() {
            @Override
            public void onLogsFetched(List<Map<String, Object>> logs) {
                try {
                    StringBuilder sb = new StringBuilder();
                    for (Map<String, Object> log : logs) {
                        sb.append(log.get("emotion")).append(": ").append(log.get("comment")).append("\n");
                    }
                    
                    String response = OpenAIAPI.getProvisionalDiagnosis(sb.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response);
                    String diagnosis = root.path("choices").get(0).path("message").path("content").asText();
                    
                    JTextArea textArea = new JTextArea(diagnosis, 20, 50);
                    textArea.setWrapStyleWord(true);
                    textArea.setLineWrap(true);
                    textArea.setCaretPosition(0);
                    textArea.setEditable(false);
                    textArea.setBackground(ThemeManager.getBackgroundColor());
                    textArea.setForeground(ThemeManager.getForegroundColor());

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(600, 400));

                    applyThemeToDialogs();
                    JOptionPane.showMessageDialog(frame, scrollPane, "Diagnóstico OpenAI", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    applyThemeToDialogs();
                    JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void onLogsFetchFailed(String error) {
                applyThemeToDialogs();
                JOptionPane.showMessageDialog(frame, "Error: " + error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void showProfessionalDiagnosis() {
        FirebaseManager.fetchDiagnosis(userId, new FirebaseManager.DiagnosisListener() {
            @Override
            public void onDiagnosisFetched(String diagnosis) {
                JTextArea textArea = new JTextArea(diagnosis, 20, 50);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setCaretPosition(0);
                textArea.setEditable(false);
                textArea.setBackground(ThemeManager.getBackgroundColor());
                textArea.setForeground(ThemeManager.getForegroundColor());

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                applyThemeToDialogs();
                JOptionPane.showMessageDialog(frame, scrollPane, "Diagnóstico Profesional", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onDiagnosisFetchFailed(String error) {
                applyThemeToDialogs();
                JOptionPane.showMessageDialog(frame, "Error: " + error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    static class SectorButton extends JButton {
        private final double startAngle;
        private final double arcAngle;
        private final int innerRadius;
        private final int outerRadius;
        private final int size;

        public SectorButton(String text, double startAngle, double arcAngle, int innerRadius, int outerRadius, int size) {
            super(text);
            this.startAngle = startAngle;
            this.arcAngle = arcAngle;
            this.innerRadius = innerRadius;
            this.outerRadius = outerRadius;
            this.size = size;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape sector = new Arc2D.Double(
                size / 2.0 - outerRadius, 
                size / 2.0 - outerRadius,
                2 * outerRadius, 
                2 * outerRadius,
                startAngle, 
                arcAngle, 
                Arc2D.PIE
            );
            
            g2.setColor(getBackground());
            g2.fill(sector);

            g2.setColor(ThemeManager.isDarkTheme() ? Color.WHITE : Color.BLACK);
            g2.draw(sector);

            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D textBounds = fm.getStringBounds(getText(), g2);
            
            double angle = Math.toRadians(startAngle + arcAngle / 2);
            double textRadius = (innerRadius + outerRadius) / 2.0;
            double textX = size / 2.0 + textRadius * Math.cos(angle) - textBounds.getWidth() / 2;
            double textY = size / 2.0 - textRadius * Math.sin(angle) + textBounds.getHeight() / 4;

            g2.setColor(ThemeManager.isDarkTheme() ? Color.BLACK : Color.WHITE);
            g2.drawString(getText(), (int) textX - 1, (int) textY - 1);
            g2.drawString(getText(), (int) textX + 1, (int) textY + 1);
            g2.drawString(getText(), (int) textX - 1, (int) textY + 1);
            g2.drawString(getText(), (int) textX + 1, (int) textY - 1);

            g2.setColor(getForeground());
            g2.drawString(getText(), (int) textX, (int) textY);

            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            Shape sector = new Arc2D.Double(
                size / 2.0 - outerRadius,
                size / 2.0 - outerRadius,
                2 * outerRadius,
                2 * outerRadius,
                startAngle,
                arcAngle,
                Arc2D.PIE
            );
            return sector.contains(x, y);
        }
    }

    static class RoundedButton extends JButton {
        private static final int ARC_WIDTH = 20;
        private static final int ARC_HEIGHT = 20;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC_WIDTH, ARC_HEIGHT));

            g2.setColor(ThemeManager.isDarkTheme() ? Color.WHITE : Color.BLACK);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT));

            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D textBounds = fm.getStringBounds(getText(), g2);
            int textX = (int) ((getWidth() - textBounds.getWidth()) / 2);
            int textY = (int) ((getHeight() - textBounds.getHeight()) / 2 + fm.getAscent());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }
}