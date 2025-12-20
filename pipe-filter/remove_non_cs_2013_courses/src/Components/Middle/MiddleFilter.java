/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University.
 */
package Components.Middle;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import Framework.CommonFilterImpl;

public class MiddleFilter extends CommonFilterImpl{
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

    private void processLine(byte[] rawLine) throws IOException {
        if (rawLine == null || rawLine.length == 0) {
            return;
        }

        String line = new String(rawLine).trim();
        if (line.isEmpty()) {
            return;
        }

        String[] tokens = line.split("\\s+");
        if (tokens.length < 5) {
            return;
        }

        String studentId = tokens[0];
        String dept = tokens[3];

        if (!studentId.startsWith("2013")) {
            return;
        }

        if ("CS".equalsIgnoreCase(dept)) {
            return;
        }

        boolean hasRestrictedCourse = false;
        for (int i = 4; i < tokens.length; i++) {
            String course = tokens[i];
            if ("17651".equals(course) || "17652".equals(course)) {
                hasRestrictedCourse = true;
                break;
            }
        }

        if (!hasRestrictedCourse) {
            return;
        }

        byte[] outBytes = (line + "\n").getBytes();
        for (byte b : outBytes) {
            out.write(b);
        }
    }
}
