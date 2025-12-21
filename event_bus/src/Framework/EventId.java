/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Framework;

// event를 넣고 싶으면 여기에 enum으로 추가 
// RegisterCourseForStudent, GetStudentInfo 이벤트 추가

public enum EventId {
    ClientOutput, ListStudents, ListCourses, ListStudentsRegistered, ListCourseRegistered, ListCoursesCompleted, RegisterStudents, DeleteStudents, RegisterCourses, DeleteCourses, RegisterCourseForStudent, GetStudentInfo, QuitTheSystem
}
