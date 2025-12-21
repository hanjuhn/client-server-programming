package Components.Add;

import java.io.IOException;
import Framework.CommonFilterImpl;

public class AddFilter extends CommonFilterImpl {
    @Override
    public boolean specificComputationForFilter() throws IOException {
        byte[] buffer = new byte[256];
        int idx = 0;
        int byte_read;
        boolean lastWasCR = false;

        while (true) {
            byte_read = in.read();

            if (byte_read == -1) { // EOF
                if (idx > 0) processLine(buffer, idx);
                return true;
            }

            // \r\n (Windows) 대응
            if (byte_read == '\n' && lastWasCR) {
                lastWasCR = false;
                continue;
            }

            if (byte_read == '\r' || byte_read == '\n') {
                processLine(buffer, idx);
                idx = 0;
                lastWasCR = (byte_read == '\r');
            } else {
                buffer[idx++] = (byte) byte_read;
                lastWasCR = false;
            }
        }
    }

    private void processLine(byte[] buffer, int length) throws IOException {
        if (length <= 0) return;

        String line = new String(buffer, 0, length).trim();
        if (line.isEmpty()) return;

        String[] tokens = line.split("\\s+");
        if (tokens.length < 4) return;

        String dept = tokens[3].trim();

        boolean has12345 = false;
        boolean has23456 = false;

        for (int i = 4; i < tokens.length; i++) {
            // \r, \n 제거 후 비교
            String token = tokens[i].replaceAll("[\\r\\n]", "").trim();
            if (token.equals("12345")) has12345 = true;
            if (token.equals("23456")) has23456 = true;
        }

        if ("CS".equals(dept)) {
            StringBuilder sb = new StringBuilder(line);
            if (!has12345) sb.append(" 12345");
            if (!has23456) sb.append(" 23456");
            sb.append("\n");

            byte[] outBytes = sb.toString().getBytes();
            for (byte b : outBytes) out.write(b);
        } else {
            // CS가 아니면 그대로 내보냄
            line = line + "\n";
            byte[] outBytes = line.getBytes();
            for (byte b : outBytes) out.write(b);
        }
    }
}