# 🍽️ E-Mess Card System 

> A digital mess management system — students scan a QR code with their phone, log in via browser, and their meal is recorded instantly. Built with Java AWT, SQLite, and a built-in web server.

> NOTE : This is a small project that I created as part of my learning journey. 
Here, I mainly focused on developing ideas for features and functionalities theoretically rather than on optimized and perfect implementation. 
I found this idea to be quite unique, and I plan to build a final version using a more robust programming language and a cloud-based database server.
---

## ✅ Requirements

- **Java 8 or higher** — [Download here](https://www.java.com/en/download/)
- **Git** — [Download here](https://git-scm.com/)

## 🚀 How to Use it On Your PC

### macOS / Linux

```bash
git clone https://github.com/abhay7tx/E-MessCard.git
cd E-MessCard
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

### Windows

```cmd
git clone https://github.com/abhay7tx/E-MessCard.git
cd E-MessCard
mkdir out
javac -cp "lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" -d out src\*.java
java -cp "out;lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" MessCardSystem
```

---

## 🔐 Default Credentials

| Role        | Username      | Password    |
|-------------|---------------|-------------|
| Super Admin | `superadmin`  | `super@123` |
| Admin       | set by super admin | set by super admin |
| Student     | roll number   | set by admin |

> ⚠️ Change the Super Admin password after first login.

---

## 📋 First-Time Setup

1. Run the app
2. Log in as **Super Admin** → `superadmin / super@123`
3. Add an **Admin** (username, full name, password)
4. Log in as that Admin
5. Add **Students** (roll number, name, password)
6. Open **Scan Station** → 4 QR codes appear (Breakfast / Lunch / Snacks / Dinner)
7. Students scan QR with phone → log in → meal recorded ✓

---

## 📱 How It Works

```
Admin generates QR code for a meal
            ↓
QR displayed at mess entrance
            ↓
Student scans with phone camera
            ↓
Login form opens in phone browser
            ↓
Student enters Roll Number + Password
            ↓
✅ Meal recorded — student sees full meal history
```

---

## 📡 QR Code & Phone Access

The app runs a **built-in web server on port 8080**. Students' phones open a login page directly after scanning.

### Option 1 — Same Network (Hotspot)

1. Turn on iPhone Personal Hotspot
2. Connect your Mac/PC to the hotspot
3. Run the app — QR codes auto-detect the correct IP
4. Students on the same hotspot can scan and log in

### Option 2 — Any Network (via ngrok)

1. Install ngrok:
   - **Mac:** `brew install ngrok`
   - **Windows:** [Download from ngrok.com](https://ngrok.com/download)
2. Sign up at [ngrok.com](https://ngrok.com) and add your auth token:
   ```bash
   ngrok config add-authtoken YOUR_TOKEN
   ```
3. Run the app in one terminal, then in another:
   ```bash
   ngrok http 8080
   ```
4. Copy the `https://xxx.ngrok-free.app` URL
5. In the app: **Scan Station → paste URL → Apply**
6. QR codes now work over any network, including college Wi-Fi

---

## 👥 User Roles

### 🟣 Super Admin
- Hardcoded credentials (editable in `MessCardSystem.java`)
- Add / remove admins, change any admin's password

### 🔵 Admin
- Created by Super Admin
- Manages their own set of students (fully isolated from other admins)
- Add / remove students, view and reset student passwords
- Generate meal QR codes for their mess section
- View meal logs for their students

### 🟢 Student
- Created by Admin
- Scans QR → logs meal via phone browser
- Views personal meal history, can change their own password

---

## 🗄️ Database Structure

```
admins      → admin_id, username, password, full_name, created_date
students    → roll_number, name, password, admin_id, reg_date
meal_logs   → id, roll_number, admin_id, meal_type, meal_date, meal_time
```

- Each admin's students are fully isolated from other admins
- QR codes carry an admin ID — students can only log meals under their assigned admin
- SQLite database auto-created on first run as `messsystem.db`

---

## 🛠️ Tech Stack

| Technology       | Purpose                              |
|------------------|--------------------------------------|
| Java AWT         | Desktop GUI (no Swing/JavaFX)        |
| SQLite + JDBC    | Local database                       |
| ZXing            | QR code generation                   |
| Java HttpServer  | Built-in web server for phone login  |
| HTML/CSS         | Mobile login page served to phones   |

---

## 📁 Project Structure

```
E-MessCard/
├── src/
│   ├── MessCardSystem.java           ← Entry point, starts web server
│   ├── DatabaseManager.java          ← All SQLite operations
│   ├── MessWebServer.java            ← Built-in HTTP server (phone login)
│   ├── QRCodeGenerator.java          ← ZXing QR code generation
│   ├── MainFrame.java                ← Home screen
│   ├── AdminLoginDialog.java         ← Login dialog (admin + super admin)
│   ├── SuperAdminFrame.java          ← Super admin panel
│   ├── AdminFrame.java               ← Admin panel (students, QR, logs)
│   ├── ScanStationFrame.java         ← Kiosk with 4 meal QR codes
│   ├── MealLoginFrame.java           ← Kiosk login (roll + password)
│   ├── MealSuccessFrame.java         ← Success screen after meal login
│   ├── StudentLoginFrame.java        ← Student login from main screen
│   ├── StudentDashboardFrame.java    ← Student meal history
│   ├── ChangeCredentialsDialog.java  ← Change username/password
│   ├── DatabaseAccessDialog.java     ← DB password protection on startup
│   ├── FlatButton.java               ← Custom button (macOS compatible)
│   └── UIHelper.java                 ← Shared colors, fonts, components
├── lib/
│   ├── sqlite-jdbc.jar               ← SQLite driver
│   ├── zxing-core.jar                ← QR code engine
│   └── zxing-javase.jar              ← QR code image output
├── compile.sh                        ← Mac/Linux compile script
├── run.sh                            ← Mac/Linux run script
├── messsystem.db                     ← SQLite database (auto-created)
└── README.md
```

---

## ⚙️ Changing the Super Admin Password

**At runtime:** Use the **Change My Password** button in the Super Admin panel.

**In source:** Open `src/MessCardSystem.java` and edit:
```java
public static String SUPER_ADMIN_PASS = "super@123";
```

---

*Built as a college project — Java AWT + SQLite + ZXing*
