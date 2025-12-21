/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University
 */
package Components.Course;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CourseComponent {
    protected ArrayList<Course> vCourse;

    public CourseComponent(String sCourseFileName) throws FileNotFoundException, IOException { 	
        BufferedReader bufferedReader  = new BufferedReader(new FileReader(sCourseFileName));       
        this.vCourse  = new ArrayList<Course>();
        while (bufferedReader.ready()) {
            String courseInfo = bufferedReader.readLine();
            if(!courseInfo.equals("")) this.vCourse.add(new Course(courseInfo));
        }    
        bufferedReader.close();
    }
    public ArrayList<Course> getCourseList() {
        return this.vCourse;
    }
    public boolean isRegisteredCourse(String courseId) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            if(((Course) this.vCourse.get(i)).match(courseId)) return true;
        }
        return false;
    }
    // 현재 Course 객체가 전달받은 courseId와 일치하는지 확인
    public boolean deleteCourse(String courseId) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            if (this.vCourse.get(i).match(courseId)) {
                // 리스트에서 제거
                this.vCourse.remove(i);
                return true;
            }
        }
        return false;
    }
    public Course getCourse(String courseId) {
        // vCourse 리스트에 저장된 모든 Course 객체를 순회
        for (int i = 0; i < this.vCourse.size(); i++) {
            if (this.vCourse.get(i).match(courseId)) {
                // 일치하는 Course 객체를 반환
                return this.vCourse.get(i);
            }
        }
        return null;
    }
}
