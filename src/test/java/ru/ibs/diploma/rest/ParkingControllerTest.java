package ru.ibs.diploma.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ibs.diploma.cache.FirstAnswer;
import ru.ibs.diploma.data.Arguments;
import ru.ibs.diploma.data.Field;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.logging.WriteLogService;
import ru.ibs.diploma.service.AnalyseService;
import ru.ibs.diploma.validation.ArgsValidation;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ParkingController.class)
public class ParkingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AnalyseService analyseService;

    @MockitoBean
    private ArgsValidation argsValidation;

    @MockitoBean
    private WriteLogService writeLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FileNames fileNames;

    @Test
    @DisplayName("Test getting arguments")
    public void whenGetArguments_thenSuccessResponse() throws Exception {
        // when
        String body = "csv, parking.csv, population.txt, properties.csv, log";
        when(fileNames.getLogFile()).thenReturn("log");
        when(fileNames.getParkingFile()).thenReturn("parking.csv");
        when(fileNames.getPropertiesFile()).thenReturn("properties.csv");
        when(fileNames.getPopulationFile()).thenReturn("population.txt");
        when(fileNames.getParkingType()).thenReturn("csv");

        ResultActions result = mvc.perform(get("/parking/arguments"));
        // then

        result.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string(body));
    }

    @Test
    @DisplayName("Test getting empty arguments")
    public void whenGetArguments_thenNotFoundResponse() throws Exception {
        // when
        when(fileNames.getParkingFile()).thenReturn(null);
        mvc.perform(get("/parking/arguments"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(content().string("No arguments found."));
        // then

    }

    @Test
    @DisplayName("Test post and validate arguments")
    public void givenArguments_whenPostAndValidateArgs_thenSuccessResponse() throws Exception {
        // given
        Arguments args = Arguments.builder()
            .parkingFile("parking.csv").parkingFormat("csv")
            .propertiesFile("properties.txt").populationFile("population.csv")
            .logFile("log").build();

        // when
        ResultActions result= mvc.perform(post("/parking/arguments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(args)));

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(content().string("Arguments are correct. You can choose parameter"));
    }

    @Test
    @DisplayName("Test post and validate empty arguments")
    public void givenWrongArguments_whenPostAndValidateArgs_thenBadRequestResponse() throws Exception {
        // given
        Arguments args = Arguments.builder().build();

        doThrow(new IllegalArgumentException("No arguments provided. Expected: format data.csv output.csv config.csv log.txt"))
            .when(argsValidation).validateArgs(any(String[].class));

        // when
        ResultActions result = mvc.perform(post("/parking/arguments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(args)));

        // then
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(content().string("No arguments provided. Expected: format data.csv output.csv config.csv log.txt"));
    }

    @Test
    @DisplayName("Test getting questions list")
    public void whenGetQuestionsList_thenSuccessResponse() throws Exception {
        mvc.perform(get("/parking/questions"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("Test getting answer question by number")
    public void givenNumber_whenAnswerQuestionByNumber_thenSuccessResponse() throws Exception {
        when(analyseService.totalPopulation()).thenReturn(new FirstAnswer(new BigDecimal(14)));

        mvc.perform(get("/parking/questions/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("14"));
    }

    @Test
    @DisplayName("Test getting answer question by number with ZIP-code")
    public void givenNumberAndZip_whenAnswerQuestionByNumber_thenSuccessResponse() throws Exception {
        when(analyseService.averageProperties("12345", Field.MARKET_VALUE))
            .thenReturn(new FirstAnswer(new BigDecimal("10")));

        mvc.perform(get("/parking/questions/3?zip=12345"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("10"));
    }

    @Test
    @DisplayName("Test getting answer question by number without ZIP-code")
    public void givenNumberAndNoZip_whenAnswerQuestionByNumber_thenSuccessResponse() throws Exception {
        mvc.perform(get("/parking/questions/3"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(content().string("You should enter ZIP-code for this question"));
    }

    @Test
    @DisplayName("Test IoException handling")
    public void givenNumber_whenAnswerQuestionByNumber_thenInternalServerErrorResponse() throws Exception {
        // when
        when(analyseService.surpriseOption()).thenThrow(new IOException("couldn't open file"));

        // then

        mvc.perform(get("/parking/questions/6"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(content().string("The problem occurred: couldn't open file"));
    }

    @Test
    @DisplayName("Test unknown option for get answer question by number")
    public void givenWrongNumber_whenAnswerQuestionByNumber_thenBadRequestResponse() throws Exception {
        mvc.perform(get("/parking/questions/7"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(content().string("Unknown question, try choosing another one."));
    }
}
