package Framework;

import Components.Add.AddFilter;
import Components.Middle.MiddleFilter;
import Components.Sink.SinkFilter;
import Components.Source.SourceFilter;

public class LifeCycleManager {
    public static void main(String[] args) {
        try {
            // 필터 생성
            CommonFilter source = new SourceFilter("Students.txt");
            CommonFilter add = new AddFilter();
            CommonFilter middle = new MiddleFilter();
            CommonFilter sink = new SinkFilter("Output.txt");

            // 연결: Source → Add → Middle → Sink
            source.connectOutputTo(add);
            add.connectOutputTo(middle);
            middle.connectOutputTo(sink);

            // 스레드 생성
            Thread tSource = new Thread(source);
            Thread tAdd = new Thread(add);
            Thread tMiddle = new Thread(middle);
            Thread tSink = new Thread(sink);

            // 실행 순서: 소비자부터 생산자 순으로 실행해야 데이터가 흐름
            tAdd.start();
            tMiddle.start();
            tSink.start();
            tSource.start();

            // 모든 스레드 종료 대기
            tSource.join();
            tAdd.join();
            tMiddle.join();
            tSink.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}