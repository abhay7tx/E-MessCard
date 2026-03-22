# College Mess Card System
### Java AWT + SQLite | QR Code based meal logging

---

## Features
- QR code generation for 4 daily meals: Breakfast, Lunch, Snacks, Dinner
- Student registration with Roll Number + Password
- Meal access validation (checks if student is registered)
- Duplicate meal prevention (one meal type per student per day)
- Student dashboard showing full meal history
- Admin panel: add/remove students, generate QRs, view all logs
- SQLite database (no separate server needed)

---

## Project Structure

```
MessCardSystem/
├── src/
│   ├── MessCardSystem.java        ← Main entry point
│   ├── DatabaseManager.java       ← All SQLite operations
│   ├── QRCodeGenerator.java       ← ZXing QR generation
│   ├── UIHelper.java              ← Shared colours, fonts, components
│   ├── MainFrame.java             ← Landing screen
│   ├── AdminLoginDialog.java      ← Admin password dialog
│   ├── AdminFrame.java            ← Admin panel (3 tabs)
│   ├── ScanStationFrame.java      ← Kiosk with 4 QR codes
│   ├── StudentLoginFrame.java     ← Student roll + password login
│   └── StudentDashboardFrame.java ← Welcome + meal records
├── lib/                           ← JARs go here (see setup)
├── out/                           ← Compiled .class files
├── setup.sh                       ← Downloads dependencies
├── compile.sh                     ← Compiles the project
├── run.sh                         ← Runs the application
└── README.md
```

---

## Setup Instructions

### Step 1 – Download dependencies (3 JARs needed)

Download these JARs and put them in the `lib/` folder:

| JAR | Download URL |
|-----|-------------|
| `sqlite-jdbc.jar` | https://github.com/xerial/sqlite-jdbc/releases → latest |
| `zxing-core.jar` | https://mvnrepository.com/artifact/com.google.zxing/core/3.5.3 |
| `zxing-javase.jar` | https://mvnrepository.com/artifact/com.google.zxing/javase/3.5.3 |

**OR** if you have internet, just run:
```bash
chmod +x setup.sh
./setup.sh
```

### Step 2 – Compile

```bash
chmod +x compile.sh
./compile.sh
```

### Step 3 – Run

```bash
chmod +x run.sh
./run.sh
```

On **Windows**, use this instead:
```bat
javac -cp "lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" -d out src\*.java
java -cp "out;lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" MessCardSystem
```

---

## How To Use

### Admin Workflow
1. Click **ADMIN PANEL** on the home screen
2. Enter password: `admin123`
3. **Students tab** → Add students (roll number, name, password)
4. **Generate QR tab** → Select meal type → click Generate
5. Display the QR at the mess entrance (or print/project it)
6. **Meal Logs tab** → View all recorded meals

### Student Workflow
1. Student scans the QR code with phone → sees meal info
2. At the kiosk, click **LOGIN FOR [MEAL]**
3. Enter roll number + password
4. System validates and records the meal
5. Dashboard shows "Enjoy your meal!" + full meal history

### Error Cases Handled
| Situation | What happens |
|-----------|-------------|
| Roll not in DB | Red "Not Registered" dialog shown |
| Wrong password | Error message, try again |
| Already had this meal today | Warning, can still view records |
| Missing fields | Inline validation message |

---

## Database

SQLite file `messsystem.db` is auto-created on first run.

**Tables:**
```sql
students   (roll_number PK, name, password, reg_date)
meal_logs  (id, roll_number FK, meal_type, meal_date, meal_time)
```

---

## Default Admin Password
```
admin123
```
To change it, edit the `ADMIN_PASSWORD` constant in `AdminLoginDialog.java`.

---

## Requirements
- Java 8 or later
- sqlite-jdbc JAR
- ZXing core + javase JARs
