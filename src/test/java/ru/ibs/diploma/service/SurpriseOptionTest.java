package ru.ibs.diploma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.cache.*;
import ru.ibs.diploma.data.Field;
import ru.ibs.diploma.data.Parking;
import ru.ibs.diploma.data.Properties;
import ru.ibs.diploma.datamanagement.ReadParking;
import ru.ibs.diploma.datamanagement.ReadPopulation;
import ru.ibs.diploma.datamanagement.ReadProperties;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class SurpriseOptionTest {

    @InjectMocks
    private AnalyseService analyseService;

    @Mock
    private CachedAnswerInterface answers;

    @Mock
    private ReadPopulation readPopulation;

    @Mock
    private ReadProperties readProperties;

    @Mock
    private ReadParking readParking;

    List<Properties> properties = new ArrayList<>();
    Map<String, BigDecimal> population = new HashMap<>();
    List<Parking> parking = new ArrayList<>();

    @BeforeEach
    void setUp(){
        population = new TreeMap<>();
        population.put("19102", new BigDecimal("1000"));
        population.put("19103", new BigDecimal("2000"));
        population.put("19104", new BigDecimal("500"));
        population.put("10001", new BigDecimal("1500"));

        parking = Arrays.asList(
                // 19102: 3 -> 0.0030
                new Parking("19102", "PA", 100),
                new Parking("19102", "PA", 150),
                new Parking("19102", "PA", 50),

                // 19103: 4 -> 0.0020
                new Parking("19103", "PA", 200),
                new Parking("19103", "PA", 200),
                new Parking("19103", "PA", 200),
                new Parking("19103", "PA", 200),

                // 19104: 1 -> 0.0020
                new Parking("19104", "PA", 100),

                // 10001: 2 -> 0.0013
                new Parking("10001", "NY", 300),
                new Parking("10001", "NY", 300),

                // 19105: skip
                new Parking("19105", "PA", 100),

                // 19107: skip
                new Parking(null, "PA", 100)
        );

        properties = Arrays.asList(
                // 19102: 2 -> 30k
                new Properties(new BigDecimal("25000"), null, "19102"),
                new Properties(new BigDecimal("35000"), null, "19102"),

                // 19103: 1 -> 40k
                new Properties(new BigDecimal("40000"), null, "19103"),

                // 10001: 3 -> 20k
                new Properties(new BigDecimal("10000"), null, "10001"),
                new Properties(new BigDecimal("20000"), null, "10001"),
                new Properties(new BigDecimal("30000"), null, "10001"),

                // 19106: skip
                new Properties(null, null, "19106"));
    }

    @Test
    void whenSurpriseOption_thenShouldReturnAnswer() throws Exception {
        // given
        when(answers.searchCache(eq(6))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        when(readPopulation.readFile()).thenReturn(population);
        when(readParking.readFile(Parking.class)).thenReturn(parking);
        doNothing().when(answers).cacheAnswer(eq(6), any());
        doNothing().when(answers).cacheAnswer(eq(3), any());

        FirstAnswer avg19102 = new FirstAnswer(new BigDecimal("30000"));
        FirstAnswer avg19103 = new FirstAnswer(new BigDecimal("40000"));
        FirstAnswer avg19104 = new FirstAnswer(BigDecimal.ZERO);
        FirstAnswer avg10001 = new FirstAnswer(new BigDecimal("20000"));

        Field field = Field.MARKET_VALUE;

        Answer expected = getAnswer();

        //when
        Answer result = analyseService.surpriseOption();

        //then
        assertEquals(expected, result);
    }

    @Test
    void whenSurpriseOption_thenShouldReturnCachedAnswer()throws Exception{
        // given
        Answer expected = getAnswer();

        when(answers.searchCache(eq(6))).thenReturn(expected);

        // when
        Answer result = analyseService.surpriseOption();

        //then
        assertEquals(result, expected);

        verify(readParking, never()).readFile(Parking.class);
        verify(readProperties, never()).readFile(Properties.class);
        verify(readPopulation, never()).readFile();
        verify(answers, never()).cacheAnswer(anyInt(), any());
    }

    private static Answer getAnswer() {
        Map<String, Statistics> statisticsMap = new HashMap<>();
        statisticsMap.put("19102", new Statistics(new BigDecimal("30000"), new BigDecimal("0.0030")));
        statisticsMap.put("19103", new Statistics(new BigDecimal("40000"), new BigDecimal("0.0020")));
        statisticsMap.put("19104", new Statistics(new BigDecimal("0"), new BigDecimal("0.0020")));
        statisticsMap.put("10001", new Statistics(new BigDecimal("20000"), new BigDecimal("0.0013")));

        return new SurpriseAnswer(statisticsMap);
    }
}
