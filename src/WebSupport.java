import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Small helper methods for the web UI.
 */
public class WebSupport {

    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    };

    private static final String[] MEAL_TYPES = {"BREAKFAST", "LUNCH", "SNACKS", "DINNER"};
    private static final String[] MEAL_COLORS = {"#f39c12", "#27ae60", "#2980b9", "#8e44ad"};
    private static final String[] MEAL_TIMES = {
        "7:00 - 9:00 AM",
        "12:00 - 2:00 PM",
        "4:00 - 5:30 PM",
        "7:00 - 9:00 PM"
    };

    public static String renderMealForm(String mealType, int adminId) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("pageTitle", "Mess Login");
        values.put("mealColor", mealHex(mealType));
        values.put("today", escapeHtml(LocalDate.now().toString()));
        values.put("mealType", escapeHtml(mealType));
        values.put("adminId", String.valueOf(adminId));
        return TemplateRenderer.render("meal.html", values);
    }

    public static String renderRecordsPage(String roll, List<String[]> records, int adminId) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("pageTitle", "Meal Records");
        values.put("roll", escapeHtml(roll));
        values.put("rows", buildRecordRows(records));
        values.put("backLink", "/meal?type=BREAKFAST&admin=" + adminId);
        return TemplateRenderer.render("records.html", values);
    }

    public static String renderMenuPage(int adminId, String selectedDay, String today, List<String[]> menu)
            throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("pageTitle", "Mess Menu");
        values.put("selectedDay", escapeHtml(selectedDay));
        values.put("todayDate", escapeHtml(LocalDate.now().toString()));
        values.put("dayButtons", buildDayButtons(adminId, selectedDay, today));
        values.put("menuCards", buildMenuCards(menu, selectedDay));
        values.put("backLink", "/meal?type=BREAKFAST&admin=" + adminId);
        return TemplateRenderer.render("menu.html", values);
    }

    public static String renderResultPage(
            String pageTitle,
            String accentColor,
            String heading,
            String message,
            String actionButtons,
            String mealType,
            int adminId
    ) throws IOException {
        Map<String, String> values = new HashMap<>();
        values.put("pageTitle", escapeHtml(pageTitle));
        values.put("accentColor", accentColor);
        values.put("heading", escapeHtml(heading));
        values.put("message", message);
        values.put("actionButtons", actionButtons);
        values.put(
            "menuLink",
            actionLink("/menu?admin=" + adminId + "&day=", "View Today's Menu", "button button-muted")
        );
        values.put(
            "loginLink",
            actionLink("/meal?type=" + mealType + "&admin=" + adminId, "Back to Login", "button button-link")
        );
        return TemplateRenderer.render("result.html", values);
    }

    public static String actionLink(String href, String text, String className) {
        return "<a class='" + className + "' href='" + href + "'>" + escapeHtml(text) + "</a>";
    }

    public static String parseParam(String query, String key) {
        if (query == null) return null;

        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                try {
                    return URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name());
                } catch (Exception e) {
                    return kv[1];
                }
            }
        }
        return null;
    }

    public static int safeAdminId(String adminText) {
        try {
            return Integer.parseInt(adminText);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static void sendHtml(HttpExchange exchange, int code, String html) throws IOException {
        sendBytes(exchange, code, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
    }

    public static void sendText(HttpExchange exchange, int code, String contentType, String text) throws IOException {
        sendBytes(exchange, code, contentType, text.getBytes(StandardCharsets.UTF_8));
    }

    public static void sendBytes(HttpExchange exchange, int code, String contentType, byte[] bytes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    public static String mealHex(String meal) {
        switch (meal.toUpperCase()) {
            case "BREAKFAST":
                return "#f39c12";
            case "LUNCH":
                return "#27ae60";
            case "SNACKS":
                return "#2980b9";
            case "DINNER":
                return "#8e44ad";
            default:
                return "#1b2a4a";
        }
    }

    public static String guessContentType(String assetPath) {
        if (assetPath.endsWith(".css")) return "text/css; charset=UTF-8";
        if (assetPath.endsWith(".js")) return "application/javascript; charset=UTF-8";
        return "application/octet-stream";
    }

    public static String escapeHtml(String value) {
        if (value == null) return "";
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    private static String buildRecordRows(List<String[]> records) {
        if (records.isEmpty()) {
            return "<tr><td colspan='3' class='empty-row'>No meal records yet.</td></tr>";
        }

        StringBuilder rows = new StringBuilder();
        for (String[] record : records) {
            rows.append("<tr>")
                .append("<td>").append(escapeHtml(record[0])).append("</td>")
                .append("<td>").append(escapeHtml(record[1])).append("</td>")
                .append("<td>").append(escapeHtml(record[2])).append("</td>")
                .append("</tr>");
        }
        return rows.toString();
    }

    private static String buildDayButtons(int adminId, String selectedDay, String today) {
        StringBuilder html = new StringBuilder();
        for (String day : DAYS) {
            String className = "day-link";
            if (day.equals(selectedDay)) className += " day-link-active";
            if (day.equals(today)) className += " day-link-today";

            html.append("<a class='").append(className)
                .append("' href='/menu?admin=").append(adminId)
                .append("&day=").append(day).append("'>")
                .append(escapeHtml(day.substring(0, 3)))
                .append("</a>");
        }
        return html.toString();
    }

    private static String buildMenuCards(List<String[]> menu, String day) {
        StringBuilder html = new StringBuilder();

        for (int i = 0; i < MEAL_TYPES.length; i++) {
            String items = findMenuItems(menu, MEAL_TYPES[i]);
            if (items.isEmpty()) continue;

            html.append("<section class='menu-card'>")
                .append("<div class='menu-card-head' style='background:")
                .append(MEAL_COLORS[i]).append(";'>")
                .append("<h2>").append(escapeHtml(MEAL_TYPES[i])).append("</h2>")
                .append("<p>").append(escapeHtml(MEAL_TIMES[i])).append("</p>")
                .append("</div><ul class='menu-items'>");

            for (String item : items.split("[,\n]+")) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    html.append("<li>").append(escapeHtml(trimmed)).append("</li>");
                }
            }

            html.append("</ul></section>");
        }

        if (html.length() == 0) {
            html.append("<div class='empty-state'>")
                .append("<p>No menu is set for <strong>").append(escapeHtml(day)).append("</strong>.</p>")
                .append("<p>Please ask your mess admin to update it.</p>")
                .append("</div>");
        }

        return html.toString();
    }

    private static String findMenuItems(List<String[]> menu, String mealType) {
        for (String[] row : menu) {
            if (row[0].equals(mealType)) return row[1];
        }
        return "";
    }
}
