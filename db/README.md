# DBMS Notes

This folder is kept separate so you can explain the database part independently in class.

Files:
- `schema_reference.sql`: tables, keys, views, and triggers used by the project.

Main DBMS points:
- `admins` and `students` store users.
- `students` uses a composite primary key: `(roll_number, admin_id)`.
- `meal_types` is a lookup table, so meal names are stored once.
- `meal_logs` stores daily meal entries and prevents duplicate meal logging on the same day.
- `mess_menu` stores the weekly menu for each admin and meal.
- `meal_plan` is a many-to-many junction table between students and meal types.
- `audit_log` is filled automatically by triggers.
- `v_student_info` and `v_meal_summary` are views for simpler reporting.

Good points to tell your DBMS professor:
- Normalization is used to reduce repeated data.
- Primary keys, foreign keys, unique constraints, and defaults are used.
- Views simplify repeated joins.
- Triggers automatically write audit data.
- Prepared statements are used in Java to avoid SQL injection.
