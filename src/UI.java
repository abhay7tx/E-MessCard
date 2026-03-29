import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * UI.java — Shared colors, buttons, and helper methods for the whole app.
 *
 * Keeps all styling in one place so the rest of the code stays clean.
 */
public class UI {

    private static final boolean IS_MAC =
        System.getProperty("os.name", "").toLowerCase().contains("mac");

    // ── Shared Colors ────────────────────────────────────────────────────────
    public static final Color NAVY   = new Color(27,  42,  74);
    public static final Color GREEN  = new Color(39, 174,  96);
    public static final Color RED    = new Color(192,  57,  43);
    public static final Color ORANGE = new Color(230, 126,  34);
    public static final Color BLUE   = new Color(41,  128, 185);
    public static final Color MUTED  = new Color(127, 140, 141);
    public static final Color BG     = new Color(235, 238, 242);
    public static final Color WHITE  = Color.WHITE;

    // Returns the color for each meal type
    public static Color mealColor(String meal) {
        switch (meal.toUpperCase()) {
            case "BREAKFAST": return new Color(243, 156,  18);
            case "LUNCH":     return new Color(39,  174,  96);
            case "SNACKS":    return new Color(41,  128, 185);
            case "DINNER":    return new Color(142,  68, 173);
            default:          return NAVY;
        }
    }

    // ── Simple Helpers ───────────────────────────────────────────────────────

    /** Creates a styled text field with the given number of columns. */
    public static TextField textField(int cols) {
        TextField tf = new TextField(cols);
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        return tf;
    }

    /** Creates a styled password field. */
    public static TextField passwordField(int cols) {
        TextField tf = textField(cols);
        tf.setEchoChar('*');
        return tf;
    }

    /** Creates a bold label for form field names. */
    public static Label boldLabel(String text) {
        Label l = new Label(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        return l;
    }

    /** Creates one simple label + field row used in forms. */
    public static Panel formRow(String labelText, Component field) {
        Panel row = new Panel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        Label label = boldLabel(labelText + ":");
        label.setPreferredSize(new Dimension(110, 24));
        Panel fieldWrap = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldWrap.setBackground(Color.WHITE);
        fieldWrap.add(field);
        row.add(label, BorderLayout.WEST);
        row.add(fieldWrap, BorderLayout.CENTER);
        return row;
    }

    /** Creates a button with readable text across platforms, including macOS. */
    public static Button button(String text, Color bg) {
        Button b = new Button(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        if (IS_MAC) {
            b.setBackground(SystemColor.control);
            b.setForeground(NAVY);
        } else {
            b.setBackground(bg);
            b.setForeground(buttonTextColor(bg));
        }
        return b;
    }

    /** Creates a tab button with a consistent size. */
    public static Button tabButton(String text) {
        Button b = button(text, NAVY);
        b.setFont(new Font("Arial", Font.BOLD, 11));
        b.setPreferredSize(new Dimension(108, 30));
        return b;
    }

    /** Updates a tab button so the active tab is clearly visible on every OS. */
    public static void setTabState(Button button, String text, boolean active) {
        button.setLabel(active ? "• " + text : text);
        if (IS_MAC) {
            button.setBackground(SystemColor.control);
            button.setForeground(active ? NAVY : MUTED);
        } else {
            button.setBackground(active ? NAVY : new Color(80, 100, 120));
            button.setForeground(Color.WHITE);
        }
    }

    private static Color buttonTextColor(Color bg) {
        int brightness = (bg.getRed() * 299 + bg.getGreen() * 587 + bg.getBlue() * 114) / 1000;
        return brightness > 150 ? NAVY : Color.WHITE;
    }

    /** Creates a read-only text area for displaying records. */
    public static TextArea recordArea() {
        TextArea ta = new TextArea("", 10, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        ta.setFont(new Font("Monospaced", Font.BOLD, 12));
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);
        ta.setForeground(new Color(50, 60, 70));
        return ta;
    }

    /** Centers a frame on the screen. */
    public static void center(Frame f) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(
            (screen.width  - f.getWidth())  / 2,
            (screen.height - f.getHeight()) / 2
        );
    }

    /** Creates a header panel with a title and subtitle. */
    public static Panel header(String title, String subtitle) {
        int rows = subtitle.isEmpty() ? 1 : 2;
        Panel p = new Panel(new GridLayout(rows, 1));
        p.setBackground(NAVY);
        p.setPreferredSize(new Dimension(900, subtitle.isEmpty() ? 56 : 78));

        Label t = new Label(title, Label.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 21));
        t.setForeground(Color.WHITE);
        p.add(t);

        if (!subtitle.isEmpty()) {
            Label s = new Label(subtitle, Label.CENTER);
            s.setFont(new Font("Arial", Font.PLAIN, 12));
            s.setForeground(new Color(189, 195, 199));
            p.add(s);
        }
        return p;
    }

