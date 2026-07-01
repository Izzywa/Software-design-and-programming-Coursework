import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import utils.LogToCsv;

/**
 * Tests for {@link LogToCsv} CSV conversion and file saving.
 */
public class LogToCsvTest {
    @Test
    public void testConvertToCsv() {
        String[] data = { "value1", "value2", "value3" };

        String expected = "value1,value2,value3";

        String actual = LogToCsv.convertToCsv(data);

        assertEquals(
                expected, actual,
                "Expected: " + expected + ", but got: " + actual);
    }

    @Test
    public void testSaveToCsv() {
        String filename = "test_output.csv";
        String[] headers = { "Header1", "Header2", "Header3" };

        List<String[]> data = List.of(
                new String[] {"row1col1", "row1col2", "row1col3"},
                new String[] {"row2col1", "row2col2", "row2col3"});

        LogToCsv.saveToCsv(filename, headers, data);

        assertTrue(new File("src/test/resources/benchmark/" + filename)
                .exists(), "CSV file was not created.");
    }
}
