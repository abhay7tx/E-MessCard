import java.awt.*;
import java.awt.event.*;

public class StudentLoginFrame extends Frame {

    private TextField rollTf, passTf;
    private Label statusLbl;

    public StudentLoginFrame(Frame parent) {
        setTitle("Student Login");
        setSize(420, 380);
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        setResizable(false);

        Panel header = new Panel(new GridLayout(2,1));
        header.setBackground(UIHelper.COL_GREEN);
        header.setPreferredSize(new Dimension(420,70));
        Label title = new Label("STUDENT LOGIN", Label.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,20)); title.setForeground(Color.WHITE);
        Label sub = new Label("View your meal records", Label.CENTER);
        sub.setFont(new Font("Arial",Font.PLAIN,13)); sub.setForeground(new Color(212,239,223));
        header.add(title); header.add(sub);
        add(header, BorderLayout.NORTH);

        Panel form = new Panel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(14,30,8,30);

        g.gridx = 0; g.gridy = 0;
        Label rl = new Label("Roll Number:"); rl.setFont(new Font("Arial",Font.BOLD,13));
        form.add(rl, g);
        g.gridx = 1; rollTf = UIHelper.styledTextField(14); form.add(rollTf, g);

        g.gridx = 0; g.gridy = 1;
        Label pl = new Label("Password:"); pl.setFont(new Font("Arial",Font.BOLD,13));
        form.add(pl, g);
        g.gridx = 1; passTf = UIHelper.styledTextField(14);
        passTf.setEchoChar('*'); form.add(passTf, g);

        g.gridy = 2; g.gridx = 0; g.gridwidth = 2;
        statusLbl = new Label("", Label.CENTER);
        statusLbl.setFont(new Font("Arial",Font.BOLD,13));
        form.add(statusLbl, g);
        add(form, BorderLayout.CENTER);

        Panel btnBar = new Panel(new FlowLayout(FlowLayout.CENTER,14,14));
        btnBar.setBackground(new Color(15, 23, 42));
        FlatButton loginBtn  = UIHelper.styledButton("VIEW MY RECORDS", UIHelper.COL_GREEN, new Dimension(190,42));
        FlatButton cancelBtn = UIHelper.styledButton("CANCEL", UIHelper.COL_MUTED, new Dimension(110,42));
        btnBar.add(loginBtn); btnBar.add(cancelBtn);
        add(btnBar, BorderLayout.SOUTH);

        loginBtn.addActionListener(e  -> attemptLogin());
        passTf.addActionListener(e    -> attemptLogin());
        cancelBtn.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        Dimension ps = parent.getSize(); Point pl2 = parent.getLocation();
        setLocation(pl2.x + (ps.width-420)/2, pl2.y + (ps.height-380)/2);
    }

    private void attemptLogin() {
        String roll = rollTf.getText().trim();
        String pass = passTf.getText().trim();

        if (roll.isEmpty() || pass.isEmpty()) {
            statusLbl.setForeground(UIHelper.COL_RED);
            statusLbl.setText("Please enter roll number and password.");
            return;
        }

        // Auto-detect which admin this student belongs to
        int adminId = DatabaseManager.findAdminForRoll(roll);
        if (adminId == -1) {
            statusLbl.setForeground(UIHelper.COL_RED);
            statusLbl.setText("Roll number not registered in this mess!");
            return;
        }

        String name = DatabaseManager.validateStudent(roll, pass, adminId);
        if (name == null) {
            statusLbl.setForeground(UIHelper.COL_RED);
            statusLbl.setText("Incorrect password. Try again.");
            passTf.setText(""); return;
        }

        dispose();
        new StudentDashboardFrame(roll.toUpperCase(), name, adminId).setVisible(true);
    }
}
