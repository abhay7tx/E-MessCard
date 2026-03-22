public class MessCardSystem {

    public static MessWebServer webServer;
    public static String        serverIP   = "127.0.0.1";
    public static int           serverPort = 8080;
    public static String        ngrokUrl   = "";

    // Super admin credentials (hardcoded)
    public static final String SUPER_ADMIN_USER = "superadmin";
    public static String SUPER_ADMIN_PASS = "super@123";

    public static void main(String[] args) {
        try { Class.forName("org.sqlite.JDBC"); }
        catch (ClassNotFoundException e) {
            System.err.println("ERROR: sqlite-jdbc.jar missing from lib/");
            System.exit(1);
        }

        DatabaseManager.initialize();

        serverIP = MessWebServer.getLocalIP();
        try {
            webServer = new MessWebServer(serverPort);
            webServer.start();
            System.out.println("Web server: http://" + serverIP + ":" + serverPort);
        } catch (Exception e) {
            System.err.println("Warning: Could not start web server — " + e.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (webServer != null) webServer.stop();
        }));
    }
}
