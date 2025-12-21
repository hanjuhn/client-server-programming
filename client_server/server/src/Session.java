import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Duration;

public class Session implements Serializable {
    private static final long serialVersionUID = 1L;
	private final String sessionId;
    private final String userId;
    private final LocalDateTime createdAt;
    private LocalDateTime lastAccess;

    // 🔹 세션 만료 기준 (분 단위)
    private static final long IDLE_TIMEOUT_MINUTES = 30;    // 30분 동안 활동 없으면 만료
    private static final long ABSOLUTE_TIMEOUT_HOURS = 24;  // 로그인 후 24시간 지나면 무조건 만료

    public Session(String sessionId, String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.lastAccess = LocalDateTime.now();
    }

    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }

    public void touch() {
        this.lastAccess = LocalDateTime.now();
    }

    // 🔹 세션 만료 여부 판단
    public boolean isExpired() {
        long idleMinutes = Duration.between(lastAccess, LocalDateTime.now()).toMinutes();
        long totalHours = Duration.between(createdAt, LocalDateTime.now()).toHours();

        if (idleMinutes > IDLE_TIMEOUT_MINUTES) return true;  // 유휴 시간 초과
        if (totalHours > ABSOLUTE_TIMEOUT_HOURS) return true; // 절대 시간 초과
        return false;
    }

    @Override
    public String toString() {
        return "Session[" + sessionId + " / " + userId + 
               " / lastAccess=" + lastAccess + "]";
    }
}