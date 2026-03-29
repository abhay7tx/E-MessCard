import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * TemplateRenderer.java — Loads HTML templates and static frontend files.
 *
 * This keeps the web pages separate from the Java logic so the project is
 * easier to explain and edit.
 */
public class TemplateRenderer {

    private static final Path WEB_ROOT = Paths.get("web");
    private static final Path TEMPLATE_ROOT = WEB_ROOT.resolve("templates");
    private static final Path STATIC_ROOT = WEB_ROOT.resolve("static");

    public static String render(String templateName, Map<String, String> values) throws IOException {
        Path templatePath = TEMPLATE_ROOT.resolve(templateName).normalize();
        if (!templatePath.startsWith(TEMPLATE_ROOT)) {
            throw new IOException("Invalid template path: " + templateName);
        }

        String html = readUtf8(templatePath);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }

    public static byte[] readStaticAsset(String assetPath) throws IOException {
        Path filePath = STATIC_ROOT.resolve(assetPath).normalize();
        if (!filePath.startsWith(STATIC_ROOT)) {
            throw new IOException("Invalid asset path: " + assetPath);
        }
        return Files.readAllBytes(filePath);
    }

    private static String readUtf8(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
