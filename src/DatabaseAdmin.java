import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin-related queries.
 */
public class DatabaseAdmin {

    public static boolean addAdmin(String username, String password, String fullName, String messName) {
        String sql = "INSERT INTO admins(username, password, full_name, mess_name) VALUES(?,?,?,?)";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, password);
            ps.setString(3, fullName.trim());
            ps.setString(4, messName.trim().isEmpty() ? "College Mess" : messName.trim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static String[] validateAdmin(String username, String password) {
        String sql = "SELECT admin_id, full_name, mess_name FROM admins WHERE username=? AND password=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt(1)), rs.getString(2), rs.getString(3)
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String[]> getAllAdmins() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT admin_id, username, mess_name, created_date FROM admins ORDER BY admin_id";
        try (Connection c = DatabaseCore.connect(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt(1)), rs.getString(2),
                    rs.getString(3), rs.getString(4)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean removeAdmin(int adminId) {
        try (Connection c = DatabaseCore.connect()) {
            c.setAutoCommit(false);
            try {
                PreparedStatement ps = c.prepareStatement("DELETE FROM admins WHERE admin_id=?");
                ps.setInt(1, adminId);
                int rows = ps.executeUpdate();
                c.commit();
                return rows > 0;
            } catch (SQLException e) {
                c.rollback();
                e.printStackTrace();
                return false;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeAdminPassword(int adminId, String newPass) {
        String sql = "UPDATE admins SET password=? WHERE admin_id=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
