import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class QRCodeGenerator {

    /**
     * Builds a URL pointing to the built-in web server.
     * When scanned, the phone opens a login page in its browser.
     */
    public static String buildQRUrl(String mealType, String ip, int port) {
        return "http://" + ip + ":" + port + "/meal?type=" + mealType.toUpperCase();
    }

    public static BufferedImage generateQRImage(String data, int size) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    img.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            return img;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
