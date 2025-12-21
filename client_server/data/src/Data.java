import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Data extends UnicastRemoteObject implements DataIF {

    protected static StudentList studentList;
    protected static CourseList courseList;
    protected static ReservationList reservationList;
    protected static PasswordManager passwordManager;
    private static final long serialVersionUID = 1L;

    protected Data() throws RemoteException {
        super();
    }

    public static void main(String[] arg) throws FileNotFoundException, IOException {
        try {
            Data data = new Data();
            Naming.rebind("Data", data);
            System.out.println("Data is ready !!");

            studentList = new StudentList("Students.txt");
            courseList  = new CourseList("Courses.txt");
            reservationList = new ReservationList();
            passwordManager = new PasswordManager("/Users/baehanjun/eclipse-workspace/Data/src/Password.txt");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Student> getAllStudentData() throws RemoteException, NullDataException {
        LogManager.writeLog("SYSTEM", "getAllStudentData");
        return studentList.getAllStudentRecords();
    }

    @Override
    public ArrayList<Course> getAllCourseData() throws RemoteException {
        LogManager.writeLog("SYSTEM", "getAllCourseData");
        return courseList.getAllCourseRecords();
    }

    @Override
    public boolean addStudent(String studentInfo) throws RemoteException {
        LogManager.writeLog("ADMIN", "addStudent");
        return studentList.addStudentRecords(studentInfo);
    }
    
    @Override
    public void addAccount(String id) throws RemoteException {
        passwordManager.addAccount(id);
    }

    @Override
    public boolean deleteStudent(String studentId) throws RemoteException {
        LogManager.writeLog(studentId, "deleteStudent");
        return studentList.deleteStudentRecords(studentId);
    }

    @Override
    public boolean addCourse(String courseInfo) throws RemoteException {
        LogManager.writeLog("ADMIN", "addCourse");
        return courseList.addCourseRecords(courseInfo);
    }

    @Override
    public boolean deleteCourse(String courseId) throws RemoteException {
        LogManager.writeLog("ADMIN", "deleteCourse");
        return courseList.deleteCourseRecords(courseId);
    }

    @Override
    public boolean makeReservation(String studentId, String courseId) throws RemoteException {
        LogManager.writeLog(studentId, "makeReservation(" + courseId + ")");
        // Data는 단순히 reservationList에 추가만 수행
        return reservationList.addReservation(studentId, courseId);
    }

    @Override
    public ArrayList<Reservation> getAllReservations() throws RemoteException, NullDataException {
        LogManager.writeLog("SYSTEM", "getAllReservations");
        return reservationList.getAllReservations();
    }
    
    @Override
    public String getPassword(String id) throws RemoteException {
        return passwordManager.getPassword(id);
    }

    @Override
    public void setPassword(String id, String password) throws RemoteException {
        passwordManager.addPassword(id, password);
    }

    @Override
    public boolean hasPassword(String id) throws RemoteException {
        return passwordManager.exists(id);
    }

}