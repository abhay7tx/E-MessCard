# E-Mess Card System

This version is arranged so it is easier to explain as a college project.

Project structure:
- `src/`: Java source code for the desktop app, server, QR flow, and database operations.
- `web/`: Separate HTML templates and CSS for the web/mobile frontend.
- `db/`: Separate DBMS reference notes and schema file for presentation.

Useful files for explanation:
- `src/MessCardSystem.java`: main entry point.
- `src/Screens.java`: Java AWT screens and flow.
- `src/MessWebServer.java`: server routes for the HTML frontend.
- `src/DatabaseManager.java`: Java database operations.
- `db/schema_reference.sql`: SQL schema for DBMS explanation.

Frontend change:
- The mobile/web pages are no longer hardcoded inside Java strings.
- HTML is in `web/templates/`.
- CSS is in `web/static/styles.css`.
- The desktop AWT home screen and login dialogs were simplified for easier classroom presentation.

Button visibility fix:
- `src/UI.java` now uses safer button colors on macOS so text stays readable.

Build note:
- The project expects these jars in `lib/`:
  `sqlite-jdbc.jar`, `zxing-core.jar`, and `zxing-javase.jar`