    /** Formats a list of meal records into readable text for a TextArea. */
    public static String formatMealRecords(List<String[]> records) {
        if (records.isEmpty()) return "  No meal records found yet.\n\n  Your meal entries will appear here.";
        StringBuilder sb = new StringBuilder();
        sb.append("  RECENT MEAL RECORDS\n");
        sb.append("  ").append("-".repeat(46)).append("\n");
        sb.append(String.format("  %-3s %-12s %-12s %-10s%n", "#", "MEAL", "DATE", "TIME"));
        sb.append("  ").append("-".repeat(46)).append("\n");
        for (int i = 0; i < records.size(); i++) {
            String[] r = records.get(i);
            sb.append(String.format("  %-3s %-12s %-12s %-10s%n", i + 1, r[0], r[1], r[2]));
        }
        return sb.toString();
    }

    /** Formats the admin list for the super admin screen. */
    public static String formatAdminList(List<String[]> admins) {
        if (admins.isEmpty()) return "  No admins added yet.";
        StringBuilder sb = new StringBuilder();
        sb.append("  TOTAL ADMINS: ").append(admins.size()).append("\n");
        sb.append("  ").append("-".repeat(62)).append("\n");
        sb.append(String.format("  %-4s %-12s %-20s %-12s%n", "ID", "USER", "MESS", "CREATED"));
        sb.append("  ").append("-".repeat(62)).append("\n");
        for (String[] a : admins) {
            sb.append(String.format("  %-4s %-12s %-20s %-12s%n", a[0], a[1], a[2], a[3]));
        }
        return sb.toString();
    }

    /** Formats the student list for the admin screen. */
    public static String formatStudentList(List<String[]> students) {
        if (students.isEmpty()) return "  No students registered yet.";
        StringBuilder sb = new StringBuilder();
        sb.append("  ACTIVE STUDENTS: ").append(students.size()).append("\n");
        sb.append("  ").append("-".repeat(52)).append("\n");
        sb.append(String.format("  %-12s %-22s %-12s%n", "ROLL", "NAME", "REG DATE"));
        sb.append("  ").append("-".repeat(52)).append("\n");
        for (String[] s : students) {
            sb.append(String.format("  %-12s %-22s %-12s%n", s[0], s[1], s[2]));
        }
        return sb.toString();
    }

    /** Formats a list of all meal logs (used by admin). */
    public static String formatAllLogs(List<String[]> logs) {
        if (logs.isEmpty()) return "  No meal logs found yet.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-3s %-12s %-18s %-12s %-12s %-10s%n",
                "#", "ROLL", "NAME", "MEAL", "DATE", "TIME"));
        sb.append("  ").append("-".repeat(76)).append("\n");
        for (int i = 0; i < logs.size(); i++) {
            String[] r = logs.get(i);
            sb.append(String.format("  %-3s %-12s %-18s %-12s %-12s %-10s%n",
                    i + 1, r[0], r[1], r[2], r[3], r[4]));
        }
        return sb.toString();
    }

    // ── Canvas for displaying QR code images ─────────────────────────────────
    public static class ImageCanvas extends Canvas {
        private BufferedImage img;
        private final int W, H;

        public ImageCanvas(int w, int h) {
            W = w; H = h;
            setPreferredSize(new Dimension(w, h));
            setSize(w, h);
        }

        public void setImage(BufferedImage i) { img = i; repaint(); }

        public void paint(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, W, H);
            if (img != null) g.drawImage(img, 0, 0, W, H, this);
        }
    }
}
