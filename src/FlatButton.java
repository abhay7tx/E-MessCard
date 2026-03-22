import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Canvas-based button — renders correctly on macOS where native AWT
 * buttons ignore setBackground / setForeground.
 */
public class FlatButton extends Canvas {

    private String label;
    private Color bgColor;
    private Color fgColor;
    private Color hoverColor;
    private boolean hovered = false;
    private final List<ActionListener> listeners = new ArrayList<>();
    private int fontSize = 13;
    private boolean bold = true;

    public FlatButton(String label, Color bg, Color fg) {
        this.label    = label;
        this.bgColor  = bg;
        this.fgColor  = fg;
        this.hoverColor = bg.darker();

        setPreferredSize(new Dimension(170, 38));
        setSize(170, 38);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            public void mouseClicked(MouseEvent e) {
                ActionEvent ae = new ActionEvent(FlatButton.this, ActionEvent.ACTION_PERFORMED, label);
                for (ActionListener l : listeners) l.actionPerformed(ae);
            }
        });
    }

    public FlatButton(String label, Color bg) { this(label, bg, Color.WHITE); }

    public void setFontSize(int size)  { this.fontSize = size; repaint(); }
    public void setBold(boolean bold)  { this.bold = bold;     repaint(); }
    public void setLabel(String text)  { this.label = text;    repaint(); }
    /** Override so tab switching can change colour and repaint correctly. */
    public void setBackground(Color c) { this.bgColor = c; this.hoverColor = c.darker(); repaint(); }

    public void addActionListener(ActionListener l) { listeners.add(l); }

    public void paint(Graphics g) {
        int w = getWidth(), h = getHeight();

        // Background
        g.setColor(hovered ? hoverColor : bgColor);
        g.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);

        // Border (subtle)
        g.setColor(bgColor.darker());
        g.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);

        // Text
        g.setColor(fgColor);
        g.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, fontSize));
        FontMetrics fm = g.getFontMetrics();
        int tx = (w - fm.stringWidth(label)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(label, tx, ty);
    }
}
// Allow external color changes (for tab highlighting)
