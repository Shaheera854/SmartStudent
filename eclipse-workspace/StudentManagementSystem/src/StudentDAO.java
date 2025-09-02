import java.sql.*;
import java.util.*;

public class StudentDAO {

    // create / ensure table exists if you prefer runtime creation (optional)
    public void ensureTableExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "roll_no VARCHAR(50) NOT NULL UNIQUE," +
                "department VARCHAR(100)," +
                "email VARCHAR(150) UNIQUE," +
                "phone VARCHAR(20) UNIQUE," +
                "marks INT DEFAULT 0" +
                ") ENGINE=InnoDB";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    public Student create(Student s) throws SQLException {
        String sql = "INSERT INTO students (name, roll_no, department, email, phone, marks) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getRollNo());
            ps.setString(3, s.getDepartment());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setInt(6, s.getMarks());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) s.setId(rs.getLong(1));
            }
            return s;
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE students SET name=?, roll_no=?, department=?, email=?, phone=?, marks=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getRollNo());
            ps.setString(3, s.getDepartment());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setInt(6, s.getMarks());
            ps.setLong(7, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<Student> getAll() throws SQLException {
        String sql = "SELECT id, name, roll_no, department, email, phone, marks FROM students ORDER BY id ASC";
        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setRollNo(rs.getString("roll_no"));
                s.setDepartment(rs.getString("department"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setMarks(rs.getInt("marks"));
                list.add(s);
            }
        }
        return list;
    }

    // Search: dynamic filters (null or blank means ignore)
    public List<Student> search(String name, String department, String rollNo, Integer minMarks, Integer maxMarks) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, name, roll_no, department, email, phone, marks FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND LOWER(name) LIKE ?");
            params.add("%" + name.trim().toLowerCase() + "%");
        }
        if (department != null && !department.trim().isEmpty()) {
            sql.append(" AND LOWER(department) = ?");
            params.add(department.trim().toLowerCase());
        }
        if (rollNo != null && !rollNo.trim().isEmpty()) {
            sql.append(" AND LOWER(roll_no) LIKE ?");
            params.add("%" + rollNo.trim().toLowerCase() + "%");
        }
        if (minMarks != null) {
            sql.append(" AND marks >= ?");
            params.add(minMarks);
        }
        if (maxMarks != null) {
            sql.append(" AND marks <= ?");
            params.add(maxMarks);
        }

        sql.append(" ORDER BY id ASC");

        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getLong("id"));
                    s.setName(rs.getString("name"));
                    s.setRollNo(rs.getString("roll_no"));
                    s.setDepartment(rs.getString("department"));
                    s.setEmail(rs.getString("email"));
                    s.setPhone(rs.getString("phone"));
                    s.setMarks(rs.getInt("marks"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public Stats getStats() throws SQLException {
        Stats stats = new Stats();
        String sql = "SELECT COUNT(*) cnt, MAX(marks) maxm, MIN(marks) minm FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                stats.setTotalStudents(rs.getInt("cnt"));
                stats.setMaxMarks(rs.getInt("maxm"));
                stats.setMinMarks(rs.getInt("minm"));
            }
        }

        String deptSql = "SELECT department, COUNT(*) c FROM students GROUP BY department ORDER BY c DESC";
        Map<String,Integer> map = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(deptSql)) {
            while (rs.next()) {
                String dept = rs.getString("department");
                int c = rs.getInt("c");
                map.put(dept == null ? "Unknown" : dept, c);
            }
        }
        stats.setDeptCounts(map);
        return stats;
    }
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setDepartment(rs.getString("department"));
                student.setMarks(rs.getInt("marks"));

                students.add(student);
            }
        }
        return students;
    }

}
