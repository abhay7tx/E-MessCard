import java.awt.*;
import java.awt.event.*;

/**
 * Shown on first launch if a database file exists.
 * User must enter the DB password to access it.
 * If wrong password or they choose fresh start → new empty DB is used.
 */
public class DatabaseAccessDialog extends Dialog {

    // ── Change this password to whatever you want ──────────────────────────
    private static final String DB_PASSWORD = "mess@2024";
    // ──────────────────────────────────────────────────────────────────────

    private boolean accessGranted = false;
    private boolean useFresh      = false;

    public DatabaseAccessDialog(Frame parent) {
        super(parent, "Database Access", true);
        setSize(420, 320);
        setLayout(new BorderLayout());
        setBackground(UIHelper.COL_BG);
        setResizable(false);

        // ── Header ──────────────────────────────────────────────────────────
        Panel header = new Panel(new GridLayout(2, 1));
        header.setBackground(UIHelper.COL_NAVY);
        header.setPreferredSize(new Dimension(420, 70));

        Label title = new Label("DATABASE ACCESS", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        Label sub = new Label("An existing database was found", Label.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(189, 195, 199));

        header.add(title);
        header.add(sub);
        add(header, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────────────────
        Panel form = new Panel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(14, 28, 8, 28);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        Label info = new Label("Enter password to access existing data:", Label.CENTER);
        info.setFont(new Font("Arial", Font.PLAIN, 13));
        info.setForeground(UIHelper.COL_DARK);
        form.add(info, g);

        g.gridy = 1; g.gridwidth = 1; g.gridx = 0;
        Label pl = new Label("Password:");
        pl.setFont(new Font("Arial", Font.BOLD, 13));
        form.add(pl, g);

        g.gridx = 1;
        TextField passTf = new TextField(14);
        passTf.setEchoChar('*');
        passTf.setFont(new Font("Arial", Font.PLAIN, 14));
        form.add(passTf, g);

        g.gridy = 2; g.gridx = 0; g.gridwidth = 2;
        Label statusLbl = new Label("", Label.CENTER);
        statusLbl.setFont(new Font("Arial", Font.BOLD, 12));
        form.add(statusLbl, g);

        add(form, BorderLayout.CENTER);

        // ── Buttons ──────────────────────────────────────────────────────────
        Panel btnBar = new Panel(new GridLayout(2, 1, 0, 6));
        btnBar.setBackground(UIHelper.COL_BG);

        Panel row1 = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        row1.setBackground(UIHelper.COL_BG);
        FlatButton accessBtn = UIHelper.styledButton("ACCESS DATABASE", UIHelper.COL_NAVY, new Dimension(200, 38));
        row1.add(accessBtn);

        Panel row2 = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row2.setBackground(UIHelper.COL_BG);
        FlatButton freshBtn = UIHelper.styledButton("START FRESH", UIHelper.COL_MUTED, new Dimension(160, 34));
        freshBtn.setFontSize(12);
        Label freshLbl = new Label("(ignore existing data)");
        freshLbl.setFont(new Font("Arial", Font.ITALIC, 11));
        freshLbl.setForeground(UIHelper.COL_MUTED);
        row2.add(freshBtn);
        row2.add(freshLbl);

        btnBar.add(row1);
        btnBar.add(row2);
        add(btnBar, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────────────
        Runnable attempt = () -> {
            if (DB_PASSWORD.equals(passTf.getText())) {
                accessGranted = true;
                dispose();
            } else {
                statusLbl.setForeground(UIHelper.COL_RED);
                statusLbl.setText("Wrong password!");
                passTf.setText("");
            }
        };

        accessBtn.addActionListener(e -> attempt.run());
        passTf.addActionListener(e   -> attempt.run());

        freshBtn.addActionListener(e -> {
            useFresh = true;
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                useFresh = true;
                dispose();
            }
        });

        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((scr.width - 420) / 2, (scr.height - 320) / 2);
    }

    public boolean isAccessGranted() { return accessGranted; }
    public boolean isUseFresh()      { return useFresh; }

    /**
     * Call this at startup. Returns the DB path to use.
     * If DB exists → show password dialog.
     * If no DB → just use default path (new DB created automatically).
     */
    public static String resolveDBPath() {
        java.io.File dbFile = new java.io.File("messsystem.db");

        if (!dbFile.exists()) {
            // No existing DB — fresh start, no dialog needed
            return "messsystem.db";
        }

        // DB exists — ask for password
        Frame dummy = new Frame();
        DatabaseAccessDialog dlg = new DatabaseAccessDialog(dummy);
        dlg.setVisible(true);
        dummy.dispose();

        if (dlg.isAccessGranted()) {
            System.out.println("Database access granted.");
            return "messsystem.db";
        } else {
            // Fresh start — use a temp in-memory DB
            System.out.println("Starting fresh (in-memory database).");
            return ":memory:";
        }
    }
}
