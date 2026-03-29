import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reports and audit queries.
 */
public class DatabaseReport {

    public static List<String[]> getMealCountByType(int adminId) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT mt.meal_name, COUNT(ml.log_id) AS total" +
            " FROM meal_types mt" +
            " LEFT JOIN meal_logs ml ON ml.meal_type_id = mt.meal_type_id AND ml.admin_id=?" +
            " GROUP BY mt.meal_name ORDER BY mt.meal_type_id";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new String[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String[]> getMonthlyBill(int adminId, String yearMonth) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT s.roll_number, s.name," +
            "       SUM(mt.base_cost) AS total_bill," +
            "       COUNT(ml.log_id) AS meal_count" +
            " FROM students s" +
            " LEFT JOIN meal_logs ml ON ml.roll_number=s.roll_number AND ml.admin_id=s.admin_id" +
            "                      AND strftime('%Y-%m', ml.log_date)=?" +
            " LEFT JOIN meal_types mt ON ml.meal_type_id=mt.meal_type_id" +
            " WHERE s.admin_id=? AND s.is_active=1" +
            " GROUP BY s.roll_number, s.name ORDER BY total_bill DESC";
        try (Connection c = DatabaseCore.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, yearMonth);
            ps.setInt(2, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString(1), rs.getString(2),
                    String.format("%.2f", rs.getDouble(3)),
                    String.valueOf(rs.getInt(4))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
