import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {

    private static final String LOG_FILE = "/Users/baehanjun/eclipse-workspace/Data/src/Log.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 동기화 메서드로 여러 클라이언트 동시 접근 시 충돌 방지
    public synchronized static void writeLog(String userId, String command) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String timeStamp = LocalDateTime.now().format(formatter);
            fw.write(String.format("[%s]  User:%s  Command:%s%n", timeStamp, userId, command));
        } catch (IOException e) {
            System.err.println("로그 파일 저장 오류: " + e.getMessage());
        }
    }
}
