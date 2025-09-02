import java.util.Map;
import java.util.LinkedHashMap;

public class Stats {
    private int totalStudents;
    private int maxMarks;
    private int minMarks;
    private Map<String, Integer> deptCounts = new LinkedHashMap<>();

    // getters / setters
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }
    public int getMinMarks() { return minMarks; }
    public void setMinMarks(int minMarks) { this.minMarks = minMarks; }
    public Map<String,Integer> getDeptCounts() { return deptCounts; }
    public void setDeptCounts(Map<String,Integer> deptCounts) { this.deptCounts = deptCounts; }
}
