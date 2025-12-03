package relead.relead_schoolmanagement.util;

import org.apache.commons.csv.*;
import org.springframework.web.multipart.MultipartFile;
import relead.relead_schoolmanagement.entities.Level;
import relead.relead_schoolmanagement.entities.Student;
import relead.relead_schoolmanagement.exceptions.AppExceptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Csv {

    public static final String TYPE = "text/csv";
    static final String[] HEADERS = { "Username", "Level" };

    // 1. Add a private constructor to hide the implicit public one
    private Csv() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean hasCSVFormat(MultipartFile file) {
        if (file == null) {
            return false;
        }

        String fileName = file.getOriginalFilename();

        return TYPE.equals(file.getContentType()) ||
                (fileName != null && fileName.endsWith(".csv"));
    }

    public static ByteArrayInputStream studentsToCSV(List<Student> students) {
        final CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setQuoteMode(QuoteMode.MINIMAL)
                .build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            for (Student student : students) {
                List<String> data = Arrays.asList(
                        student.getUsername(),
                        student.getLevel().name()
                );
                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            // 3. Throw a dedicated exception (On réutilise CsvImportException par simplicité ou on en crée une CsvExportException)
            throw new AppExceptions.CsvImportException("Fail to export data to CSV file: " + e.getMessage());
        }
    }

    public static List<Student> csvToStudents(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true).build())) {

            List<Student> students = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                // 4. Extract nested try block into a separate method
                Level level = parseLevel(csvRecord.get("Level"));

                Student student = new Student(
                        null,
                        csvRecord.get("Username"),
                        level
                );
                students.add(student);
            }

            return students;
        } catch (IOException e) {
            // 5. Throw a dedicated exception
            throw new AppExceptions.CsvImportException("Fail to parse CSV file: " + e.getMessage());
        }
    }

    // Helper method pour parser le Level proprement
    private static Level parseLevel(String levelStr) {
        if (levelStr == null || levelStr.isBlank()) {
            return Level.FRESHMAN;
        }
        try {
            return Level.valueOf(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Level.FRESHMAN;
        }
    }
}