package Framework;

import Components.Middle.MiddleFilter;
import Components.Sink.SinkFilter;
import Components.Source.SourceFilter;
import Components.Delete.DeleteFilter;

public class LifeCycleManager {
    public static void main(String[] args) {
        try {
            // 필터 생성
            CommonFilter source = new SourceFilter("Students.txt");
            CommonFilter middle = new MiddleFilter();
            CommonFilter delete = new DeleteFilter();
            CommonFilter sink = new SinkFilter("Output.txt");

            // 연결: Source → Middle → Delete → Sink
            source.connectOutputTo(middle);
            middle.connectOutputTo(delete);
            delete.connectOutputTo(sink);

            // 스레드 생성
            Thread tSource = new Thread(source);
            Thread tMiddle = new Thread(middle);
            Thread tDelete = new Thread(delete);
            Thread tSink = new Thread(sink);

            // 실행 순서: 소비자부터 생산자 순으로 실행해야 데이터가 흐름
            tSink.start();
            tDelete.start();
            tMiddle.start();
            tSource.start();

            // 모든 스레드 종료 대기
            tSource.join();
            tMiddle.join();
            tDelete.join();
            tSink.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}