package ru.ibs.diploma.datamanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.data.Parking;
import ru.ibs.diploma.data.SafeCast;
import ru.ibs.diploma.logging.WriteLogService;

import javax.print.attribute.standard.PrinterMessageFromOperator;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadParkingTest {

    @InjectMocks
    private ReadParking readParking;

    @Mock
    private WriteLogService writeLogService;

    @Mock
    private FileNames fileNames;

    @Mock
    private SafeCast safeCast;

    List<Parking> parking = new ArrayList<>();
    String parkingCsv = "src/test/resources/parking.csv";
    String parkingJson = "src/test/resources/parking.json";

    @BeforeEach
    void setUp(){
        parking.add(new Parking(Instant.parse("2013-04-03T15:15:00Z"), 36, "METER EXPIRED CC", 1322731, "PA", 2905938, "19104"));
        parking.add(new Parking(Instant.parse("2013-04-03T07:35:00Z"), 51, "DOUBLE PARKED", 1322731, "PA", 2905939, "19104"));
        parking.add(new Parking(Instant.parse("2013-09-18T14:58:00Z"), 41, "EXPIRED INSPECTION", 1322731, "PA", 2905940, "19104"));
        parking.add(new Parking(Instant.parse("2013-09-23T13:58:00Z"), 41, "EXPIRED INSPECTION", 1322731, "PA", 2905941, "19104"));
        parking.add(new Parking(Instant.parse("2013-01-11T13:31:00Z"), 36, "METER EXPIRED CC", 1199878, "PA", 2905942, "19103"));
    }

    @Test
    void readParkingCsv_ShouldReturnListOfParking(){
        // when
        try{
            doNothing().when(writeLogService).logFileEntry(anyString());
            when(fileNames.getParkingFile()).thenReturn(parkingCsv);
            for(Parking p : parking){
                String[] row = {p.timestamp().toString(), String.valueOf(p.moneyAmount()),
                                p.reason(), String.valueOf(p.carId()), p.state(),
                                String.valueOf(p.violationId()), p.postIndex()};

                when(safeCast.safeGetInstance(eq(row), eq(0))).thenReturn(p.timestamp());
                when(safeCast.safeGetInt(eq(row), eq(1))).thenReturn(p.moneyAmount());
                when(safeCast.safeGetString(eq(row), eq(2))).thenReturn(p.reason());
                when(safeCast.safeGetLong(eq(row), eq(3))).thenReturn(p.carId());
                when(safeCast.safeGetString(eq(row), eq(4))).thenReturn(p.state());
                when(safeCast.safeGetLong(eq(row), eq(5))).thenReturn(p.violationId());
                when(safeCast.safeGetString(eq(row), eq(6))).thenReturn(p.postIndex());
            }

            List<Parking> testReading = readParking.readCsvParking();

            //then
            assertEquals(testReading.size(), 5);
            for(Parking p : parking){
                assertTrue(testReading.contains(p));
            }


        }catch(IOException e){
            fail(e.getMessage());
        }
    }

    @Test
    void readParkingJson_ShouldReturnListOfParking(){
        try{
            //when
            doNothing().when(writeLogService).logFileEntry(anyString());
            when(fileNames.getParkingFile()).thenReturn(parkingJson);
            for(Parking p : parking) {
                String[] row = {p.timestamp().toString(), String.valueOf(p.moneyAmount()),
                        p.reason(), String.valueOf(p.carId()), p.state(),
                        String.valueOf(p.violationId()), p.postIndex()};

                when(safeCast.safeGetInstance(eq(row), eq(0))).thenReturn(p.timestamp());
                when(safeCast.safeGetInt(eq(row), eq(1))).thenReturn(p.moneyAmount());
                when(safeCast.safeGetString(eq(row), eq(2))).thenReturn(p.reason());
                when(safeCast.safeGetLong(eq(row), eq(3))).thenReturn(p.carId());
                when(safeCast.safeGetString(eq(row), eq(4))).thenReturn(p.state());
                when(safeCast.safeGetLong(eq(row), eq(5))).thenReturn(p.violationId());
                when(safeCast.safeGetString(eq(row), eq(6))).thenReturn(p.postIndex());
            }
            List<Parking> testReading = readParking.readJsonParking();

            //then
            assertEquals(testReading.size(), 5);
            for(Parking p : parking){
                assertTrue(testReading.contains(p));
            }


        }catch(IOException e){
            fail(e.getMessage());
        }
    }

    @Test
    void givenStringArrayParkingList_whenAddParking_thenAddToList(){
            // Given
            Instant timestamp = Instant.parse("2013-04-03T15:15:00Z");
            String[] line = new String[7];
            line[0] = "2013-04-03T15:15:00Z";
            line[1] = "36";
            line[2] = "METER EXPIRED CC";
            line[3] = "1322731";
            line[4] = "PA";
            line[5] = "2905938";
            line[6] = "19104";

            when(safeCast.safeGetInstance(eq(line), eq(0)))
                    .thenReturn(timestamp);
            when(safeCast.safeGetInt(eq(line), eq(1)))
                    .thenReturn(36);
            when(safeCast.safeGetString(eq(line), eq(2)))
                    .thenReturn("METER EXPIRED CC");
            when(safeCast.safeGetLong(eq(line), eq(3)))
                    .thenReturn(1322731L);
            when(safeCast.safeGetString(eq(line), eq(4)))
                    .thenReturn("PA");
            when(safeCast.safeGetLong(eq(line), eq(5)))
                    .thenReturn(2905938L);
            when(safeCast.safeGetString(eq(line), eq(6)))
                    .thenReturn("19104");

            // When
        List<Parking> result = new ArrayList<>();
        try {
            readParking.addParking(line, result);
        }catch (IOException e){
            fail(e.getMessage());
        }

            // Then
            assertEquals(1, result.size());
            Parking parking = result.get(0);

            assertEquals(timestamp, parking.timestamp());
            assertEquals(36, parking.moneyAmount());
            assertEquals("METER EXPIRED CC", parking.reason());
            assertEquals(1322731L, parking.carId());
            assertEquals("PA", parking.state());
            assertEquals(2905938L, parking.violationId());
            assertEquals("19104", parking.postIndex());
    }
}
