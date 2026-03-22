import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScanStationFrame extends Frame {

    private static final String[] MEALS = {"BREAKFAST","LUNCH","SNACKS","DINNER"};
    private static final String[] TIMES = {"7:00–9:00 AM","12:00–2:00 PM","4:00–5:30 PM","7:00–9:00 PM"};

    private final int adminId;
    private UIHelper.ImageCanvas[] canvases = new UIHelper.ImageCanvas[4];
    private Label statusLbl;

    public ScanStationFrame(int adminId) {
        this.adminId = adminId;
        setTitle("Mess Scan Station");
        setSize(960, 680);
        setLayout(new BorderLayout());
        setBackground(UIHelper.COL_BG);
        setResizable(false);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"));
        add(UIHelper.headerPanel("MESS SCAN STATION", today), BorderLayout.NORTH);

        Panel statusBar = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        statusBar.setBackground(new Color(39, 174, 96));
        statusLbl = new Label("", Label.CENTER);
        statusLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLbl.setForeground(Color.WHITE);
        statusBar.add(statusLbl);

        Panel grid = new Panel(new GridLayout(2, 2, 12, 12));
        grid.setBackground(UIHelper.COL_BG);
        for (int i = 0; i < MEALS.length; i++) grid.add(buildMealCard(i));

        Panel center = new Panel(new BorderLayout());
        center.setBackground(UIHelper.COL_BG);
        center.add(statusBar, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);

        Panel footer = new Panel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(UIHelper.COL_BG);
        FlatButton closeBtn = UIHelper.styledButton("CLOSE STATION", UIHelper.COL_RED, new Dimension(180,36));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);

        add(center, BorderLayout.CENTER);
        add(footer,  BorderLayout.SOUTH);

        generateAllQRCodes();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); }
        });
        UIHelper.centreFrame(this);
    }

    private Panel buildMealCard(int idx) {
        String meal  = MEALS[idx];
        String time  = TIMES[idx];
        Color  color = UIHelper.mealColor(meal);

        Panel card = new Panel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;

        g.gridy = 0; g.insets = new Insets(0,0,0,0);
        Panel stripe = new Panel(); stripe.setBackground(color);
        stripe.setPreferredSize(new Dimension(420,6));
        card.add(stripe, g);

        g.gridy = 1; g.insets = new Insets(8,10,2,10);
        Label ml = new Label(meal, Label.CENTER);
        ml.setFont(new Font("Arial", Font.BOLD, 15));
        ml.setForeground(color);
        card.add(ml, g);

        g.gridy = 2; g.insets = new Insets(0,10,4,10);
        Label tl = new Label(time, Label.CENTER);
        tl.setFont(new Font("Arial", Font.ITALIC, 11));
        tl.setForeground(UIHelper.COL_MUTED);
        card.add(tl, g);

        g.gridy = 3; g.insets = new Insets(4,10,4,10);
        canvases[idx] = new UIHelper.ImageCanvas(160, 160);
        card.add(canvases[idx], g);

        g.gridy = 4; g.insets = new Insets(4,20,12,20);
        FlatButton loginBtn = UIHelper.styledButton("KIOSK LOGIN", color, new Dimension(200,32));
        loginBtn.setFontSize(12);
        final String mealName = meal;
        loginBtn.addActionListener(e ->
            new MealLoginFrame(mealName, adminId, ScanStationFrame.this).setVisible(true));
        card.add(loginBtn, g);

        return card;
    }

    private void generateAllQRCodes() {
        String base = MessCardSystem.ngrokUrl.isEmpty()
            ? "http://" + MessCardSystem.serverIP + ":" + MessCardSystem.serverPort
            : MessCardSystem.ngrokUrl;
        statusLbl.setText("Server: " + base + "  |  Admin ID: " + adminId);
        for (int i = 0; i < MEALS.length; i++) {
            String url = base + "/meal?type=" + MEALS[i] + "&admin=" + adminId;
            BufferedImage img = QRCodeGenerator.generateQRImage(url, 160);
            if (img != null) canvases[i].setImage(img);
        }
    }
}
