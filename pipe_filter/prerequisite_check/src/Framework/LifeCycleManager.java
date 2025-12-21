package Framework;

import Components.Middle.MiddleFilter;
import Components.Sink.SinkFilter;
import Components.Source.SourceFilter;

public class LifeCycleManager {
    public static void main(String[] args) {
        try {
            CommonFilter source = new SourceFilter("Students.txt");
            CommonFilter middle = new MiddleFilter("Courses.txt");
            CommonFilter sink = new SinkFilter("Output-1.txt", "Output-2.txt");

            source.connectOutputTo(middle);
            middle.connectOutputTo(sink);

            Thread tSource = new Thread(source);
            Thread tMiddle = new Thread(middle);
            Thread tSink = new Thread(sink);

            tSink.start();
            tMiddle.start();
            tSource.start();

            tSource.join();
            tMiddle.join();
            tSink.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}