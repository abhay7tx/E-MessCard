import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Meal-related queries.
 */
public class DatabaseMeal {

    public static int getMealTypeId(String mealName) {
        String sql = "SELECT meal_type_id FROM meal_types WHERE meal_name=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mealName.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static boolean hasMealToday(String roll, int adminId, String mealType) {
        int mealId = getMealTypeId(mealType);
        String today = LocalDate.now().toString();
        String sql = "SELECT 1 FROM meal_logs WHERE roll_number=? AND admin_id=? AND meal_type_id=? AND log_date=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            ps.setInt(3, mealId);
            ps.setString(4, today);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean logMeal(String roll, int adminId, String mealType) {
        if (hasMealToday(roll, adminId, mealType)) return false;

        int mealId = getMealTypeId(mealType);
        String today = LocalDate.now().toString();
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String sql = "INSERT INTO meal_logs(roll_number, admin_id, meal_type_id, log_date, log_time) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            ps.setInt(3, mealId);
            ps.setString(4, today);
            ps.setString(5, now);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String[]> getMealRecords(String roll, int adminId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT meal_name, log_date, log_time FROM v_meal_summary " +
                     "WHERE roll_number=? AND admin_id=? ORDER BY log_date DESC, log_time DESC";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String[]> getAllMealLogs(int adminId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT roll_number, student_name, meal_name, log_date, log_time " +
                     "FROM v_meal_summary WHERE admin_id=? ORDER BY log_date DESC, log_time DESC";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
