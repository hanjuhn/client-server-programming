/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */
package Components.Course;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class CourseMain {
	public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("CourseMain (ID:" + componentId + ") is successfully registered...");

		CourseComponent coursesList = new CourseComponent("Courses.txt");
		Event event = null;
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent();
				switch (event.getEventId()) {
				case ListCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeCourseList(coursesList)));
					break;
				case RegisterCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerCourse(coursesList, event.getMessage())));
					break;
				// deleteCourse 함수로 학생 목록에서 삭제 작업을 수행함
    			// deleteCourse의 결과를 ClientOutput 이벤트로 만들어 EventBus에 전달함
				case DeleteCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteCourse(coursesList, event.getMessage())));
					break;
				case RegisterCourseForStudent:
					printLogEvent("Get", event);
					String message = event.getMessage();
					 // 1. 학생 정보 조회가 완료되어 STUDENT_INFO 메시지가 도착한 경우
					if (message != null && message.startsWith("STUDENT_INFO")) {
					// STUDENT_INFO에는 studentId, courseId, completedCourses가 포함됨
           			// 이를 기반으로 실제 수강 신청 로직을 수행하고 결과를 ClientOutput으로 전송함
						String result = registerCourseForStudent(coursesList, message);
						eventBus.sendEvent(new Event(EventId.ClientOutput, result));
						printLogEvent("Send ClientOutput", new Event(EventId.ClientOutput, result));
					 // 2. 학생이 존재하지 않아 STUDENT_NOT_FOUND 메시지가 도착한 경우
					} else if (message != null && message.startsWith("STUDENT_NOT_FOUND")) {
						String[] tokens = message.split("\\s+");
						String studentId = tokens.length > 1 ? tokens[1] : "";
						String courseId = tokens.length > 2 ? tokens[2] : "";
						// 존재하지 않는 학생이 신청한 경우 처리 거부 메시지를 출력함
						String errorMsg = "존재하지 않는 학생 (ID: " + studentId + ")이 신청한 경우 수강 신청을 받아주지 않습니다.";
						eventBus.sendEvent(new Event(EventId.ClientOutput, errorMsg));
						printLogEvent("Send ClientOutput", new Event(EventId.ClientOutput, errorMsg));
					// 3. 학생 정보가 확인되지 않은 상태에서 RegisterCourseForStudent 요청이 들어온 경우
    				// 먼저 학생 정보를 조회해야 하므로 GetStudentInfo 이벤트로 재요청함
					} else if (message != null && !message.trim().isEmpty()) {
						eventBus.sendEvent(new Event(EventId.GetStudentInfo, message));
					}
					break;
				case QuitTheSystem:
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	private static String registerCourse(CourseComponent coursesList, String message) {
		Course course = new Course(message);
		if (!coursesList.isRegisteredCourse(course.courseId)) {
			coursesList.vCourse.add(course);
			return "This course is successfully added.";
		} else
			return "This course is already registered.";
	}
	private static String makeCourseList(CourseComponent coursesList) {
		String returnString = "";
		for (int j = 0; j < coursesList.vCourse.size(); j++) {
			returnString += coursesList.getCourseList().get(j).getString() + "\n";
		}
		return returnString;
	}
	// coursesList 내부의 deleteCourse 메서드가 true를 반환하면 삭제 성공
	private static String deleteCourse(CourseComponent coursesList, String message) {
		String courseId = message.trim();
		if (coursesList.deleteCourse(courseId)) {
			return "선택된 Course (ID: " + courseId + ")가 삭제되었습니다.";
		} else {
			return "Course (ID: " + courseId + ")를 찾을 수 없습니다.";
		}
	}
	private static String registerCourseForStudent(CourseComponent coursesList, String message) {
		if (message == null || message.trim().isEmpty()) {
			return "잘못된 수강 신청 정보입니다.";
		}
		String[] tokens = message.trim().split("\\s+");
		// 메시지 형식이 STUDENT_INFO studentId courseId 형태가 아니면 오류 처리
		if (tokens.length < 3) {
			return "잘못된 수강 신청 정보입니다.";
		}
		// studentId와 courseId를 추출
		String studentId = tokens[1];
		String courseId = tokens[2];
		
		// 2. 존재하지 않는 과목을 신청한 경우
		Course course = coursesList.getCourse(courseId);
		if (course == null) {
			return "존재하지 않는 과목 (ID: " + courseId + ")을 신청한 경우 수강 신청을 받아주지 않습니다.";
		}
		
		// 3. 학생이 신청한 과목의 선수과목을 이수하지 않는 경우
		// 학생이 이수한 과목들을 리스트로 저장
		java.util.ArrayList<String> completedCourses = new java.util.ArrayList<String>();
		for (int i = 3; i < tokens.length; i++) {
			if (!tokens[i].isEmpty()) {
				completedCourses.add(tokens[i]);
			}
		}
		// 신청한 과목의 선수과목 목록 가져오기
		java.util.ArrayList<String> prerequisites = course.prerequisiteCoursesList;
		// 모든 선수과목을 이수했는지 확인
		for (String prerequisite : prerequisites) {
			if (!completedCourses.contains(prerequisite)) {
				// 선수과목 하나라도 안 했으면 수강 신청 거부
				return "학생이 신청한 과목 (ID: " + courseId + ")의 선수과목 (ID: " + prerequisite + ")을 이수하지 않는 경우 수강 신청을 받아주지 않습니다.";
			}
		}
		// 모든 선수과목을 이수했으면 수강 신청 성공
		return "수강 신청이 성공적으로 완료되었습니다. (학생 ID: " + studentId + ", 과목 ID: " + courseId + ")";
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
