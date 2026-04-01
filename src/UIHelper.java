import java.awt.*;
import java.util.List;

public class UIHelper {

    public static final Color COL_NAVY   = new Color(27,  42,  74);
    public static final Color COL_GREEN  = new Color(39, 174,  96);
    public static final Color COL_RED    = new Color(192,  57,  43);
    public static final Color COL_ORANGE = new Color(230, 126,  34);
    public static final Color COL_BLUE   = new Color(41, 128, 185);
    public static final Color COL_PURPLE = new Color(142,  68, 173);
    public static final Color COL_BG     = new Color(235, 238, 242);
    public static final Color COL_WHITE  = Color.WHITE;
    public static final Color COL_DARK   = new Color(44,  62,  80);
    public static final Color COL_MUTED  = new Color(127, 140, 141);

    public static Color mealColor(String meal) {
        switch (meal.toUpperCase()) {
            case "BREAKFAST": return new Color(243, 156,  18);
            case "LUNCH":     return new Color(39,  174,  96);
            case "SNACKS":    return new Color(41,  128, 185);
            case "DINNER":    return new Color(142,  68, 173);
            default:          return COL_NAVY;
        }
    }

    public static FlatButton styledButton(String text, Color bg) {
        FlatButton b = new FlatButton(text, bg, Color.WHITE);
        b.setPreferredSize(new Dimension(170, 38));
        return b;
    }

    public static FlatButton styledButton(String text, Color bg, Dimension size) {
        FlatButton b = new FlatButton(text, bg, Color.WHITE);
        b.setPreferredSize(size);
        b.setSize(size);
        return b;
    }

    public static Label headingLabel(String text) {
        Label l = new Label(text, Label.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setForeground(COL_DARK);
        return l;
    }

    public static TextField styledTextField(int cols) {
        TextField tf = new TextField(cols);
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        return tf;
    }

    public static Panel headerPanel(String title, String subtitle) {
        int rows = subtitle.isEmpty() ? 1 : 2;
        Panel p = new Panel(new GridLayout(rows, 1));
        p.setBackground(COL_NAVY);
        p.setPreferredSize(new Dimension(900, subtitle.isEmpty() ? 52 : 70));

        Label t = new Label(title, Label.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 21));
        t.setForeground(Color.WHITE);
        p.add(t);

        if (!subtitle.isEmpty()) {
            Label s = new Label(subtitle, Label.CENTER);
            s.setFont(new Font("Arial", Font.PLAIN, 13));
            s.setForeground(new Color(189, 195, 199));
            p.add(s);
        }
        return p;
    }

    public static void centreFrame(Frame f) {
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation((scr.width - f.getWidth()) / 2, (scr.height - f.getHeight()) / 2);
    }

    public static class ImageCanvas extends Canvas {
        private java.awt.image.BufferedImage img;
        private final int W, H;
        public ImageCanvas(int w, int h) {
            W = w; H = h;
            setPreferredSize(new Dimension(w, h));
            setSize(w, h);
        }
        public void setImage(java.awt.image.BufferedImage i) { img = i; repaint(); }
        public void paint(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, W, H);
            if (img != null) g.drawImage(img, 0, 0, W, H, this);
        }
    }

    public static TextArea recordTextArea() {
        TextArea ta = new TextArea("", 10, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setBackground(new Color(248, 249, 250));
        return ta;
    }

    public static String formatMealRecords(List<String[]> records) {
        if (records.isEmpty()) return "   No meal records found.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-14s  %-12s  %-10s%n", "MEAL", "DATE", "TIME"));
        sb.append("  " + "-".repeat(42) + "\n");
        for (String[] r : records)
            sb.append(String.format("  %-14s  %-12s  %-10s%n", r[0], r[1], r[2]));
        return sb.toString();
    }

    public static String formatAllLogs(List<String[]> logs) {
        if (logs.isEmpty()) return "   No meal logs found.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-12s  %-18s  %-12s  %-12s  %-10s%n",
                "ROLL", "NAME", "MEAL", "DATE", "TIME"));
        sb.append("  " + "-".repeat(72) + "\n");
        for (String[] r : logs)
            sb.append(String.format("  %-12s  %-18s  %-12s  %-12s  %-10s%n",
                    r[0], r[1], r[2], r[3], r[4]));
        return sb.toString();
    }
}
