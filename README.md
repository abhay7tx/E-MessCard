# 🍽️ E-Mess Card System

> A digital mess management system built with **Java AWT** and **SQLite** — featuring QR code based meal access, multi-admin support, and a built-in web server for phone-based student login.

---

## 📱 How It Works

```
Admin generates QR code for a meal (Breakfast / Lunch / Snacks / Dinner)
                    ↓
QR is displayed on a screen at the mess entrance
                    ↓
Student scans QR with phone camera
                    ↓
Login form opens in phone browser
                    ↓
Student enters Roll Number + Password
                    ↓
✅ Meal is recorded in the database
Student sees their full meal history
```

---

## 🖥️ Screenshots Overview

| Screen | Description |
|--------|-------------|
| Home Screen | 3 login cards — Super Admin, Admin, Student |
| Super Admin Panel | Add/remove admins, change passwords |
| Admin Panel | Manage students, generate QR codes, view meal logs |
| Scan Station | Live QR codes for all 4 meals |
| Student Dashboard | Meal history with counts per meal type |
| Phone Login | Mobile-friendly web form after scanning QR |

---

## 👥 User Roles

### 🟣 Super Admin
- Hardcoded credentials (change in `MessCardSystem.java`)
- Default: `superadmin / super@123`
- Can add and remove admins
- Can change any admin's password
- Can change their own password

### 🔵 Admin
- Created by Super Admin
- Manages their own set of students only
- Can add/remove students
- Can view student passwords and change them (e.g. if student forgets)
- Generates meal QR codes scoped to their mess section
- Views meal logs for their students only
- Can change their own username and password

### 🟢 Student
- Created by Admin
- Scans QR code → logs meal via phone browser
- Can view their own meal records
- Can change their own password

---

## 🗄️ Database Structure

```
admins      → admin_id, username, password, full_name, created_date
students    → roll_number, name, password, admin_id, reg_date
meal_logs   → id, roll_number, admin_id, meal_type, meal_date, meal_time
```

- Each admin's students are completely isolated from other admins
- QR codes contain admin ID — students can only log meals under their own admin
- SQLite database auto-created on first run as `messsystem.db`

---

## 🚀 Getting Started

### Requirements
- Java 8 or higher
- Git (to clone)

### Clone and Run

**Mac / Linux:**
```bash
git clone https://github.com/abhay7tx/E-MessCard.git
cd E-MessCard
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

**Windows:**
```cmd
git clone https://github.com/abhay7tx/E-MessCard.git
cd E-MessCard
mkdir out
javac -cp "lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" -d out src\*.java
java -cp "out;lib\sqlite-jdbc.jar;lib\zxing-core.jar;lib\zxing-javase.jar" MessCardSystem
```

No additional setup needed — all libraries are included in `lib/`.

---

## 📡 QR Code & Phone Access

The app runs a **built-in web server on port 8080**. When a student scans the QR code, their phone opens a login form in the browser.

### Same Network (Hotspot recommended)
1. Turn on iPhone Personal Hotspot
2. Connect Mac to iPhone hotspot
3. Run the app — QR codes automatically use the correct IP
4. Students on the same hotspot can scan and login

### Any Network (using ngrok)
1. Install ngrok: `brew install ngrok`
2. Sign up at https://ngrok.com and add your auth token:
   ```bash
   ngrok config add-authtoken YOUR_TOKEN
   ```
3. Run the app in one terminal, ngrok in another:
   ```bash
   ngrok http 8080
   ```
4. Copy the `https://xxx.ngrok-free.app` URL
5. Open Scan Station → paste URL → click Apply
6. QR codes now work on any network including college WiFi

---

## 📁 Project Structure

```
E-MessCard/
├── src/
│   ├── MessCardSystem.java          ← Entry point, starts web server
│   ├── DatabaseManager.java         ← All SQLite operations
│   ├── MessWebServer.java           ← Built-in HTTP server (phone login)
│   ├── QRCodeGenerator.java         ← ZXing QR code generation
│   ├── FlatButton.java              ← Custom button (macOS compatible)
│   ├── UIHelper.java                ← Shared colors, fonts, components
│   ├── MainFrame.java               ← Home screen
│   ├── AdminLoginDialog.java        ← Login dialog (admin + super admin)
│   ├── SuperAdminFrame.java         ← Super admin panel
│   ├── AdminFrame.java              ← Admin panel (students, QR, logs)
│   ├── ScanStationFrame.java        ← Kiosk with 4 meal QR codes
│   ├── MealLoginFrame.java          ← Kiosk login (roll + password)
│   ├── MealSuccessFrame.java        ← Success screen after meal login
│   ├── StudentLoginFrame.java       ← Student login from main screen
│   ├── StudentDashboardFrame.java   ← Student meal history
│   ├── ChangeCredentialsDialog.java ← Change username/password
│   └── DatabaseAccessDialog.java   ← DB password protection on startup
├── lib/
│   ├── sqlite-jdbc.jar              ← SQLite driver (no install needed)
│   ├── zxing-core.jar               ← QR code engine
│   └── zxing-javase.jar             ← QR code image output
├── compile.sh                       ← Mac/Linux compile script
├── run.sh                           ← Mac/Linux run script
├── messsystem.db                    ← SQLite database (auto-created)
└── README.md
```

---

## 🔐 Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Super Admin | `superadmin` | `super@123` |
| Admin | set by super admin | set by super admin |
| Student | roll number | set by admin |

> ⚠️ Change the Super Admin password after first login via the **Change My Password** button.

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|-------|
| Java AWT | Desktop GUI (no Swing/JavaFX) |
| SQLite + JDBC | Local database |
| ZXing | QR code generation |
| Java HttpServer | Built-in web server for phone login |
| HTML/CSS | Mobile login page served to phones |

---

## 📋 First Time Setup

1. Run the app
2. Login as **Super Admin** → `superadmin / super@123`
3. Add an **Admin** (username, full name, password)
4. Login as that Admin
5. Add **Students** (roll number, name, password)
6. Open **Scan Station** → 4 QR codes appear
7. Students scan QR with phone → login → meal logged ✓

---

## ⚙️ Changing Default Super Admin Password

Open `src/MessCardSystem.java` and change:
```java
public static String SUPER_ADMIN_PASS = "super@123";
```
Or change it at runtime via the **Change My Password** button in the Super Admin panel.

---

*Built as a college project — Java AWT + SQLite + ZXing*
