package viewmodel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

public class SectorButton extends JButton {
    private double startAngle;
    private double arcAngle;
    private int innerRadius;
    private int outerRadius;
    private int size;

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
                size / 2.0 - outerRadius, size / 2.0 - outerRadius,
                2 * outerRadius, 2 * outerRadius,
                startAngle, arcAngle, Arc2D.PIE
        );
        g2.setColor(getBackground());
        g2.fill(sector);

        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D textBounds = fm.getStringBounds(getText(), g2);

        double angle = Math.toRadians(startAngle + arcAngle / 2);
        double textRadius = (innerRadius + outerRadius) / 2.0;
        double textX = size / 2.0 + textRadius * Math.cos(angle) - textBounds.getWidth() / 2;
        double textY = size / 2.0 - textRadius * Math.sin(angle) + textBounds.getHeight() / 4;

        g2.drawString(getText(), (int) textX, (int) textY);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Shape sector = new Arc2D.Double(
                size / 2.0 - outerRadius, size / 2.0 - outerRadius,
                2 * outerRadius, 2 * outerRadius,
                startAngle, arcAngle, Arc2D.PIE
        );
        return sector.contains(x, y);
    }
}