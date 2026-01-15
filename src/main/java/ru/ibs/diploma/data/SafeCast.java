package ru.ibs.diploma.data;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;


/**
 * Утилитарный сервис для безопасного извлечения и преобразования данных из строкового массива (например, CSV).
 * <p>
 * Методы возвращают значения по умолчанию (или {@code null}) при ошибках или отсутствии данных.
 * Помечен как {@link Service}, чтобы быть доступным для внедрения в другие компоненты Spring.
 * </p>
 */
@Service
public class SafeCast {

    /**
     * Извлекает строку по индексу. Возвращает null, если индекс вне диапазона, значение пустое или null.
     *
     * @param line  массив строк
     * @param index индекс значения
     * @return обрезанная строка или null
     */
    public String safeGetString(String[] line, int index) {
        if (index >= line.length) return null;
        String value = line[index];
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    /**
     * Извлекает long значение. Возвращает 0 при ошибке или отсутствии данных.
     *
     * @param line  массив строк
     * @param index индекс значения
     * @return число или 0
     */
    public long safeGetLong(String[] line, int index) {
        String value = safeGetString(line, index);
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Парсит строку в объект {@link Instant}. Возвращает null при ошибке.
     *
     * @param line  массив строк
     * @param index индекс значения
     * @return Instant или null
     */
    public Instant safeGetInstance(String[] line, int index) {
        String value = safeGetString(line, index);
        if (value == null) {
            return null;
        }
        try {
            return Instant.parse(value.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Извлекает int значение. Возвращает 0 при ошибке или отсутствии данных.
     *
     * @param line  массив строк
     * @param index индекс значения
     * @return число или 0
     */
    public int safeGetInt(String[] line, int index) {
        String value = safeGetString(line, index);
        if (value == null) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Возвращает подстроку длиной до указанного предела. Защита от слишком длинных значений.
     *
     * @param line   исходная строка
     * @param length максимальная длина
     * @return подстрока от 0 до length, или вся строка, если короче
     */
    public String safeSubstring(String line, int length){
        if(line == null){
            return null;
        }else if(line.length()<length){
            return line;
        }
        return line.substring(0, length);
    }

    /**
     * Извлекает значение типа {@link BigDecimal}. Возвращает null, если значение отсутствует.
     * При ошибке парсинга выбрасывает {@link NumberFormatException} — по умолчанию не подавляется.
     *
     * @param line  массив строк
     * @param index индекс значения
     * @return BigDecimal или null
     */
    public BigDecimal safeGetBigDecimal(String[] line, int index){
        String value = safeGetString(line, index);
        if(value == null){
            return null;
        }
        return new BigDecimal(value);
    }
}
