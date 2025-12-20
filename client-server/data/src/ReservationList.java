import java.util.ArrayList;

public class ReservationList {
    protected ArrayList<Reservation> vReservation;

    public ReservationList() {
        vReservation = new ArrayList<>();
    }

    public boolean addReservation(String studentId, String courseId) {
        // 중복 예약 방지
        for (Reservation r : vReservation) {
            if (r.match(studentId, courseId)) return false;
        }
        vReservation.add(new Reservation(studentId, courseId));
        return true;
    }

    public ArrayList<Reservation> getAllReservations() {
        return vReservation;
    }
}