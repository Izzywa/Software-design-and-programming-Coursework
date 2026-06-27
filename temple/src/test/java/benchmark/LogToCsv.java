package benchmark;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility class for logging data to a CSV file used in benchmarks. Provides methods to save headers
 * and rows to a CSV file and to convert a String[] to a CSV-formatted line.
 *
 * Reference: <a href="https://www.baeldung.com/java-csv">baeldung.com</a>
 */
public class LogToCsv {
  /** Base directory for benchmark CSV output files. */
  private static final String BASE_PATH = "benchmark/";

  /**
   * Saves the given data to the benchmark/ folder as a CSV file.
   *
   * @param filename the CSV file name
   * @param headers the CSV header values
   * @param dataLines the CSV rows
   */
  public static void saveToCsv(final String filename, final String[] headers,
      final List<String[]> dataLines) {

    File logDir = new File(BASE_PATH);
    if (!logDir.exists())
      logDir.mkdirs();

    File csvOutputFile = new File(BASE_PATH + filename);
    try (PrintWriter pw = new PrintWriter(csvOutputFile, UTF_8)) {
      String headerLine = String.join(",", headers);
      pw.println(headerLine);

      dataLines.stream().map(LogToCsv::convertToCsv).forEach(pw::println);
    } catch (Exception e) {
      System.err.println("Error writing to CSV file: " + e.getMessage());
    }
  }

  /**
   * Converts a String array into a CSV-formatted line.
   *
   * @param data the values to convert
   * @return a comma-separated CSV line
   */
  public static String convertToCsv(String[] data) {
    return String.join(",", data);
  }

}
