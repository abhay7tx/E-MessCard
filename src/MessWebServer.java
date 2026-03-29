import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class MessWebServer {

    private final HttpServer server;

    public MessWebServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/meal", this::handleMealForm);
        server.createContext("/login", this::handleLogin);
        server.createContext("/records", this::handleRecords);
        server.createContext("/menu", this::handleMenu);
        server.createContext("/static", this::handleStaticFile);
        server.setExecutor(null);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (network.isLoopback() || !network.isUp()) continue;

                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) return address.getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    private void handleMealForm(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String mealType = defaultMeal(WebSupport.parseParam(query, "type"));
        int adminId = WebSupport.safeAdminId(WebSupport.parseParam(query, "admin"));
        WebSupport.sendHtml(exchange, 200, WebSupport.renderMealForm(mealType, adminId));
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String roll = cleanRoll(WebSupport.parseParam(body, "roll"));
        String pass = valueOrEmpty(WebSupport.parseParam(body, "pass"));
        String mealType = defaultMeal(WebSupport.parseParam(body, "meal"));
        int adminId = WebSupport.safeAdminId(WebSupport.parseParam(body, "admin"));

        String html;
        if (!DatabaseManager.isRegistered(roll, adminId)) {
            html = WebSupport.renderResultPage(
                "Access Denied",
                WebSupport.mealHex(mealType),
                "Access Denied",
                "Roll <strong>" + WebSupport.escapeHtml(roll) + "</strong> is not registered in this mess section. Please contact your mess admin.",
                "",
                mealType,
                adminId
            );
        } else {
            String name = DatabaseManager.validateStudent(roll, pass, adminId);
            html = buildLoginResult(roll, name, mealType, adminId);
        }

        WebSupport.sendHtml(exchange, 200, html);
    }

    private void handleRecords(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String roll = cleanRoll(WebSupport.parseParam(query, "roll"));
        int adminId = WebSupport.safeAdminId(WebSupport.parseParam(query, "admin"));

        if (roll.isEmpty()) {
            WebSupport.sendText(exchange, 400, "text/plain; charset=UTF-8", "Roll number is required.");
            return;
        }

        List<String[]> records = DatabaseManager.getMealRecords(roll, adminId);
        WebSupport.sendHtml(exchange, 200, WebSupport.renderRecordsPage(roll, records, adminId));
    }

    private void handleMenu(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int adminId = WebSupport.safeAdminId(WebSupport.parseParam(query, "admin"));
        String selectedDay = WebSupport.parseParam(query, "day");
        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        if (selectedDay == null || selectedDay.isEmpty()) {
            selectedDay = today;
        }

        List<String[]> menu = DatabaseManager.getDayMenu(adminId, selectedDay);
        WebSupport.sendHtml(exchange, 200, WebSupport.renderMenuPage(adminId, selectedDay, today, menu));
    }

    private void handleStaticFile(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String assetPath = path.replaceFirst("^/static/?", "");

        if (assetPath.isEmpty()) {
            WebSupport.sendText(exchange, 404, "text/plain; charset=UTF-8", "Asset not found.");
            return;
        }

        try {
            byte[] content = TemplateRenderer.readStaticAsset(assetPath);
            WebSupport.sendBytes(exchange, 200, WebSupport.guessContentType(assetPath), content);
        } catch (IOException e) {
            WebSupport.sendText(exchange, 404, "text/plain; charset=UTF-8", "Asset not found.");
        }
    }

    private String buildLoginResult(String roll, String name, String mealType, int adminId) throws IOException {
        if (name == null) {
            return WebSupport.renderResultPage(
                "Wrong Password",
                "#e67e22",
                "Wrong Password",
                "Incorrect password for <strong>" + WebSupport.escapeHtml(roll) + "</strong>. Please try again.",
                WebSupport.actionLink(
                    "/meal?type=" + mealType + "&admin=" + adminId,
                    "Try Again",
                    "button button-primary"
                ),
                mealType,
                adminId
            );
        }

        if (DatabaseManager.hasMealToday(roll, adminId, mealType)) {
            return WebSupport.renderResultPage(
                "Already Recorded",
                "#e67e22",
                "Meal Already Recorded",
                "Hello <strong>" + WebSupport.escapeHtml(name) + "</strong>. Your <strong>" +
                    WebSupport.escapeHtml(mealType) + "</strong> entry is already recorded for today.",
                WebSupport.actionLink(
                    "/records?roll=" + roll + "&admin=" + adminId,
                    "View My Records",
                    "button button-secondary"
                ),
                mealType,
                adminId
            );
        }

        DatabaseManager.logMeal(roll, adminId, mealType);
        String actions =
            WebSupport.actionLink(
                "/records?roll=" + roll + "&admin=" + adminId,
                "View My Records",
                "button button-secondary"
            ) +
            WebSupport.actionLink(
                "/meal?type=" + mealType + "&admin=" + adminId,
                "Record Another Meal",
                "button button-primary"
            );

        return WebSupport.renderResultPage(
            "Meal Recorded",
            WebSupport.mealHex(mealType),
            "Meal Recorded",
            "Hello <strong>" + WebSupport.escapeHtml(name) + "</strong>. Your <strong>" +
                WebSupport.escapeHtml(mealType) + "</strong> entry has been saved successfully.",
            actions,
            mealType,
            adminId
        );
    }

    private String cleanRoll(String roll) {
        return valueOrEmpty(roll).toUpperCase().trim();
    }

    private String defaultMeal(String mealType) {
        String value = valueOrEmpty(mealType).trim();
        return value.isEmpty() ? "MEAL" : value;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
