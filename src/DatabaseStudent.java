import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Student-related queries.
 */
public class DatabaseStudent {

    public static boolean addStudent(String roll, String name, String password, int adminId) {
        String sql = "INSERT INTO students(roll_number, admin_id, name, password) VALUES(?,?,?,?)";
        String cleanRoll = roll.toUpperCase().trim();
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cleanRoll);
            ps.setInt(2, adminId);
            ps.setString(3, name.trim());
            ps.setString(4, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean removeStudent(String roll, int adminId) {
        String sql = "DELETE FROM students WHERE roll_number=? AND admin_id=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeStudentPassword(String roll, int adminId, String newPass) {
        String sql = "UPDATE students SET password=? WHERE roll_number=? AND admin_id=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setString(2, roll.toUpperCase().trim());
            ps.setInt(3, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String validateStudent(String roll, String password, int adminId) {
        String sql = "SELECT name FROM students WHERE roll_number=? AND password=? AND admin_id=? AND is_active=1";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setString(2, password);
            ps.setInt(3, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isRegistered(String roll, int adminId) {
        String sql = "SELECT 1 FROM students WHERE roll_number=? AND admin_id=? AND is_active=1";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int findAdminForRoll(String roll) {
        String sql = "SELECT admin_id FROM students WHERE roll_number=? AND is_active=1 LIMIT 1";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<String[]> getStudentsByAdmin(int adminId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT roll_number, name, reg_date FROM students WHERE admin_id=? AND is_active=1 ORDER BY roll_number";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
