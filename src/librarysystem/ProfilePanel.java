package librarysystem;

import javax.swing.*;
import java.awt.*;

class ProfilePanel extends JPanel {
    private Image backgroundImage;

    public ProfilePanel() {  // Remove imagePath parameter (not needed)
        
        setOpaque(false); // Ensure transparency for rounded effect
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background image first
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            System.out.println("Background image is null!");
        }

        // Draw rounded background over image
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);

        // Draw border
        g2.setColor(new Color(0xDEDEDE));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 50, 50);
    }
}
