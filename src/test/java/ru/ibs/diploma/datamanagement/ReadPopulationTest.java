package ru.ibs.diploma.datamanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.logging.WriteLogService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadPopulationTest {

    @InjectMocks
    private ReadPopulation readPopulation;

    @Mock
    private FileNames fileNames;

    @Mock
    private WriteLogService writeLogService;

    Map<String, BigDecimal> population = new HashMap<>();
    String populationFile = "src/test/resources/population.txt";

    @BeforeEach
    void setUp(){
        population.put("19102", BigDecimal.valueOf(4705));
        population.put("19103", BigDecimal.valueOf(21908));
        population.put("19104", BigDecimal.valueOf(51808));
        population.put("19106", BigDecimal.valueOf(11740));
        population.put("19107", BigDecimal.valueOf(14875));
    }

    @Test
    void readFile_ShouldReturnMapOfStringBigDecimal(){
        try{
            //when
            doNothing().when(writeLogService).logFileEntry(anyString());
            when(fileNames.getPopulationFile()).thenReturn(populationFile);
            Map<String, BigDecimal> testReading = readPopulation.readFile();

            //then
            assertEquals(testReading.keySet().size(), 5);
            assertEquals(testReading.keySet(), population.keySet());
            for(String key : population.keySet()){
                assertEquals(testReading.get(key), population.get(key));
            }
        }catch (IOException e){
            fail(e.getMessage());
        }

    }
}
