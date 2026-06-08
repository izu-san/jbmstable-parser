package bms.table;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DifficultyTableParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesMinimalDifficultyTableHeader() throws IOException {
        Path header = write("header.json",
                "{"
                        + "\"name\":\"Test Table\","
                        + "\"symbol\":\"TT\","
                        + "\"tag\":\"#tt\","
                        + "\"level_order\":[\"1\",\"2\"],"
                        + "\"data_url\":\"data.json\","
                        + "\"mode\":\"7K\""
                        + "}");

        DifficultyTable table = new DifficultyTable();
        new DifficultyTableParser().decodeJSONTableHeader(table, header.toFile());

        assertEquals("Test Table", table.getName());
        assertEquals("TT", table.getID());
        assertEquals("#tt", table.getTag());
        assertArrayEquals(new String[] { "1", "2" }, table.getLevelDescription());
        assertArrayEquals(new String[] { "data.json" }, table.getDataURL());
        assertEquals("7K", table.getMode());
    }

    @Test
    void parsesTableElementsAndKeepsBeatorajaFields() throws IOException {
        Path data = write("data.json",
                "[{"
                        + "\"level\":\"1\","
                        + "\"title\":\"Song A\","
                        + "\"artist\":\"Artist A\","
                        + "\"md5\":\"0123456789abcdef0123456789abcdef\","
                        + "\"sha256\":\"0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\","
                        + "\"url\":\"https://example.invalid/song-a\","
                        + "\"mode\":\"7K\""
                        + "}]");

        DifficultyTable table = new DifficultyTable();
        new DifficultyTableParser().decodeJSONTableData(table, data.toFile());

        DifficultyTableElement[] elements = table.getElements();
        assertEquals(1, elements.length);
        assertEquals("1", elements[0].getLevel());
        assertEquals("Song A", elements[0].getTitle());
        assertEquals("Artist A", elements[0].getArtist());
        assertEquals("0123456789abcdef0123456789abcdef", elements[0].getMD5());
        assertEquals("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef", elements[0].getSHA256());
        assertEquals("https://example.invalid/song-a", elements[0].getURL());
        assertEquals("7K", elements[0].getMode());
        assertArrayEquals(new String[] { "1" }, table.getLevelDescription());
    }

    @Test
    void parsesCourseDefinitionsWithChartObjects() throws IOException {
        Path header = write("header.json",
                "{"
                        + "\"name\":\"Course Table\","
                        + "\"symbol\":\"CT\","
                        + "\"data_url\":\"data.json\","
                        + "\"course\":[[{"
                        + "\"name\":\"First\","
                        + "\"style\":\"gold\","
                        + "\"constraint\":[\"grade_mirror\",\"gauge_lr2\"],"
                        + "\"charts\":[{"
                        + "\"title\":\"Course Song\","
                        + "\"md5\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\","
                        + "\"sha256\":\"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\","
                        + "\"url\":\"https://example.invalid/course\","
                        + "\"level\":\"3\""
                        + "}],"
                        + "\"trophy\":[{\"name\":\"Clear\",\"missrate\":10.5,\"scorerate\":80.0,\"style\":\"silver\"}]"
                        + "}]]"
                        + "}");

        DifficultyTable table = new DifficultyTable();
        new DifficultyTableParser().decodeJSONTableHeader(table, header.toFile());

        assertEquals(1, table.getCourse().length);
        assertEquals(1, table.getCourse()[0].length);
        Course course = table.getCourse()[0][0];
        assertEquals("First", course.getName());
        assertEquals("gold", course.getStyle());
        assertArrayEquals(new String[] { "grade_mirror", "gauge_lr2" }, course.getConstraint());
        assertEquals(1, course.getCharts().length);
        assertEquals("Course Song", course.getCharts()[0].getTitle());
        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", course.getCharts()[0].getMD5());
        assertEquals(1, course.getTrophy().length);
        assertEquals("Clear", course.getTrophy()[0].getName());
        assertEquals(10.5, course.getTrophy()[0].getMissrate());
        assertEquals(80.0, course.getTrophy()[0].getScorerate());
    }

    @Test
    void detectsHtmlHeaderAndAlternateHeader() throws IOException {
        Path header = write("table.html",
                "<html><head>\n"
                        + "<meta name=\"bmstable\" content=\"header.json\">\n"
                        + "<meta name=\"bmstable-alt\" content=\"alternate.json\">\n"
                        + "</head><body></body></html>\n");

        DifficultyTableParser parser = new DifficultyTableParser();
        String url = header.toUri().toURL().toExternalForm();

        assertTrue(parser.containsHeader(url));
        assertEquals("alternate.json", parser.getAlternateBMSTableURL(url));
    }

    private Path write(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
