-- E-Mess Card System
-- Reference SQL for DBMS explanation
-- This file mirrors the schema used inside DatabaseManager.initialize().

CREATE TABLE IF NOT EXISTS admins (
  admin_id     INTEGER PRIMARY KEY AUTOINCREMENT,
  username     TEXT    NOT NULL UNIQUE,
  password     TEXT    NOT NULL,
  full_name    TEXT    NOT NULL,
  mess_name    TEXT    NOT NULL DEFAULT 'College Mess',
  created_date TEXT    NOT NULL DEFAULT (date('now'))
);

CREATE TABLE IF NOT EXISTS students (
  roll_number  TEXT    NOT NULL,
  admin_id     INTEGER NOT NULL,
  name         TEXT    NOT NULL,
  password     TEXT    NOT NULL,
  reg_date     TEXT    NOT NULL DEFAULT (date('now')),
  is_active    INTEGER NOT NULL DEFAULT 1,
  PRIMARY KEY (roll_number, admin_id),
  FOREIGN KEY (admin_id) REFERENCES admins(admin_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS meal_types (
  meal_type_id INTEGER PRIMARY KEY,
  meal_name    TEXT    NOT NULL UNIQUE,
  start_time   TEXT    NOT NULL,
  end_time     TEXT    NOT NULL,
  base_cost    REAL    NOT NULL DEFAULT 0.0
);

INSERT OR IGNORE INTO meal_types VALUES
  (1, 'BREAKFAST', '07:00', '09:00', 50.0),
  (2, 'LUNCH',     '12:00', '14:00', 80.0),
  (3, 'SNACKS',    '16:00', '17:30', 30.0),
  (4, 'DINNER',    '19:00', '21:00', 80.0);

CREATE TABLE IF NOT EXISTS meal_logs (
  log_id       INTEGER PRIMARY KEY AUTOINCREMENT,
  roll_number  TEXT    NOT NULL,
  admin_id     INTEGER NOT NULL,
  meal_type_id INTEGER NOT NULL,
  log_date     TEXT    NOT NULL DEFAULT (date('now')),
  log_time     TEXT    NOT NULL DEFAULT (time('now')),
  FOREIGN KEY (roll_number, admin_id) REFERENCES students(roll_number, admin_id) ON DELETE CASCADE,
  FOREIGN KEY (meal_type_id)          REFERENCES meal_types(meal_type_id),
  UNIQUE (roll_number, admin_id, meal_type_id, log_date)
);

CREATE TABLE IF NOT EXISTS mess_menu (
  menu_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  admin_id     INTEGER NOT NULL,
  day_of_week  TEXT    NOT NULL,
  meal_type_id INTEGER NOT NULL,
  items        TEXT    NOT NULL,
  updated_at   TEXT    NOT NULL DEFAULT (datetime('now')),
  FOREIGN KEY (admin_id)     REFERENCES admins(admin_id)     ON DELETE CASCADE,
  FOREIGN KEY (meal_type_id) REFERENCES meal_types(meal_type_id),
  UNIQUE (admin_id, day_of_week, meal_type_id)
);

CREATE TABLE IF NOT EXISTS meal_plan (
  plan_id      INTEGER PRIMARY KEY AUTOINCREMENT,
  roll_number  TEXT    NOT NULL,
  admin_id     INTEGER NOT NULL,
  meal_type_id INTEGER NOT NULL,
  subscribed   INTEGER NOT NULL DEFAULT 1,
  FOREIGN KEY (roll_number, admin_id) REFERENCES students(roll_number, admin_id) ON DELETE CASCADE,
  FOREIGN KEY (meal_type_id)          REFERENCES meal_types(meal_type_id),
  UNIQUE (roll_number, admin_id, meal_type_id)
);

CREATE TABLE IF NOT EXISTS audit_log (
  audit_id   INTEGER PRIMARY KEY AUTOINCREMENT,
  action     TEXT NOT NULL,
  table_name TEXT NOT NULL,
  record_ref TEXT NOT NULL,
  admin_id   INTEGER,
  logged_at  TEXT NOT NULL DEFAULT (datetime('now'))
);

DROP VIEW IF EXISTS v_student_info;
CREATE VIEW v_student_info AS
SELECT s.roll_number, s.name, s.reg_date, s.is_active,
       a.admin_id, a.username, a.mess_name
FROM   students s
JOIN   admins   a ON s.admin_id = a.admin_id;

DROP VIEW IF EXISTS v_meal_summary;
CREATE VIEW v_meal_summary AS
SELECT ml.log_id, ml.roll_number, ml.admin_id,
       mt.meal_name, mt.base_cost,
       ml.log_date, ml.log_time,
       s.name AS student_name
FROM   meal_logs  ml
JOIN   meal_types mt ON ml.meal_type_id = mt.meal_type_id
JOIN   students   s  ON ml.roll_number = s.roll_number
                    AND ml.admin_id    = s.admin_id;

DROP TRIGGER IF EXISTS trg_student_added;
CREATE TRIGGER trg_student_added
AFTER INSERT ON students
BEGIN
  INSERT INTO audit_log(action, table_name, record_ref, admin_id)
  VALUES('STUDENT_ADDED', 'students', NEW.roll_number, NEW.admin_id);
END;

DROP TRIGGER IF EXISTS trg_student_removed;
CREATE TRIGGER trg_student_removed
AFTER DELETE ON students
BEGIN
  INSERT INTO audit_log(action, table_name, record_ref, admin_id)
  VALUES('STUDENT_REMOVED', 'students', OLD.roll_number, OLD.admin_id);
END;

DROP TRIGGER IF EXISTS trg_meal_logged;
CREATE TRIGGER trg_meal_logged
AFTER INSERT ON meal_logs
BEGIN
  INSERT INTO audit_log(action, table_name, record_ref, admin_id)
  VALUES(
    'MEAL_LOGGED',
    'meal_logs',
    NEW.roll_number || ':' || NEW.meal_type_id || ':' || NEW.log_date,
    NEW.admin_id
  );
END;
