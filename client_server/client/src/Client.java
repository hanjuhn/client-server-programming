import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        ServerIF server;
        BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            server = (ServerIF) Naming.lookup("Server");

            while (true) {
                printMenu();
                String sChoice = objReader.readLine().trim();

                switch (sChoice) {
                    case "1":
                        ArrayList<Student> students = server.getAllStudentData();
                        printStudents(students);
                        break;
                    case "2":
                        ArrayList<Course> courses = server.getAllCourseData();
                        printCourses(courses);
                        break;
                    case "3":
                        addStudent(server, objReader);
                        break;
                    case "4":
                        deleteStudent(server, objReader);
                        break;
                    case "5":
                        addCourse(server, objReader);
                        break;
                    case "6":
                        deleteCourse(server, objReader);
                        break;
                    case "7":
                        makeReservation(server, objReader);
                        break;
                    case "8":
                        viewReservations(server);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullDataException e) {
            e.printStackTrace();
        }
    }

    // 학생 추가 및 삭제
    private static void addStudent(ServerIF server, BufferedReader objReader) throws RemoteException, IOException {
        System.out.println("------- Student Information: -------");
        System.out.print("Student ID: ");
        String studentId = objReader.readLine().trim();
        System.out.print("Student Name: ");
        String studentName = objReader.readLine().trim();
        System.out.print("Student Department: ");
        String studentDept = objReader.readLine().trim();
        System.out.print("Student Completed Course List: ");
        String completedCourses = objReader.readLine().trim();

        if (server.addStudent(studentId + " " + studentName + " " + studentDept + " " + completedCourses))
            System.out.println("SUCCESS");
        else
            System.out.println("FAIL");
    }

    private static void deleteStudent(ServerIF server, BufferedReader objReader) throws RemoteException, IOException {
        System.out.print("Student ID: ");
        if (server.deleteStudent(objReader.readLine().trim()))
            System.out.println("SUCCESS");
        else
            System.out.println("FAIL");
    }

    // 과목 추가 및 삭제
    private static void addCourse(ServerIF server, BufferedReader objReader) throws RemoteException, IOException {
        System.out.println("------- Course Information: -------");
        System.out.print("Course ID: ");
        String courseId = objReader.readLine().trim();
        System.out.print("Course Name: ");
        String courseName = objReader.readLine().trim();
        System.out.print("Course Instructor: ");
        String courseInstructor = objReader.readLine().trim();
        System.out.print("Prerequisite Course ID: ");
        String prerequisite = objReader.readLine().trim();

        if (server.addCourse(courseId + " " + courseName + " " + courseInstructor + " " + prerequisite))
            System.out.println("SUCCESS");
        else
            System.out.println("FAIL");
    }

    private static void deleteCourse(ServerIF server, BufferedReader objReader) throws RemoteException, IOException {
        System.out.print("Course ID: ");
        if (server.deleteCourse(objReader.readLine().trim()))
            System.out.println("SUCCESS");
        else
            System.out.println("FAIL");
    }

    // 수강신청 기능
    private static void makeReservation(ServerIF server, BufferedReader objReader)
            throws RemoteException, IOException {
        System.out.println("------- Make Reservation -------");
        System.out.print("Student ID: ");
        String studentId = objReader.readLine().trim();
        System.out.print("Course ID: ");
        String courseId = objReader.readLine().trim();

        String resultMsg = server.makeReservation(studentId, courseId);
        System.out.println(resultMsg);
    }
    
    private static void viewReservations(ServerIF server) throws RemoteException {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);

        System.out.println("==== 로그인 ====");
        System.out.print("학번: ");
        String id = sc.nextLine();

        // 학생 존재 여부 확인
        boolean validStudent = false;
        try {
            ArrayList<Student> students = server.getAllStudentData();
            for (Student s : students) {
                if (s.getId().equals(id)) {
                    validStudent = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("학생 데이터 조회 중 오류: " + e.getMessage());
            return;
        }

        if (!validStudent) {
            System.out.println("해당 학번은 존재하지 않습니다. 로그인할 수 없습니다.");
            return;
        }

        // 비밀번호 존재 여부 확인
        boolean hasPw = false;
        try {
            hasPw = server.hasPassword(id);
        } catch (Exception e) {
            System.out.println("서버 통신 중 오류: " + e.getMessage());
            return;
        }

        // 비밀번호가 없는 경우 (새 비밀번호 등록)
        if (!hasPw) {
            System.out.println("이 학번은 등록된 비밀번호가 없습니다.");
            System.out.print("새 비밀번호를 등록하시겠습니까? (Y/N): ");
            String ans = sc.nextLine();

            if (ans.equalsIgnoreCase("Y")) {
                System.out.print("새 비밀번호 입력: ");
                String newPw = sc.nextLine();
                server.registerPassword(id, newPw);
                System.out.println("비밀번호가 등록되었습니다. 이제 로그인할 수 있습니다.");
            } else {
                System.out.println("로그인을 취소했습니다.");
                return;
            }
        }

     // 로그인 시도 (비밀번호 틀리면 다시 입력)
        String sessionId = null;
        while (sessionId == null) {
            System.out.print("비밀번호: ");
            String pw = sc.nextLine();

            String result = server.login(id, pw); // 로그인 결과 코드 or 세션 ID 반환

            if (result == null) {
                System.out.println("서버 응답 오류. 다시 시도하세요.");
                continue;
            }

            switch (result) {
                case "WRONG_PASSWORD":
                    System.out.println("비밀번호가 틀렸습니다. 다시 입력하세요.");
                    break;

                case "ALREADY_LOGGED_IN":
                    System.out.println("이미 로그인 중입니다. 다른 기기에서 로그아웃 후 시도하세요.");
                    return;

                default:
                    sessionId = result;
                    break;
            }
        }
        System.out.println("로그인 성공 (세션 ID: " + sessionId + ")\n");

        // 세션 유효성 검사
        if (!server.validateSession(sessionId)) {
            System.out.println("세션이 만료되었습니다. 다시 로그인해주세요.");
            return;
        }

        // 로그인 성공 시 수강 신청 내역 출력
        try {
            ArrayList<Reservation> reservations = server.getAllReservations();
            ArrayList<Student> students = server.getAllStudentData();
            ArrayList<Course> courses = server.getAllCourseData();

            if (reservations == null || reservations.isEmpty()) {
                System.out.println("수강신청 내역이 없습니다.");
            } else {
                System.out.println("==== 내 수강신청 내역 ====");
                int idx = 1;
                boolean found = false;

                for (Reservation r : reservations) {
                    if (!r.getStudentId().equals(id)) continue;
                    found = true;

                    String studentInfo = findStudentInfo(r.getStudentId(), students);
                    String courseInfo = findCourseInfo(r.getCourseId(), courses);

                    System.out.println(idx + ")");
                    System.out.println(" Student: " + studentInfo);
                    System.out.println(" Course : " + courseInfo);
                    System.out.println("--------------------------------");
                    idx++;
                }

                if (!found) {
                    System.out.println("수강신청 내역이 없습니다.");
                }
            }

        } catch (NullDataException e) {
            System.out.println("Error: no reservation data available");
        }

        // 로그아웃 선택
        System.out.print("\n로그아웃하시겠습니까? (Y/N): ");
        String logoutAns = sc.nextLine();
        if (logoutAns.equalsIgnoreCase("Y")) {
            try {
                server.logout(sessionId);
                System.out.println("로그아웃 완료. 세션이 종료되었습니다.");
            } catch (Exception e) {
                System.out.println("로그아웃 중 오류: " + e.getMessage());
            }
        } else {
            System.out.println("세션을 유지합니다.");
        }
    }
    
    private static String findStudentInfo(String studentId, ArrayList<Student> students) {
        for (Student s : students) {
            if (s.match(studentId)) {
                return String.format("%s (%s %s, %s)",
                        s.getId(),
                        s.getLastName(),
                        s.getFirstName(),
                        s.getDepartment());
            }
        }
        return studentId + " (정보 없음)";
    }

    private static String findCourseInfo(String courseId, ArrayList<Course> courses) {
        for (Course c : courses) {
            if (c.match(courseId)) {
                return c.getCourseId() + " (" + c.getCourseName() + ", " + c.getInstructor() + ")";
            }
        }
        return courseId + " (정보 없음)";
    }

    // 출력 및 메뉴
    private static void printMenu() {
        System.out.println("***************** MENU *****************");
        System.out.println("1. List Students");
        System.out.println("2. List Courses");
        System.out.println("3. Add Student");
        System.out.println("4. Delete Student");
        System.out.println("5. Add Course");
        System.out.println("6. Delete Course");
        System.out.println("7. Make Reservation");
        System.out.println("8. View Reservations");
        System.out.println("0. Exit");
        System.out.print("Select: ");
    }

    private static void printStudents(ArrayList<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("No students found");
            return;
        }
        System.out.println("===========================================================================================================");
        System.out.println("                                            Student List                                                 ");
        System.out.println("===========================================================================================================");
        System.out.printf("%-4s %-10s %-12s %-12s %-15s %-50s%n",
                          "No", "ID", "LastName", "FirstName", "Department", "Courses");
        System.out.println("===========================================================================================================");

        int idx = 1;
        for (Student s : students) {
            String completed = (s.completedCoursesList == null || s.completedCoursesList.isEmpty())
                    ? "-"
                    : String.join(", ", s.completedCoursesList);

            System.out.printf("%-4d %-10s %-12s %-12s %-15s %-50s%n",
                              idx,
                              s.getId(),
                              s.getLastName(),
                              s.getFirstName(),
                              s.getDepartment(),
                              completed);
            idx++;
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }

    private static void printCourses(ArrayList<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            System.out.println("No courses found");
            return;
        }

        System.out.println("===========================================================================================================");
        System.out.println("                                            Course List                                                 ");
        System.out.println("===========================================================================================================");
        System.out.printf("%-4s %-10s %-35s %-20s %-15s%n",
                          "No", "ID", "Course Name", "Instructor", "Prerequisite");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        int idx = 1;
        for (Course c : courses) {
            String prereq = (c.getPrerequisite() == null || c.getPrerequisite().trim().equals("") || c.getPrerequisite().equalsIgnoreCase("none"))
                    ? "-"
                    : c.getPrerequisite();

            System.out.printf("%-4d %-10s %-35s %-20s %-15s%n",
                              idx,
                              c.getCourseId(),
                              c.getCourseName().replace('_', ' '),
                              c.getInstructor(),
                              prereq);
            idx++;
        }

        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }
}