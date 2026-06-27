package benchmark;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
    * Utility class for logging data to a CSV file.
    Reference: https://www.baeldung.com/java-csv
*/
public class LogToCsv {
    private static final String BASE_PATH = "benchmark/";

    public static void saveToCsv(
        final String fileneame, 
        final String[] headers, 
        final List<String[]> data
    )
    {
      File csvOutputFile = new File(BASE_PATH + fileneame);
      try (PrintWriter pw = new PrintWriter(csvOutputFile)) 
      {
          String headerLine = Stream.of(headers)
              .collect(Collectors.joining(","));
          pw.println(headerLine);

          List<String[]> dataLines = data;
            dataLines.stream()
            .map(LogToCsv::convertToCsv)
            .forEach(pw::println);
        }
        catch (Exception e) {
          System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public static String convertToCsv(String[] data) {
        return Stream.of(data)
            .collect(Collectors.joining(","));
    }

}
