/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University.
 */
package Components.Sink;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import Framework.CommonFilterImpl;


// MiddleFilter 를 통해 SAT/UNSAT 가 붙은 결과를 입력 스트림으로 받고
// SAT → satisfiedOutputFile
// UNSAT → unsatisfiedOutputFile로 각각 저장하는 Sink 역할을 수행
public class SinkFilter extends CommonFilterImpl {
    private final String satisfiedOutputFile;
    private final String unsatisfiedOutputFile;

    public SinkFilter(String satisfiedOutputFile, String unsatisfiedOutputFile) {
        this.satisfiedOutputFile = satisfiedOutputFile;
        this.unsatisfiedOutputFile = unsatisfiedOutputFile;
    }

    @Override
    public boolean specificComputationForFilter() throws IOException {
    	// 결과를 기록할 두 writer 생성 (UTF-8)
        try (BufferedWriter satisfiedWriter = java.nio.file.Files.newBufferedWriter(
                java.nio.file.Paths.get(satisfiedOutputFile), StandardCharsets.UTF_8);
             BufferedWriter unsatisfiedWriter = java.nio.file.Files.newBufferedWriter(
                     java.nio.file.Paths.get(unsatisfiedOutputFile), StandardCharsets.UTF_8)) {

            StringBuilder lineBuffer = new StringBuilder();
            int byteRead;

            while (true) {
                byteRead = in.read();
                if (byteRead == -1) {
                    flushLine(lineBuffer, satisfiedWriter, unsatisfiedWriter);
                    System.out.print("::Filtering is finished; Output files are created.");
                    return true;
                }

                if (byteRead == '\n') {
                    flushLine(lineBuffer, satisfiedWriter, unsatisfiedWriter);
                } else if (byteRead == '\r') {
                    continue;
                } else {
                    lineBuffer.append((char) byteRead);
                }
            }
        }
    }

    private void flushLine(StringBuilder lineBuffer, BufferedWriter satisfiedWriter,
                           BufferedWriter unsatisfiedWriter) throws IOException {
        if (lineBuffer.length() == 0) {
            return;
        }

        String line = lineBuffer.toString().trim();
        lineBuffer.setLength(0);

        if (line.isEmpty()) {
            return;
        }

        if (line.startsWith("SAT:")) {
            satisfiedWriter.write(line.substring(4).trim());
            satisfiedWriter.newLine();
        } else if (line.startsWith("UNSAT:")) {
            unsatisfiedWriter.write(line.substring(6).trim());
            unsatisfiedWriter.newLine();
        }
    }
}
