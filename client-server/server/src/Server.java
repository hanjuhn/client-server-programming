import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Server extends UnicastRemoteObject implements ServerIF {

    private static final long serialVersionUID = 1L;
    private static DataIF data;

    // 세션 저장소 (메모리)
    private static final Map<String, Session> sessionMap = new HashMap<>();

    protected Server() throws RemoteException { super(); }

    public static void main(String[] arg) throws NotBoundException {
        try {
            Server server = new Server();
            Naming.rebind("Server", server);
            System.out.println("Server is ready !!");

            data = (DataIF) Naming.lookup("Data");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Student> getAllStudentData() throws RemoteException, NullDataException {
        return data.getAllStudentData();
    }

    @Override
    public ArrayList<Course> getAllCourseData() throws RemoteException {
        return data.getAllCourseData();
    }

    // 학생 추가: StudentList에 추가 성공 시 계정만 Password.txt에 등록(비번 없음)
    @Override
    public boolean addStudent(String studentInfo) throws RemoteException {
        boolean result = data.addStudent(studentInfo);
        if (result) {
            String[] tokens = studentInfo.trim().split("\\s+");
            String studentId = tokens[0];

            // 계정만 추가 (비밀번호 없이)
            if (!hasPassword(studentId)) {
                data.addAccount(studentId);
                LogManager.writeLog("ADMIN", "autoRegisterAccount(" + studentId + ")");
            }
        }
        return result;
    }

    @Override
    public boolean deleteStudent(String studentId) throws RemoteException {
        return data.deleteStudent(studentId);
    }

    @Override
    public boolean addCourse(String courseInfo) throws RemoteException {
        return data.addCourse(courseInfo);
    }

    @Override
    public boolean deleteCourse(String courseId) throws RemoteException {
        return data.deleteCourse(courseId);
    }

    @Override
    public String makeReservation(String studentId, String courseId) throws RemoteException {
        try {
            Student targetStudent = null;
            Course targetCourse = null;

            // 학생 찾기
            for (Student s : data.getAllStudentData()) {
                if (s.match(studentId)) { targetStudent = s; break; }
            }
            if (targetStudent == null)
                return "Reservation failed: invalid student ID (" + studentId + ")";

            // 과목 찾기
            for (Course c : data.getAllCourseData()) {
                if (c.match(courseId)) { targetCourse = c; break; }
            }
            if (targetCourse == null)
                return "Reservation failed: invalid course ID (" + courseId + ")";

            // 선수과목 확인
            String prereq = targetCourse.getPrerequisite();
            if (prereq != null && !prereq.trim().isEmpty() && !"none".equalsIgnoreCase(prereq)) {
                if (targetStudent.completedCoursesList == null ||
                    !targetStudent.completedCoursesList.contains(prereq)) {
                    return "Reservation failed: prerequisite course (" + prereq + ") not completed";
                }
            }

            // 중복 신청 확인
            for (Reservation r : data.getAllReservations()) {
                if (r.getStudentId().equals(studentId) && r.getCourseId().equals(courseId)) {
                    return "Reservation failed: already reserved";
                }
            }

            // 신청 수행
            if (data.makeReservation(studentId, courseId))
                return "Reservation Successful: Student [" + studentId + "] has been registered for Course [" + courseId + "]";
            else
                return "Reservation failed: data layer error";

        } catch (NullDataException e) {
            e.printStackTrace();
            return "Reservation failed: internal data error";
        }
    }

    @Override
    public ArrayList<Reservation> getAllReservations() throws RemoteException, NullDataException {
        return data.getAllReservations();
    }

    @Override
    public String login(String id, String inputPw) throws RemoteException {
    	
    	
        // 비밀번호 존재 여부 확인
        if (!hasPassword(id)) {
            LogManager.writeLog(id, "login_failed(no_password)");
            return null;
        }
        
        
        // 저장된 암호화된 비밀번호 가져오기
        String storedHashedPw = data.getPassword(id);
        if (storedHashedPw == null || storedHashedPw.trim().isEmpty()) {
            LogManager.writeLog(id, "login_failed(empty_password)");
            return null;
        }
        // 입력 비밀번호 해시화
        String inputHashedPw = hashPassword(inputPw);
        
        // 중복 세션 검사
        for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
            if (entry.getValue().getUserId().equals(id)) {
                LogManager.writeLog(id, "login_blocked(duplicate_session=" + entry.getKey() + ")");
                return "ALREADY_LOGGED_IN";
            }
        }
        
        // 비밀번호 검증
        if (storedHashedPw.equals(inputHashedPw)) {
            // 로그인 성공 후 세션 발급
            String sessionId = UUID.randomUUID().toString();
            sessionMap.put(sessionId, new Session(sessionId, id));

            LogManager.writeLog(id, "login_success(session=" + sessionId + ")");
            System.out.println("로그인 성공: " + id + " (세션 ID: " + sessionId + ")");
            return sessionId;
        } else {
            LogManager.writeLog(id, "login_failed(wrong_pw)");
            return "WRONG_PASSWORD";
        }
    }
    
    // 세션 유효성 검사  
    @Override
    public boolean validateSession(String sessionId) throws RemoteException {
        Session s = sessionMap.get(sessionId);
        if (s == null) return false;
        if (s.isExpired()) {
            sessionMap.remove(sessionId);
            LogManager.writeLog(s.getUserId(), "session_expired");
            return false;
        }
        s.touch();
        return true;
    }

    @Override
    public void logout(String sessionId) throws RemoteException {
        Session s = sessionMap.remove(sessionId);
        if (s != null) {
            LogManager.writeLog(s.getUserId(), "logout(session=" + sessionId + ")");
        }
    }

    // 서버 비즈니스 로직: "비밀번호가 실제로 존재하느냐?" (공백/NULL은 없음으로 간주)
    @Override
    public boolean hasPassword(String id) throws RemoteException {
        String pw = data.getPassword(id);  // CRUD에서 암호화된 문자열 그대로 가져옴
        return (pw != null && !pw.trim().isEmpty());
    }

    // 비밀번호 등록
    @Override
    public void registerPassword(String id, String newPw) throws RemoteException {
        data.setPassword(id, newPw);
        LogManager.writeLog(id, "registerPassword");
    }

    // SHA-256 해시 함수 
    private String hashPassword(String pw) {
        if (pw == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(pw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }
}