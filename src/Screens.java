import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Screens.java — All AWT windows for the E-Mess Card System.
 *
 * Contains:
 *   MainScreen         → Home screen with 3 login buttons
 *   SuperAdminScreen   → Manage admins
 *   AdminScreen        → Manage students, QR, logs, menu, analytics
 *   StudentScreen      → View meal records
 *   MealLoginScreen    → Kiosk login at scan station
 *   ScanStationScreen  → Shows QR codes for all 4 meals
 *
 * All screens are static inner classes so they live in one file.
 * This keeps the project simple — fewer files to navigate.
 */
public class Screens {

    private static Panel whitePanel(LayoutManager layout) {
        Panel panel = new Panel(layout);
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private static Panel wrapCard(Panel panel) {
        Panel outer = new Panel(new BorderLayout(1, 1));
        outer.setBackground(new Color(194, 201, 210));
        outer.add(panel, BorderLayout.CENTER);
        return outer;
    }

    private static Label sectionTitle(String text, int align) {
        Label label = new Label(text, align);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(UI.NAVY);
        return label;
    }

    private static Label mutedLabel(String text, int align) {
        Label label = new Label(text, align);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(UI.MUTED);
        return label;
    }

    private static String editorMenuText(String items) {
        if (items == null || items.trim().isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String item : items.split("[,\n]+")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(trimmed);
            }
        }
        return sb.toString();
    }

