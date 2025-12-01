import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private Connection conn;

    // Connect to SQLite database (company.db)
    public boolean connect(String dbName) {
        try {
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");

            // SQLite connection string
            String url = "jdbc:sqlite:" + dbName;

            conn = DriverManager.getConnection(url);

            // Activate foreign key support
            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getDepartments() {
        ArrayList<String> list = new ArrayList<>();
        try {
            String sql = "SELECT Dname FROM DEPARTMENT";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getString("Dname"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> getProjects() {
        ArrayList<String> list = new ArrayList<>();
        try {
            String sql = "SELECT Pname FROM PROJECT";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                list.add(rs.getString("Pname"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> searchEmployees(
            ArrayList<String> depts, boolean notDept,
            ArrayList<String> projs, boolean notProj) {

        ArrayList<String> results = new ArrayList<>();

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("SELECT DISTINCT E.Fname, E.Lname ");
            sb.append("FROM EMPLOYEE E ");
            sb.append("LEFT JOIN DEPARTMENT D ON E.Dno = D.Dnumber ");
            sb.append("LEFT JOIN WORKS_ON W ON E.Ssn = W.Essn ");
            sb.append("LEFT JOIN PROJECT P ON W.Pno = P.Pnumber ");
            sb.append("WHERE 1=1 ");

            // Department filters
            if (!depts.isEmpty()) {
                sb.append(" AND ");
                sb.append("D.Dname ");
                sb.append(notDept ? "NOT IN (" : "IN (");

                for (int i = 0; i < depts.size(); i++) {
                    sb.append("'" + depts.get(i) + "'");
                    if (i < depts.size() - 1) sb.append(", ");
                }
                sb.append(") ");
            }

            // Project filters
            if (!projs.isEmpty()) {
                sb.append(" AND ");
                sb.append("P.Pname ");
                sb.append(notProj ? "NOT IN (" : "IN (");

                for (int i = 0; i < projs.size(); i++) {
                    sb.append("'" + projs.get(i) + "'");
                    if (i < projs.size() - 1) sb.append(", ");
                }
                sb.append(") ");
            }

            ResultSet rs = conn.createStatement().executeQuery(sb.toString());

            while (rs.next()) {
                results.add(rs.getString("Fname") + " " + rs.getString("Lname"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}