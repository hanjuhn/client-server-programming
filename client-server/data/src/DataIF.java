import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface DataIF extends Remote {
    // 모든 학생 데이터 반환
    ArrayList<Student> getAllStudentData() throws RemoteException, NullDataException;

    // 모든 과목 데이터 반환
    ArrayList<Course> getAllCourseData() throws RemoteException;
    
    boolean addStudent(String studentInfo) throws RemoteException;
    boolean deleteStudent(String studentId) throws RemoteException;
    boolean addCourse(String courseInfo) throws RemoteException;
    boolean deleteCourse(String courseId) throws RemoteException;
    
    boolean makeReservation(String studentId, String courseId) throws RemoteException;
    ArrayList<Reservation> getAllReservations() throws RemoteException, NullDataException;
    
    String getPassword(String id) throws RemoteException;
    void setPassword(String id, String password) throws RemoteException;
    boolean hasPassword(String id) throws RemoteException;
    void addAccount(String id) throws RemoteException;  
}
