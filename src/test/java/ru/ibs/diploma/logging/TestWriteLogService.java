package ru.ibs.diploma.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.data.FileNames;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TestWriteLogService {

    @InjectMocks
    private WriteLogService writeLogService;

    @TempDir
    Path tempDir;

    @Mock
    private FileNames fileNames;

    private Path logFilePath;

    @BeforeEach
    void setUp() {
        logFilePath = tempDir.resolve("test-log");

    }

    @Test
    void logChoice_shouldWriteTimestampAndChoice() throws IOException {
        // When
        when(fileNames.getLogFile()).thenReturn(logFilePath.toString());

        writeLogService.logChoice("3");

        // Then
        assertTrue(Files.exists(logFilePath));
        List<String> lines = Files.readAllLines(logFilePath);
        assertEquals(1, lines.size());
        assertEquals(2, lines.get(0).split(" ").length);
    }

    @Test
    void logEntry_shouldWriteAllArgsInOneLine() throws IOException {
        // Given
        String[] args = {"json", "input.json", "output.json", "config.json", "app.log"};

        // When
        when(fileNames.getLogFile()).thenReturn(logFilePath.toString());
        writeLogService.logEntry(args);

        // Then
        List<String> lines = Files.readAllLines(logFilePath);
        String line = lines.get(0);
        assertTrue(line.matches("\\d+ json input\\.json output\\.json config\\.json app\\.log"));
    }

    @Test
    void logFileEntry_shouldWriteFileName() throws IOException {
        // When
        when(fileNames.getLogFile()).thenReturn(logFilePath.toString());
        writeLogService.logFileEntry("properties.csv");

        // Then
        List<String> lines = Files.readAllLines(logFilePath);
        String line = lines.get(0);
        assertTrue(line.matches("\\d+ properties\\.csv"));
    }

    @Test
    void multipleCalls_shouldAppendToSameFile() throws IOException {
        // When
        when(fileNames.getLogFile()).thenReturn(logFilePath.toString());
        writeLogService.logChoice("1");
        writeLogService.logChoice("2");
        writeLogService.logChoice("3");

        // Then
        List<String> lines = Files.readAllLines(logFilePath);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).endsWith(" 1"));
        assertTrue(lines.get(1).endsWith(" 2"));
        assertTrue(lines.get(2).endsWith(" 3"));
    }

    @Test
    void logEntry_withEmptyArgs_shouldWriteEmptyLineAfterTimestamp() throws IOException {
        // When
        when(fileNames.getLogFile()).thenReturn(logFilePath.toString());
        writeLogService.logEntry(new String[]{});

        // Then
        List<String> lines = Files.readAllLines(logFilePath);
        String line = lines.get(0);
        assertTrue(line.matches("\\d+ "));
    }
}
