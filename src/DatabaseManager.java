import java.util.List;

/**
 * Small facade used by the rest of the app.
 * Each database area is kept in its own file for easier explanation.
 */
public class DatabaseManager {

    public static void initialize() {
        DatabaseSchema.initialize();
    }

    public static boolean addAdmin(String username, String password, String fullName, String messName) {
        return DatabaseAdmin.addAdmin(username, password, fullName, messName);
    }

    public static String[] validateAdmin(String username, String password) {
        return DatabaseAdmin.validateAdmin(username, password);
    }

    public static List<String[]> getAllAdmins() {
        return DatabaseAdmin.getAllAdmins();
    }

    public static boolean removeAdmin(int adminId) {
        return DatabaseAdmin.removeAdmin(adminId);
    }

    public static boolean changeAdminPassword(int adminId, String newPass) {
        return DatabaseAdmin.changeAdminPassword(adminId, newPass);
    }

    public static boolean addStudent(String roll, String name, String password, int adminId) {
        return DatabaseStudent.addStudent(roll, name, password, adminId);
    }

    public static boolean removeStudent(String roll, int adminId) {
        return DatabaseStudent.removeStudent(roll, adminId);
    }

    public static boolean changeStudentPassword(String roll, int adminId, String newPass) {
        return DatabaseStudent.changeStudentPassword(roll, adminId, newPass);
    }

    public static String validateStudent(String roll, String password, int adminId) {
        return DatabaseStudent.validateStudent(roll, password, adminId);
    }

    public static boolean isRegistered(String roll, int adminId) {
        return DatabaseStudent.isRegistered(roll, adminId);
    }

    public static int findAdminForRoll(String roll) {
        return DatabaseStudent.findAdminForRoll(roll);
    }

    public static List<String[]> getStudentsByAdmin(int adminId) {
        return DatabaseStudent.getStudentsByAdmin(adminId);
    }

    public static boolean hasMealToday(String roll, int adminId, String mealType) {
        return DatabaseMeal.hasMealToday(roll, adminId, mealType);
    }

    public static boolean logMeal(String roll, int adminId, String mealType) {
        return DatabaseMeal.logMeal(roll, adminId, mealType);
    }

    public static List<String[]> getMealRecords(String roll, int adminId) {
        return DatabaseMeal.getMealRecords(roll, adminId);
    }

    public static List<String[]> getAllMealLogs(int adminId) {
        return DatabaseMeal.getAllMealLogs(adminId);
    }

    public static List<String[]> getMealCountByType(int adminId) {
        return DatabaseReport.getMealCountByType(adminId);
    }

    public static List<String[]> getMonthlyBill(int adminId, String yearMonth) {
        return DatabaseReport.getMonthlyBill(adminId, yearMonth);
    }

    public static boolean setMenu(int adminId, String day, String mealType, String items) {
        return DatabaseMenu.setMenu(adminId, day, mealType, items);
    }

    public static String getMenu(int adminId, String day, String mealType) {
        return DatabaseMenu.getMenu(adminId, day, mealType);
    }

    public static List<String[]> getDayMenu(int adminId, String day) {
        return DatabaseMenu.getDayMenu(adminId, day);
    }

    public static boolean deleteMenu(int adminId, String day, String mealType) {
        return DatabaseMenu.deleteMenu(adminId, day, mealType);
    }
}
