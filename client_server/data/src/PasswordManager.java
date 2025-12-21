import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PasswordManager {

    private final Map<String, String> passwordMap = new HashMap<>();
    private final File file;

    public PasswordManager(String filePath) {
        this.file = new File(filePath);
        loadPasswords();
    }

    private void loadPasswords() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Password 파일 생성 실패: " + e.getMessage());
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] token = line.trim().split("\\s+");
                if (token.length == 2) {
                    passwordMap.put(token[0], token[1]);
                } else if (token.length == 1) { // 기존 계정만 존재하는 경우
                    passwordMap.put(token[0], "");
                }
            }
        } catch (IOException e) {
            System.out.println("Password 파일 로드 실패: " + e.getMessage());
        }
    }

    // 비밀번호 등록/갱신 시 암호화
    public synchronized void addPassword(String id, String pw) {
        String hashedPw = hashPassword(pw);
        passwordMap.put(id, hashedPw);
        save();
    }

    // 비밀번호 없이 계정만 추가
    public synchronized void addAccount(String id) {
        if (!passwordMap.containsKey(id)) {
            passwordMap.put(id, "");
            save();
        }
    }

    public synchronized String getPassword(String id) {
        return passwordMap.get(id);
    }

    public synchronized boolean exists(String id) {
        return passwordMap.containsKey(id);
    }

    public synchronized void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> e : passwordMap.entrySet()) {
                bw.write(e.getKey() + " " + e.getValue());
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("Password 저장 실패: " + e.getMessage());
        }
    }

    // SHA-256 해시 함수
    private String hashPassword(String pw) {
        if (pw == null || pw.isEmpty()) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(pw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 지원 안 함", e);
        }
    }
}