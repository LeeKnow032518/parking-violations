package ru.ibs.diploma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.cache.Answer;
import ru.ibs.diploma.cache.CachedAnswerInterface;
import ru.ibs.diploma.cache.SecondAnswer;
import ru.ibs.diploma.data.Parking;
import ru.ibs.diploma.datamanagement.ReadParking;
import ru.ibs.diploma.datamanagement.ReadPopulation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TotalParkingFinesPerCapitaTest {

    @InjectMocks
    private AnalyseService analyseService;

    @Mock
    private CachedAnswerInterface answers;

    @Mock
    private ReadPopulation readPopulation;

    @Mock
    private ReadParking readParking;

    Map<String, BigDecimal> population = new HashMap<>();
    List<Parking> parking = new ArrayList<>();

    @BeforeEach
    void setUp(){
        population.put("19102", BigDecimal.valueOf(1000));
        population.put("19103", BigDecimal.valueOf(2000));
        population.put("19104", BigDecimal.valueOf(0));
        population.put("10001", BigDecimal.valueOf(1500));

        // total 300
        parking.add(new Parking(Instant.parse("2013-04-03T15:15:00Z"), 100, "METER EXPIRED CC", 1322731, "PA", 2905938, "19102"));
        parking.add(new Parking(Instant.parse("2013-04-03T07:35:00Z"), 150, "DOUBLE PARKED", 1322731, "PA", 2905939, "19102"));
        parking.add(new Parking(Instant.parse("2013-09-18T14:58:00Z"), 50, "EXPIRED INSPECTION", 1322731, "PA", 2905940, "19102"));

        // total 80
        parking.add(new Parking(Instant.parse("2013-09-23T13:58:00Z"), 40, "EXPIRED INSPECTION", 1322731, "PA", 2905941, "19103"));
        parking.add(new Parking(Instant.parse("2013-09-23T13:58:00Z"), 40, "EXPIRED INSPECTION", 1322731, "PA", 2905942, "19103"));

        // zero people
        parking.add(new Parking(Instant.parse("2013-01-11T13:31:00Z"), 30, "METER EXPIRED CC", 1199878, "PA", 2905943, "19104"));

        //NY not PA
        parking.add(new Parking(Instant.parse("2013-09-23T13:58:00Z"), 400, "EXPIRED INSPECTION", 1322731, "NY", 2905944, "10001"));

        // not in population
        parking.add(new Parking(Instant.parse("2013-09-23T13:58:00Z"), 0, "EXPIRED INSPECTION", 1322731, "PA", 2905945, "19106"));
        parking.add(new Parking(Instant.parse("2013-09-23T13:58:00Z"), 0, "EXPIRED INSPECTION", 1322731, "PA", 2905946, "19106"));
    }

    @Test
    void whenTotalParkingFinesPerCapita_thenShouldReturnAnswer() throws Exception{
        //given
        Map<String, BigDecimal> expectedMap = new TreeMap<>();
        expectedMap.put("19102", new BigDecimal("0.3000"));
        expectedMap.put("19103", new BigDecimal("0.0400"));
        Answer expected = new SecondAnswer(expectedMap);

        when(answers.searchCache(eq(2))).thenReturn(null);
        when(readPopulation.readFile()).thenReturn(population);
        when(readParking.readFile(Parking.class)).thenReturn(parking);
        doNothing().when(answers).cacheAnswer(eq(2), any());

        //when
        Answer result = analyseService.totalParkingFinesPerCapita();

        //then
        assertEquals(expected, result);
        verify(answers).cacheAnswer(eq(2), any());
    }

    @Test
    void whenTotalParkingFinesPerCapita_thenShouldReturnCachedAnswer()throws  Exception{
        //given
        Map<String, BigDecimal> expectedMap = new TreeMap<>();
        expectedMap.put("19102", new BigDecimal("0.3000"));
        expectedMap.put("19103", new BigDecimal("0.0400"));
        Answer expected = new SecondAnswer(expectedMap);

        when(answers.searchCache(eq(2))).thenReturn(expected);

        //when
        Answer result = analyseService.totalParkingFinesPerCapita();

        //then
        assertEquals(expected, result);

        verify(readPopulation, never()).readFile();
        verify(readParking, never()).readFile(any());
        verify(answers, never()).cacheAnswer(anyInt(), any());
    }

    @Test
    void givenEmptyPopulation_whenTotalParkingFinesPerCapita_thenShouldReturnZero() throws Exception{
        //given
        Map<String, BigDecimal> expectedMap = new TreeMap<>();
        Answer expected = new SecondAnswer(expectedMap);
        Map<String, BigDecimal> emptyPopulation = new HashMap<>();

        when(answers.searchCache(eq(2))).thenReturn(null);
        when(readPopulation.readFile()).thenReturn(emptyPopulation);
        when(readParking.readFile(Parking.class)).thenReturn(parking);
        doNothing().when(answers).cacheAnswer(eq(2), any());

        //when
        Answer result = analyseService.totalParkingFinesPerCapita();

        //then
        assertEquals(result, expected);
    }

    @Test
    void givenEmptyParking_whenTotalParkingFinesPerCapita_thenShouldReturnZero() throws Exception{
        //given
        Map<String, BigDecimal> expectedMap = new TreeMap<>();
        Answer expected = new SecondAnswer(expectedMap);
        List<Parking> emptyParking = new ArrayList<>();

        when(answers.searchCache(eq(2))).thenReturn(null);
        when(readPopulation.readFile()).thenReturn(population);
        when(readParking.readFile(Parking.class)).thenReturn(emptyParking);
        doNothing().when(answers).cacheAnswer(eq(2), any());

        //when
        Answer result = analyseService.totalParkingFinesPerCapita();

        //then
        assertEquals(result, expected);
    }
}
