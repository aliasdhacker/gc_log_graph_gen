import javafx.scene.input.DataFormat;
import jdk.nashorn.internal.parser.DateParser;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class go {

    private static final String LOG_TO_ANALYZE_FILENAME = "catalina-gc.log";

    public static void main(String[] a) throws IOException {

        if (a.length < 1) {
            System.out.print("Usage: java go <year-of-log>");
            System.exit(-3);
        }
        StringBuilder output = new StringBuilder();
        File file = new File(LOG_TO_ANALYZE_FILENAME);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String value1 = "";
        ArrayList x = new ArrayList();
        ArrayList y = new ArrayList();

        ArrayList xSampleSet = new ArrayList();
        ArrayList ySampleSet = new ArrayList();
        String value2 = "";
        String line = "";
        int valuesLoaded = 0;
        while ((line = bufferedReader.readLine()) != null) {

            if (line != null && line.contains("T") && line.contains(":")
                    && line.contains(a[0])
                    && !line.contains("Parallel Time")
            ) {
                try {
                    int count = 0;
                    int indices = 0;
                    do {
                        indices = line.indexOf("-", indices + 1);
                        count++;
                    } while (count < 3 && indices != -1);
                    value1 = line
                            .substring(0, indices);


                } catch (Exception e) {

                }
                line = bufferedReader.readLine();

            }
            if (line != null && line.toLowerCase().contains("eden")) {
                try {
                    String[] s = line.split("Heap:");
                    line = s[1].split("->")[0];
                    value2 = line.split("\\(")[0];
                    value2 = value2.replace('M', ' ');
                    value2 = value2.trim();
                    value1 = value1.trim();
                    Instant instant = getInstant(value1.concat("Z")); //.replace('T',' '));
                    x.add(((Long) instant.toEpochMilli()).doubleValue() / 1000000);
//                    output.append(value1);
//                    output.append(",");
//                    output.append(value2);
                    y.add(Double.parseDouble(value2));
//                 output.append('\n');

                    if (valuesLoaded == 0 || valuesLoaded % 1000 == 0) {
                        double timeStampValue = ((Long) instant.toEpochMilli()).doubleValue() / 1000000000;
                        ;
                        double heapValue = Double.parseDouble(value2);

                        System.out.println("Loaded sampleset data - time: " + timeStampValue + " heap: " + heapValue);

                        xSampleSet.add(timeStampValue);
                        ySampleSet.add(heapValue);
                    }
                    valuesLoaded++;
                } catch (Exception e) {
                    System.err.print("Error:");
                    System.err.println(e);
                    System.exit(-2);
                }

//                if (valuesLoaded > 0 && valuesLoaded % 1000 == 0) {
//                    System.out.println("Loaded " + valuesLoaded + "values...");
//                }
            }


        }

//        FileWriter outputWriter = new FileWriter("output.csv");
//        outputWriter.write(output.toString());
//        outputWriter.flush();
//        outputWriter.close();
        XYChart chart = QuickChart.getChart("Memory Consumption Over Time", "EPOCH TIME", "MEGABYTES CONSUMED", "y(x)", xSampleSet, ySampleSet);
        new SwingWrapper(chart).displayChart();
    }

    /**
     * Text '2021-01-09T10:53:31.088'
     *
     * @param value1
     * @return
     */
    private static Instant getInstant(String value1) {
        return Instant.parse(value1);
    }
}
