package ru.ibs.diploma.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class SafeCastTest {

    @InjectMocks
    private SafeCast safeCast;

    @Test
    void givenStringIntIndex_whenSafeGetString_thenReturnString(){
        //given
        String[] line = {"string1", "string2"};
        int index = 0;

        //when
        String result = safeCast.safeGetString(line, index);

        //then
        assertEquals("string1", result);
    }

    @Test
    void givenWrongStringIntIndex_whenSafeGetString_thenReturnNull(){
        //given
        String[] line = {null, "string2"};
        int index = 0;

        //when
        String result = safeCast.safeGetString(line, index);

        //then
        assertNull(result);
    }

    @Test
    void givenStringWrongIntIndex_whenSafeGetString_thenReturnNull(){
        //given
        String[] line = {"string1", "string2"};
        int index = 4;

        //when
        String result = safeCast.safeGetString(line, index);

        //then
        assertNull(result);
    }

    @Test
    void givenEmptyStringIntIndex_whenSafeGetString_thenReturnNull(){
        //given
        String[] line = {"", "string2"};
        int index = 0;

        //when
        String result = safeCast.safeGetString(line, index);

        //then
        assertNull(result);
    }

    @Test
    void givenStringIntIndex_whenSafeGetLong_thenReturnLong(){
        //given
        String[] line = {"1234", "5678"};
        int index = 0;

        //when
        long result = safeCast.safeGetLong(line, index);

        //then
        assertEquals(1234L, result);
    }

    @Test
    void givenWrongStringIntIndex_whenSafeGetLong_thenReturnNull(){
        //given
        String[] line = {"dog", "5678"};
        int index = 0;

        //when
        long result = safeCast.safeGetLong(line, index);

        //then
        assertEquals(0, result);
    }

    @Test
    void givenStringWrongIntIndex_whenSafeGetLong_thenReturnNull(){
        //given
        String[] line = {"1234", "5678"};
        int index = 4;

        //when
        long result = safeCast.safeGetLong(line, index);

        //then
        assertEquals(0, result);
    }

    @Test
    void givenEmptyStringIntIndex_whenSafeGetLong_thenReturnLong(){
        //given
        String[] line = {"", "5678"};
        int index = 0;

        //when
        long result = safeCast.safeGetLong(line, index);

        //then
        assertEquals(0, result);
    }

    @Test
    void givenStringIntIndex_whenSafeGetInstance_thenReturnInstance(){
        //given
        String[] line = {"2013-04-03T15:15:00Z", "2014-04-03T15:15:00Z"};
        int index = 0;

        //when
        Instant result = safeCast.safeGetInstance(line, index);

        //then
        assertEquals(Instant.parse("2013-04-03T15:15:00Z"), result);
    }

    @Test
    void givenStringWrongIntIndex_whenSafeGetInstance_thenReturnNull(){
        //given
        String[] line = {"2013-04-03T15:15:00Z", "2014-04-03T15:15:00Z"};
        int index = 4;

        //when
        Instant result = safeCast.safeGetInstance(line, index);

        //then
        assertNull(result);
    }

    @Test
    void givenWrongStringIntIndex_whenSafeGetInstance_thenReturnNull(){
        //given
        String[] line = {"dog", "2014-04-03T15:15:00Z"};
        int index = 0;

        //when
        Instant result = safeCast.safeGetInstance(line, index);

        //then
        assertNull(result);
    }

    @Test
    void givenStringWrongIntIndex_whenSafeGetInt_thenReturnInt(){
        //given
        String[] line = {"18", "42"};
        int index = 0;

        //when
        int result = safeCast.safeGetInt(line, index);

        //then
        assertEquals(18, result);
    }

    @Test
    void givenWrongStringIntIndex_whenSafeGetInt_thenReturnNull(){
        //given
        String[] line = {"", "42"};
        int index = 0;

        //when
        int result = safeCast.safeGetInt(line, index);

        //then
        assertEquals(0, result);
    }

    @Test
    void givenUnparsableStringIntIndex_whenSafeGetInt_thenReturnNull(){
        //given
        String[] line = {"1,6", "42"};
        int index = 0;

        //when
        int result = safeCast.safeGetInt(line, index);

        //then
        assertEquals(0, result);
    }

    @Test
    void givenStringIntLength_whenSafeGetSubstring_thenReturnString(){
        //given
        String line = "1234567";
        int length = 5;

        //when
        String result = safeCast.safeSubstring(line, length);

        //then
        assertEquals("12345", result);
    }

    @Test
    void givenStringWrongIntLength_whenSafeGetSubstring_thenReturnString(){
        //given
        String line = "1234567";
        int length = 10;

        //when
        String result = safeCast.safeSubstring(line, length);

        //then
        assertEquals(line, result);
    }

    @Test
    void givenEmptyStringIntLength_whenSafeGetSubstring_thenReturnNull(){
        //given
        String line = null;
        int length = 5;

        //when
        String result = safeCast.safeSubstring(line, length);

        //then
        assertNull(result);
    }

    @Test
    void givenStringIntIndex_whenSafeGetBigDecimal_thenReturnBigDecimal(){
        //given
        String[] line = {"12345", "6789"};
        int index = 0;

        //when
        BigDecimal result = safeCast.safeGetBigDecimal(line, index);

        //then
        assertEquals(new BigDecimal("12345"), result);
    }

    @Test
    void givenEmptyStringIntIndex_whenSafeGetBigDecimal_thenReturnNul(){
        //given
        String[] line = {"", "6789"};
        int index = 0;

        //when
        BigDecimal result = safeCast.safeGetBigDecimal(line, index);

        //then
        assertNull(result);
    }
}
