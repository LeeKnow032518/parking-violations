package ru.ibs.diploma.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.ibs.diploma.data.Parking;
import ru.ibs.diploma.data.Properties;
import ru.ibs.diploma.datamanagement.ReadParking;
import ru.ibs.diploma.datamanagement.ReadPopulation;
import ru.ibs.diploma.datamanagement.ReadProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckTest {

    @InjectMocks
    private AnalyseService analyseService;

    @Mock
    private ReadPopulation readPopulation;

    @Mock
    private ReadProperties readProperties;

    @Mock
    private ReadParking readParking;

    @Test
    void checkPopulation_whenPopulationEmpty_shouldReadFromFile() throws IOException {
        // Given
        Map<String, BigDecimal> mockData = Map.of("19102", new BigDecimal("1000"));
        when(readPopulation.readFile()).thenReturn(mockData);

        // When
        analyseService.checkPopulation();

        // Then
        verify(readPopulation).readFile();
    }

    @Test
    void checkPopulation_whenPopulationNotEmpty_shouldNotRead() throws IOException {
        // Given
        Map<String, BigDecimal> mockData = Map.of("19102", new BigDecimal("1000"));

        ReflectionTestUtils.setField(analyseService, "population", mockData);
        // When
        analyseService.checkPopulation();

        // Then
        verify(readPopulation, never()).readFile();
    }

    @Test
    void checkParking_whenParkingEmpty_shouldReadFromFile() throws IOException {
        // Given
        List<Parking> mockData = new ArrayList<>();
        when(readParking.readFile(Parking.class)).thenReturn(mockData);

        // When
        analyseService.checkParking();

        // Then
        verify(readParking).readFile(Parking.class);
    }

    @Test
    void checkParking_whenParkingNotEmpty_shouldNotRead() throws IOException {
        // Given
        List<Parking> nonEmptyParking = List.of(
                new Parking("19102", "PA", 50)
        );

        ReflectionTestUtils.setField(analyseService, "parking", nonEmptyParking);
        // When
        analyseService.checkParking();

        // Then
        verify(readParking, never()).readFile(Parking.class);
    }

    @Test
    void checkProperties_whenPropertiesEmpty_shouldReadFromFile() throws IOException {
        // Given
        List<Properties> mockData = new ArrayList<>();
        when(readProperties.readFile(Properties.class)).thenReturn(mockData);

        // When
        analyseService.checkProperties();

        // Then
        verify(readProperties).readFile(Properties.class);
    }

    @Test
    void checkProperties_whenPropertiesNotEmpty_shouldNotRead() throws IOException {
        // Given
        List<Properties> mockData = List.of(
                new Properties(BigDecimal.valueOf(10), BigDecimal.valueOf(10), "19102")
        );

        ReflectionTestUtils.setField(analyseService, "properties", mockData);
        // When
        analyseService.checkProperties();

        // Then
        verify(readProperties, never()).readFile(Properties.class);
    }
}
