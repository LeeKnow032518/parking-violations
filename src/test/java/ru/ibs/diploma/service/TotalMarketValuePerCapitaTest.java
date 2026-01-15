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
import ru.ibs.diploma.cache.PropertyAnswer;
import ru.ibs.diploma.data.Field;
import ru.ibs.diploma.data.Properties;
import ru.ibs.diploma.datamanagement.ReadPopulation;
import ru.ibs.diploma.datamanagement.ReadProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TotalMarketValuePerCapitaTest {

    @InjectMocks
    private AnalyseService analyseService;

    @Mock
    private CachedAnswerInterface answers;

    @Mock
    private ReadPopulation readPopulation;

    @Mock
    private ReadProperties readProperties;

    List<Properties> properties = new ArrayList<>();
    Map<String, BigDecimal> population = new HashMap<>();

    @BeforeEach
    void setUp(){
        population.put("19102", BigDecimal.valueOf(1000));
        population.put("19103", BigDecimal.valueOf(2000));
        population.put("19104", BigDecimal.valueOf(0));
        population.put("19106", BigDecimal.valueOf(1));
        population.put("10001", BigDecimal.valueOf(1500));

        // avg = 90
        properties.add(new Properties(new BigDecimal("25000"), null, "19102"));
        properties.add(new Properties(new BigDecimal("35000"), null, "19102"));
        properties.add(new Properties(new BigDecimal("30000"), null, "19102"));

        // avg = 20
        properties.add(new Properties(new BigDecimal("40000"), null, "19103"));

        // avg = 0
        properties.add(new Properties(null, null, "19104"));

        // avg = 10
        properties.add(new Properties(new BigDecimal("10.99"), null, "19106"));

    }

    @Test
    void whenTotalMarketValuePerCapita_thenShouldReturnAnswer() throws Exception{
        // given
        when(answers.searchCache(eq(5))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        when(readPopulation.readFile()).thenReturn(population);
        doNothing().when(answers).cacheAnswer(eq(5), any());

        Answer expected = new FirstAnswer(new BigDecimal(90));

        // when
        Answer result = analyseService.totalMarketValuePerCapita("19102");

        // then
        assertEquals(expected, result);
        verify(answers).cacheAnswer(anyInt(), any());
    }

    @Test
    void whenTotalMarketValuePerCapita_thenShouldReturnCachedAnswer()throws Exception {
        // given
        FirstAnswer expected = new FirstAnswer(new BigDecimal(90));
        Map<String, FirstAnswer> cachedAnswers = new HashMap<>();
        cachedAnswers.put("19102", expected);
        PropertyAnswer propertyAnswer = new PropertyAnswer(cachedAnswers);

        when(answers.searchCache(eq(5))).thenReturn(propertyAnswer);

        // when
        Answer result = analyseService.totalMarketValuePerCapita("19102");

        //then
        assertEquals(expected, result);

        verify(readProperties, never()).readFile(Properties.class);
        verify(readPopulation, never()).readFile();
        verify(answers, never()).cacheAnswer(anyInt(), any());
    }

    @Test
    void givenAnswerIsNotCached_whenTotalMarketValuePerCapita_thenShouldReturnCachedAnswer() throws Exception{
        // given
        FirstAnswer cached = new FirstAnswer(new BigDecimal(30000));
        Map<String, FirstAnswer> cachedAnswers = new HashMap<>();
        cachedAnswers.put("19102", cached);
        PropertyAnswer propertyAnswer = new PropertyAnswer(cachedAnswers);

        when(answers.searchCache(eq(5))).thenReturn(propertyAnswer);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        when(readPopulation.readFile()).thenReturn(population);
        doNothing().when(answers).cacheAnswer(eq(5), any());

        Answer expected = new FirstAnswer(new BigDecimal(20));

        //when
        Answer result = analyseService.totalMarketValuePerCapita("19103");

        assertEquals(expected, result);
    }

    @Test
    void givenEmptyMarketValueField_whenTotalMarketValuePerCapita_thenShouldReturnZero() throws Exception{
        //given
        when((answers.searchCache(eq(5)))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        when(readPopulation.readFile()).thenReturn(population);
        doNothing().when(answers).cacheAnswer(eq(5), any());

        Answer expected = new FirstAnswer(new BigDecimal(0));

        //when
        Answer result = analyseService.totalMarketValuePerCapita("19104");

        assertEquals(expected, result);
    }

    @Test
    void givenEmptyCode_whenTotalMarketValuePerCapita_thenShouldReturnZero() throws Exception {
        //given
        when((answers.searchCache(eq(5)))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        when(readPopulation.readFile()).thenReturn(population);
        doNothing().when(answers).cacheAnswer(eq(5), any());

        Answer expected = new FirstAnswer(new BigDecimal(0));

        //when
        Answer result = analyseService.totalMarketValuePerCapita("19105");

        assertEquals(expected, result);
        verify(answers).cacheAnswer(anyInt(), any());
    }

    @Test
    void whenTotalMarketValuePerCapita_thenShouldReturnRoundedAnswer() throws Exception{
        //given
        when((answers.searchCache(eq(5)))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        when(readPopulation.readFile()).thenReturn(population);
        doNothing().when(answers).cacheAnswer(eq(5), any());

        Answer expected = new FirstAnswer(new BigDecimal(10));

        //when
        Answer result = analyseService.totalMarketValuePerCapita("19106");

        assertEquals(expected, result);
        verify(answers).cacheAnswer(anyInt(), any());
    }
}
