import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MealSuccessFrame extends Frame {

    public MealSuccessFrame(String roll, String name, String mealType, int adminId) {
        Color mealColor = UIHelper.mealColor(mealType);
        setTitle("Welcome - " + name);
        setSize(520, 500);
        setLayout(new BorderLayout());
        setBackground(UIHelper.COL_BG);
        setResizable(false);

        Panel header = new Panel(new GridLayout(3,1));
        header.setBackground(UIHelper.COL_NAVY);
        header.setPreferredSize(new Dimension(520, 90));
        Panel strip = new Panel(); strip.setBackground(mealColor);
        strip.setPreferredSize(new Dimension(520,7));
        Label greet = new Label("Welcome, " + name + "!", Label.CENTER);
        greet.setFont(new Font("Arial",Font.BOLD,20)); greet.setForeground(Color.WHITE);
        Label rollLbl = new Label("Roll: " + roll, Label.CENTER);
        rollLbl.setFont(new Font("Arial",Font.PLAIN,13)); rollLbl.setForeground(new Color(189,195,199));
        header.add(strip); header.add(greet); header.add(rollLbl);
        add(header, BorderLayout.NORTH);

        Panel banner = new Panel(new FlowLayout(FlowLayout.CENTER,12,12));
        banner.setBackground(new Color(39,174,96,50));
        Label check = new Label("✓", Label.CENTER);
        check.setFont(new Font("Arial",Font.BOLD,22)); check.setForeground(UIHelper.COL_GREEN);
        Label msg = new Label(mealType + " recorded! Enjoy your meal.");
        msg.setFont(new Font("Arial",Font.BOLD,14)); msg.setForeground(UIHelper.COL_DARK);
        banner.add(check); banner.add(msg);

        Panel recPanel = new Panel(new BorderLayout());
        recPanel.setBackground(Color.WHITE);
        Panel recHeader = new Panel(new FlowLayout(FlowLayout.LEFT,12,6));
        recHeader.setBackground(new Color(236,240,241));
        Label recTitle = new Label("Your Previous Meal Records");
        recTitle.setFont(new Font("Arial",Font.BOLD,13)); recTitle.setForeground(UIHelper.COL_NAVY);
        recHeader.add(recTitle);
        TextArea recArea = UIHelper.recordTextArea();
        List<String[]> records = DatabaseManager.getMealRecords(roll, adminId);
        recArea.setText(UIHelper.formatMealRecords(records));
        Panel totalBar = new Panel(new FlowLayout(FlowLayout.RIGHT,12,4));
        totalBar.setBackground(new Color(236,240,241));
        Label sumLbl = new Label("Total meals: " + records.size());
        sumLbl.setFont(new Font("Arial",Font.ITALIC,11)); sumLbl.setForeground(UIHelper.COL_MUTED);
        totalBar.add(sumLbl);
        recPanel.add(recHeader, BorderLayout.NORTH);
        recPanel.add(recArea,   BorderLayout.CENTER);
        recPanel.add(totalBar,  BorderLayout.SOUTH);

        Panel body = new Panel(new BorderLayout(0,4));
        body.setBackground(UIHelper.COL_BG);
        body.add(banner,   BorderLayout.NORTH);
        body.add(recPanel, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        Panel footer = new Panel(new FlowLayout(FlowLayout.CENTER,0,12));
        footer.setBackground(UIHelper.COL_BG);
        FlatButton doneBtn = UIHelper.styledButton("DONE", mealColor, new Dimension(160,42));
        doneBtn.setFontSize(15); doneBtn.addActionListener(e -> dispose());
        footer.add(doneBtn);
        add(footer, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        UIHelper.centreFrame(this);
    }
}