    private static String normalizeMenuText(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String item : text.split("[,\n]+")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(trimmed);
            }
        }
        return sb.toString();
    }

    private static String previewMenuText(String title, List<String[]> menu) {
        if (menu.isEmpty()) return "  No menu set yet.";

        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(title.toUpperCase()).append("\n");
        sb.append("  ").append("-".repeat(44)).append("\n");
        for (String[] row : menu) {
            sb.append("\n  ").append(row[0]).append("\n");
            for (String item : row[1].split("[,\n]+")) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    sb.append("    - ").append(trimmed).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private static void centerDialog(Dialog dialog, Component parent) {
        Dimension ps = parent.getSize();
        Point pl = parent.getLocation();
        dialog.setLocation(
            pl.x + (ps.width - dialog.getWidth()) / 2,
            pl.y + (ps.height - dialog.getHeight()) / 2
        );
    }

    // ══════════════════════════════════════════════════════════════════════
    //  1. MAIN SCREEN — Home page with 3 login options
    // ══════════════════════════════════════════════════════════════════════

    public static class MainScreen extends Frame {

        public MainScreen() {
            setTitle("E-Mess Card System");
            setSize(680, 500);
            setLayout(new BorderLayout());
            setBackground(UI.BG);
            setResizable(false);

            add(UI.header("E-MESS CARD SYSTEM", "Clean and simple digital meal management"), BorderLayout.NORTH);

            Panel center = new Panel(new BorderLayout(0, 16));
            center.setBackground(UI.BG);

            Panel intro = whitePanel(new GridLayout(3, 1, 0, 4));
            intro.add(sectionTitle("Select Your Role", Label.CENTER));
            intro.add(mutedLabel("This project has three user roles with a simple flow.", Label.CENTER));
            intro.add(mutedLabel("Super Admin manages admins, Admin manages students, and Student checks records.", Label.CENTER));

            Panel actions = new Panel(new GridLayout(3, 1, 0, 12));
            actions.setBackground(UI.BG);
            actions.add(loginCard(
                "SUPER ADMIN",
                "Add admins, remove admins, and manage system access.",
                new Color(139, 92, 246),
                "Super Admin Login",
                e -> showSuperAdminLogin()
            ));
            actions.add(loginCard(
                "ADMIN LOGIN",
                "Manage students, generate QR codes, check logs, and update menu.",
                new Color(59, 130, 246),
                "Admin Login",
                e -> showAdminLogin()
            ));
            actions.add(loginCard(
                "STUDENT LOGIN",
                "View meal history and check today's menu.",
                UI.GREEN,
                "Student Login",
                e -> new StudentLoginDialog(MainScreen.this).setVisible(true)
            ));

            center.add(intro, BorderLayout.NORTH);
            center.add(actions, BorderLayout.CENTER);

            Panel wrap = new Panel(new BorderLayout());
            wrap.setBackground(UI.BG);
            wrap.add(center, BorderLayout.CENTER);
            add(wrap, BorderLayout.CENTER);

            // Footer
            Panel footer = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            footer.setBackground(UI.BG);
            Label fl = new Label("E-Mess Card System  •  Java AWT + SQLite  •  Simple presentation layout");
            fl.setFont(new Font("Arial", Font.PLAIN, 11));
            fl.setForeground(UI.MUTED);
            footer.add(fl);
            add(footer, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { System.exit(0); }
            });
            UI.center(this);
        }

        // Builds one login card panel
        private Panel loginCard(String title, String desc, Color color, String buttonText, ActionListener action) {
            Panel card = whitePanel(new BorderLayout(14, 0));
            Panel strip = new Panel();
            strip.setBackground(color);
            strip.setPreferredSize(new Dimension(10, 70));

            Panel info = whitePanel(new GridLayout(2, 1, 0, 4));
            Label t = new Label(title);
            t.setFont(new Font("Arial", Font.BOLD, 14));
            t.setForeground(UI.NAVY);
            Label d = mutedLabel(desc, Label.LEFT);
            info.add(t);
            info.add(d);

            Button btn = UI.button(buttonText, color);
            btn.setPreferredSize(new Dimension(190, 36));
            btn.addActionListener(action);

            card.add(strip, BorderLayout.WEST);
            card.add(info, BorderLayout.CENTER);
            card.add(btn, BorderLayout.EAST);
            return card;
        }

        private void showSuperAdminLogin() {
            Dialog d = new Dialog(this, "Super Admin Login", true);
            d.setSize(360, 220);
            d.setLayout(new BorderLayout(8, 8));
            d.setBackground(UI.BG);

            Panel form = whitePanel(new GridLayout(4, 1, 6, 6));
            Label lbl = sectionTitle("Super Admin Login", Label.CENTER);
            Label note = mutedLabel("Enter the super admin password to continue.", Label.CENTER);
            TextField tf = UI.passwordField(20);
            Label status = new Label("", Label.CENTER);
            Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
            buttons.setBackground(UI.BG);
            Button btn = UI.button("LOGIN", new Color(139, 92, 246));
            Button cancelBtn = UI.button("CANCEL", UI.MUTED);

            btn.addActionListener(e -> {
                if (tf.getText().equals(MessCardSystem.SUPER_ADMIN_PASS)) {
                    d.dispose();
                    new SuperAdminScreen().setVisible(true);
                } else {
                    status.setForeground(UI.RED);
                    status.setText("Wrong password!");
                    tf.setText("");
                }
            });
            tf.addActionListener(e -> btn.getActionListeners()[0].actionPerformed(e));
            cancelBtn.addActionListener(e -> d.dispose());

            form.add(lbl);
            form.add(note);
            form.add(UI.boldLabel("Password:"));
            form.add(tf);

            buttons.add(btn);
            buttons.add(cancelBtn);

            d.add(form, BorderLayout.CENTER);
            d.add(status, BorderLayout.NORTH);
            d.add(buttons, BorderLayout.SOUTH);
            d.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { d.dispose(); }
            });
            centerDialog(d, this);
            d.setVisible(true);
        }

        private void showAdminLogin() {
            Dialog d = new Dialog(this, "Admin Login", true);
            d.setSize(380, 280);
            d.setLayout(new BorderLayout(8, 8));
            d.setBackground(UI.BG);

            Panel form = whitePanel(new GridLayout(6, 1, 6, 6));
            form.add(sectionTitle("Admin Login", Label.CENTER));
            form.add(mutedLabel("Use the admin username and password given by super admin.", Label.CENTER));
            form.add(UI.boldLabel("Username:"));
            TextField userTf = UI.textField(20);
            form.add(userTf);
            form.add(UI.boldLabel("Password:"));
            TextField passTf = UI.passwordField(20);
            form.add(passTf);
            Label status = new Label("", Label.CENTER);
            Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
            buttons.setBackground(UI.BG);
            Button btn = UI.button("LOGIN", new Color(59, 130, 246));
            Button cancelBtn = UI.button("CANCEL", UI.MUTED);

            btn.addActionListener(e -> {
                String user = userTf.getText().trim();
                String pass = passTf.getText().trim();
                if (user.isEmpty() || pass.isEmpty()) {
                    status.setForeground(UI.RED);
                    status.setText("Please fill in all fields.");
                    return;
                }
                String[] result = DatabaseManager.validateAdmin(user, pass);
                if (result != null) {
                    d.dispose();
                    new AdminScreen(Integer.parseInt(result[0]), result[1], user).setVisible(true);
                } else {
                    status.setForeground(UI.RED);
                    status.setText("Invalid username or password!");
                    passTf.setText("");
                }
            });
            passTf.addActionListener(e -> btn.getActionListeners()[0].actionPerformed(e));
            cancelBtn.addActionListener(e -> d.dispose());

            buttons.add(btn);
            buttons.add(cancelBtn);

            d.add(status, BorderLayout.NORTH);
            d.add(form, BorderLayout.CENTER);
            d.add(buttons, BorderLayout.SOUTH);
            d.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { d.dispose(); }
            });
            centerDialog(d, this);
            d.setVisible(true);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  1b. STUDENT LOGIN DIALOG (opened from MainScreen)
    // ══════════════════════════════════════════════════════════════════════

    static class StudentLoginDialog extends Dialog {
        StudentLoginDialog(Frame parent) {
            super(parent, "Student Login", true);
            setSize(380, 280);
            setLayout(new BorderLayout(8, 8));
            setBackground(UI.BG);

            Panel form = whitePanel(new GridLayout(6, 1, 6, 6));
            form.add(sectionTitle("Student Login", Label.CENTER));
            form.add(mutedLabel("Enter your roll number and password to view your records.", Label.CENTER));
            form.add(UI.boldLabel("Roll Number:"));
            TextField rollTf = UI.textField(20);
            form.add(rollTf);
            form.add(UI.boldLabel("Password:"));
            TextField passTf = UI.passwordField(20);
            form.add(passTf);
            Label status = new Label("", Label.CENTER);
            Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
            buttons.setBackground(UI.BG);
            Button btn = UI.button("VIEW MY RECORDS", UI.GREEN);
            Button cancelBtn = UI.button("CANCEL", UI.MUTED);

            btn.addActionListener(e -> {
                String roll = rollTf.getText().trim();
                String pass = passTf.getText().trim();
                if (roll.isEmpty() || pass.isEmpty()) {
                    status.setForeground(UI.RED); status.setText("Fill in all fields."); return;
                }
                int adminId = DatabaseManager.findAdminForRoll(roll);
                if (adminId == -1) {
                    status.setForeground(UI.RED); status.setText("Roll number not registered!"); return;
                }
                String name = DatabaseManager.validateStudent(roll, pass, adminId);
                if (name == null) {
                    status.setForeground(UI.RED); status.setText("Incorrect password."); passTf.setText(""); return;
                }
                dispose();
                new StudentScreen(roll.toUpperCase(), name, adminId).setVisible(true);
            });
            passTf.addActionListener(e -> btn.getActionListeners()[0].actionPerformed(e));
            cancelBtn.addActionListener(e -> dispose());

            buttons.add(btn);
            buttons.add(cancelBtn);

            add(status, BorderLayout.NORTH);
            add(form, BorderLayout.CENTER);
            add(buttons, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            centerDialog(this, parent);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  2. SUPER ADMIN SCREEN — Add/remove admins
    // ══════════════════════════════════════════════════════════════════════

    public static class SuperAdminScreen extends Frame {

        private TextArea listArea;
        private TextField userTf, messTf, passTf;
        private Label statusLbl;

        public SuperAdminScreen() {
            setTitle("Super Admin Panel");
            setSize(900, 560);
            setLayout(new BorderLayout());
            setBackground(UI.BG);
            setResizable(false);

            add(UI.header("SUPER ADMIN PANEL", "Manage all admins in the system"), BorderLayout.NORTH);

            Panel split = new Panel(new GridLayout(1, 2, 14, 0));
            split.setBackground(UI.BG);

            Panel addCard = whitePanel(new BorderLayout(0, 10));
            Panel addHead = whitePanel(new GridLayout(2, 1, 0, 4));
            addHead.add(sectionTitle("Create Admin", Label.LEFT));
            addHead.add(mutedLabel("Username, mess name, and password are enough here.", Label.LEFT));

            userTf  = UI.textField(18);
            messTf  = UI.textField(18);
            passTf  = UI.passwordField(18);
            statusLbl = new Label("", Label.LEFT);
            statusLbl.setFont(new Font("Arial", Font.ITALIC, 12));

            Panel addForm = whitePanel(new GridLayout(3, 1, 0, 10));
            addForm.add(UI.formRow("Username", userTf));
            addForm.add(UI.formRow("Mess Name", messTf));
            addForm.add(UI.formRow("Password", passTf));

            Button addBtn = UI.button("ADD ADMIN", new Color(100, 30, 120));
            addBtn.addActionListener(e -> addAdmin());

            Panel addActions = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            addActions.setBackground(Color.WHITE);
            addActions.add(addBtn);
            addActions.add(statusLbl);

            addCard.add(addHead, BorderLayout.NORTH);
            addCard.add(addForm, BorderLayout.CENTER);
            addCard.add(addActions, BorderLayout.SOUTH);

            Panel listCard = whitePanel(new BorderLayout(0, 8));
            Panel listHead = whitePanel(new GridLayout(2, 1, 0, 4));
            listHead.add(sectionTitle("Admin List", Label.LEFT));
            listHead.add(mutedLabel("Use the admin ID below to remove an admin or change a password.", Label.LEFT));
            listCard.add(listHead, BorderLayout.NORTH);

            listArea = UI.recordArea();
            listCard.add(listArea, BorderLayout.CENTER);

            Panel actions = new Panel(new GridLayout(2, 1, 0, 4));
            actions.setBackground(new Color(236, 240, 241));

            Panel rmRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            rmRow.setBackground(new Color(236, 240, 241));
            TextField rmIdTf = UI.textField(5);
            Button rmBtn = UI.button("REMOVE ADMIN", UI.RED);
            Label rmStatus = new Label("");
            rmBtn.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(rmIdTf.getText().trim());
                    if (DatabaseManager.removeAdmin(id)) {
                        rmStatus.setForeground(UI.GREEN); rmStatus.setText("Removed!");
                        rmIdTf.setText(""); refreshList();
                    } else {
                        rmStatus.setForeground(UI.RED); rmStatus.setText("ID not found.");
                    }
                } catch (NumberFormatException ex) {
                    rmStatus.setForeground(UI.RED); rmStatus.setText("Enter a valid ID.");
                }
            });
            rmRow.add(UI.boldLabel("Admin ID:")); rmRow.add(rmIdTf); rmRow.add(rmBtn); rmRow.add(rmStatus);

            Panel pwRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pwRow.setBackground(new Color(236, 240, 241));
            TextField pwIdTf = UI.textField(5);
            TextField newPwTf = UI.passwordField(10);
            Button pwBtn = UI.button("CHANGE PASSWORD", UI.BLUE);
            Label pwStatus = new Label("");
            pwBtn.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(pwIdTf.getText().trim());
                    String np = newPwTf.getText().trim();
                    if (np.isEmpty()) { pwStatus.setForeground(UI.RED); pwStatus.setText("Enter new password."); return; }
                    if (DatabaseManager.changeAdminPassword(id, np)) {
                        pwStatus.setForeground(UI.GREEN); pwStatus.setText("Password updated!");
                        pwIdTf.setText(""); newPwTf.setText("");
                    } else {
                        pwStatus.setForeground(UI.RED); pwStatus.setText("ID not found.");
                    }
                } catch (NumberFormatException ex) {
                    pwStatus.setForeground(UI.RED); pwStatus.setText("Enter valid ID.");
                }
            });
            pwRow.add(UI.boldLabel("Admin ID:")); pwRow.add(pwIdTf);
            pwRow.add(UI.boldLabel("New Password:")); pwRow.add(newPwTf);
            pwRow.add(pwBtn); pwRow.add(pwStatus);

            actions.add(rmRow); actions.add(pwRow);
            listCard.add(actions, BorderLayout.SOUTH);

            split.add(addCard);
            split.add(listCard);
            add(split, BorderLayout.CENTER);

            Panel footer = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
            footer.setBackground(UI.BG);

            Button changePwBtn = UI.button("CHANGE MY PASSWORD", new Color(139, 92, 246));
            changePwBtn.addActionListener(e -> {
                Dialog d = new Dialog(this, "Change Super Admin Password", true);
                d.setSize(360, 260);
                d.setLayout(new BorderLayout(8, 8));
                d.setBackground(UI.BG);
                Panel form = whitePanel(new GridLayout(6, 1, 6, 6));
                form.add(sectionTitle("Change Password", Label.CENTER));
                form.add(mutedLabel("Update the super admin password here.", Label.CENTER));
                form.add(UI.boldLabel("Old Password:"));
                TextField oldTf = UI.passwordField(16);
                form.add(oldTf);
                form.add(UI.boldLabel("New Password:"));
                TextField newTf = UI.passwordField(16);
                form.add(newTf);
                Label st = new Label("", Label.CENTER);
                Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
                buttons.setBackground(UI.BG);
                Button save = UI.button("SAVE", UI.GREEN);
                Button cancel = UI.button("CANCEL", UI.MUTED);
                save.addActionListener(ev -> {
                    if (!oldTf.getText().equals(MessCardSystem.SUPER_ADMIN_PASS)) {
                        st.setForeground(UI.RED); st.setText("Current password wrong!"); return;
                    }
                    if (newTf.getText().trim().length() < 4) {
                        st.setForeground(UI.RED); st.setText("Min 4 characters."); return;
                    }
                    MessCardSystem.SUPER_ADMIN_PASS = newTf.getText().trim();
                    st.setForeground(UI.GREEN); st.setText("Password changed!");
                });
                cancel.addActionListener(ev -> d.dispose());
                buttons.add(save);
                buttons.add(cancel);
                d.add(st, BorderLayout.NORTH);
                d.add(form, BorderLayout.CENTER);
                d.add(buttons, BorderLayout.SOUTH);
                d.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent ev) { d.dispose(); }});
                centerDialog(d, this);
                d.setVisible(true);
            });

            Button closeBtn = UI.button("CLOSE", UI.RED);
            closeBtn.addActionListener(e -> dispose());
            footer.add(changePwBtn);
            footer.add(closeBtn);
            add(footer, BorderLayout.SOUTH);

            refreshList();
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            UI.center(this);
        }

        private void addAdmin() {
            String user = userTf.getText().trim().toLowerCase();
            String mess = messTf.getText().trim();
            String pass = passTf.getText().trim();
            if (user.isEmpty() || mess.isEmpty() || pass.isEmpty()) {
                statusLbl.setForeground(UI.RED); statusLbl.setText("Username, mess name, and password are required."); return;
            }
            if (user.equals(MessCardSystem.SUPER_ADMIN_USER)) {
                statusLbl.setForeground(UI.RED); statusLbl.setText("That username is reserved!"); return;
            }
            if (DatabaseManager.addAdmin(user, pass, mess, mess)) {
                statusLbl.setForeground(UI.GREEN); statusLbl.setText("Admin '" + user + "' added!");
                userTf.setText(""); messTf.setText(""); passTf.setText("");
                refreshList();
            } else {
                statusLbl.setForeground(UI.RED); statusLbl.setText("Username already exists!");
            }
        }

        private void refreshList() {
            listArea.setText(UI.formatAdminList(DatabaseManager.getAllAdmins()));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  3. ADMIN SCREEN — Tabbed panel for admin functions
    // ══════════════════════════════════════════════════════════════════════

    public static class AdminScreen extends Frame {

        private static final String[] TABS = {"Students", "QR", "Logs", "Menu", "Reports"};

        private final int    adminId;
        private final String adminName;

        private Panel      content;
        private CardLayout cards;
        private Button[]   tabBtns;

        // Students tab
        private TextField addRollTf, addNameTf, addPassTf;
        private TextArea  studentListTa;

        // QR tab
        private UI.ImageCanvas qrCanvas;
        private Choice mealChoice;
        private Label  qrStatus;

        // Logs tab
        private TextArea logsTa;

        public AdminScreen(int adminId, String adminName, String username) {
            this.adminId   = adminId;
            this.adminName = adminName;

            setTitle("Admin Panel — " + adminName);
            setSize(900, 580);
            setLayout(new BorderLayout());
            setBackground(UI.BG);
            setResizable(false);

            add(UI.header("ADMIN PANEL — " + adminName.toUpperCase(), "Username: " + username), BorderLayout.NORTH);

            Panel tabBar = new Panel(new GridLayout(1, TABS.length, 8, 0));
            tabBar.setBackground(new Color(44, 62, 80));
            tabBtns = new Button[TABS.length];
            for (int i = 0; i < TABS.length; i++) {
                final String tab = TABS[i];
                Button tb = UI.tabButton(tab);
                tb.addActionListener(e -> switchTab(tab));
                tabBtns[i] = tb;
                tabBar.add(tb);
            }

            cards   = new CardLayout();
            content = new Panel(cards);

            buildStudentsTab();
            buildQRTab();
            buildLogsTab();
            buildMenuTab();
            buildAnalyticsTab();

            Panel footer = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
            footer.setBackground(UI.BG);
            Button scanBtn = UI.button("OPEN SCAN STATION", UI.GREEN);
            scanBtn.addActionListener(e -> new ScanStationScreen(adminId).setVisible(true));
            Button closeBtn = UI.button("LOGOUT", UI.RED);
            closeBtn.addActionListener(e -> dispose());
            footer.add(scanBtn);
            footer.add(closeBtn);

            Panel tabWrap = new Panel(new BorderLayout());
            tabWrap.setBackground(new Color(44, 62, 80));
            tabWrap.add(tabBar, BorderLayout.CENTER);

            Panel main = new Panel(new BorderLayout(0, 8));
            main.setBackground(UI.BG);
            main.add(tabWrap, BorderLayout.NORTH);
            main.add(content, BorderLayout.CENTER);
            main.add(footer, BorderLayout.SOUTH);
            add(main, BorderLayout.CENTER);

            switchTab("Students");
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            UI.center(this);
        }

        private void switchTab(String tab) {
            cards.show(content, tab);
            for (int i = 0; i < TABS.length; i++) {
                UI.setTabState(tabBtns[i], TABS[i], TABS[i].equals(tab));
            }
            if (tab.equals("Students")) refreshStudents();
            if (tab.equals("Logs")) refreshLogs();
        }

        // ── Students tab ──────────────────────────────────────────────────

        private void buildStudentsTab() {
            Panel p = new Panel(new BorderLayout(0, 8));
            p.setBackground(UI.BG);

            Panel top = new Panel(new GridLayout(1, 2, 8, 0));
            top.setBackground(UI.BG);

            Panel addCard = whitePanel(new BorderLayout(0, 8));
            Panel addHead = whitePanel(new GridLayout(2, 1, 0, 4));
            addHead.add(sectionTitle("Add Student", Label.LEFT));
            addHead.add(mutedLabel("Add one student with roll, name, and password.", Label.LEFT));
            addCard.add(addHead, BorderLayout.NORTH);

            addRollTf = UI.textField(10);
            addNameTf = UI.textField(12);
            addPassTf = UI.passwordField(10);
            Label addStatus = new Label("", Label.LEFT);
            addStatus.setFont(new Font("Arial", Font.ITALIC, 12));

            Panel addForm = whitePanel(new GridLayout(3, 1, 0, 10));
            addForm.add(UI.formRow("Roll No", addRollTf));
            addForm.add(UI.formRow("Name", addNameTf));
            addForm.add(UI.formRow("Password", addPassTf));

            Button addBtn = UI.button("ADD STUDENT", UI.GREEN);
            addBtn.addActionListener(e -> {
                String roll = addRollTf.getText().trim();
                String name = addNameTf.getText().trim();
                String pass = addPassTf.getText().trim();
                if (roll.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                    addStatus.setForeground(UI.RED); addStatus.setText("All fields required."); return;
                }
                if (DatabaseManager.addStudent(roll, name, pass, adminId)) {
                    addStatus.setForeground(UI.GREEN); addStatus.setText("Added: " + roll.toUpperCase());
                    addRollTf.setText(""); addNameTf.setText(""); addPassTf.setText("");
                    refreshStudents();
                } else {
                    addStatus.setForeground(UI.RED); addStatus.setText("Roll already exists!");
                }
            });

            Panel addActions = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            addActions.setBackground(Color.WHITE);
            addActions.add(addBtn);
            addActions.add(addStatus);

            addCard.add(addForm, BorderLayout.CENTER);
            addCard.add(addActions, BorderLayout.SOUTH);

            Panel listCard = whitePanel(new BorderLayout(0, 8));
            Panel listHead = whitePanel(new GridLayout(2, 1, 0, 4));
            listHead.add(sectionTitle("Current Students", Label.LEFT));
            listHead.add(mutedLabel("Active students in this mess section.", Label.LEFT));
            listCard.add(listHead, BorderLayout.NORTH);

            studentListTa = UI.recordArea();
            studentListTa.setRows(9);
            listCard.add(studentListTa, BorderLayout.CENTER);

            Panel actionCard = whitePanel(new BorderLayout(0, 8));
            Panel actionHead = whitePanel(new GridLayout(2, 1, 0, 4));
            actionHead.add(sectionTitle("Quick Actions", Label.LEFT));
            actionHead.add(mutedLabel("Remove a student or reset a password when needed.", Label.LEFT));
            actionCard.add(actionHead, BorderLayout.NORTH);

            Panel actions = whitePanel(new GridLayout(2, 1, 0, 8));

            Panel rmRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            rmRow.setBackground(Color.WHITE);
            TextField rmTf = UI.textField(8);
            Button rmBtn = UI.button("REMOVE", UI.RED);
            Label rmStatus = new Label("");
            rmBtn.addActionListener(e -> {
                String r = rmTf.getText().trim();
                if (r.isEmpty()) { rmStatus.setForeground(UI.RED); rmStatus.setText("Enter roll!"); return; }
                if (DatabaseManager.removeStudent(r, adminId)) {
                    rmStatus.setForeground(UI.GREEN); rmStatus.setText("Removed " + r.toUpperCase());
                    rmTf.setText(""); refreshStudents();
                } else {
                    rmStatus.setForeground(UI.RED); rmStatus.setText("Not found.");
                }
            });
            rmRow.add(UI.boldLabel("Remove Roll:")); rmRow.add(rmTf); rmRow.add(rmBtn); rmRow.add(rmStatus);

            Panel pwRow = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pwRow.setBackground(Color.WHITE);
            TextField pwRollTf = UI.textField(8);
            TextField newPwTf  = UI.passwordField(8);
            Button pwBtn = UI.button("CHANGE PASSWORD", UI.ORANGE);
            Label pwStatus = new Label("");
            pwBtn.addActionListener(e -> {
                String r  = pwRollTf.getText().trim();
                String np = newPwTf.getText().trim();
                if (r.isEmpty() || np.isEmpty()) { pwStatus.setForeground(UI.RED); pwStatus.setText("Fill both fields."); return; }
                if (DatabaseManager.changeStudentPassword(r, adminId, np)) {
                    pwStatus.setForeground(UI.GREEN); pwStatus.setText("Password changed!");
                    pwRollTf.setText(""); newPwTf.setText(""); refreshStudents();
                } else {
                    pwStatus.setForeground(UI.RED); pwStatus.setText("Roll not found.");
                }
            });
            pwRow.add(UI.boldLabel("Roll No:")); pwRow.add(pwRollTf);
            pwRow.add(UI.boldLabel("New Password:")); pwRow.add(newPwTf);
            pwRow.add(pwBtn); pwRow.add(pwStatus);

            actions.add(rmRow); actions.add(pwRow);
            actionCard.add(actions, BorderLayout.CENTER);

            top.add(wrapCard(addCard));
            top.add(wrapCard(listCard));

            p.add(top, BorderLayout.NORTH);
            p.add(wrapCard(actionCard), BorderLayout.CENTER);
            content.add(p, "Students");
        }

        private void refreshStudents() {
            studentListTa.setText(UI.formatStudentList(DatabaseManager.getStudentsByAdmin(adminId)));
        }

        // ── QR Code tab ───────────────────────────────────────────────────

        private void buildQRTab() {
            Panel p = new Panel(new BorderLayout(0, 8));
            p.setBackground(UI.BG);

            Panel topCard = whitePanel(new BorderLayout(0, 8));
            Panel topHead = whitePanel(new GridLayout(2, 1, 0, 4));
            topHead.add(sectionTitle("QR Code Generator", Label.LEFT));
            topHead.add(mutedLabel("Select a meal and generate a QR code for student login.", Label.LEFT));

            Panel controls = new Panel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            controls.setBackground(Color.WHITE);
            controls.add(UI.boldLabel("Select Meal:"));
            mealChoice = new Choice();
            mealChoice.addItem("BREAKFAST"); mealChoice.addItem("LUNCH");
            mealChoice.addItem("SNACKS");    mealChoice.addItem("DINNER");
            mealChoice.setFont(new Font("Arial", Font.BOLD, 13));
            controls.add(mealChoice);

            Button genBtn = UI.button("GENERATE QR", UI.NAVY);
            genBtn.addActionListener(e -> {
                String meal = mealChoice.getSelectedItem();
                String base = MessCardSystem.ngrokUrl.isEmpty()
                    ? "http://" + MessCardSystem.serverIP + ":" + MessCardSystem.serverPort
                    : MessCardSystem.ngrokUrl;
                String url = base + "/meal?type=" + meal + "&admin=" + adminId;
                java.awt.image.BufferedImage img = QRCodeGenerator.generateQRImage(url, 280);
                if (img != null) {
                    qrCanvas.setImage(img);
                    qrStatus.setForeground(UI.mealColor(meal));
                    qrStatus.setText(meal + " QR ready! URL: " + url);
                } else {
                    qrStatus.setForeground(UI.RED);
                    qrStatus.setText("QR generation failed.");
                }
            });
            controls.add(genBtn);

            topCard.add(topHead, BorderLayout.NORTH);
            topCard.add(controls, BorderLayout.SOUTH);

            qrStatus = new Label("", Label.CENTER);
            qrStatus.setFont(new Font("Arial", Font.BOLD, 12));

            Panel qrCard = whitePanel(new BorderLayout(0, 8));
            Label qrNote = mutedLabel("Generated QR code will appear below.", Label.CENTER);
            Panel canvasWrap = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 20));
            canvasWrap.setBackground(Color.WHITE);
            qrCanvas = new UI.ImageCanvas(280, 280);
            canvasWrap.add(qrCanvas);
            qrCard.add(qrNote, BorderLayout.NORTH);
            qrCard.add(canvasWrap, BorderLayout.CENTER);

            p.add(topCard, BorderLayout.NORTH);
            p.add(qrCard, BorderLayout.CENTER);
            p.add(qrStatus, BorderLayout.SOUTH);
            content.add(p, "QR");
        }

        // ── Meal Logs tab ─────────────────────────────────────────────────

        private void buildLogsTab() {
            Panel p = new Panel(new BorderLayout(0, 8));
            p.setBackground(UI.BG);

            Panel top = whitePanel(new BorderLayout(0, 8));
            Panel head = whitePanel(new GridLayout(2, 1, 0, 4));
            head.add(sectionTitle("Meal Logs", Label.LEFT));
            head.add(mutedLabel("This page shows meal entries recorded for students under this admin.", Label.LEFT));

            Panel controls = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            controls.setBackground(Color.WHITE);
            Button ref = UI.button("REFRESH", UI.BLUE);
            ref.addActionListener(e -> refreshLogs());
            controls.add(ref);
            top.add(head, BorderLayout.NORTH);
            top.add(controls, BorderLayout.SOUTH);

            logsTa = UI.recordArea();
            Panel logCard = whitePanel(new BorderLayout());
            logCard.add(logsTa, BorderLayout.CENTER);

            p.add(top, BorderLayout.NORTH);
            p.add(logCard, BorderLayout.CENTER);
            content.add(p, "Logs");
        }

        private void refreshLogs() {
            List<String[]> logs = DatabaseManager.getAllMealLogs(adminId);
            StringBuilder sb = new StringBuilder();
            sb.append("  Total Logs: ").append(logs.size()).append("\n");
            sb.append("  " + "-".repeat(76) + "\n");
            sb.append(UI.formatAllLogs(logs));
            logsTa.setText(sb.toString());
        }

        // ── Menu tab ──────────────────────────────────────────────────────

        private void buildMenuTab() {
            Panel p = new Panel(new BorderLayout(0, 8));
            p.setBackground(UI.BG);

            String[] DAYS  = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
            String[] MEALS = {"BREAKFAST","LUNCH","SNACKS","DINNER"};

            Choice dayChoice  = new Choice(); for (String d : DAYS)  dayChoice.addItem(d);
            Choice mealChoice2 = new Choice(); for (String m : MEALS) mealChoice2.addItem(m);
            TextArea itemsArea = new TextArea("", 10, 24, TextArea.SCROLLBARS_VERTICAL_ONLY);
            itemsArea.setFont(new Font("Arial", Font.PLAIN, 13));
            itemsArea.setBackground(Color.WHITE);
            Label menuStatus = new Label("", Label.CENTER);

            Panel editorCard = whitePanel(new BorderLayout(0, 8));
            Panel editorHead = whitePanel(new GridLayout(2, 1, 0, 4));
            editorHead.add(sectionTitle("Menu Editor", Label.LEFT));
            editorHead.add(mutedLabel("Choose a day and meal, enter the menu items, then save or update.", Label.LEFT));
            editorCard.add(editorHead, BorderLayout.NORTH);

            Panel editorBody = whitePanel(new BorderLayout(0, 8));
            Panel selectorGrid = whitePanel(new GridLayout(2, 2, 8, 8));
            selectorGrid.add(UI.boldLabel("Day:"));
            selectorGrid.add(dayChoice);
            selectorGrid.add(UI.boldLabel("Meal:"));
            selectorGrid.add(mealChoice2);
            editorBody.add(selectorGrid, BorderLayout.NORTH);

            Panel itemsWrap = whitePanel(new BorderLayout(0, 4));
            itemsWrap.add(UI.boldLabel("Items (one per line):"), BorderLayout.NORTH);
            itemsWrap.add(itemsArea, BorderLayout.CENTER);
            editorBody.add(itemsWrap, BorderLayout.CENTER);

            Button fetchBtn  = UI.button("FETCH EXISTING", UI.BLUE);
            Button saveBtn   = UI.button("SAVE MENU", UI.GREEN);
            Button deleteBtn = UI.button("DELETE MENU", UI.RED);

            Panel editorActions = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            editorActions.setBackground(Color.WHITE);
            editorActions.add(fetchBtn);
            editorActions.add(saveBtn);
            editorActions.add(deleteBtn);
            editorActions.add(menuStatus);

            editorCard.add(editorBody, BorderLayout.CENTER);
            editorCard.add(editorActions, BorderLayout.SOUTH);

            Panel viewCard = whitePanel(new BorderLayout(0, 8));
            TextArea menuDisplay = new TextArea("", 14, 28, TextArea.SCROLLBARS_BOTH);
            menuDisplay.setFont(new Font("Monospaced", Font.BOLD, 12));
            menuDisplay.setEditable(false);
            menuDisplay.setBackground(Color.WHITE);

            Panel viewTop = whitePanel(new GridLayout(3, 1, 0, 4));
            viewTop.add(sectionTitle("Menu Preview", Label.LEFT));
            viewTop.add(mutedLabel("Scrollable preview for one full day menu.", Label.LEFT));
            Panel viewControls = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            viewControls.setBackground(Color.WHITE);
            Choice viewDay = new Choice(); viewDay.addItem("TODAY"); for (String d : DAYS) viewDay.addItem(d);
            Button viewBtn = UI.button("SHOW MENU", UI.NAVY);
            Runnable refreshPreview = () -> {
                String sel = viewDay.getSelectedItem();
                String day = sel.equals("TODAY")
                    ? LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : sel;
                List<String[]> menu = DatabaseManager.getDayMenu(adminId, day);
                menuDisplay.setText(previewMenuText("Menu for " + day, menu));
            };
            viewBtn.addActionListener(e -> refreshPreview.run());
            viewControls.add(UI.boldLabel("Show menu for:"));
            viewControls.add(viewDay);
            viewControls.add(viewBtn);
            viewTop.add(viewControls);
            viewCard.add(viewTop, BorderLayout.NORTH);
            viewCard.add(menuDisplay, BorderLayout.CENTER);

            fetchBtn.addActionListener(e -> {
                String existing = DatabaseManager.getMenu(adminId, dayChoice.getSelectedItem(), mealChoice2.getSelectedItem());
                itemsArea.setText(editorMenuText(existing));
                menuStatus.setForeground(UI.MUTED);
                menuStatus.setText(existing.isEmpty() ? "No saved menu found." : "Existing menu loaded.");
                viewDay.select(dayChoice.getSelectedItem());
                refreshPreview.run();
            });
            saveBtn.addActionListener(e -> {
                String items = normalizeMenuText(itemsArea.getText());
                if (items.isEmpty()) {
                    menuStatus.setForeground(UI.RED);
                    menuStatus.setText("Items cannot be empty!");
                    return;
                }
                if (DatabaseManager.setMenu(adminId, dayChoice.getSelectedItem(), mealChoice2.getSelectedItem(), items)) {
                    itemsArea.setText(editorMenuText(items));
                    menuStatus.setForeground(UI.GREEN);
                    menuStatus.setText("Menu saved successfully.");
                    viewDay.select(dayChoice.getSelectedItem());
                    refreshPreview.run();
                } else {
                    menuStatus.setForeground(UI.RED);
                    menuStatus.setText("Could not save menu.");
                }
            });
            deleteBtn.addActionListener(e -> {
                if (DatabaseManager.deleteMenu(adminId, dayChoice.getSelectedItem(), mealChoice2.getSelectedItem())) {
                    itemsArea.setText("");
                    menuStatus.setForeground(UI.ORANGE);
                    menuStatus.setText("Menu deleted.");
                    viewDay.select(dayChoice.getSelectedItem());
                    refreshPreview.run();
                } else {
                    menuStatus.setForeground(UI.RED);
                    menuStatus.setText("Nothing to delete for this selection.");
                }
            });

            Panel body = new Panel(new GridLayout(1, 2, 8, 0));
            body.setBackground(UI.BG);
            body.add(wrapCard(editorCard));
            body.add(wrapCard(viewCard));

            p.add(body, BorderLayout.CENTER);
            refreshPreview.run();
            content.add(p, "Menu");
        }

        // ── Analytics tab ─────────────────────────────────────────────────

        private void buildAnalyticsTab() {
            Panel p = new Panel(new BorderLayout(0, 8));
            p.setBackground(UI.BG);

            Panel top = whitePanel(new BorderLayout(0, 8));
            Panel reportHead = whitePanel(new GridLayout(2, 1, 0, 4));
            reportHead.add(sectionTitle("Reports", Label.LEFT));
            reportHead.add(mutedLabel("This tab shows a simple meal summary and monthly bill report.", Label.LEFT));
            TextField monthTf = UI.textField(8);
            monthTf.setText(LocalDate.now().toString().substring(0, 7));
            Button refreshBtn = UI.button("REFRESH", UI.BLUE);
            Panel controls = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            controls.setBackground(Color.WHITE);
            controls.add(UI.boldLabel("Month (YYYY-MM):"));
            controls.add(monthTf);
            controls.add(refreshBtn);
            top.add(reportHead, BorderLayout.NORTH);
            top.add(controls, BorderLayout.SOUTH);

            TextArea area = UI.recordArea();

            Runnable refresh = () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("  Report Summary\n");
                sb.append("  " + "-".repeat(36) + "\n");
                sb.append("  1. Meals by Type\n");
                sb.append("  " + "-".repeat(36) + "\n");
                for (String[] r : DatabaseManager.getMealCountByType(adminId))
                    sb.append(String.format("  %-14s  %s meals%n", r[0], r[1]));
                sb.append("\n  2. Monthly Bill for ").append(monthTf.getText()).append("\n");
                sb.append("  " + "-".repeat(60) + "\n");
                sb.append(String.format("  %-12s  %-22s  %-10s  %-8s%n", "ROLL", "NAME", "BILL (₹)", "MEALS"));
                for (String[] r : DatabaseManager.getMonthlyBill(adminId, monthTf.getText()))
                    sb.append(String.format("  %-12s  %-22s  %-10s  %-8s%n", r[0], r[1], r[2], r[3]));
                area.setText(sb.toString());
            };

            refreshBtn.addActionListener(e -> refresh.run());
            refresh.run();

            Panel resultCard = whitePanel(new BorderLayout());
            resultCard.add(area, BorderLayout.CENTER);

            p.add(top, BorderLayout.NORTH);
            p.add(resultCard, BorderLayout.CENTER);
            content.add(p, "Reports");
        }

    }

    // ══════════════════════════════════════════════════════════════════════
    //  4. STUDENT SCREEN — Meal history dashboard
    // ══════════════════════════════════════════════════════════════════════

    public static class StudentScreen extends Frame {

        public StudentScreen(String roll, String name, int adminId) {
            setTitle("Meal Records — " + name);
            setSize(560, 500);
            setLayout(new BorderLayout());
            setBackground(UI.BG);
            setResizable(false);

            add(UI.header("STUDENT RECORDS", "Meal history and today's menu for " + name), BorderLayout.NORTH);

            Label summaryLbl = new Label("", Label.LEFT);
            summaryLbl.setFont(new Font("Arial", Font.BOLD, 12));
            summaryLbl.setForeground(UI.NAVY);

            TextArea recArea = UI.recordArea();
            TextArea menuArea = UI.recordArea();
            menuArea.setRows(8);

            Runnable refresh = () -> {
                List<String[]> records = DatabaseManager.getMealRecords(roll, adminId);
                long bf = records.stream().filter(r -> r[0].equals("BREAKFAST")).count();
                long lu = records.stream().filter(r -> r[0].equals("LUNCH")).count();
                long sn = records.stream().filter(r -> r[0].equals("SNACKS")).count();
                long di = records.stream().filter(r -> r[0].equals("DINNER")).count();
                summaryLbl.setText(
                    "Roll: " + roll +
                    "   Breakfast: " + bf +
                    "   Lunch: " + lu +
                    "   Snacks: " + sn +
                    "   Dinner: " + di
                );
                recArea.setText(UI.formatMealRecords(records));

                String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                List<String[]> menu = DatabaseManager.getDayMenu(adminId, today);
                if (menu.isEmpty()) {
                    menuArea.setText("  No menu set for today (" + today + ") yet.");
                } else {
                    menuArea.setText(previewMenuText("Today's Menu (" + today + ")", menu));
                }
            };

            Panel summaryCard = whitePanel(new BorderLayout(0, 4));
            summaryCard.add(sectionTitle("Student Summary", Label.LEFT), BorderLayout.NORTH);
            summaryCard.add(summaryLbl, BorderLayout.CENTER);

            Panel recordsCard = whitePanel(new BorderLayout(0, 4));
            recordsCard.add(sectionTitle("Meal Records", Label.LEFT), BorderLayout.NORTH);
            recordsCard.add(recArea, BorderLayout.CENTER);

            Panel menuCard = whitePanel(new BorderLayout(0, 4));
            menuCard.add(sectionTitle("Today's Menu", Label.LEFT), BorderLayout.NORTH);
            menuCard.add(menuArea, BorderLayout.CENTER);

            Panel center = new Panel(new GridLayout(3, 1, 0, 8));
            center.setBackground(UI.BG);
            center.add(summaryCard);
            center.add(recordsCard);
            center.add(menuCard);
            add(center, BorderLayout.CENTER);

            Panel footer = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
            footer.setBackground(UI.BG);
            Button refreshBtn = UI.button("REFRESH", UI.BLUE);
            refreshBtn.addActionListener(e -> refresh.run());
            Button closeBtn = UI.button("LOGOUT", UI.RED);
            closeBtn.addActionListener(e -> dispose());
            footer.add(refreshBtn); footer.add(closeBtn);
            add(footer, BorderLayout.SOUTH);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            refresh.run();
            UI.center(this);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  5. SCAN STATION SCREEN — Shows QR codes for all 4 meals
    // ══════════════════════════════════════════════════════════════════════

    public static class ScanStationScreen extends Frame {

        private static final String[] MEALS = {"BREAKFAST","LUNCH","SNACKS","DINNER"};
        private static final String[] TIMES = {"7:00–9:00 AM","12:00–2:00 PM","4:00–5:30 PM","7:00–9:00 PM"};

        private final int adminId;
        private UI.ImageCanvas[] canvases = new UI.ImageCanvas[4];

        public ScanStationScreen(int adminId) {
            this.adminId = adminId;
            setTitle("Mess Scan Station");
            setSize(900, 620);
            setLayout(new BorderLayout());
            setBackground(UI.BG);
            setResizable(false);

            add(UI.header("MESS SCAN STATION", "Students scan the QR code for their meal"), BorderLayout.NORTH);

            Panel grid = new Panel(new GridLayout(2, 2, 10, 10));
            grid.setBackground(UI.BG);
            for (int i = 0; i < MEALS.length; i++) grid.add(buildMealCard(i));

            Panel center = new Panel(new BorderLayout());
            center.setBackground(UI.BG);
            center.add(grid, BorderLayout.CENTER);
            add(center, BorderLayout.CENTER);

            Panel footer = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
            footer.setBackground(UI.BG);
            Button closeBtn = UI.button("CLOSE STATION", UI.RED);
            closeBtn.addActionListener(e -> dispose());
            footer.add(closeBtn);
            add(footer, BorderLayout.SOUTH);

            generateQRCodes();
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            UI.center(this);
        }

        private Panel buildMealCard(int idx) {
            String meal  = MEALS[idx];
            String time  = TIMES[idx];
            Color  color = UI.mealColor(meal);

            Panel card = new Panel(new BorderLayout(4, 4));
            card.setBackground(Color.WHITE);

            // Meal name header
            Label ml = new Label(meal + "  " + time, Label.CENTER);
            ml.setFont(new Font("Arial", Font.BOLD, 13));
            ml.setForeground(color);
            card.add(ml, BorderLayout.NORTH);

            // QR code image
            canvases[idx] = new UI.ImageCanvas(180, 180);
            Panel canvasWrap = new Panel(new FlowLayout(FlowLayout.CENTER));
            canvasWrap.setBackground(Color.WHITE);
            canvasWrap.add(canvases[idx]);
            card.add(canvasWrap, BorderLayout.CENTER);

            // Kiosk login button
            Button loginBtn = UI.button("KIOSK LOGIN", color);
            loginBtn.addActionListener(e -> new MealLoginScreen(meal, adminId, ScanStationScreen.this).setVisible(true));
            card.add(loginBtn, BorderLayout.SOUTH);
            return card;
        }

        private void generateQRCodes() {
            String base = MessCardSystem.ngrokUrl.isEmpty()
                ? "http://" + MessCardSystem.serverIP + ":" + MessCardSystem.serverPort
                : MessCardSystem.ngrokUrl;
            for (int i = 0; i < MEALS.length; i++) {
                String url = base + "/meal?type=" + MEALS[i] + "&admin=" + adminId;
                java.awt.image.BufferedImage img = QRCodeGenerator.generateQRImage(url, 180);
                if (img != null) canvases[i].setImage(img);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  6. MEAL LOGIN SCREEN — Kiosk login for students at the scan station
    // ══════════════════════════════════════════════════════════════════════

    public static class MealLoginScreen extends Frame {

        private final String mealType;
        private final int    adminId;

        public MealLoginScreen(String mealType, int adminId, Frame parent) {
            this.mealType = mealType;
            this.adminId  = adminId;
            Color color = UI.mealColor(mealType);

            setTitle("Meal Login — " + mealType);
            setSize(380, 300);
            setLayout(new BorderLayout());
            setBackground(UI.BG);
            setResizable(false);

            // Header
            Panel header = new Panel(new GridLayout(2, 1));
            header.setBackground(UI.NAVY);
            header.setPreferredSize(new Dimension(380, 70));
            Label title = new Label("MEAL CHECK-IN", Label.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 18));
            title.setForeground(Color.WHITE);
            Label mealLbl = new Label(mealType, Label.CENTER);
            mealLbl.setFont(new Font("Arial", Font.BOLD, 13));
            mealLbl.setForeground(color);
            header.add(title); header.add(mealLbl);
            add(header, BorderLayout.NORTH);

            // Form
            Panel form = new Panel(new GridLayout(6, 1, 6, 6));
            form.setBackground(Color.WHITE);
            TextField rollTf = UI.textField(16);
            TextField passTf = UI.passwordField(16);
            Label statusLbl = new Label("", Label.CENTER);
            statusLbl.setFont(new Font("Arial", Font.BOLD, 12));

            form.add(UI.boldLabel("Roll Number:")); form.add(rollTf);
            form.add(UI.boldLabel("Password:"));   form.add(passTf);
            form.add(statusLbl); form.add(new Label(""));
            add(form, BorderLayout.CENTER);

            Panel btnBar = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            btnBar.setBackground(UI.BG);
            Button confirmBtn = UI.button("CONFIRM MEAL", color);
            Button cancelBtn  = UI.button("CANCEL", UI.MUTED);
            btnBar.add(confirmBtn); btnBar.add(cancelBtn);
            add(btnBar, BorderLayout.SOUTH);

            Runnable attempt = () -> {
                String roll = rollTf.getText().trim();
                String pass = passTf.getText().trim();
                if (roll.isEmpty() || pass.isEmpty()) {
                    statusLbl.setForeground(UI.RED); statusLbl.setText("Fill in all fields."); return;
                }
                if (!DatabaseManager.isRegistered(roll, adminId)) {
                    statusLbl.setForeground(UI.RED); statusLbl.setText("Roll not registered in this mess!"); return;
                }
                String name = DatabaseManager.validateStudent(roll, pass, adminId);
                if (name == null) {
                    statusLbl.setForeground(UI.RED); statusLbl.setText("Incorrect password."); passTf.setText(""); return;
                }
                if (DatabaseManager.hasMealToday(roll, adminId, mealType)) {
                    statusLbl.setForeground(UI.ORANGE); statusLbl.setText(mealType + " already recorded today!"); return;
                }
                DatabaseManager.logMeal(roll, adminId, mealType);
                dispose();
                showSuccess(roll.toUpperCase(), name);
            };

            confirmBtn.addActionListener(e -> attempt.run());
            passTf.addActionListener(e -> attempt.run());
            cancelBtn.addActionListener(e -> dispose());

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            Dimension ps = parent.getSize(); Point pl = parent.getLocation();
            setLocation(pl.x + (ps.width - 380) / 2, pl.y + (ps.height - 300) / 2);
        }

        private void showSuccess(String roll, String name) {
            // Show a simple success dialog instead of a whole new frame
            Dialog d = new Dialog(new Frame(), "Meal Recorded!", true);
            d.setSize(360, 200);
            d.setLayout(new GridLayout(4, 1, 6, 6));
            d.setBackground(UI.GREEN);

            Label title = new Label("✓  MEAL RECORDED", Label.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 18));
            title.setForeground(Color.WHITE);

            Label msg = new Label("Welcome, " + name + "!", Label.CENTER);
            msg.setFont(new Font("Arial", Font.PLAIN, 14));
            msg.setForeground(Color.WHITE);

            Label detail = new Label(mealType + " for " + roll + " has been recorded.", Label.CENTER);
            detail.setFont(new Font("Arial", Font.ITALIC, 12));
            detail.setForeground(new Color(220, 255, 220));

            Button doneBtn = UI.button("DONE", UI.NAVY);
            doneBtn.addActionListener(e -> d.dispose());

            d.add(title); d.add(msg); d.add(detail); d.add(doneBtn);
            d.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { d.dispose(); }
            });
            Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
            d.setLocation((scr.width - 360) / 2, (scr.height - 200) / 2);
            d.setVisible(true);
        }
    }
}
