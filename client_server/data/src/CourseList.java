import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CourseList {
    protected ArrayList<Course> vCourse;

    public CourseList(String sCourseFileName) throws FileNotFoundException, IOException {
        BufferedReader objCourseFile = new BufferedReader(new FileReader(sCourseFileName));
        this.vCourse = new ArrayList<Course>();
        while (objCourseFile.ready()) {
            String courseInfo = objCourseFile.readLine();
            if (courseInfo != null && !courseInfo.trim().equals("")) {
                this.vCourse.add(new Course(courseInfo));
            }
        }
        objCourseFile.close();
    }

    public ArrayList<Course> getAllCourseRecords() {
        return this.vCourse;
    }
    
    public boolean addCourseRecords(String courseInfo) {
        if (this.vCourse.add(new Course(courseInfo))) return true;
        else return false;
    }

    public boolean deleteCourseRecords(String courseId) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            Course course = this.vCourse.get(i);
            if (course.match(courseId)) {
                if (this.vCourse.remove(course)) return true;
                else return false;
            }
        }
        return false;
    }

    public boolean isOfferedCourse(String sCID) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            Course objCourse = this.vCourse.get(i);
            if (objCourse.match(sCID)) {
                return true;
            }
        }
        return false;
    }

    public Course getCourseById(String sCID) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            Course objCourse = this.vCourse.get(i);
            if (objCourse.match(sCID)) {
                return objCourse;
            }
        }
        return null;
    }
}