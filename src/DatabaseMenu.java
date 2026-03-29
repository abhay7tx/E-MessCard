import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu-related queries.
 */
public class DatabaseMenu {

    public static boolean setMenu(int adminId, String day, String mealType, String items) {
        int mealId = DatabaseMeal.getMealTypeId(mealType);
        String sql =
            "INSERT INTO mess_menu(admin_id, day_of_week, meal_type_id, items, updated_at)" +
            " VALUES(?,?,?,?,datetime('now'))" +
            " ON CONFLICT(admin_id, day_of_week, meal_type_id)" +
            " DO UPDATE SET items=excluded.items, updated_at=excluded.updated_at";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setString(2, day);
            ps.setInt(3, mealId);
            ps.setString(4, items.trim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getMenu(int adminId, String day, String mealType) {
        int mealId = DatabaseMeal.getMealTypeId(mealType);
        String sql = "SELECT items FROM mess_menu WHERE admin_id=? AND day_of_week=? AND meal_type_id=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setString(2, day);
            ps.setInt(3, mealId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("items");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<String[]> getDayMenu(int adminId, String day) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT mt.meal_name, mm.items" +
            " FROM mess_menu mm" +
            " JOIN meal_types mt ON mm.meal_type_id = mt.meal_type_id" +
            " WHERE mm.admin_id=? AND mm.day_of_week=?" +
            " ORDER BY mt.meal_type_id";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setString(2, day);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{rs.getString(1), rs.getString(2)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean deleteMenu(int adminId, String day, String mealType) {
        int mealId = DatabaseMeal.getMealTypeId(mealType);
        String sql = "DELETE FROM mess_menu WHERE admin_id=? AND day_of_week=? AND meal_type_id=?";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setString(2, day);
            ps.setInt(3, mealId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
