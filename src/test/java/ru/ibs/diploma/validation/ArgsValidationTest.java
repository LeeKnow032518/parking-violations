package ru.ibs.diploma.validation;

import lombok.Locked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.logging.WriteLogService;
import ru.ibs.diploma.ui.UserChoice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class ArgsValidationTest {

    @InjectMocks
    private ArgsValidation argsValidation;

    @Mock
    private UserChoice userChoice;

    @Mock
    private WriteLogService writeLogService;

    @Mock
    private FileNames fileNames;

    @TempDir(cleanup = CleanupMode.NEVER)
    Path tempDir;

    @Test
    void givenStringArgs_whenValidateArgs_thenShouldNotThrow() throws IOException {
        //given
        Path parkingFile = tempDir.resolve("parking.csv");
        Path populationFile = tempDir.resolve("population.txt");
        Path propertiesFile = tempDir.resolve("properties.csv");
        Path logFile = tempDir.resolve("log.txt");

        Files.createFile(parkingFile);
        Files.createFile(populationFile);
        Files.createFile(propertiesFile);

        String[] args = {
                "csv",
                parkingFile.toString(),
                populationFile.toString(),
                propertiesFile.toString(),
                logFile.toString()
        };

        //when & then
        assertDoesNotThrow(() -> argsValidation.validateArgs(args));
        assertTrue(Files.exists(logFile));
    }

    @Test
    void givenWrongNumOfArgs_whenValidateArgs_thenShouldThrow(){
        //given
        String[] args = {"csv", "parking.csv"};

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidation.validateArgs(args));

        assertTrue(exception.getMessage().contains("Wrong number of args. Expected 5, received 2"));
    }

    @Test
    void givenWrongFirstArgument_whenValidateArgs_thenShouldThrow(){
        //given
        String[] args = {"txt", "parking.json", "population.txt", "properties.csv", "log"};

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidation.validateArgs(args));

        assertTrue(exception.getMessage().contains("Wrong file format. Expected \"csv\" or \"json\", but received txt"));
    }

    @Test
    void givenWrongSecondArgument_whenValidateArgs_thenShouldThrow(){
        //given
        String[] args = {"json", "parking.txt", "population.txt", "properties.csv", "log"};

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidation.validateArgs(args));

        assertTrue(exception.getMessage().contains("Wrong file extension. Expected json but received txt"));
    }

    @Test
    void givenNonExistentFile_whenValidateArgs_thenShouldThrow(){
        // Given
        String[] args = {"csv", "non-existent.csv", "population.txt", "properties.csv", "log"};

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> argsValidation.validateArgs(args)
        );

        assertTrue(exception.getMessage().contains("Cannot open file"));
    }

    @Test
    void givenFile_whenValidateFile_thenShouldNotThrow()throws Exception{
        //given
        Path parkingFile = tempDir.resolve("parking.csv");
        Files.createFile(parkingFile);

        //when & then
        assertDoesNotThrow(() -> argsValidation.validateFile(parkingFile.toString()));
    }

    @Test
    void givenNonExistentFile_whenValidateFile_thenShouldThrow() {
        // Given
        String fileName = "non-existent.txt";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> argsValidation.validateFile(fileName)
        );

        assertTrue(exception.getMessage().contains("Cannot open file"));
    }

    @Test
    void givenFileName_whenCreateLog_thenShouldCreateFileIfNotExists() throws IOException {
        // Given
        Path logFile = tempDir.resolve("app.log");
        assertFalse(Files.exists(logFile));

        // When
        argsValidation.createLog(logFile.toString());

        // Then
        assertTrue(Files.exists(logFile));
    }

    @Test
    void givenInvalidPath_whenCreateLog_thenShouldThrow() {
        // Given
        String invalidPath = "/forbidden/log.txt";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> argsValidation.createLog(invalidPath)
        );

        assertTrue(exception.getMessage().contains("Couldn't create log file " + invalidPath));
    }

}
