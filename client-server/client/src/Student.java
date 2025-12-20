import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;                  
    private String lastName;                   
    private String firstName;                  
    private String department;                 
    protected ArrayList<String> completedCoursesList; 

    public Student(String studentInfo) {
        String[] tokens = studentInfo.trim().split("\\s+");

        if (tokens.length < 4) {
            throw new IllegalArgumentException("잘못된 학생 데이터 형식: " + studentInfo);
        }

        this.studentId = tokens[0];
        this.lastName = tokens[1];
        this.firstName = tokens[2];
        this.department = tokens[3];

        this.completedCoursesList = new ArrayList<>();
        if (tokens.length > 4) {
            this.completedCoursesList.addAll(Arrays.asList(tokens).subList(4, tokens.length));
        }
    }

    public String getId() { return studentId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getDepartment() { return department; }

    public boolean match(String id) {
        return this.studentId.equals(id);
    }

    @Override
    public String toString() {
        String completed = completedCoursesList.isEmpty() ? "-" : String.join(", ", completedCoursesList);
        return String.format("%-10s %-12s %-12s %-15s %s",
                studentId, lastName, firstName, department, completed);
    }
}