import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseManager {

    private static String DB_URL = "jdbc:sqlite:messsystem.db";

    public static void setDBPath(String path) {
        DB_URL = "jdbc:sqlite:" + path;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initialize() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {

            // Admins table
            s.execute(
                "CREATE TABLE IF NOT EXISTS admins (" +
                "  admin_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username   TEXT UNIQUE NOT NULL," +
                "  password   TEXT NOT NULL," +
                "  full_name  TEXT NOT NULL," +
                "  created_date TEXT DEFAULT CURRENT_DATE" +
                ")"
            );

            // Students scoped to admin
            s.execute(
                "CREATE TABLE IF NOT EXISTS students (" +
                "  roll_number TEXT NOT NULL," +
                "  name        TEXT NOT NULL," +
                "  password    TEXT NOT NULL," +
                "  admin_id    INTEGER NOT NULL," +
                "  reg_date    TEXT DEFAULT CURRENT_DATE," +
                "  PRIMARY KEY (roll_number, admin_id)," +
                "  FOREIGN KEY (admin_id) REFERENCES admins(admin_id)" +
                ")"
            );

            // Meal logs scoped to admin
            s.execute(
                "CREATE TABLE IF NOT EXISTS meal_logs (" +
                "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  roll_number TEXT NOT NULL," +
                "  admin_id    INTEGER NOT NULL," +
                "  meal_type   TEXT NOT NULL," +
                "  meal_date   TEXT NOT NULL," +
                "  meal_time   TEXT NOT NULL," +
                "  FOREIGN KEY (admin_id) REFERENCES admins(admin_id)" +
                ")"
            );

            System.out.println("Database initialised.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Admin operations ──────────────────────────────────────────────────────

    /** Add a new admin. Returns true on success. */
    public static boolean addAdmin(String username, String password, String fullName) {
        String sql = "INSERT INTO admins(username,password,full_name) VALUES(?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, password);
            ps.setString(3, fullName.trim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    /** Returns [admin_id, full_name] if credentials match, null otherwise. */
    public static String[] validateAdmin(String username, String password) {
        String sql = "SELECT admin_id, full_name FROM admins WHERE username=? AND password=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new String[]{String.valueOf(rs.getInt("admin_id")), rs.getString("full_name")};
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Returns all admins: [admin_id, username, full_name, created_date] */
    public static List<String[]> getAllAdmins() {
        List<String[]> list = new ArrayList<>();
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(
                "SELECT admin_id,username,full_name,created_date FROM admins ORDER BY admin_id");
            while (rs.next())
                list.add(new String[]{
                    String.valueOf(rs.getInt(1)), rs.getString(2),
                    rs.getString(3), rs.getString(4)
                });
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean removeAdmin(int adminId) {
        try (Connection c = getConnection()) {
            // Remove their meal logs and students first
            PreparedStatement ps1 = c.prepareStatement("DELETE FROM meal_logs WHERE admin_id=?");
            ps1.setInt(1, adminId); ps1.executeUpdate();
            PreparedStatement ps2 = c.prepareStatement("DELETE FROM students WHERE admin_id=?");
            ps2.setInt(1, adminId); ps2.executeUpdate();
            PreparedStatement ps3 = c.prepareStatement("DELETE FROM admins WHERE admin_id=?");
            ps3.setInt(1, adminId);
            return ps3.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static boolean changeAdminPassword(int adminId, String newPassword) {
        String sql = "UPDATE admins SET password=? WHERE admin_id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── Student operations (scoped to admin) ──────────────────────────────────

    public static boolean addStudent(String roll, String name, String password, int adminId) {
        String sql = "INSERT INTO students(roll_number,name,password,admin_id) VALUES(?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setString(2, name.trim());
            ps.setString(3, password);
            ps.setInt(4, adminId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public static boolean removeStudent(String roll, int adminId) {
        String sql = "DELETE FROM students WHERE roll_number=? AND admin_id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Change a student's password (admin scoped) */
    public static boolean changeStudentPassword(String roll, int adminId, String newPassword) {
        String sql = "UPDATE students SET password=? WHERE roll_number=? AND admin_id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, roll.toUpperCase().trim());
            ps.setInt(3, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Returns [roll, name, password, reg_date] for all students of an admin */
    public static List<String[]> getStudentsByAdmin(int adminId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT roll_number,name,password,reg_date FROM students " +
                     "WHERE admin_id=? ORDER BY roll_number";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new String[]{rs.getString(1), rs.getString(2),
                                      rs.getString(3), rs.getString(4)});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean isRegistered(String roll, int adminId) {
        String sql = "SELECT 1 FROM students WHERE roll_number=? AND admin_id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Returns student name if credentials match under given admin, null otherwise */
    public static String validateStudent(String roll, String password, int adminId) {
        String sql = "SELECT name FROM students WHERE roll_number=? AND password=? AND admin_id=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setString(2, password);
            ps.setInt(3, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Finds which admin a roll number belongs to.
     * Returns admin_id or -1 if not found anywhere.
     */
    public static int findAdminForRoll(String roll) {
        String sql = "SELECT admin_id FROM students WHERE roll_number=? LIMIT 1";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // ── Meal log operations ───────────────────────────────────────────────────

    public static boolean hasMealToday(String roll, int adminId, String mealType) {
        String today = LocalDate.now().toString();
        String sql   = "SELECT 1 FROM meal_logs WHERE roll_number=? AND admin_id=? " +
                       "AND meal_type=? AND meal_date=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            ps.setString(3, mealType.toUpperCase());
            ps.setString(4, today);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static boolean logMeal(String roll, int adminId, String mealType) {
        if (hasMealToday(roll, adminId, mealType)) return false;
        String today = LocalDate.now().toString();
        String now   = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String sql   = "INSERT INTO meal_logs(roll_number,admin_id,meal_type,meal_date,meal_time) VALUES(?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            ps.setString(3, mealType.toUpperCase());
            ps.setString(4, today);
            ps.setString(5, now);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Returns [meal_type, meal_date, meal_time] for a student under an admin */
    public static List<String[]> getMealRecords(String roll, int adminId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT meal_type,meal_date,meal_time FROM meal_logs " +
                     "WHERE roll_number=? AND admin_id=? ORDER BY meal_date DESC, meal_time DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll.toUpperCase().trim());
            ps.setInt(2, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Returns [roll, name, meal_type, date, time] for all logs under an admin */
    public static List<String[]> getAllMealLogs(int adminId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT ml.roll_number,s.name,ml.meal_type,ml.meal_date,ml.meal_time " +
                     "FROM meal_logs ml JOIN students s " +
                     "ON ml.roll_number=s.roll_number AND ml.admin_id=s.admin_id " +
                     "WHERE ml.admin_id=? ORDER BY ml.meal_date DESC, ml.meal_time DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new String[]{rs.getString(1), rs.getString(2),
                                      rs.getString(3), rs.getString(4), rs.getString(5)});
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Extra methods for credential changes

    /** Validate admin by ID + password. Returns [username, full_name] or null. */
    public static String[] validateAdminById(int adminId, String password) {
        String sql = "SELECT username,full_name FROM admins WHERE admin_id=? AND password=?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new String[]{rs.getString(1), rs.getString(2)};
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Update admin username and/or password. Pass null for newUsername to keep existing. */
    public static boolean updateAdminCredentials(int adminId, String newUsername, String newPassword) {
        try (Connection c = getConnection()) {
            if (newUsername != null && !newUsername.isEmpty()) {
                PreparedStatement ps = c.prepareStatement(
                    "UPDATE admins SET username=?,password=? WHERE admin_id=?");
                ps.setString(1, newUsername); ps.setString(2, newPassword); ps.setInt(3, adminId);
                return ps.executeUpdate() > 0;
            } else {
                PreparedStatement ps = c.prepareStatement(
                    "UPDATE admins SET password=? WHERE admin_id=?");
                ps.setString(1, newPassword); ps.setInt(2, adminId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

}
