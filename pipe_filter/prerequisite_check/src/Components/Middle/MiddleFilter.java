/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University.
 */
package Components.Middle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Framework.CommonFilterImpl;

// 입력 스트림에서 학생 정보 라인을 읽어 각 학생이 수강한 과목들에 대해 coursesFile에 정의된 선수과목을 모두 들었는지 검사

public class MiddleFilter extends CommonFilterImpl {
    private final Map<String, Set<String>> coursePrerequisites;

    public MiddleFilter(String coursesFile) throws IOException {
        this.coursePrerequisites = loadCoursePrerequisites(coursesFile);
    }

    // 입력 스트림(in)을 한 줄씩 읽어 처리
    @Override
    public boolean specificComputationForFilter() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byteRead;
        boolean lastWasCR = false;

        while (true) {
            byteRead = in.read();

            if (byteRead == -1) {
                if (buffer.size() > 0) {
                    processLine(buffer.toByteArray());
                }
                return true;
            }

            if (byteRead == '\n') {
                if (!lastWasCR) {
                    processLine(buffer.toByteArray());
                }
                buffer.reset();
                lastWasCR = false;
                continue;
            }

            if (byteRead == '\r') {
                processLine(buffer.toByteArray());
                buffer.reset();
                lastWasCR = true;
                continue;
            }

            buffer.write(byteRead);
            lastWasCR = false;
        }
    }
    
    // 한 줄을 실제로 분석
    private void processLine(byte[] rawLine) throws IOException {
        if (rawLine == null || rawLine.length == 0) {
            return;
        }

        String line = new String(rawLine, StandardCharsets.UTF_8).trim();
        if (line.isEmpty()) {
            return;
        }

        // 공백 기준 토큰 분리
        String[] tokens = line.split("\\s+");
        // 최소 4개 미만이면 과목 정보 없음으로 불만족 처리
        if (tokens.length < 4) {
            writeResult(false, line);
            return;
        }

        // index 4 이후 수강 완료 과목들
        Set<String> completedCourses = new HashSet<>();
        for (int i = 4; i < tokens.length; i++) {
            completedCourses.add(tokens[i]);
        }

        // 선수과목 충족 여부 검사
        boolean meetsAllPrerequisites = true;
        for (String courseId : completedCourses) {
        	// 해당 과목의 선수과목 조회
            Set<String> prerequisites = coursePrerequisites.get(courseId);
            // 선수과목 없음 -> 조건 충족
            if (prerequisites == null || prerequisites.isEmpty()) {
                continue;
            }

            // 선수과목이 모두 completedCourses 안에 있어야 함
            if (!completedCourses.containsAll(prerequisites)) {
                meetsAllPrerequisites = false;
                break;
            }
        }

        // 결과 기록
        writeResult(meetsAllPrerequisites, line);
    }
    

    // 결과를 out 스트림에 기록
    private void writeResult(boolean satisfied, String line) throws IOException {
        String result = (satisfied ? "SAT:" : "UNSAT:") + line + "\n";
        byte[] outBytes = result.getBytes(StandardCharsets.UTF_8);
        for (byte b : outBytes) {
            out.write(b);
        }
    }

    // 선수과목 파일을 읽어 Map 생성
    private Map<String, Set<String>> loadCoursePrerequisites(String coursesFile) throws IOException {
        Map<String, Set<String>> prerequisites = new HashMap<>();
        Path path = Paths.get(coursesFile);

        // 텍스트 파일을 라인 단위로 읽음
        for (String rawLine : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] tokens = line.split("\\s+");
            if (tokens.length == 0) {
                continue;
            }

            // 첫 번째 토큰 = courseId
            String courseId = tokens[0];
            // index 3 이후 선수과목 목록
            Set<String> requiredCourses = new HashSet<>();
            for (int i = 3; i < tokens.length; i++) {
                requiredCourses.add(tokens[i]);
            }
            prerequisites.put(courseId, requiredCourses);
        }

        return prerequisites;
    }
}
