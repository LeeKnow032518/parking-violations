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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class AveragePropertiesTest {

    @InjectMocks
    private AnalyseService analyseService;

    @Mock
    private CachedAnswerInterface answers;

    @Mock
    private ReadProperties readProperties;

    List<Properties> properties = new ArrayList<>();

    @BeforeEach
    void setUp(){
        // avgMV = 30_000, avg = 90
        properties.add(new Properties(new BigDecimal("25000"), new BigDecimal("80"), "19102"));
        properties.add(new Properties(new BigDecimal("35000"), new BigDecimal("90"), "19102"));
        properties.add(new Properties(new BigDecimal("30000"), new BigDecimal("100"), "19102"));

        // avg = 25_000
        properties.add(new Properties(new BigDecimal("20000"), new BigDecimal("70"), "19103"));
        properties.add(new Properties(new BigDecimal("30000"), new BigDecimal("100"), "19103"));

        // avg = 0
        properties.add(new Properties(null, new BigDecimal("75"), "19104"));

        // skipped
        properties.add(new Properties(new BigDecimal("50000"), new BigDecimal("10.99"), "19106"));

        // avg = 60_000
        properties.add(new Properties(new BigDecimal("60000"), null, "19107"));
    }

    @Test
    void givenEnumMarketValue_whenAverageProperties_thenShouldReturnAnswer() throws Exception{
        //given
        Field field = Field.MARKET_VALUE;
        when((answers.searchCache(eq(field.getQuestionNumber())))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        doNothing().when(answers).cacheAnswer(eq(field.getQuestionNumber()), any());

        Answer expected = new FirstAnswer(new BigDecimal(30000));

        //when
        Answer result = analyseService.averageProperties("19102", field);

        assertEquals(expected, result);
        verify(answers).cacheAnswer(anyInt(), any());
    }

    @Test
    void givenEnumLivableArea_whenAverageProperties_thenShouldReturnAnswer() throws Exception{
        //given
        Field field = Field.LIVABLE_AREA;
        when((answers.searchCache(eq(field.getQuestionNumber())))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        doNothing().when(answers).cacheAnswer(eq(field.getQuestionNumber()), any());

        Answer expected = new FirstAnswer(new BigDecimal(90));

        //when
        Answer result = analyseService.averageProperties("19102", field);

        assertEquals(expected, result);
        verify(answers).cacheAnswer(anyInt(), any());
    }

    @Test
    void whenAverageProperties_thenShouldReturnCachedAnswer() throws Exception {
        // given
        Field field = Field.MARKET_VALUE;
        FirstAnswer expected = new FirstAnswer(new BigDecimal(30000));
        Map<String, FirstAnswer> cachedAnswers = new HashMap<>();
        cachedAnswers.put("19102", expected);
        PropertyAnswer propertyAnswer = new PropertyAnswer(cachedAnswers);

        when(answers.searchCache(eq(field.getQuestionNumber()))).thenReturn(propertyAnswer);

        // when
        Answer result = analyseService.averageProperties("19102", field);

        // then
        assertEquals(expected, result);

        verify(readProperties, never()).readFile(Properties.class);
        verify(answers, never()).cacheAnswer(anyInt(), any());
    }

    @Test
    void givenAnswerIsNotCached_whenAverageProperties_thenShouldReturnCachedAnswer() throws Exception {
        // given
        Field field = Field.MARKET_VALUE;
        FirstAnswer cached = new FirstAnswer(new BigDecimal(30000));
        Map<String, FirstAnswer> cachedAnswers = new HashMap<>();
        cachedAnswers.put("19102", cached);
        PropertyAnswer propertyAnswer = new PropertyAnswer(cachedAnswers);

        when(answers.searchCache(eq(field.getQuestionNumber()))).thenReturn(propertyAnswer);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        doNothing().when(answers).cacheAnswer(eq(field.getQuestionNumber()), any());

        Answer expected = new FirstAnswer(new BigDecimal(25000));

        //when
        Answer result = analyseService.averageProperties("19103", field);

        assertEquals(expected, result);
    }

    @Test
    void givenEmptyMarketValueField_whenAverageProperties_thenShouldReturnZero() throws Exception {
        //given
        Field field = Field.MARKET_VALUE;
        when((answers.searchCache(eq(field.getQuestionNumber())))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        doNothing().when(answers).cacheAnswer(eq(field.getQuestionNumber()), any());

        Answer expected = new FirstAnswer(new BigDecimal(0));

        //when
        Answer result = analyseService.averageProperties("19104", field);

        assertEquals(expected, result);
    }

    @Test
    void givenEmptyCode_whenAverageProperties_thenShouldReturnZero() throws Exception {
        //given
        Field field = Field.MARKET_VALUE;
        when((answers.searchCache(eq(field.getQuestionNumber())))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        doNothing().when(answers).cacheAnswer(eq(field.getQuestionNumber()), any());

        Answer expected = new FirstAnswer(new BigDecimal(0));

        //when
        Answer result = analyseService.averageProperties("19105", field);

        assertEquals(expected, result);
        verify(answers).cacheAnswer(anyInt(), any());
    }

    @Test
    void whenAverageProperties_thenShouldReturnRoundedAnswer() throws Exception{
        //given
        Field field = Field.LIVABLE_AREA;
        when((answers.searchCache(eq(field.getQuestionNumber())))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        doNothing().when(answers).cacheAnswer(eq(field.getQuestionNumber()), any());

        Answer expected = new FirstAnswer(new BigDecimal(10));

        //when
        Answer result = analyseService.averageProperties("19106", field);

        assertEquals(expected, result);
        verify(answers).cacheAnswer(anyInt(), any());
    }

    @Test
    void givenEmptyLivableAreaField_whenAverageProperties_thenShouldReturnZero() throws Exception {
        //given
        Field field = Field.LIVABLE_AREA;
        when((answers.searchCache(eq(field.getQuestionNumber())))).thenReturn(null);
        when(readProperties.readFile(Properties.class)).thenReturn(properties);
        doNothing().when(answers).cacheAnswer(eq(field.getQuestionNumber()), any());

        Answer expected = new FirstAnswer(new BigDecimal(0));

        //when
        Answer result = analyseService.averageProperties("19107", field);

        assertEquals(expected, result);
    }
}
