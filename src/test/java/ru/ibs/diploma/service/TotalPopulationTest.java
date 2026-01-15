package ru.ibs.diploma.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.cache.Answer;
import ru.ibs.diploma.cache.CachedAnswerInterface;
import ru.ibs.diploma.cache.FirstAnswer;
import ru.ibs.diploma.datamanagement.ReadParking;
import ru.ibs.diploma.datamanagement.ReadPopulation;
import ru.ibs.diploma.datamanagement.ReadProperties;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TotalPopulationTest {

    @Mock
    private CachedAnswerInterface answers;

    @Mock
    private ReadPopulation readPopulation;

    @InjectMocks
    private AnalyseService analyseService;

    private Map<String, BigDecimal> population = new HashMap<>();

    @BeforeEach
    void setUp(){
        population.put("19102", BigDecimal.valueOf(1));
        population.put("19103", BigDecimal.valueOf(1));
        population.put("19104", BigDecimal.valueOf(1));
        population.put("19106", BigDecimal.valueOf(1));
        population.put("19107", BigDecimal.valueOf(10));
    }

    @Test
    void whenTotalPopulation_thenShouldReturnLong() throws Exception {
        //when
        when(answers.searchCache(eq(1))).thenReturn(null);
        when(readPopulation.readFile()).thenReturn(population);
        doNothing().when(answers).cacheAnswer(eq(1), any());
        Answer expected = new FirstAnswer(new BigDecimal(14));

        Answer result = analyseService.totalPopulation();

        //then
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void whenTotalPopulation_thenShouldReturnCachedAnswer() throws Exception{
        // given
        Answer expected = new FirstAnswer(new BigDecimal(14));

        // when
        when(answers.searchCache(eq(1))).thenReturn(expected);

        Answer result = analyseService.totalPopulation();

        //then
        assertEquals(expected, result);
        verify(readPopulation, never()).readFile();
        verify(answers, never()).cacheAnswer(anyInt(), any());
    }

    @Test
    void givenEmptyPopulation_whenTotalPopulation_thenShouldReturnZero() throws Exception{
        //given
        Map<String, BigDecimal> emptyPopulation = new HashMap<>();
        when(answers.searchCache(eq(1))).thenReturn(null);
        when(readPopulation.readFile()).thenReturn(emptyPopulation);
        doNothing().when(answers).cacheAnswer(eq(1), any());
        Answer expected = new FirstAnswer(new BigDecimal(0));


        Answer result = analyseService.totalPopulation();
        //then
        assertEquals(expected, result);
    }
}
