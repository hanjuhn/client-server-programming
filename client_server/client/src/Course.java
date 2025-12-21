import java.io.Serializable;
import java.util.StringTokenizer;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseId;       // 과목 코드
    private String instructor;     // 담당 교수
    private String courseName;     // 과목 이름
    private String prerequisite;   // 선수 과목 코드 (없으면 null)

    // Course.txt의 한 줄을 받아서 초기화
    public Course(String inputString) {
        StringTokenizer st = new StringTokenizer(inputString);
        this.courseId = st.nextToken();
        this.instructor = st.nextToken();
        this.courseName = st.nextToken();
        if (st.hasMoreTokens()) {
            this.prerequisite = st.nextToken();
        } else {
            this.prerequisite = null;
        }
    }

    // Getter 메소드
    public String getCourseId() {
        return courseId;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    // 과목 코드 일치 여부 확인
    public boolean match(String courseId) {
        return this.courseId.equals(courseId);
    }

    @Override
    public String toString() {
        String result = courseId + " " + instructor + " " + courseName;
        if (prerequisite != null) {
            result += " " + prerequisite;
        }
        return result;
    }
}
