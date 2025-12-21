package Framework;

import java.io.EOFException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public abstract class CommonFilterImpl implements CommonFilter {

	// array list로  
    protected PipedInputStream in = new PipedInputStream();
    protected PipedOutputStream out;

    // port num 명시하여 연결 
    public void connectOutputTo(CommonFilter nextFilter) throws IOException {
        // 새로운 out 스트림을 만들어 다음 필터의 in과 직접 연결
        this.out = new PipedOutputStream(nextFilter.getPipedInputStream());
    }

    public void connectInputTo(CommonFilter previousFilter) throws IOException {
        this.in = new PipedInputStream(previousFilter.getPipedOutputStream());
    }

    public PipedInputStream getPipedInputStream() { //int port num 해서 파라미터가 들어가야 함 
        return in;
    }

    public PipedOutputStream getPipedOutputStream() {
        return out;
    }

    public abstract boolean specificComputationForFilter() throws IOException;

    @Override
    public void run() {
        try {
            specificComputationForFilter();
        } catch (IOException e) {
            if (!(e instanceof EOFException)) e.printStackTrace();
        } finally {
            closePorts();
        }
    }

    private void closePorts() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}