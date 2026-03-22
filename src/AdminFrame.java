import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Admin Panel — scoped to a specific admin.
 * Admin can only see and manage their own students.
 */
public class AdminFrame extends Frame {

    private static final String[] TABS = {"Students", "Generate QR", "Meal Logs", "Scan Station"};

    private final int    adminId;
    private final String adminName;
    private final String adminUsername;

    private Panel       contentPanel;
    private CardLayout  cardLayout;
    private FlatButton[] tabBtns;

    private TextField addRollTf, addNameTf, addPassTf;
    private TextArea  studentListTa;
    private Choice    mealChoice;
    private UIHelper.ImageCanvas qrCanvas;
    private Label     qrInfoLbl;
    private TextArea  logsTa;

    public AdminFrame(int adminId, String adminName, String adminUsername) {
        this.adminId       = adminId;
        this.adminName     = adminName;
        this.adminUsername = adminUsername;

        setTitle("Admin Panel — " + adminName);
        setSize(920, 620);
        setLayout(new BorderLayout());
        setBackground(UIHelper.COL_BG);
        setResizable(false);

        add(UIHelper.headerPanel(
            "ADMIN PANEL — " + adminName.toUpperCase(),
            "Username: " + adminUsername + "  |  Your students only"
        ), BorderLayout.NORTH);

        // ── Tab bar ──────────────────────────────────────────────────────────
        Panel tabBar = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 6));
        tabBar.setBackground(new Color(44, 62, 80));
        tabBtns = new FlatButton[TABS.length];
        for (int i = 0; i < TABS.length; i++) {
            final String tabName = TABS[i];
            FlatButton tb = new FlatButton("  " + TABS[i] + "  ", new Color(80,100,120), Color.WHITE);
            tb.setPreferredSize(new Dimension(160, 32)); tb.setSize(160, 32);
            tb.addActionListener(e -> switchTab(tabName));
            tabBtns[i] = tb; tabBar.add(tb);
        }

        cardLayout   = new CardLayout();
        contentPanel = new Panel(cardLayout);
        contentPanel.setBackground(UIHelper.COL_BG);

        buildStudentsPanel();
        buildQRPanel();
        buildLogsPanel();
        buildScanPanel();

        Panel footer = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(UIHelper.COL_BG);
        FlatButton changePwBtn = UIHelper.styledButton("CHANGE MY CREDENTIALS", new Color(59,130,246), new Dimension(230,36));
        changePwBtn.setFontSize(11);
        changePwBtn.addActionListener(e ->
            new ChangeCredentialsDialog(AdminFrame.this,
                ChangeCredentialsDialog.Role.ADMIN, adminId, null).setVisible(true));
        FlatButton closeBtn = UIHelper.styledButton("LOGOUT", UIHelper.COL_RED, new Dimension(110,36));
        closeBtn.addActionListener(e -> dispose());
        footer.add(changePwBtn);
        footer.add(closeBtn);

        Panel main = new Panel(new BorderLayout());
        main.add(tabBar, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        main.add(footer, BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);

        switchTab("Students");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        UIHelper.centreFrame(this);
    }

    private void switchTab(String tab) {
        cardLayout.show(contentPanel, tab);
        for (int i = 0; i < TABS.length; i++) {
            tabBtns[i].setBackground(TABS[i].equals(tab)
                ? UIHelper.COL_NAVY : new Color(80,100,120));
            tabBtns[i].repaint();
        }
        if (tab.equals("Students"))  refreshStudentList();
        if (tab.equals("Meal Logs")) refreshLogs();
    }

    // ── Students panel ───────────────────────────────────────────────────────

    private void buildStudentsPanel() {
        Panel p = new Panel(new GridLayout(1, 2, 12, 0));
        p.setBackground(UIHelper.COL_BG);

        // Left: Add form
        Panel addCard = new Panel(new GridBagLayout());
        addCard.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10,18,7,18);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        Label h = new Label("Add New Student", Label.CENTER);
        h.setFont(new Font("Arial", Font.BOLD, 15));
        h.setForeground(UIHelper.COL_NAVY);
        addCard.add(h, g);

        g.gridwidth = 1;
        g.gridy++; g.gridx = 0; addCard.add(bold("Roll Number:"), g);
        g.gridx = 1; addRollTf = UIHelper.styledTextField(14); addCard.add(addRollTf, g);
        g.gridy++; g.gridx = 0; addCard.add(bold("Full Name:"), g);
        g.gridx = 1; addNameTf = UIHelper.styledTextField(14); addCard.add(addNameTf, g);
        g.gridy++; g.gridx = 0; addCard.add(bold("Password:"), g);
        g.gridx = 1; addPassTf = UIHelper.styledTextField(14); addCard.add(addPassTf, g);

        Label statusLbl = new Label("", Label.CENTER);
        statusLbl.setFont(new Font("Arial", Font.ITALIC, 12));
        g.gridy++; g.gridx = 0; g.gridwidth = 2; addCard.add(statusLbl, g);

        FlatButton addBtn = UIHelper.styledButton("ADD STUDENT", UIHelper.COL_GREEN, new Dimension(200,40));
        g.gridy++; addCard.add(addBtn, g);

        addBtn.addActionListener(e -> {
            String roll = addRollTf.getText().trim();
            String name = addNameTf.getText().trim();
            String pass = addPassTf.getText().trim();
            if (roll.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                statusLbl.setForeground(UIHelper.COL_RED);
                statusLbl.setText("All fields required.");
                return;
            }
            if (DatabaseManager.addStudent(roll, name, pass, adminId)) {
                statusLbl.setForeground(UIHelper.COL_GREEN);
                statusLbl.setText("Added: " + roll.toUpperCase());
                addRollTf.setText(""); addNameTf.setText(""); addPassTf.setText("");
                refreshStudentList();
            } else {
                statusLbl.setForeground(UIHelper.COL_RED);
                statusLbl.setText("Roll already exists in your domain!");
            }
        });

        // Right: Student list + actions
        Panel listCard = new Panel(new BorderLayout(4,4));
        listCard.setBackground(Color.WHITE);
        Label lh = new Label("Your Students", Label.CENTER);
        lh.setFont(new Font("Arial", Font.BOLD, 15));
        lh.setForeground(UIHelper.COL_NAVY);
        listCard.add(lh, BorderLayout.NORTH);

        studentListTa = UIHelper.recordTextArea();
        listCard.add(studentListTa, BorderLayout.CENTER);

        // Actions: Remove | Change Password
        Panel actBar = new Panel(new GridLayout(2,1,0,4));
        actBar.setBackground(new Color(236,240,241));

        Panel rmRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        rmRow.setBackground(new Color(236,240,241));
        TextField rmTf = UIHelper.styledTextField(10);
        FlatButton rmBtn = UIHelper.styledButton("REMOVE", UIHelper.COL_RED, new Dimension(100,30));
        rmBtn.setFontSize(11);
        Label rmStatus = new Label(""); rmStatus.setFont(new Font("Arial",Font.ITALIC,11));
        rmRow.add(new Label("Roll:")); rmRow.add(rmTf); rmRow.add(rmBtn); rmRow.add(rmStatus);

        Panel pwRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pwRow.setBackground(new Color(236,240,241));
        TextField pwRollTf = UIHelper.styledTextField(8);
        TextField newPwTf  = UIHelper.styledTextField(10);
        FlatButton pwBtn   = UIHelper.styledButton("CHANGE PW", UIHelper.COL_ORANGE, new Dimension(120,30));
        pwBtn.setFontSize(11);
        Label pwStatus = new Label(""); pwStatus.setFont(new Font("Arial",Font.ITALIC,11));
        pwRow.add(new Label("Roll:")); pwRow.add(pwRollTf);
        pwRow.add(new Label("New PW:")); pwRow.add(newPwTf);
        pwRow.add(pwBtn); pwRow.add(pwStatus);

        actBar.add(rmRow); actBar.add(pwRow);
        listCard.add(actBar, BorderLayout.SOUTH);

        rmBtn.addActionListener(e -> {
            String r = rmTf.getText().trim();
            if (r.isEmpty()) { rmStatus.setForeground(UIHelper.COL_RED); rmStatus.setText("Enter roll!"); return; }
            if (DatabaseManager.removeStudent(r, adminId)) {
                rmStatus.setForeground(UIHelper.COL_GREEN); rmStatus.setText("Removed " + r.toUpperCase());
                rmTf.setText(""); refreshStudentList();
            } else {
                rmStatus.setForeground(UIHelper.COL_RED); rmStatus.setText("Not found in your domain.");
            }
        });

        pwBtn.addActionListener(e -> {
            String r  = pwRollTf.getText().trim();
            String np = newPwTf.getText().trim();
            if (r.isEmpty() || np.isEmpty()) {
                pwStatus.setForeground(UIHelper.COL_RED); pwStatus.setText("Fill both fields."); return;
            }
            if (DatabaseManager.changeStudentPassword(r, adminId, np)) {
                pwStatus.setForeground(UIHelper.COL_GREEN); pwStatus.setText("Password changed for " + r.toUpperCase());
                pwRollTf.setText(""); newPwTf.setText(""); refreshStudentList();
            } else {
                pwStatus.setForeground(UIHelper.COL_RED); pwStatus.setText("Roll not found in your domain.");
            }
        });

        p.add(addCard); p.add(listCard);
        contentPanel.add(p, "Students");
    }

    private void refreshStudentList() {
        List<String[]> students = DatabaseManager.getStudentsByAdmin(adminId);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-12s  %-20s  %-14s  %-12s%n", "ROLL", "NAME", "PASSWORD", "REG DATE"));
        sb.append("  " + "-".repeat(65) + "\n");
        for (String[] s : students)
            sb.append(String.format("  %-12s  %-20s  %-14s  %-12s%n", s[0], s[1], s[2], s[3]));
        if (students.isEmpty()) sb.append("  No students registered yet.");
        studentListTa.setText(sb.toString());
    }

    // ── QR panel ─────────────────────────────────────────────────────────────

    private void buildQRPanel() {
        Panel p = new Panel(new BorderLayout(8,8));
        p.setBackground(UIHelper.COL_BG);

        Panel controls = new Panel(new FlowLayout(FlowLayout.CENTER, 14, 16));
        controls.setBackground(Color.WHITE);
        controls.add(new Label("Select Meal:"));
        mealChoice = new Choice();
        mealChoice.addItem("BREAKFAST"); mealChoice.addItem("LUNCH");
        mealChoice.addItem("SNACKS");    mealChoice.addItem("DINNER");
        mealChoice.setFont(new Font("Arial", Font.BOLD, 14));
        controls.add(mealChoice);

        FlatButton genBtn = UIHelper.styledButton("GENERATE QR", UIHelper.COL_NAVY, new Dimension(160,38));
        controls.add(genBtn);

        qrInfoLbl = new Label("", Label.CENTER);
        qrInfoLbl.setFont(new Font("Arial", Font.BOLD, 13));
        controls.add(qrInfoLbl);

        Panel canvasWrap = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        canvasWrap.setBackground(UIHelper.COL_BG);
        qrCanvas = new UIHelper.ImageCanvas(290, 290);
        qrCanvas.setBackground(Color.WHITE);
        canvasWrap.add(qrCanvas);

        genBtn.addActionListener(e -> {
            String meal = mealChoice.getSelectedItem();
            String base = MessCardSystem.ngrokUrl.isEmpty()
                ? "http://" + MessCardSystem.serverIP + ":" + MessCardSystem.serverPort
                : MessCardSystem.ngrokUrl;
            String url  = base + "/meal?type=" + meal + "&admin=" + adminId;
            java.awt.image.BufferedImage img = QRCodeGenerator.generateQRImage(url, 290);
            if (img != null) {
                qrCanvas.setImage(img);
                qrInfoLbl.setForeground(UIHelper.mealColor(meal));
                qrInfoLbl.setText(meal + " QR ready!");
            } else {
                qrInfoLbl.setForeground(UIHelper.COL_RED);
                qrInfoLbl.setText("QR generation failed.");
            }
        });

        p.add(controls, BorderLayout.NORTH);
        p.add(canvasWrap, BorderLayout.CENTER);
        contentPanel.add(p, "Generate QR");
    }

    // ── Logs panel ────────────────────────────────────────────────────────────

    private void buildLogsPanel() {
        Panel p = new Panel(new BorderLayout(4,4));
        p.setBackground(UIHelper.COL_BG);
        Panel topBar = new Panel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topBar.setBackground(Color.WHITE);
        Label h = new Label("Meal Logs — Your Students Only");
        h.setFont(new Font("Arial", Font.BOLD, 15));
        h.setForeground(UIHelper.COL_NAVY);
        topBar.add(h);
        FlatButton ref = UIHelper.styledButton("REFRESH", UIHelper.COL_BLUE, new Dimension(110,32));
        ref.addActionListener(e -> refreshLogs());
        topBar.add(ref);
        logsTa = UIHelper.recordTextArea();
        p.add(topBar, BorderLayout.NORTH);
        p.add(logsTa, BorderLayout.CENTER);
        contentPanel.add(p, "Meal Logs");
    }

    private void refreshLogs() {
        logsTa.setText(UIHelper.formatAllLogs(DatabaseManager.getAllMealLogs(adminId)));
    }

    // ── Scan Station panel ────────────────────────────────────────────────────

    private void buildScanPanel() {
        Panel p = new Panel(new GridBagLayout());
        p.setBackground(UIHelper.COL_BG);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.insets = new Insets(20,0,10,0);
        Label info = new Label("Open the Scan Station kiosk for your students to log meals.", Label.CENTER);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setForeground(UIHelper.COL_DARK);
        p.add(info, g);

        g.gridy = 1; g.insets = new Insets(4,0,20,0);
        Label sub = new Label("Only your registered students can log in.", Label.CENTER);
        sub.setFont(new Font("Arial", Font.ITALIC, 12));
        sub.setForeground(UIHelper.COL_MUTED);
        p.add(sub, g);

        g.gridy = 2; g.insets = new Insets(0,0,0,0);
        FlatButton openBtn = UIHelper.styledButton("OPEN SCAN STATION", UIHelper.COL_GREEN, new Dimension(240,50));
        openBtn.setFontSize(15);
        openBtn.addActionListener(e -> new ScanStationFrame(adminId).setVisible(true));
        p.add(openBtn, g);

        contentPanel.add(p, "Scan Station");
    }

    private Label bold(String text) {
        Label l = new Label(text); l.setFont(new Font("Arial", Font.BOLD, 12)); return l;
    }
}
