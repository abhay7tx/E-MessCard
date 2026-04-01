import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainFrame extends Frame {

    public MainFrame() {
        setTitle("College Mess Card System");
        setSize(780, 560);
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        setResizable(false);

        // ── Top header ───────────────────────────────────────────────────────
        Panel header = new Panel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42));
        header.setPreferredSize(new Dimension(780, 130));

        Panel titlePanel = new Panel(new GridLayout(3, 1));
        titlePanel.setBackground(new Color(15, 23, 42));

        Label title = new Label("E-MESS CARD SYSTEM", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        Label sub = new Label("Digital Meal Management  •  QR Based Access  •  Multi Admin", Label.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(148, 163, 184));

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        Label dateLbl = new Label(today, Label.CENTER);
        dateLbl.setFont(new Font("Arial", Font.ITALIC, 11));
        dateLbl.setForeground(new Color(100, 116, 139));

        titlePanel.add(title);
        titlePanel.add(sub);
        titlePanel.add(dateLbl);
        header.add(titlePanel, BorderLayout.CENTER);

        // Divider line
        Panel divider = new Panel();
        divider.setBackground(new Color(30, 41, 59));
        divider.setPreferredSize(new Dimension(780, 2));
        header.add(divider, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // ── Three login cards ────────────────────────────────────────────────
        Panel cardsRow = new Panel(new GridLayout(1, 3, 14, 0));
        cardsRow.setBackground(new Color(15, 23, 42));

        cardsRow.add(buildCard(
            "SUPER ADMIN",
            "Full system control.\nManage all admins\nand system settings.",
            new Color(139, 92, 246),   // purple
            new Color(30, 27, 75),
            e -> new AdminLoginDialog(this, "SUPER ADMIN", "superadmin").setVisible(true)
        ));

        cardsRow.add(buildCard(
            "ADMIN LOGIN",
            "Manage your students,\ngenerate QR codes\nand view meal logs.",
            new Color(59, 130, 246),   // blue
            new Color(23, 37, 84),
            e -> new AdminLoginDialog(this, "ADMIN LOGIN", null).setVisible(true)
        ));

        cardsRow.add(buildCard(
            "STUDENT LOGIN",
            "View your meal records\nand track your daily\nmeal history.",
            new Color(16, 185, 129),   // green
            new Color(6, 78, 59),
            e -> new StudentLoginFrame(this).setVisible(true)
        ));

        Panel centerWrap = new Panel(new BorderLayout());
        centerWrap.setBackground(new Color(15, 23, 42));
        Panel padder = new Panel(new FlowLayout(FlowLayout.CENTER, 24, 28));
        padder.setBackground(new Color(15, 23, 42));
        padder.add(cardsRow);
        centerWrap.add(padder, BorderLayout.CENTER);
        add(centerWrap, BorderLayout.CENTER);

        // ── Footer ───────────────────────────────────────────────────────────
        Panel footer = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        footer.setBackground(new Color(15, 23, 42));
        Label fl = new Label("E-Mess Card System v2.0  •  Java AWT + SQLite  •  Built for College");
        fl.setFont(new Font("Arial", Font.PLAIN, 10));
        fl.setForeground(new Color(71, 85, 105));
        footer.add(fl);
        add(footer, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
        UIHelper.centreFrame(this);
    }

    private Panel buildCard(String title, String desc, Color accent, Color darkBg, ActionListener action) {
        Panel card = new Panel(new GridBagLayout());
        card.setBackground(new Color(30, 41, 59));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;

        // Top accent bar
        g.gridy = 0; g.insets = new Insets(0, 0, 0, 0);
        Panel bar = new Panel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(220, 5));
        card.add(bar, g);

        // Icon circle (colored background with letter)
        g.gridy = 1; g.insets = new Insets(20, 20, 8, 20);
        Panel iconCircle = new Panel() {
            public void paint(Graphics gr) {
                gr.setColor(accent);
                gr.fillOval(0, 0, 44, 44);
                gr.setColor(Color.WHITE);
                gr.setFont(new Font("Arial", Font.BOLD, 22));
                FontMetrics fm = gr.getFontMetrics();
                String letter = title.substring(0, 1);
                gr.drawString(letter, (44 - fm.stringWidth(letter)) / 2,
                    (44 - fm.getHeight()) / 2 + fm.getAscent());
            }
        };
        iconCircle.setPreferredSize(new Dimension(44, 44));
        iconCircle.setBackground(new Color(30, 41, 59));
        card.add(iconCircle, g);

        // Title
        g.gridy = 2; g.insets = new Insets(4, 20, 6, 20);
        Label t = new Label(title, Label.LEFT);
        t.setFont(new Font("Arial", Font.BOLD, 15));
        t.setForeground(Color.WHITE);
        card.add(t, g);

        // Description
        g.gridy = 3; g.insets = new Insets(0, 18, 14, 18);
        TextArea ta = new TextArea(desc, 3, 22, TextArea.SCROLLBARS_NONE);
        ta.setFont(new Font("Arial", Font.PLAIN, 12));
        ta.setForeground(new Color(148, 163, 184));
        ta.setBackground(new Color(30, 41, 59));
        ta.setEditable(false);
        card.add(ta, g);

        // Button
        g.gridy = 4; g.insets = new Insets(0, 20, 22, 20);
        FlatButton btn = new FlatButton("LOGIN  →", accent, Color.WHITE);
        btn.setPreferredSize(new Dimension(180, 38));
        btn.setFontSize(13);
        btn.addActionListener(action);
        card.add(btn, g);

        return card;
    }
}
