/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Components.Student;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class StudentMain {
	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** StudentMain(ID:" + componentId + ") is successfully registered. \n");

		StudentComponent studentsList = new StudentComponent("Students.txt");
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
				case ListStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeStudentList(studentsList)));
					break;
				case RegisterStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerStudent(studentsList, event.getMessage())));
					break;
				// DeleteStudents 이벤트가 발생했을 때 실행되는 분기
				case DeleteStudents:
					printLogEvent("Get", event);
					// deleteStudent 함수로 학생 목록에서 삭제 작업을 수행함
    				// deleteStudent의 결과를 ClientOutput 이벤트로 만들어 EventBus에 전달함
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteStudent(studentsList, event.getMessage())));
					break;
				case GetStudentInfo:
					printLogEvent("Get", event);
					// getStudentInfo 함수로 학생 정보를 조회함
					// 조회된 학생 정보를 RegisterCourseForStudent 이벤트 형태로 응답 (STUDENT_INFO 또는 STUDENT_NOT_FOUND 포함)
					eventBus.sendEvent(new Event(EventId.RegisterCourseForStudent, getStudentInfo(studentsList, event.getMessage())));
					break;
				case QuitTheSystem:
					printLogEvent("Get", event);
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	private static String registerStudent(StudentComponent studentsList, String message) {
		Student  student = new Student(message);
		if (!studentsList.isRegisteredStudent(student.studentId)) {
			studentsList.vStudent.add(student);
			return "This student is successfully added.";
		} else
			return "This student is already registered.";
	}
	private static String makeStudentList(StudentComponent studentsList) {
		String returnString = "";
		for (int j = 0; j < studentsList.vStudent.size(); j++) {
			returnString += studentsList.getStudentList().get(j).getString() + "\n";
		}
		return returnString;
	}
	// 학생 삭제
	private static String deleteStudent(StudentComponent studentsList, String message) {
		String studentId = message.trim();
		// studentsList 내부의 deleteStudent 메서드가 true를 반환하면 삭제 성공
		if (studentsList.deleteStudent(studentId)) {
			return "선택된 Student (ID: " + studentId + ")가 삭제되었습니다.";
		} else {
			return "Student (ID: " + studentId + ")를 찾을 수 없습니다.";
		}
	}
	private static String getStudentInfo(StudentComponent studentsList, String message) {
		// message는 "studentId courseId" 형태의 문자열
		if (message == null || message.trim().isEmpty()) {
			return "STUDENT_NOT_FOUND ";
		}
		String[] tokens = message.trim().split("\\s+");
		if (tokens.length < 2) {
			return "STUDENT_NOT_FOUND " + (tokens.length > 0 ? tokens[0] : "") + " ";
		}
		String studentId = tokens[0];
		String courseId = tokens[1];
		
		Student student = studentsList.getStudent(studentId);
		if (student == null) {
			return "STUDENT_NOT_FOUND " + studentId + " " + courseId;
		}
		
		// 학생이 수강 완료한 과목 ID들을 하나의 문자열로 연결
		String completedCourses = "";
		for (String course : student.getCompletedCourses()) {
			if (!completedCourses.isEmpty()) {
				completedCourses += " ";
			}
			completedCourses += course;
		}
		// completedCourses가 비어있어도 공백은 제거하여 반환
		String result = "STUDENT_INFO " + studentId + " " + courseId;
		if (!completedCourses.isEmpty()) {
			result += " " + completedCourses;
		}
		return result;
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
