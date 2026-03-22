import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StudentDashboardFrame extends Frame {

    public StudentDashboardFrame(String roll, String name, int adminId) {
        setTitle("Meal Records - " + name);
        setSize(560, 520);
        setLayout(new BorderLayout());
        setBackground(UIHelper.COL_BG);
        setResizable(false);

        Panel header = new Panel(new GridLayout(3,1));
        header.setBackground(UIHelper.COL_NAVY);
        header.setPreferredSize(new Dimension(560,90));
        Label greet = new Label("Welcome, " + name + "!", Label.CENTER);
        greet.setFont(new Font("Arial",Font.BOLD,20)); greet.setForeground(Color.WHITE);
        Label rollLbl = new Label("Roll Number: " + roll, Label.CENTER);
        rollLbl.setFont(new Font("Arial",Font.PLAIN,13)); rollLbl.setForeground(new Color(189,195,199));
        Label subLbl = new Label("Your Meal Records", Label.CENTER);
        subLbl.setFont(new Font("Arial",Font.ITALIC,12)); subLbl.setForeground(UIHelper.COL_GREEN);
        header.add(greet); header.add(rollLbl); header.add(subLbl);
        add(header, BorderLayout.NORTH);

        List<String[]> records = DatabaseManager.getMealRecords(roll, adminId);
        long bf = records.stream().filter(r -> r[0].equals("BREAKFAST")).count();
        long lu = records.stream().filter(r -> r[0].equals("LUNCH")).count();
        long sn = records.stream().filter(r -> r[0].equals("SNACKS")).count();
        long di = records.stream().filter(r -> r[0].equals("DINNER")).count();

        Panel statsBar = new Panel(new GridLayout(1,4,2,0));
        statsBar.setBackground(UIHelper.COL_BG);
        statsBar.setPreferredSize(new Dimension(560,52));
        statsBar.add(statBox("BREAKFAST", bf, UIHelper.mealColor("BREAKFAST")));
        statsBar.add(statBox("LUNCH",     lu, UIHelper.mealColor("LUNCH")));
        statsBar.add(statBox("SNACKS",    sn, UIHelper.mealColor("SNACKS")));
        statsBar.add(statBox("DINNER",    di, UIHelper.mealColor("DINNER")));

        Panel recPanel = new Panel(new BorderLayout());
        recPanel.setBackground(Color.WHITE);

        Panel recHeader = new Panel(new FlowLayout(FlowLayout.LEFT,12,6));
        recHeader.setBackground(new Color(236,240,241));
        Label recTitle = new Label("Previous Meal Records");
        recTitle.setFont(new Font("Arial",Font.BOLD,13)); recTitle.setForeground(UIHelper.COL_NAVY);
        recHeader.add(recTitle);

        TextArea recArea = UIHelper.recordTextArea();
        recArea.setText(UIHelper.formatMealRecords(records));

        Panel totalBar = new Panel(new FlowLayout(FlowLayout.RIGHT,12,4));
        totalBar.setBackground(new Color(236,240,241));
        Label sumLbl = new Label("Total meals: " + records.size());
        sumLbl.setFont(new Font("Arial",Font.ITALIC,11)); sumLbl.setForeground(UIHelper.COL_MUTED);
        totalBar.add(sumLbl);

        recPanel.add(statsBar,  BorderLayout.NORTH);
        recPanel.add(recHeader, BorderLayout.CENTER);

        Panel recBody = new Panel(new BorderLayout());
        recBody.add(recArea,  BorderLayout.CENTER);
        recBody.add(totalBar, BorderLayout.SOUTH);

        Panel recMain = new Panel(new BorderLayout());
        recMain.add(statsBar, BorderLayout.NORTH);
        recMain.add(recArea,  BorderLayout.CENTER);
        recMain.add(totalBar, BorderLayout.SOUTH);

        add(recMain, BorderLayout.CENTER);

        Panel footer = new Panel(new FlowLayout(FlowLayout.CENTER,12,12));
        footer.setBackground(UIHelper.COL_BG);
        FlatButton refreshBtn  = UIHelper.styledButton("REFRESH", UIHelper.COL_BLUE, new Dimension(120,38));
        FlatButton changePwBtn = UIHelper.styledButton("CHANGE PASSWORD", new Color(16,185,129), new Dimension(180,38));
        changePwBtn.setFontSize(12);
        FlatButton closeBtn    = UIHelper.styledButton("LOGOUT", UIHelper.COL_RED, new Dimension(100,38));
        refreshBtn.addActionListener(e ->
            recArea.setText(UIHelper.formatMealRecords(DatabaseManager.getMealRecords(roll, adminId))));
        changePwBtn.addActionListener(e ->
            new ChangeCredentialsDialog(StudentDashboardFrame.this,
                ChangeCredentialsDialog.Role.STUDENT, adminId, roll).setVisible(true));
        closeBtn.addActionListener(e -> dispose());
        footer.add(refreshBtn); footer.add(changePwBtn); footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        UIHelper.centreFrame(this);
    }

    private Panel statBox(String meal, long count, Color color) {
        Panel p = new Panel(new GridLayout(2,1));
        p.setBackground(color);
        Label c = new Label(String.valueOf(count), Label.CENTER);
        c.setFont(new Font("Arial",Font.BOLD,18)); c.setForeground(Color.WHITE);
        Label n = new Label(meal, Label.CENTER);
        n.setFont(new Font("Arial",Font.PLAIN,10)); n.setForeground(new Color(255,255,255,200));
        p.add(c); p.add(n); return p;
    }
}
