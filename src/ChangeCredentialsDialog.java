import java.awt.*;
import java.awt.event.*;

/**
 * Reusable dialog for changing username/password.
 * Works for super admin, admin, and student.
 */
public class ChangeCredentialsDialog extends Dialog {

    public enum Role { SUPER_ADMIN, ADMIN, STUDENT }

    public ChangeCredentialsDialog(Frame parent, Role role, int adminId, String roll) {
        super(parent, "Change Credentials", true);
        setSize(420, 360);
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        setResizable(false);

        Color accent = role == Role.SUPER_ADMIN ? new Color(139, 92, 246)
                     : role == Role.ADMIN        ? new Color(59, 130, 246)
                     :                             new Color(16, 185, 129);

        // ── Header ──────────────────────────────────────────────────────────
        Panel header = new Panel(new GridLayout(2, 1));
        header.setBackground(new Color(30, 41, 59));
        header.setPreferredSize(new Dimension(420, 60));
        Panel stripe = new Panel(); stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(420, 4));
        Label title = new Label("CHANGE CREDENTIALS", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(stripe); header.add(title);
        add(header, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────────────────
        Panel form = new Panel(new GridBagLayout());
        form.setBackground(new Color(15, 23, 42));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 28, 6, 28);

        // Current password (verify identity)
        g.gridx = 0; g.gridy = 0;
        form.add(whiteLabel("Current Password:"), g);
        g.gridx = 1;
        TextField curPassTf = UIHelper.styledTextField(14);
        curPassTf.setEchoChar('*');
        form.add(curPassTf, g);

        // New username (only for admin — not for super admin, not for student)
        TextField newUserTf = null;
        if (role == Role.ADMIN) {
            g.gridx = 0; g.gridy = 1;
            form.add(whiteLabel("New Username:"), g);
            g.gridx = 1;
            newUserTf = UIHelper.styledTextField(14);
            form.add(newUserTf, g);
        }

        int nextRow = (role == Role.ADMIN) ? 2 : 1;
        g.gridx = 0; g.gridy = nextRow;
        form.add(whiteLabel("New Password:"), g);
        g.gridx = 1;
        TextField newPassTf = UIHelper.styledTextField(14);
        newPassTf.setEchoChar('*');
        form.add(newPassTf, g);

        g.gridy = nextRow + 1;
        form.add(whiteLabel("Confirm Password:"), g);
        g.gridx = 1;
        TextField confPassTf = UIHelper.styledTextField(14);
        confPassTf.setEchoChar('*');
        form.add(confPassTf, g);

        g.gridy = nextRow + 2; g.gridx = 0; g.gridwidth = 2;
        Label statusLbl = new Label("", Label.CENTER);
        statusLbl.setFont(new Font("Arial", Font.BOLD, 12));
        form.add(statusLbl, g);

        add(form, BorderLayout.CENTER);

        // ── Buttons ──────────────────────────────────────────────────────────
        Panel btnBar = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        btnBar.setBackground(new Color(15, 23, 42));
        FlatButton saveBtn   = new FlatButton("SAVE CHANGES", accent, Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(160, 38));
        FlatButton cancelBtn = new FlatButton("CANCEL", new Color(51, 65, 85), new Color(148, 163, 184));
        cancelBtn.setPreferredSize(new Dimension(100, 38));
        btnBar.add(saveBtn); btnBar.add(cancelBtn);
        add(btnBar, BorderLayout.SOUTH);

        final TextField finalNewUserTf = newUserTf;

        saveBtn.addActionListener(e -> {
            String curPass  = curPassTf.getText().trim();
            String newPass  = newPassTf.getText().trim();
            String confPass = confPassTf.getText().trim();
            String newUser  = finalNewUserTf != null ? finalNewUserTf.getText().trim().toLowerCase() : "";

            if (curPass.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
                statusLbl.setForeground(new Color(239, 68, 68));
                statusLbl.setText("Fill in all fields."); return;
            }
            if (!newPass.equals(confPass)) {
                statusLbl.setForeground(new Color(239, 68, 68));
                statusLbl.setText("New passwords don't match!"); return;
            }
            if (newPass.length() < 4) {
                statusLbl.setForeground(new Color(239, 68, 68));
                statusLbl.setText("Password must be at least 4 characters."); return;
            }

            boolean success = false;

            if (role == Role.SUPER_ADMIN) {
                if (!curPass.equals(MessCardSystem.SUPER_ADMIN_PASS)) {
                    statusLbl.setForeground(new Color(239, 68, 68));
                    statusLbl.setText("Current password wrong!"); return;
                }
                MessCardSystem.SUPER_ADMIN_PASS = newPass;
                success = true;

            } else if (role == Role.ADMIN) {
                String[] check = DatabaseManager.validateAdminById(adminId, curPass);
                if (check == null) {
                    statusLbl.setForeground(new Color(239, 68, 68));
                    statusLbl.setText("Current password wrong!"); return;
                }
                success = DatabaseManager.updateAdminCredentials(adminId,
                    newUser.isEmpty() ? null : newUser, newPass);

            } else { // STUDENT
                int aid = DatabaseManager.findAdminForRoll(roll);
                String name = DatabaseManager.validateStudent(roll, curPass, aid);
                if (name == null) {
                    statusLbl.setForeground(new Color(239, 68, 68));
                    statusLbl.setText("Current password wrong!"); return;
                }
                success = DatabaseManager.changeStudentPassword(roll, aid, newPass);
            }

            if (success) {
                statusLbl.setForeground(new Color(16, 185, 129));
                statusLbl.setText("Credentials updated successfully!");
                curPassTf.setText(""); newPassTf.setText(""); confPassTf.setText("");
            } else {
                statusLbl.setForeground(new Color(239, 68, 68));
                statusLbl.setText("Update failed. Try again.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        Dimension ps = parent.getSize(); Point pl2 = parent.getLocation();
        setLocation(pl2.x + (ps.width - 420) / 2, pl2.y + (ps.height - 360) / 2);
    }

    private Label whiteLabel(String text) {
        Label l = new Label(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(new Color(148, 163, 184));
        return l;
    }
}
