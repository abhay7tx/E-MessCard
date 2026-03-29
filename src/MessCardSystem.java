/**
 * MessCardSystem.java — Entry point for the E-Mess Card System.
 *
 * Starts the database, the web server, and launches the main window.
 */
public class MessCardSystem {

    // Super admin credentials (change the password after first login)
    public static final String SUPER_ADMIN_USER = "superadmin";
    public static       String SUPER_ADMIN_PASS = "super@123";

    // Web server info (used for QR code URLs)
    public static String serverIP   = "127.0.0.1";
    public static int    serverPort = 8080;
    public static String ngrokUrl   = ""; // Optional: paste ngrok URL here

    public static MessWebServer webServer;

    public static void main(String[] args) {
        // 1. Make sure the SQLite driver is available
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: sqlite-jdbc.jar missing from lib/");
            System.exit(1);
        }

        // 2. Create all database tables, views, and triggers
        DatabaseManager.initialize();

        // 3. Start the built-in web server (for QR code phone login)
        serverIP = MessWebServer.getLocalIP();
        try {
            webServer = new MessWebServer(serverPort);
            webServer.start();
            System.out.println("Web server running at http://" + serverIP + ":" + serverPort);
        } catch (Exception e) {
            System.err.println("Warning: Could not start web server — " + e.getMessage());
        }

        // 4. Launch the main AWT window on the UI thread
        java.awt.EventQueue.invokeLater(() -> new Screens.MainScreen().setVisible(true));

        // 5. Stop the web server cleanly when the app exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (webServer != null) webServer.stop();
        }));
    }
}
