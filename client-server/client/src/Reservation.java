import java.io.Serializable;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String studentId;
    private String courseId;

    public Reservation(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public boolean match(String sId, String cId) {
        return this.studentId.equals(sId) && this.courseId.equals(cId);
    }

    @Override
    public String toString() {
        return "Reservation [studentId=" + studentId + ", courseId=" + courseId + "]";
    }
}