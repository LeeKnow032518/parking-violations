package ru.ibs.diploma.datamanagement;

import lombok.Locked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.data.Properties;
import ru.ibs.diploma.data.SafeCast;
import ru.ibs.diploma.logging.WriteLogService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadPropertiesTest {

    @InjectMocks
    private ReadProperties readProperties;

    @Mock
    private FileNames fileNames;

    @Mock
    private WriteLogService writeLogService;

    @Mock
    private SafeCast safeCast;

    List<Properties> properties = new ArrayList<>();
    String propertiesFile = "src/test/resources/properties.csv";

    @BeforeEach
    void setUp(){
        properties.add(new Properties(BigDecimal.valueOf(123), BigDecimal.valueOf(556), "19102"));
        properties.add(new Properties(BigDecimal.valueOf(223), BigDecimal.valueOf(656), "19103"));
        properties.add(new Properties(BigDecimal.valueOf(323), BigDecimal.valueOf(756), "19104"));
        properties.add(new Properties(BigDecimal.valueOf(423), BigDecimal.valueOf(856), "19105"));
        properties.add(new Properties(BigDecimal.valueOf(523), BigDecimal.valueOf(956), "19106"));
    }

    @Test
    void readFile_ShouldReturnListOfProperties(){
        try{
            //when
            doNothing().when(writeLogService).logFileEntry(anyString());
            when(fileNames.getPropertiesFile()).thenReturn(propertiesFile);
            for(Properties p: properties){
                String[] row = {p.MarketValue().toString(), "empty", "empty", p.TotalLivableArea().toString(), "empty", p.ZipCode()};
                when(safeCast.safeGetBigDecimal(eq(row), eq(0))).thenReturn(p.MarketValue());
                when(safeCast.safeGetBigDecimal(eq(row), eq(3))).thenReturn(p.TotalLivableArea());
                when(safeCast.safeGetString(eq(row), eq(5))).thenReturn(p.ZipCode());

                when(safeCast.safeSubstring(row[5], 5)).thenReturn(p.ZipCode());
            }

            List<Properties> testReading = readProperties.readFile(Properties.class);

            //then
            assertEquals(testReading.size(), 5);
            for(Properties p : testReading){
                assertTrue(properties.contains(p));
            }
        }catch (IOException e){
            fail(e.getMessage());
        }
    }

    @Test
    void givenStringAndRow_whenFindIndex_ThenReturnIntegerIndex() {
        //given
        String name = "market_value";
        String[] row = {"empty1", "market_value", "empty2", "maarket_value"};

        //when
        Integer index = readProperties.findIndex(name, row);

        //then
        assertNotNull(index);
        assertEquals(index, 1);
    }

    @Test
    void givenStringAndRow_whenFindIndex_ThenReturnNull(){
        //given
        String name = "market_value";
        String[] row = {"empty1", "empty2", "maarket_value"};

        //when
        Integer index = readProperties.findIndex(name, row);

        //then
        assertNull(index);
    }

    @Test
    void givenString_whenFindIndex__thenReturnNull(){
        //given
        String name = "market_value";
        String[] row = {};

        //when
        Integer index = readProperties.findIndex(name, row);

        //then
        assertNull(index);
    }
}
