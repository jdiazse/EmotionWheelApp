package view;

import model.FirebaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;

public class ProfessionalGUI {

    private String therapistUserId;
    private JFrame frame;
    private JTextArea logsArea;
    private JMenuBar menuBar;
    private JMenu themeMenu;
    private JPanel topPanel;
    private JPanel searchPanel;
    private JPanel buttonPanel;

    public ProfessionalGUI(String loggedInUserId) {
        this.therapistUserId = loggedInUserId;

        frame = new JFrame("Professional Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        menuBar = new JMenuBar();
        themeMenu = new JMenu("Tema");
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

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeManager.getBackgroundColor());
        mainPanel.add(topPanel, BorderLayout.NORTH);

        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(ThemeManager.getBackgroundColor());

        JLabel searchLabel = new JLabel("Buscar paciente por username:");
        searchLabel.setForeground(ThemeManager.getForegroundColor());
        JTextField searchField = new JTextField(20);
        searchField.setBackground(ThemeManager.getButtonBackgroundColor());
        searchField.setForeground(ThemeManager.getButtonForegroundColor());
        JButton searchButton = new RoundedButton("Buscar");

        JLabel instructionLabel = new JLabel("(Ingrese el username del paciente)");
        instructionLabel.setForeground(ThemeManager.getForegroundColor());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(instructionLabel);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        logsArea = new JTextArea();
        logsArea.setEditable(false);
        logsArea.setBackground(ThemeManager.getBackgroundColor());
        logsArea.setForeground(ThemeManager.getForegroundColor());
        JScrollPane scrollPane = new JScrollPane(logsArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(150, 100));
        buttonPanel.setBackground(ThemeManager.getBackgroundColor());

        JButton refreshButton = new RoundedButton("Refresh Logs");
        JButton sendDiagnosisButton = new RoundedButton("Send Diagnosis");

        buttonPanel.add(refreshButton);
        buttonPanel.add(sendDiagnosisButton);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        applyTheme();

        searchButton.addActionListener(e -> {
            String username = searchField.getText().trim();
            if (!username.isEmpty()) {
                searchPatientLogs(username, logsArea);
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, ingresa un username.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> refreshLogs(logsArea));
        sendDiagnosisButton.addActionListener(e -> sendDiagnosis());

        frame.setVisible(true);
        refreshLogs(logsArea);
    }

    private void applyTheme() {
        Color bgColor = ThemeManager.getBackgroundColor();
        Color fgColor = ThemeManager.getForegroundColor();
        Color btnBgColor = ThemeManager.getButtonBackgroundColor();
        Color btnFgColor = ThemeManager.getButtonForegroundColor();

        frame.getContentPane().setBackground(bgColor);
        logsArea.setBackground(bgColor);
        logsArea.setForeground(fgColor);

        menuBar.setBackground(bgColor);
        menuBar.setForeground(fgColor);
        themeMenu.setForeground(fgColor);

        topPanel.setBackground(bgColor);
        searchPanel.setBackground(bgColor);
        buttonPanel.setBackground(bgColor);

        updateButtonColors((JPanel) frame.getContentPane(), btnBgColor, btnFgColor);
    }

    private void updateButtonColors(JPanel panel, Color bgColor, Color fgColor) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(bgColor);
                button.setForeground(fgColor);
            } else if (comp instanceof JPanel) {
                updateButtonColors((JPanel) comp, bgColor, fgColor);
            }
        }
    }

    private void toggleTheme() {
        ThemeManager.setDarkTheme(!ThemeManager.isDarkTheme());
        applyTheme();
        frame.revalidate();
        frame.repaint();
    }

    private void refreshLogs(JTextArea logsArea) {
        FirebaseManager.fetchPatientsLogs(therapistUserId, new FirebaseManager.PatientsLogsListener() {
            @Override
            public void onPatientsFetched(List<String> patientIds) {
                StringBuilder logsText = new StringBuilder();
                for (String patientId : patientIds) {
                    FirebaseManager.fetchEmotionLogs(patientId, new FirebaseManager.EmotionLogsListener() {
                        @Override
                        public void onLogsFetched(List<Map<String, Object>> logs) {
                            logsText.append("Patient ID: ").append(patientId).append("\n");
                            for (Map<String, Object> log : logs) {
                                logsText.append("Emotion: ").append(log.get("emotion")).append("\n")
                                        .append("Comment: ").append(log.get("comment")).append("\n")
                                        .append("Timestamp: ").append(log.get("timestamp")).append("\n\n");
                            }
                            logsArea.setText(logsText.toString());
                        }

                        @Override
                        public void onLogsFetchFailed(String errorMessage) {
                            logsArea.setText("Failed to fetch logs: " + errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onPatientsFetchFailed(String errorMessage) {
                logsArea.setText("Failed to fetch patients: " + errorMessage);
            }
        });
    }

    private void searchPatientLogs(String username, JTextArea logsArea) {
        FirebaseManager.getUserIdFromUsername(username, new FirebaseManager.UsernameListener() {
            @Override
            public void onUserIdFound(String patientUserId) {
                FirebaseManager.fetchEmotionLogs(patientUserId, new FirebaseManager.EmotionLogsListener() {
                    @Override
                    public void onLogsFetched(List<Map<String, Object>> logs) {
                        StringBuilder logsText = new StringBuilder();
                        logsText.append("Logs del paciente: ").append(username).append("\n\n");
                        for (Map<String, Object> log : logs) {
                            logsText.append("Emotion: ").append(log.get("emotion")).append("\n")
                                    .append("Comment: ").append(log.get("comment")).append("\n")
                                    .append("Timestamp: ").append(log.get("timestamp")).append("\n\n");
                        }
                        logsArea.setText(logsText.toString());
                    }

                    @Override
                    public void onLogsFetchFailed(String errorMessage) {
                        logsArea.setText("Failed to fetch logs: " + errorMessage);
                    }
                });
            }

            @Override
            public void onUserIdNotFound() {
                logsArea.setText("Usuario no encontrado: " + username);
            }

            @Override
            public void onError(String errorMessage) {
                logsArea.setText("Error: " + errorMessage);
            }
        });
    }

    private void sendDiagnosis() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBackground(ThemeManager.getBackgroundColor());

        JLabel usernameLabel = new JLabel("Nombre de usuario del paciente:");
        usernameLabel.setForeground(ThemeManager.getForegroundColor());
        JTextField usernameField = new JTextField(20);
        usernameField.setBackground(ThemeManager.getButtonBackgroundColor());
        usernameField.setForeground(ThemeManager.getButtonForegroundColor());

        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        diagnosisLabel.setForeground(ThemeManager.getForegroundColor());
        JTextArea diagnosisArea = new JTextArea(5, 20);
        diagnosisArea.setBackground(ThemeManager.getButtonBackgroundColor());
        diagnosisArea.setForeground(ThemeManager.getButtonForegroundColor());
        JScrollPane diagnosisScroll = new JScrollPane(diagnosisArea);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(diagnosisLabel);
        panel.add(diagnosisScroll);

        applyThemeToDialogs();

        int result = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Enviar Diagnostico",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String diagnosis = diagnosisArea.getText();

            if (username != null && !username.trim().isEmpty() && diagnosis != null && !diagnosis.trim().isEmpty()) {
                FirebaseManager.sendDiagnosis(therapistUserId, username, diagnosis);
                JOptionPane.showMessageDialog(
                        frame,
                        "Diagnosis enviado con exito!",
                        "Exitoso",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "El usuarip o el diagnostico no puede estar vacio!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void applyThemeToDialogs() {
        UIManager.put("OptionPane.background", ThemeManager.getBackgroundColor());
        UIManager.put("Panel.background", ThemeManager.getBackgroundColor());
        UIManager.put("OptionPane.messageForeground", ThemeManager.getForegroundColor());
        UIManager.put("OptionPane.buttonBackground", ThemeManager.getButtonBackgroundColor());
        UIManager.put("OptionPane.buttonForeground", ThemeManager.getButtonForegroundColor());
        UIManager.put("TextField.background", ThemeManager.getButtonBackgroundColor());
        UIManager.put("TextField.foreground", ThemeManager.getButtonForegroundColor());
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