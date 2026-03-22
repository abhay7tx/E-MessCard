import java.awt.*;
import java.awt.event.*;

public class MealLoginFrame extends Frame {

    private final String mealType;
    private final int    adminId;
    private TextField rollTf, passTf;
    private Label statusLbl;

    public MealLoginFrame(String mealType, int adminId, Frame parent) {
        this.mealType = mealType;
        this.adminId  = adminId;
        Color mealColor = UIHelper.mealColor(mealType);

        setTitle("Meal Login - " + mealType);
        setSize(440, 400);
        setLayout(new BorderLayout());
        setBackground(UIHelper.COL_BG);
        setResizable(false);

        Panel header = new Panel(new GridLayout(3,1));
        header.setBackground(UIHelper.COL_NAVY);
        header.setPreferredSize(new Dimension(440, 90));
        Panel strip = new Panel(); strip.setBackground(mealColor);
        strip.setPreferredSize(new Dimension(440,8));
        Label title = new Label("MEAL CHECK-IN", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        Label mealLbl = new Label(mealType, Label.CENTER);
        mealLbl.setFont(new Font("Arial", Font.BOLD, 14));
        mealLbl.setForeground(mealColor);
        header.add(strip); header.add(title); header.add(mealLbl);
        add(header, BorderLayout.NORTH);

        Panel form = new Panel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(14,28,8,28);

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

        Panel btnBar = new Panel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        btnBar.setBackground(UIHelper.COL_BG);
        FlatButton confirmBtn = UIHelper.styledButton("CONFIRM MEAL", mealColor, new Dimension(190,42));
        FlatButton cancelBtn  = UIHelper.styledButton("CANCEL", UIHelper.COL_MUTED, new Dimension(110,42));
        btnBar.add(confirmBtn); btnBar.add(cancelBtn);
        add(btnBar, BorderLayout.SOUTH);

        confirmBtn.addActionListener(e -> attemptLogin());
        passTf.addActionListener(e    -> attemptLogin());
        cancelBtn.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        Dimension ps = parent.getSize(); Point pl2 = parent.getLocation();
        setLocation(pl2.x + (ps.width-440)/2, pl2.y + (ps.height-400)/2);
    }

    private void attemptLogin() {
        String roll = rollTf.getText().trim();
        String pass = passTf.getText().trim();

        if (roll.isEmpty() || pass.isEmpty()) {
            statusLbl.setForeground(UIHelper.COL_RED);
            statusLbl.setText("Please fill in all fields.");
            return;
        }

        if (!DatabaseManager.isRegistered(roll, adminId)) {
            statusLbl.setForeground(UIHelper.COL_RED);
            statusLbl.setText("Roll not registered in this mess!");
            showNotRegisteredDialog(roll);
            return;
        }

        String name = DatabaseManager.validateStudent(roll, pass, adminId);
        if (name == null) {
            statusLbl.setForeground(UIHelper.COL_RED);
            statusLbl.setText("Incorrect password. Try again.");
            passTf.setText(""); return;
        }

        if (DatabaseManager.hasMealToday(roll, adminId, mealType)) {
            statusLbl.setForeground(UIHelper.COL_ORANGE);
            statusLbl.setText(mealType + " already recorded today!");
            showAlreadyDoneDialog(name, roll); return;
        }

        DatabaseManager.logMeal(roll, adminId, mealType);
        dispose();
        new MealSuccessFrame(roll.toUpperCase(), name, mealType, adminId).setVisible(true);
    }

    private void showNotRegisteredDialog(String roll) {
        Dialog d = new Dialog(this, "Not Registered", true);
        d.setSize(420,230); d.setLayout(new BorderLayout()); d.setBackground(UIHelper.COL_BG);
        Panel top = new Panel(new GridLayout(2,1));
        top.setBackground(UIHelper.COL_RED); top.setPreferredSize(new Dimension(420,60));
        Label t = new Label("ACCESS DENIED", Label.CENTER);
        t.setFont(new Font("Arial",Font.BOLD,18)); t.setForeground(Color.WHITE);
        Label s = new Label("Roll: " + roll.toUpperCase(), Label.CENTER);
        s.setFont(new Font("Arial",Font.PLAIN,13)); s.setForeground(new Color(255,200,200));
        top.add(t); top.add(s); d.add(top, BorderLayout.NORTH);
        Panel body = new Panel(new FlowLayout(FlowLayout.CENTER,10,14));
        body.setBackground(Color.WHITE);
        TextArea ta = new TextArea(
            "This roll number is NOT registered\nwith this mess section.\n\n" +
            "Please contact your mess admin.", 4, 36, TextArea.SCROLLBARS_NONE);
        ta.setFont(new Font("Arial",Font.PLAIN,13)); ta.setEditable(false);
        ta.setBackground(Color.WHITE); body.add(ta); d.add(body, BorderLayout.CENTER);
        Panel fb = new Panel(new FlowLayout(FlowLayout.CENTER,0,8));
        fb.setBackground(UIHelper.COL_BG);
        FlatButton ok = UIHelper.styledButton("OK", UIHelper.COL_NAVY, new Dimension(120,36));
        ok.addActionListener(e -> d.dispose()); fb.add(ok); d.add(fb, BorderLayout.SOUTH);
        d.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { d.dispose(); }});
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((ss.width-420)/2,(ss.height-230)/2); d.setVisible(true);
    }

    private void showAlreadyDoneDialog(String name, String roll) {
        Dialog d = new Dialog(this, "Already Recorded", true);
        d.setSize(400,190); d.setLayout(new BorderLayout()); d.setBackground(UIHelper.COL_BG);
        Panel top = new Panel(); top.setBackground(UIHelper.COL_ORANGE);
        top.setPreferredSize(new Dimension(400,46));
        Label t = new Label("Meal Already Recorded Today", Label.CENTER);
        t.setFont(new Font("Arial",Font.BOLD,15)); t.setForeground(Color.WHITE); top.add(t);
        d.add(top, BorderLayout.NORTH);
        Panel body = new Panel(new FlowLayout(FlowLayout.CENTER,10,12));
        body.setBackground(Color.WHITE);
        Label l = new Label(name + " already had " + mealType + " today.");
        l.setFont(new Font("Arial",Font.PLAIN,13)); body.add(l); d.add(body, BorderLayout.CENTER);
        Panel fb = new Panel(new FlowLayout(FlowLayout.CENTER,8,8));
        fb.setBackground(UIHelper.COL_BG);
        FlatButton viewBtn  = UIHelper.styledButton("VIEW RECORDS", UIHelper.COL_BLUE, new Dimension(150,36));
        FlatButton closeBtn = UIHelper.styledButton("CLOSE", UIHelper.COL_MUTED, new Dimension(100,36));
        viewBtn.addActionListener(e -> { d.dispose(); dispose();
            new MealSuccessFrame(roll.toUpperCase(), name, mealType, adminId).setVisible(true); });
        closeBtn.addActionListener(e -> d.dispose());
        fb.add(viewBtn); fb.add(closeBtn); d.add(fb, BorderLayout.SOUTH);
        d.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { d.dispose(); }});
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        d.setLocation((ss.width-400)/2,(ss.height-190)/2); d.setVisible(true);
    }
}
