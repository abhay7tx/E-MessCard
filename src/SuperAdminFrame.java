import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Super Admin Panel — manage all admins.
 * Accessible only with superadmin credentials.
 */
public class SuperAdminFrame extends Frame {

    private TextArea adminListTa;
    private TextField userTf, nameTf, passTf;
    private Label statusLbl;

    public SuperAdminFrame() {
        setTitle("Super Admin Panel");
        setSize(800, 560);
        setLayout(new BorderLayout());
        setBackground(UIHelper.COL_BG);
        setResizable(false);

        // ── Header ──────────────────────────────────────────────────────────
        Panel header = new Panel(new GridLayout(2,1));
        header.setBackground(new Color(100, 30, 120));
        header.setPreferredSize(new Dimension(800, 70));
        Label title = new Label("SUPER ADMIN PANEL", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        Label sub = new Label("Manage all admins in the system", Label.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(220, 180, 230));
        header.add(title); header.add(sub);
        add(header, BorderLayout.NORTH);

        // ── Split: Add form | Admin list ─────────────────────────────────────
        Panel split = new Panel(new GridLayout(1, 2, 12, 0));
        split.setBackground(UIHelper.COL_BG);

        // Left — Add admin form
        Panel addCard = new Panel(new GridBagLayout());
        addCard.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 18, 7, 18);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        Label h = new Label("Add New Admin", Label.CENTER);
        h.setFont(new Font("Arial", Font.BOLD, 15));
        h.setForeground(new Color(100, 30, 120));
        addCard.add(h, g);

        g.gridwidth = 1;
        g.gridy++; g.gridx = 0; addCard.add(boldLabel("Username:"), g);
        g.gridx = 1; userTf = UIHelper.styledTextField(14); addCard.add(userTf, g);

        g.gridy++; g.gridx = 0; addCard.add(boldLabel("Full Name:"), g);
        g.gridx = 1; nameTf = UIHelper.styledTextField(14); addCard.add(nameTf, g);

        g.gridy++; g.gridx = 0; addCard.add(boldLabel("Password:"), g);
        g.gridx = 1; passTf = UIHelper.styledTextField(14); addCard.add(passTf, g);

        statusLbl = new Label("", Label.CENTER);
        statusLbl.setFont(new Font("Arial", Font.ITALIC, 12));
        g.gridy++; g.gridx = 0; g.gridwidth = 2; addCard.add(statusLbl, g);

        FlatButton addBtn = UIHelper.styledButton("ADD ADMIN", new Color(100,30,120), new Dimension(200,40));
        g.gridy++; addCard.add(addBtn, g);

        addBtn.addActionListener(e -> {
            String user = userTf.getText().trim().toLowerCase();
            String name = nameTf.getText().trim();
            String pass = passTf.getText().trim();
            if (user.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                statusLbl.setForeground(UIHelper.COL_RED);
                statusLbl.setText("All fields required.");
                return;
            }
            if (user.equals(MessCardSystem.SUPER_ADMIN_USER)) {
                statusLbl.setForeground(UIHelper.COL_RED);
                statusLbl.setText("That username is reserved!");
                return;
            }
            if (DatabaseManager.addAdmin(user, pass, name)) {
                statusLbl.setForeground(UIHelper.COL_GREEN);
                statusLbl.setText("Admin '" + user + "' added!");
                userTf.setText(""); nameTf.setText(""); passTf.setText("");
                refreshList();
            } else {
                statusLbl.setForeground(UIHelper.COL_RED);
                statusLbl.setText("Username already exists!");
            }
        });

        // Right — Admin list + remove + change password
        Panel listCard = new Panel(new BorderLayout(4,4));
        listCard.setBackground(Color.WHITE);
        Label lh = new Label("All Admins", Label.CENTER);
        lh.setFont(new Font("Arial", Font.BOLD, 15));
        lh.setForeground(new Color(100,30,120));
        listCard.add(lh, BorderLayout.NORTH);

        adminListTa = UIHelper.recordTextArea();
        listCard.add(adminListTa, BorderLayout.CENTER);

        // Remove + change password bar
        Panel actionBar = new Panel(new GridLayout(2, 1, 0, 4));
        actionBar.setBackground(new Color(236,240,241));

        Panel rmRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        rmRow.setBackground(new Color(236,240,241));
        TextField rmIdTf = UIHelper.styledTextField(5);
        rmIdTf.setPreferredSize(new Dimension(60, 26));
        FlatButton rmBtn = UIHelper.styledButton("REMOVE ADMIN", UIHelper.COL_RED, new Dimension(150,30));
        rmBtn.setFontSize(11);
        Label rmStatus = new Label(""); rmStatus.setFont(new Font("Arial",Font.ITALIC,11));
        rmRow.add(new Label("Admin ID:")); rmRow.add(rmIdTf); rmRow.add(rmBtn); rmRow.add(rmStatus);

        Panel pwRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pwRow.setBackground(new Color(236,240,241));
        TextField pwIdTf  = UIHelper.styledTextField(5); pwIdTf.setPreferredSize(new Dimension(60,26));
        TextField newPwTf = UIHelper.styledTextField(10);
        FlatButton pwBtn  = UIHelper.styledButton("CHANGE PW", UIHelper.COL_BLUE, new Dimension(120,30));
        pwBtn.setFontSize(11);
        Label pwStatus = new Label(""); pwStatus.setFont(new Font("Arial",Font.ITALIC,11));
        pwRow.add(new Label("ID:")); pwRow.add(pwIdTf);
        pwRow.add(new Label("New PW:")); pwRow.add(newPwTf);
        pwRow.add(pwBtn); pwRow.add(pwStatus);

        actionBar.add(rmRow); actionBar.add(pwRow);
        listCard.add(actionBar, BorderLayout.SOUTH);

        rmBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(rmIdTf.getText().trim());
                if (DatabaseManager.removeAdmin(id)) {
                    rmStatus.setForeground(UIHelper.COL_GREEN);
                    rmStatus.setText("Removed ID " + id);
                    rmIdTf.setText(""); refreshList();
                } else {
                    rmStatus.setForeground(UIHelper.COL_RED);
                    rmStatus.setText("ID not found.");
                }
            } catch (NumberFormatException ex) {
                rmStatus.setForeground(UIHelper.COL_RED);
                rmStatus.setText("Enter valid ID.");
            }
        });

        pwBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(pwIdTf.getText().trim());
                String np = newPwTf.getText().trim();
                if (np.isEmpty()) { pwStatus.setForeground(UIHelper.COL_RED); pwStatus.setText("Enter new password."); return; }
                if (DatabaseManager.changeAdminPassword(id, np)) {
                    pwStatus.setForeground(UIHelper.COL_GREEN);
                    pwStatus.setText("Password updated!");
                    pwIdTf.setText(""); newPwTf.setText("");
                } else {
                    pwStatus.setForeground(UIHelper.COL_RED);
                    pwStatus.setText("ID not found.");
                }
            } catch (NumberFormatException ex) {
                pwStatus.setForeground(UIHelper.COL_RED);
                pwStatus.setText("Enter valid ID.");
            }
        });

        split.add(addCard);
        split.add(listCard);
        add(split, BorderLayout.CENTER);

        Panel footer = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(UIHelper.COL_BG);
        FlatButton changePwBtn = UIHelper.styledButton("CHANGE MY PASSWORD", new Color(139,92,246), new Dimension(210,36));
        changePwBtn.setFontSize(12);
        changePwBtn.addActionListener(e ->
            new ChangeCredentialsDialog(SuperAdminFrame.this,
                ChangeCredentialsDialog.Role.SUPER_ADMIN, -1, null).setVisible(true));
        FlatButton closeBtn = UIHelper.styledButton("CLOSE", UIHelper.COL_RED, new Dimension(110,36));
        closeBtn.addActionListener(e -> dispose());
        footer.add(changePwBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        refreshList();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        UIHelper.centreFrame(this);
    }

    private void refreshList() {
        List<String[]> admins = DatabaseManager.getAllAdmins();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-6s  %-16s  %-22s  %-12s%n", "ID", "USERNAME", "FULL NAME", "CREATED"));
        sb.append("  " + "-".repeat(62) + "\n");
        for (String[] a : admins)
            sb.append(String.format("  %-6s  %-16s  %-22s  %-12s%n", a[0], a[1], a[2], a[3]));
        if (admins.isEmpty()) sb.append("  No admins added yet.");
        adminListTa.setText(sb.toString());
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        return l;
    }
}
