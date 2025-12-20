/**
 * Copyright(c) 2019 All rights reserved by JU Consulting
 */
package Framework;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public interface CommonFilter extends Runnable{
	// 인터페이스 
	// 인풋, 아웃풋을 연결시킨다
    public void connectOutputTo(CommonFilter filter) throws IOException;
    public void connectInputTo(CommonFilter filter) throws IOException;
    // 들어오고 나가는 파이프의 값을 받는다 
    public PipedInputStream getPipedInputStream();
    public PipedOutputStream getPipedOutputStream();
}
