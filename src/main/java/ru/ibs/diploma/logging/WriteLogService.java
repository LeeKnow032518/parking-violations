package ru.ibs.diploma.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ibs.diploma.data.FileNames;

import java.io.FileWriter;
import java.io.IOException;
/**
 * Сервис для записи логов в файл.
 * <p>
 * Предоставляет методы для логирования:
 * <ul>
 *   <li>Аргументов запуска приложения</li>
 *   <li>Выборов пользователя в интерфейсе</li>
 *   <li>Имён обрабатываемых файлов</li>
 * </ul>
 * <p>
 * Все записи сопровождаются временной меткой в миллисекундах (Unix timestamp).
 * Логи добавляются в конец файла (режим append), файл указывается через {@link FileNames#getLogFile()}.
 * <p>
 * В случае ошибки записи в файл, сообщение выводится в {@code System.out}, но работа приложения не прерывается.
 *
 * @version 1.0
 *
 * @see FileNames
 */
@Service
public class WriteLogService {

    @Autowired
    private FileNames fileNames;

    /**
     * Записывает аргументы запуска приложения в лог-файл.
     * <p>
     * Аргументы объединяются в одну строку через пробел.
     * Формат записи: {@code <timestamp> <arg1> <arg2> ...}
     *
     * @param args массив строк — аргументы командной строки
     *             не должен быть {@code null}, но элементы могут быть пустыми
     * @throws RuntimeException если не удалось открыть или записать в лог-файл
     *         (ошибка перехватывается и логируется в консоль)
     *
     * <p><strong>Пример:</strong>
     * <pre>
     * {@code
     * String[] args = {"csv", "parking.csv", "properties.csv", "population.txt", "log.txt"};
     * writeLogService.logEntry(args);
     * // В файле: 1743829123456 csv data.csv output.csv config.csv log.txt
     * }
     * </pre>
     */
    public void logEntry(String[] args){
        try(FileWriter fw = new FileWriter(fileNames.getLogFile(), true)){
            long timestamp = System.currentTimeMillis();
            String oneLine = String.join(" ", args);
            fw.write(timestamp + " " + oneLine + "\n");
        }catch (IOException e){
            System.out.println("Failed to log entry args");
        }
    }

    /**
     * Записывает выбор пользователя (например, пункт меню) в лог-файл.
     * <p>
     * Используется для отслеживания действий в интерактивном режиме.
     * Формат записи: {@code <timestamp> <choice>}
     *
     * @param choice строка, представляющая выбор пользователя
     *               может быть {@code null} или пустой
     * @throws RuntimeException если не удалось записать в файл
     *         (ошибка перехватывается и выводится в консоль)
     *
     * <p><strong>Пример:</strong>
     * <pre>
     * {@code
     * writeLogService.logChoice("1");
     * // В файле: 1743829123457 1
     * }
     * </pre>
     */
    public void logChoice(String choice){
        try (FileWriter fw = new FileWriter(fileNames.getLogFile(), true)){
            long timestamp = System.currentTimeMillis();
            fw.write(timestamp + " " + choice + "\n");
        }catch (IOException e) {
            System.out.println("Failed to log the choice");
        }
    }

    /**
     * Записывает имя обрабатываемого файла в лог-файл.
     * <p>
     * Используется для фиксации, какие файлы были прочитаны (например, data.csv, population.txt).
     * Формат записи: {@code <timestamp> <file-name>}
     *
     * @param name имя файла (с путём или без)
     *             может быть {@code null} или пустым
     * @throws RuntimeException если не удалось записать в лог-файл
     *         (ошибка перехватывается и выводится в консоль)
     *
     * <p><strong>Пример:</strong>
     * <pre>
     * {@code
     * writeLogService.logFileEntry("properties.csv");
     * // В файле: 1743829123458 properties.csv
     * }
     * </pre>
     */
    public void logFileEntry(String name){
        try (FileWriter fw = new FileWriter(fileNames.getLogFile(), true)){
            long timestamp = System.currentTimeMillis();
            fw.write(timestamp + " " + name + "\n");
        }catch (IOException e) {
            System.out.println("Failed to log the choice");
        }
    }
}
