package ru.ibs.diploma.datamanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.logging.WriteLogService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * Сервис для чтения файла населения и построения карты
 * «ZIP-код → количество жителей».
 *
 * <p>Формат входного файла:
 * <ul>
 *   <li>каждая строка содержит ровно 2 элемента, разделённых пробелом;</li>
 *   <li>первый элемент – ZIP-код (5 цифр);</li>
 *   <li>второй элемент – численность населения в виде строки, пригодной
 *       для конструктора {@link BigDecimal}.</li>
 * </ul>
 *
 * <p>Результат возвращается в виде отсортированной по ключу карты
 * ({@link TreeMap}), что обеспечивает упорядоченный вывод при итерации.
 *
 * <p>Все операции чтения фиксируются через {@link WriteLogService}.
 *
 * @since 1.0
 */
@Service
public class ReadPopulation{

    /**
     * Хранилище имён файлов, получаемое через конструктор.
     */
    private final FileNames fileNames;

    /**
     * Сервис логирования; может быть {@code null} при использовании
     * упрощённого конструктора.
     */
    @Autowired
    private WriteLogService writeLogService;

    /**
     * Полный конструктор с внедрением зависимостей.
     *
     * @param fileNames        хранилище имён файлов
     * @param writeLogService  сервис логирования
     */
    public ReadPopulation(FileNames fileNames, WriteLogService writeLogService) {
        this.fileNames = fileNames;
        this.writeLogService = writeLogService;
    }

    /**
     * Упрощённый конструктор без логирования.
     *
     * @param fileNames хранилище имён файлов
     */
    @Autowired
    public ReadPopulation(FileNames fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * Читает файл населения и строит отсортированную карту
     * «ZIP-код → количество жителей».
     *
     * <p>В случае ошибки ввода-вывода выводит сообщение в консоль и
     * возвращает пустую карту. Успешные операции чтения логируются.
     *
     * @return отсортированная карта, где ключ – ZIP-код, значение –
     *         численность населения; никогда не {@code null}
     * @throws IOException не выбрасывается наружу (перехватывается и
     *                     логируется), но объявлено для совместимости
     */
    public Map<String, BigDecimal> readFile() throws IOException{
        Map<String, BigDecimal> result = new TreeMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(fileNames.getPopulationFile()))){
            String line;
            writeLogService.logFileEntry(fileNames.getPopulationFile());
            while((line = br.readLine()) != null){
                String[] parsed = line.split(" ");

                result.put(parsed[0], new BigDecimal(parsed[1]));
            }
        }catch (IOException ie){
            System.out.println("Couldn't read from population file: " + ie.getMessage());
        }

        return result;
    }
}
