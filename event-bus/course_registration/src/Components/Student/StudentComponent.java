/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Components.Student;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StudentComponent {
	protected ArrayList<Student> vStudent;
	
	public StudentComponent(String sStudentFileName) throws FileNotFoundException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(sStudentFileName));
		this.vStudent = new ArrayList<Student>();
		while (bufferedReader.ready()) {
			String stuInfo = bufferedReader.readLine();
			if (!stuInfo.equals("")) this.vStudent.add(new Student(stuInfo));
		}
		bufferedReader.close();
	}
	public ArrayList<Student> getStudentList() {
		return vStudent;
	}
	public void setvStudent(ArrayList<Student> vStudent) {
		this.vStudent = vStudent;
	}
	public boolean isRegisteredStudent(String sSID) {
		for (int i = 0; i < this.vStudent.size(); i++) {
			if (((Student) this.vStudent.get(i)).match(sSID)) return true;
		}
		return false;
	}
	// 현재 Student 객체가 전달받은 studentId와 일치하는지 확인
	public boolean deleteStudent(String studentId) {
		for (int i = 0; i < this.vStudent.size(); i++) {
			if (this.vStudent.get(i).match(studentId)) {
				// 리스트에서 제거
				this.vStudent.remove(i);
				return true;
			}
		}
		return false;
	}
	public Student getStudent(String studentId) {
		// vStudent 리스트에 저장된 모든 Student 객체를 순회
		for (int i = 0; i < this.vStudent.size(); i++) {
			if (this.vStudent.get(i).match(studentId)) {
				// 일치하는 Student 객체를 반환
				return this.vStudent.get(i);
			}
		}
		return null;
	}
}
