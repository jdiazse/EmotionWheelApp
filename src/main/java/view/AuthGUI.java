package view;

import model.FirebaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class AuthGUI extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton loginButton;
    private JLabel statusLabel;
    private JMenuBar menuBar;
    private JPanel mainPanel;
    private JPanel statusPanel;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JLabel roleLabel;
    private JMenu themeMenu;

    public AuthGUI() {
        setTitle("Autenticación");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        themeMenu = new JMenu("Tema");
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
        setJMenuBar(menuBar);

        mainPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        initializeComponents();
        applyTheme();

        mainPanel.add(usernameLabel);
        mainPanel.add(usernameField);
        mainPanel.add(emailLabel);
        mainPanel.add(emailField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(roleLabel);
        mainPanel.add(roleComboBox);
        mainPanel.add(registerButton);
        mainPanel.add(loginButton);

        statusPanel.add(statusLabel);

        add(mainPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        FirebaseManager.initializeFirebase();

        registerButton.addActionListener(e -> registerUser());
        loginButton.addActionListener(e -> loginUser());

        setVisible(true);
    }

    private void initializeComponents() {
        usernameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        roleComboBox = new JComboBox<>(new String[]{"Paciente", "Profesional"});
        registerButton = new RoundedButton("Registrarse");
        loginButton = new RoundedButton("Iniciar Sesión");
        statusLabel = new JLabel("Ingrese email y contraseña", SwingConstants.CENTER);

        usernameLabel = new JLabel("Username:");
        emailLabel = new JLabel("Email:");
        passwordLabel = new JLabel("Contraseña:");
        roleLabel = new JLabel("Rol:");
    }

    private void applyTheme() {
        Color bgColor = ThemeManager.getBackgroundColor();
        Color fgColor = ThemeManager.getForegroundColor();
        Color btnBgColor = ThemeManager.getButtonBackgroundColor();
        Color btnFgColor = ThemeManager.getButtonForegroundColor();

        getContentPane().setBackground(bgColor);
        mainPanel.setBackground(bgColor);
        statusPanel.setBackground(bgColor);

        usernameField.setBackground(btnBgColor);
        usernameField.setForeground(fgColor);
        emailField.setBackground(btnBgColor);
        emailField.setForeground(fgColor);
        passwordField.setBackground(btnBgColor);
        passwordField.setForeground(fgColor);
        roleComboBox.setBackground(btnBgColor);
        roleComboBox.setForeground(fgColor);
        registerButton.setBackground(btnBgColor);
        registerButton.setForeground(btnFgColor);
        loginButton.setBackground(btnBgColor);
        loginButton.setForeground(btnFgColor);
        statusLabel.setForeground(fgColor);

        usernameLabel.setForeground(fgColor);
        emailLabel.setForeground(fgColor);
        passwordLabel.setForeground(fgColor);
        roleLabel.setForeground(fgColor);

        menuBar.setBackground(bgColor);
        menuBar.setForeground(fgColor);
        themeMenu.setForeground(fgColor);

        revalidate();
        repaint();
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Faltan campos obligatorios");
            return;
        }

        String userId = FirebaseManager.registerUser(email, password, role, username);
        if (userId != null) {
            statusLabel.setText("Registro exitoso");
        } else {
            statusLabel.setText("Registro fallido");
        }
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Email o contraseña no ingresados");
            return;
        }

        String userId = FirebaseManager.loginUser(email, password);
        if (userId != null) {
            statusLabel.setText("Inicio de sesión exitoso");
            FirebaseManager.checkUserRole(userId, new FirebaseManager.UserRoleListener() {
                @Override
                public void onRoleFetched(String role) {
                    System.out.println("Rol obtenido: " + role);
                    if ("Paciente".equalsIgnoreCase(role.trim())) {
                        RuedaEmocionalGUI.launchWheel(userId);
                    } else if ("Profesional".equalsIgnoreCase(role.trim())) {
                        new ProfessionalGUI(userId);
                    } else {
                        statusLabel.setText("Rol no reconocido: " + role);
                    }
                    dispose();
                }

                @Override
                public void onRoleFetchFailed(String errorMessage) {
                    statusLabel.setText("Error fetching user role: " + errorMessage);
                    dispose();
                }
            });
        } else {
            statusLabel.setText("Inicio de sesión fallido, revisa las credenciales");
        }
    }

    public static void main(String[] args) {
        new AuthGUI();
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