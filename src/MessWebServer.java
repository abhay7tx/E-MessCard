import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Enumeration;

public class MessWebServer {

    private HttpServer server;
    private int port;

    public MessWebServer(int port) throws IOException {
        this.port = port;
        server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/meal",    new MealFormHandler());
        server.createContext("/login",   new LoginHandler());
        server.createContext("/records", new RecordsHandler());
        server.setExecutor(null);
    }

    public void start() { server.start(); }
    public void stop()  { server.stop(0); }
    public int  getPort() { return port; }

    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address) return addr.getHostAddress();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "127.0.0.1";
    }

    // ── GET /meal?type=BREAKFAST&admin=1 ─────────────────────────────────────
    static class MealFormHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            String query    = ex.getRequestURI().getQuery();
            String mealType = parseParam(query, "type");
            String adminStr = parseParam(query, "admin");
            if (mealType == null) mealType = "MEAL";
            if (adminStr == null) adminStr = "0";
            String today = LocalDate.now().toString();
            String color = mealHex(mealType);

            String html = "<!DOCTYPE html><html><head>" +
                "<meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<title>Mess Login</title><style>" +
                "*{box-sizing:border-box;margin:0;padding:0;}" +
                "body{font-family:Arial,sans-serif;background:#f0f2f5;display:flex;" +
                "align-items:center;justify-content:center;min-height:100vh;}" +
                ".card{background:#fff;border-radius:12px;overflow:hidden;width:340px;" +
                "box-shadow:0 4px 20px rgba(0,0,0,0.15);}" +
                ".header{background:#1b2a4a;padding:20px;text-align:center;}" +
                ".stripe{height:6px;background:" + color + ";}" +
                ".header h2{color:#fff;font-size:20px;margin-top:10px;}" +
                ".header p{color:#bdc3c7;font-size:13px;margin-top:4px;}" +
                ".meal-badge{display:inline-block;background:" + color + ";color:#fff;" +
                "padding:4px 14px;border-radius:20px;font-weight:bold;font-size:14px;margin-top:8px;}" +
                ".form{padding:24px;}.field{margin-bottom:16px;}" +
                "label{display:block;font-size:13px;font-weight:bold;color:#2c3e50;margin-bottom:6px;}" +
                "input{width:100%;padding:10px 12px;border:1px solid #ddd;border-radius:6px;font-size:15px;}" +
                ".btn{width:100%;padding:13px;background:" + color + ";color:#fff;border:none;" +
                "border-radius:6px;font-size:16px;font-weight:bold;cursor:pointer;margin-top:4px;}" +
                ".footer{text-align:center;color:#999;font-size:11px;padding:12px;}" +
                "</style></head><body><div class='card'>" +
                "<div class='stripe'></div><div class='header'>" +
                "<h2>COLLEGE MESS SYSTEM</h2><p>" + today + "</p>" +
                "<div class='meal-badge'>" + mealType + "</div></div>" +
                "<form class='form' method='POST' action='/login'>" +
                "<input type='hidden' name='meal' value='" + mealType + "'>" +
                "<input type='hidden' name='admin' value='" + adminStr + "'>" +
                "<div class='field'><label>Roll Number</label>" +
                "<input type='text' name='roll' placeholder='e.g. CS2201' autocomplete='off' required></div>" +
                "<div class='field'><label>Password</label>" +
                "<input type='password' name='pass' placeholder='Your mess password' required></div>" +
                "<button class='btn' type='submit'>CONFIRM MEAL</button></form>" +
                "<div class='footer'>College Mess Card System</div></div></body></html>";

            sendHtml(ex, 200, html);
        }
    }

    // ── POST /login ──────────────────────────────────────────────────────────
    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (!"POST".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(405,-1); return; }
            String body     = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String roll     = parseParam(body, "roll");
            String pass     = parseParam(body, "pass");
            String meal     = parseParam(body, "meal");
            String adminStr = parseParam(body, "admin");

            if (roll == null) roll = "";
            if (pass == null) pass = "";
            if (meal == null) meal = "MEAL";
            int adminId = 0;
            try { adminId = Integer.parseInt(adminStr); } catch (Exception ignored) {}

            roll = roll.toUpperCase().trim();
            String color = mealHex(meal);
            String html;

            if (!DatabaseManager.isRegistered(roll, adminId)) {
                html = resultPage("#c0392b","ACCESS DENIED", roll,
                    "Roll <b>" + roll + "</b> is not registered in this mess section.<br><br>" +
                    "Please contact your mess admin.", meal, adminId, false, false);
            } else {
                String name = DatabaseManager.validateStudent(roll, pass, adminId);
                if (name == null) {
                    html = resultPage("#e67e22","WRONG PASSWORD", roll,
                        "Incorrect password for <b>" + roll + "</b>. Please try again.",
                        meal, adminId, true, false);
                } else if (DatabaseManager.hasMealToday(roll, adminId, meal)) {
                    html = resultPage("#e67e22","ALREADY RECORDED", roll,
                        "Hello <b>" + name + "</b>!<br><b>" + meal + "</b> already recorded today.",
                        meal, adminId, false, true);
                } else {
                    DatabaseManager.logMeal(roll, adminId, meal);
                    html = resultPage(color,"MEAL RECORDED ✓", roll,
                        "Hello <b>" + name + "</b>!<br><b>" + meal + "</b> recorded. Enjoy your meal!",
                        meal, adminId, false, true);
                }
            }
            sendHtml(ex, 200, html);
        }
    }

    // ── GET /records?roll=XXX&admin=1 ────────────────────────────────────────
    static class RecordsHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            String query    = ex.getRequestURI().getQuery();
            String roll     = parseParam(query, "roll");
            String adminStr = parseParam(query, "admin");
            if (roll == null) { ex.sendResponseHeaders(400,-1); return; }
            int adminId = 0;
            try { adminId = Integer.parseInt(adminStr); } catch (Exception ignored) {}
            roll = roll.toUpperCase().trim();

            java.util.List<String[]> recs = DatabaseManager.getMealRecords(roll, adminId);
            StringBuilder rows = new StringBuilder();
            for (String[] r : recs)
                rows.append("<tr><td>").append(r[0]).append("</td>")
                    .append("<td>").append(r[1]).append("</td>")
                    .append("<td>").append(r[2]).append("</td></tr>");
            if (recs.isEmpty()) rows.append("<tr><td colspan='3'>No records yet.</td></tr>");

            String html = "<!DOCTYPE html><html><head>" +
                "<meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<title>Meal Records</title><style>" +
                "body{font-family:Arial,sans-serif;background:#f0f2f5;padding:20px;}" +
                ".card{background:#fff;border-radius:10px;padding:20px;max-width:400px;" +
                "margin:auto;box-shadow:0 2px 12px rgba(0,0,0,0.12);}" +
                "h2{color:#1b2a4a;margin-bottom:6px;}p{color:#7f8c8d;font-size:13px;margin-bottom:16px;}" +
                "table{width:100%;border-collapse:collapse;font-size:13px;}" +
                "th{background:#1b2a4a;color:#fff;padding:8px;text-align:left;}" +
                "td{padding:7px 8px;border-bottom:1px solid #eee;}" +
                "tr:last-child td{border:none;}</style></head><body>" +
                "<div class='card'><h2>Meal Records</h2><p>Roll: " + roll + "</p>" +
                "<table><tr><th>Meal</th><th>Date</th><th>Time</th></tr>" +
                rows + "</table></div></body></html>";
            sendHtml(ex, 200, html);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String resultPage(String color, String heading, String roll,
                                     String message, String meal, int adminId,
                                     boolean showRetry, boolean showRecords) {
        String retryLink = showRetry
            ? "<a href='/meal?type=" + meal + "&admin=" + adminId + "' style='display:block;" +
              "margin-top:14px;color:#fff;text-decoration:none;background:rgba(0,0,0,0.2);" +
              "padding:10px;border-radius:6px;text-align:center;font-weight:bold;'>TRY AGAIN</a>" : "";
        String recLink = showRecords
            ? "<a href='/records?roll=" + roll + "&admin=" + adminId + "' style='display:block;" +
              "margin-top:10px;color:#fff;text-decoration:none;background:rgba(0,0,0,0.2);" +
              "padding:10px;border-radius:6px;text-align:center;'>View My Records</a>" : "";
        return "<!DOCTYPE html><html><head>" +
            "<meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
            "<title>" + heading + "</title><style>*{box-sizing:border-box;margin:0;padding:0;}" +
            "body{font-family:Arial,sans-serif;background:#f0f2f5;display:flex;" +
            "align-items:center;justify-content:center;min-height:100vh;}" +
            ".card{background:" + color + ";border-radius:12px;width:320px;" +
            "box-shadow:0 4px 20px rgba(0,0,0,0.2);padding:30px;text-align:center;color:#fff;}" +
            "h2{font-size:20px;margin-bottom:16px;}p{font-size:14px;line-height:1.6;opacity:0.92;}" +
            "</style></head><body><div class='card'><h2>" + heading + "</h2>" +
            "<p>" + message + "</p>" + retryLink + recLink + "</div></body></html>";
    }

    static String parseParam(String query, String key) {
        if (query == null) return null;
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                try { return URLDecoder.decode(kv[1], "UTF-8"); }
                catch (Exception e) { return kv[1]; }
            }
        }
        return null;
    }

    static void sendHtml(HttpExchange ex, int code, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    static String mealHex(String meal) {
        switch (meal.toUpperCase()) {
            case "BREAKFAST": return "#f39c12";
            case "LUNCH":     return "#27ae60";
            case "SNACKS":    return "#2980b9";
            case "DINNER":    return "#8e44ad";
            default:          return "#1b2a4a";
        }
    }
}
