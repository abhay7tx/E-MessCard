import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the tables, views, and triggers once at startup.
 */
public class DatabaseSchema {

    public static void initialize() {
        try (Connection c = DatabaseCore.connect(); Statement s = c.createStatement()) {
            s.execute("DROP TRIGGER IF EXISTS trg_student_added");
            s.execute("DROP TRIGGER IF EXISTS trg_student_removed");
            s.execute("DROP TRIGGER IF EXISTS trg_meal_logged");
            s.execute("DROP VIEW IF EXISTS v_student_info");
            s.execute("DROP TABLE IF EXISTS meal_plan");
            s.execute("DROP TABLE IF EXISTS audit_log");

            s.execute(
                "CREATE TABLE IF NOT EXISTS admins (" +
                "  admin_id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username     TEXT    NOT NULL UNIQUE," +
                "  password     TEXT    NOT NULL," +
                "  full_name    TEXT    NOT NULL," +
                "  mess_name    TEXT    NOT NULL DEFAULT 'College Mess'," +
                "  created_date TEXT    NOT NULL DEFAULT (date('now'))" +
                ")"
            );

            s.execute(
                "CREATE TABLE IF NOT EXISTS students (" +
                "  roll_number  TEXT    NOT NULL," +
                "  admin_id     INTEGER NOT NULL," +
                "  name         TEXT    NOT NULL," +
                "  password     TEXT    NOT NULL," +
                "  reg_date     TEXT    NOT NULL DEFAULT (date('now'))," +
                "  is_active    INTEGER NOT NULL DEFAULT 1," +
                "  PRIMARY KEY (roll_number, admin_id)," +
                "  FOREIGN KEY (admin_id) REFERENCES admins(admin_id) ON DELETE CASCADE" +
                ")"
            );

            s.execute(
                "CREATE TABLE IF NOT EXISTS meal_types (" +
                "  meal_type_id INTEGER PRIMARY KEY," +
                "  meal_name    TEXT    NOT NULL UNIQUE," +
                "  start_time   TEXT    NOT NULL," +
                "  end_time     TEXT    NOT NULL," +
                "  base_cost    REAL    NOT NULL DEFAULT 0.0" +
                ")"
            );

            s.execute(
                "INSERT OR IGNORE INTO meal_types VALUES" +
                " (1, 'BREAKFAST', '07:00', '09:00', 50.0)," +
                " (2, 'LUNCH',     '12:00', '14:00', 80.0)," +
                " (3, 'SNACKS',    '16:00', '17:30', 30.0)," +
                " (4, 'DINNER',    '19:00', '21:00', 80.0)"
            );

            s.execute(
                "CREATE TABLE IF NOT EXISTS meal_logs (" +
                "  log_id       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  roll_number  TEXT    NOT NULL," +
                "  admin_id     INTEGER NOT NULL," +
                "  meal_type_id INTEGER NOT NULL," +
                "  log_date     TEXT    NOT NULL DEFAULT (date('now'))," +
                "  log_time     TEXT    NOT NULL DEFAULT (time('now'))," +
                "  FOREIGN KEY (roll_number, admin_id) REFERENCES students(roll_number, admin_id) ON DELETE CASCADE," +
                "  FOREIGN KEY (meal_type_id)          REFERENCES meal_types(meal_type_id)," +
                "  UNIQUE (roll_number, admin_id, meal_type_id, log_date)" +
                ")"
            );

            s.execute(
                "CREATE TABLE IF NOT EXISTS mess_menu (" +
                "  menu_id      INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  admin_id     INTEGER NOT NULL," +
                "  day_of_week  TEXT    NOT NULL," +
                "  meal_type_id INTEGER NOT NULL," +
                "  items        TEXT    NOT NULL," +
                "  updated_at   TEXT    NOT NULL DEFAULT (datetime('now'))," +
                "  FOREIGN KEY (admin_id)     REFERENCES admins(admin_id)     ON DELETE CASCADE," +
                "  FOREIGN KEY (meal_type_id) REFERENCES meal_types(meal_type_id)," +
                "  UNIQUE (admin_id, day_of_week, meal_type_id)" +
                ")"
            );

            s.execute("DROP VIEW IF EXISTS v_meal_summary");
            s.execute(
                "CREATE VIEW v_meal_summary AS" +
                "  SELECT ml.log_id, ml.roll_number, ml.admin_id," +
                "         mt.meal_name, mt.base_cost," +
                "         ml.log_date, ml.log_time," +
                "         s.name AS student_name" +
                "  FROM meal_logs ml" +
                "  JOIN meal_types mt ON ml.meal_type_id = mt.meal_type_id" +
                "  JOIN students s ON ml.roll_number = s.roll_number AND ml.admin_id = s.admin_id"
            );

            System.out.println("Database ready.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
