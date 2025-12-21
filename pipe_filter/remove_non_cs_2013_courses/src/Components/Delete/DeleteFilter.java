package Components.Delete;

import java.io.IOException;
import Framework.CommonFilterImpl;

public class DeleteFilter extends CommonFilterImpl {
    @Override
    public boolean specificComputationForFilter() throws IOException {
        byte[] buffer = new byte[256];
        int idx = 0;
        int byteRead;
        boolean lastWasCR = false;

        while (true) {
            byteRead = in.read();

            if (byteRead == -1) { // EOF
                if (idx > 0) processLine(buffer, idx);
                return true;
            }

            // \r\n (Windows) 대응
            if (byteRead == '\n' && lastWasCR) {
                lastWasCR = false;
                continue;
            }

            if (byteRead == '\r' || byteRead == '\n') {
                processLine(buffer, idx);
                idx = 0;
                lastWasCR = (byteRead == '\r');
            } else {
                buffer[idx++] = (byte) byteRead;
                lastWasCR = false;
            }
        }
    }

    private void processLine(byte[] buffer, int length) throws IOException {
        if (length <= 0) return;

        String line = new String(buffer, 0, length).trim();
        if (line.isEmpty()) return;

        String[] tokens = line.split("\\s+");
        if (tokens.length < 5) return;

        StringBuilder sb = new StringBuilder();
        sb.append(tokens[0]);
        for (int i = 1; i < 4 && i < tokens.length; i++) {
            sb.append(' ').append(tokens[i]);
        }

        for (int i = 4; i < tokens.length; i++) {
            String course = tokens[i].trim();
            if (course.equals("17651") || course.equals("17652")) {
                continue;
            }
            sb.append(' ').append(course);
        }

        sb.append("\n");
        byte[] outBytes = sb.toString().getBytes();
        for (byte b : outBytes) {
            out.write(b);
        }
    }
}