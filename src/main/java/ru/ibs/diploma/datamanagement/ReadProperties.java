package ru.ibs.diploma.datamanagement;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.data.Properties;
import ru.ibs.diploma.data.SafeCast;
import ru.ibs.diploma.logging.WriteLogService;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Реализация интерфейса {@link ReadFile}, предназначенная для чтения файла
 * свойств недвижимости ({@code properties.csv}).
 *
 * <p>Ожидаемый формат CSV-файла:
 * <ul>
 *   <li>первая строка – заголовки;</li>
 *   <li>обязательные столбцы: {@code market_value}, {@code total_livable_area},
 *       {@code zip_code} (регистр не учитывается);</li>
 *   <li>каждая последующая строка преобразуется в объект {@link Properties}.</li>
 * </ul>
 *
 * <p>При инициализации использует {@link FileNames} для получения пути к файлу,
 * {@link WriteLogService} для фиксации операций чтения и {@link SafeCast}
 * для безопасного приведения типов.
 *
 */
@Service
public class ReadProperties implements ReadFile{

    /**
     * Хранилище имён файлов, получаемое через конструктор.
     */
    private final FileNames fileNames;
    /**
     * Сервис логирования; может быть {@code null} для упрощённых конструкторов.
     */
    @Autowired
    private WriteLogService writeLogService;
    /**
     * Утилита безопасного приведения типов; может быть {@code null}
     * при использовании упрощённых конструкторов.
     */
    @Autowired
    private SafeCast safeCast;
    /**
     * Полный конструктор с внедрением всех зависимостей.
     *
     * @param fileNames  хранилище имён файлов
     * @param writeLogService сервис логирования
     * @param safeCast   утилита приведения типов
     */
    public ReadProperties(FileNames fileNames, WriteLogService writeLogService, SafeCast safeCast) {
        this.fileNames = fileNames;
        this.writeLogService = writeLogService;
        this.safeCast = safeCast;
    }
    /**
     * Конструктор без {@code SafeCast} (используется в тестах или при
     * ручном создании объекта).
     *
     * @param fileNames  хранилище имён файлов
     * @param writeLogService сервис логирования
     */
    @Autowired
    public ReadProperties(FileNames fileNames, WriteLogService writeLogService) {
        this.fileNames = fileNames;
        this.writeLogService = writeLogService;
    }
    /**
     * Минимальный конструктор (только {@code FileNames}).
     *
     * @param fileNames хранилище имён файлов
     */
    public ReadProperties(FileNames fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * Читает CSV-файл свойств и возвращает список объектов {@link Properties}.
     *
     * <p>Метод generic-совместимый, но фактически работает только с
     * {@code Class<Properties>} или его наследниками. В противном случае
     * выбрасывает {@link ClassCastException}.
     *
     * @param type класс, в который должны быть преобразованы строки файла
     * @param <T>  тип элементов результирующего списка
     * @return список объектов {@link Properties}, никогда не {@code null}
     * @throws IOException        если файл не найден, повреждён или не содержит
     *                            обязательных столбцов
     * @throws ClassCastException если {@code type} не является {@link Properties}
     *                            или его наследником
     */
    @Override
    public <T> List<T> readFile(Class<T> type) throws IOException, ClassCastException{
        if (!Properties.class.isAssignableFrom(type)) {
            throw new ClassCastException("Unsupported type");
        }

        List<Properties> properties = new ArrayList<>();

        CsvParser parser = new CsvParser(new CsvParserSettings());
        parser.beginParsing(new File(fileNames.getPropertiesFile()));
        writeLogService.logFileEntry(fileNames.getPropertiesFile());

        String[] row = parser.parseNext();

        Integer marketValIndex = findIndex("market_value", row);
        Integer totalLivableAreaIndex = findIndex("total_livable_area", row);
        Integer zipCodeIndex = findIndex("zip_code", row);

        if(marketValIndex == null || totalLivableAreaIndex == null || zipCodeIndex == null){
            throw new IOException("Wrong fields in file properties.csv");
        }

        while((row = parser.parseNext()) != null){
            properties.add(new Properties(
                    safeCast.safeGetBigDecimal(row, marketValIndex),
                    safeCast.safeGetBigDecimal(row, totalLivableAreaIndex),
                    (safeCast.safeSubstring(safeCast.safeGetString(row, zipCodeIndex), 5))
            ));
        }

        return (List<T>) properties;
    }

    /**
     * Вспомогательный метод для поиска индекса столбца по имени заголовка.
     *
     * <p>Поиск выполняется без учёта регистра. Если заголовок не найден,
     * возвращается {@code null}.
     *
     * @param name имя искомого столбца
     * @param row  массив заголовков CSV-файла
     * @return индекс столбца или {@code null}, если столбец отсутствует
     */
    public Integer findIndex(String name, String[] row){
        if(row == null || row.length == 0){
            return null;
        }
        OptionalInt marketVal = IntStream.range(0, row.length)
                .filter(i -> row[i].equalsIgnoreCase(name)).findFirst();

        if(marketVal.isPresent()) return marketVal.getAsInt();
        return null;
    }
}
