import java.awt.*;
import java.awt.event.*;

/**
 * Unified login dialog for Super Admin and regular admins.
 * If fixedUsername is set (e.g. "superadmin"), username field is locked.
 */
public class AdminLoginDialog extends Dialog {

    public AdminLoginDialog(Frame parent, String headerTitle, String fixedUsername) {
        super(parent, headerTitle, true);
        boolean isSuperAdmin = fixedUsername != null;

        setSize(420, isSuperAdmin ? 280 : 320);
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        setResizable(false);

        // ── Header ──────────────────────────────────────────────────────────
        Panel header = new Panel(new GridLayout(2, 1));
        header.setBackground(new Color(30, 41, 59));
        header.setPreferredSize(new Dimension(420, 65));

        Color accent = isSuperAdmin ? new Color(139, 92, 246) : new Color(59, 130, 246);

        Panel stripe = new Panel(); stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(420, 4));

        Label title = new Label(headerTitle, Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        header.add(stripe);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────────────────
        Panel form = new Panel(new GridBagLayout());
        form.setBackground(new Color(15, 23, 42));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 28, 6, 28);

        TextField userTf = null;
        if (!isSuperAdmin) {
            g.gridx = 0; g.gridy = 0;
            Label ul = new Label("Username:"); ul.setFont(new Font("Arial", Font.BOLD, 12));
            ul.setForeground(new Color(148, 163, 184));
            form.add(ul, g);

            g.gridx = 1;
            userTf = UIHelper.styledTextField(14);
            form.add(userTf, g);
        }

        int row = isSuperAdmin ? 0 : 1;
        g.gridx = 0; g.gridy = row;
        Label pl = new Label("Password:"); pl.setFont(new Font("Arial", Font.BOLD, 12));
        pl.setForeground(new Color(148, 163, 184));
        form.add(pl, g);

        g.gridx = 1;
        TextField passTf = UIHelper.styledTextField(14);
        passTf.setEchoChar('*');
        form.add(passTf, g);

        g.gridy = row + 1; g.gridx = 0; g.gridwidth = 2;
        Label statusLbl = new Label("", Label.CENTER);
        statusLbl.setFont(new Font("Arial", Font.BOLD, 12));
        form.add(statusLbl, g);

        add(form, BorderLayout.CENTER);

        // ── Buttons ──────────────────────────────────────────────────────────
        Panel btnBar = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        btnBar.setBackground(new Color(15, 23, 42));

        FlatButton loginBtn  = new FlatButton("LOGIN", accent, Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(130, 38));

        FlatButton cancelBtn = new FlatButton("CANCEL", new Color(51, 65, 85), new Color(148, 163, 184));
        cancelBtn.setPreferredSize(new Dimension(100, 38));

        btnBar.add(loginBtn); btnBar.add(cancelBtn);
        add(btnBar, BorderLayout.SOUTH);

        final TextField finalUserTf = userTf;

        Runnable attempt = () -> {
            String pass = passTf.getText().trim();
            String user = isSuperAdmin ? fixedUsername
                : (finalUserTf != null ? finalUserTf.getText().trim().toLowerCase() : "");

            if (pass.isEmpty() || (!isSuperAdmin && user.isEmpty())) {
                statusLbl.setForeground(new Color(239, 68, 68));
                statusLbl.setText("Please fill in all fields.");
                return;
            }

            if (isSuperAdmin) {
                if (pass.equals(MessCardSystem.SUPER_ADMIN_PASS)) {
                    dispose();
                    new SuperAdminFrame().setVisible(true);
                } else {
                    statusLbl.setForeground(new Color(239, 68, 68));
                    statusLbl.setText("Wrong password!");
                    passTf.setText("");
                }
            } else {
                String[] result = DatabaseManager.validateAdmin(user, pass);
                if (result != null) {
                    dispose();
                    new AdminFrame(Integer.parseInt(result[0]), result[1], user).setVisible(true);
                } else {
                    statusLbl.setForeground(new Color(239, 68, 68));
                    statusLbl.setText("Invalid username or password!");
                    passTf.setText("");
                }
            }
        };

        loginBtn.addActionListener(e  -> attempt.run());
        passTf.addActionListener(e    -> attempt.run());
        cancelBtn.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });

        Dimension ps = parent.getSize(); Point pl2 = parent.getLocation();
        setLocation(pl2.x + (ps.width - 420) / 2, pl2.y + (ps.height - getHeight()) / 2);
    }
}
