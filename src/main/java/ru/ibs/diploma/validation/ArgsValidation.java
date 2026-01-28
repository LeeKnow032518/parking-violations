package ru.ibs.diploma.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.logging.WriteLogService;
import ru.ibs.diploma.ui.UserChoice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Компонент первичной валидации аргументов командной строки.
 *
 * <p>Запускается автоматически при старте Spring-контекста благодаря
 * {@link CommandLineRunner} и аннотации {@code @Order(1)}. Проверяет
 * количество, формат и существование переданных файлов, после чего
 * сохраняет имена файлов в объект {@link FileNames} и инициализирует
 * логирование.
 *
 * <p>Ожидает ровно 5 аргументов:
 * <ol>
 *   <li>0 – тип парковки: {@code csv} или {@code json} (регистр не важен)</li>
 *   <li>1 – файл с данными о парковке</li>
 *   <li>2 – файл свойств</li>
 *   <li>3 – файл населения</li>
 *   <li>4 – файл лога</li>
 * </ol>
 *
 * <p>В случае несоответствия требованиям выбрасывает
 * {@link IllegalArgumentException} и завершает приложение с кодом 1.
 *
 * @see FileNames
 * @see WriteLogService
 * @see UserChoice
 */
@Service
@RequiredArgsConstructor
public class ArgsValidation{
//
//    @Autowired
//    private ApplicationContext context;
    /**
     * Сервис для записи логов.
     */
    private final WriteLogService writeLogService;

    /**
     * Компонент, формирующий пользовательское приветственное сообщение.
     */
    private final UserChoice userChoice;

    /**
     * Объект-держатель имён файлов, заполняемый после успешной валидации.
     */

    private final FileNames fileNames;

    /**
     * Точка входа Spring-приложения.
     *
     * <p>Выполняет валидацию аргументов командной строки, сохраняет
     * имена файлов в {@link FileNames}, записывает стартовый лог и
     * формирует приветственное сообщение.
     *
     * @param args аргументы командной строки
     * @throws InterruptedException если поток прерван во время задержки
     *                              перед завершением приложения
     */
    public void run(String[] args) throws InterruptedException {
        try {
            validateArgs(args);
        }catch (IllegalArgumentException ie){
            System.out.println(ie.getMessage());
            Thread.sleep(3000); // so that we have enough time to read what went wrong...
            return;
        }

        System.out.println("Arguments are correct");

        fileNames.setParkingType(args[0]);
        fileNames.setParkingFile(args[1]);
        fileNames.setPopulationFile(args[3]);
        fileNames.setPropertiesFile(args[2]);
        fileNames.setLogFile(args[4]);

        writeLogService.logEntry(args);

        userChoice.createMessage();
    }

    /**
     * Проверяет корректность аргументов командной строки.
     *
     * <p>Выполняются проверки:
     * <ul>
     *   <li>количество аргументов равно 5</li>
     *   <li>первый аргумент — допустимый формат ({@code csv} или {@code json})</li>
     *   <li>расширение файла парковки соответствует заявленному формату</li>
     *   <li>файлы данных существуют и доступны для чтения</li>
     *   <li>файл лога существует или может быть создан</li>
     * </ul>
     *
     * @param args аргументы командной строки
     * @throws IllegalArgumentException если хотя бы одна проверка не пройдена
     */
    public void validateArgs(String[] args) throws IllegalArgumentException {
        int length = 0;

        for(String argument : args){
            if(argument == null){
                continue;
            }
            length ++;
        }

        if (length == 0) {
            throw new IllegalArgumentException("No arguments provided. Expected: format data.csv output.csv config.csv log.txt");
        }

        if(length < 5){
            throw new IllegalArgumentException("Wrong number of args. Expected 5, received " + length);
        }

        if(!args[0].equalsIgnoreCase("csv") && !args[0].equalsIgnoreCase("json")){
            throw new IllegalArgumentException("Wrong file format. Expected \"csv\" or \"json\", but received " + args[0]);
        }

        String format = args[1].split("\\.")[1];
        if(!format.equalsIgnoreCase(args[0])){
            throw new IllegalArgumentException("Wrong file extension. Expected " + args[0].toLowerCase() +
                    " but received " + format);
        }

        validateFile(args[1]);
        validateFile(args[2]);
        validateFile(args[3]);

        createLog(args[4]);
    }

    /**
     * Проверяет существование и читаемость файла.
     *
     * @param fileName имя файла
     * @throws IllegalArgumentException если файл не существует или недоступен
     *                                  для чтения
     */
    public void validateFile(String fileName){
        Path path = Paths.get(fileName);
        if(!Files.exists(path) || !Files.isReadable(path)){
            throw new IllegalArgumentException("Cannot open file " + path.toString());
        }
    }

    /**
     * Гарантирует существование файла лога.
     *
     * <p>Если файл не существует, пытается создать его.
     *
     * @param fileName имя файла лога
     * @throws IllegalArgumentException если создать файл не удалось
     */
    public void createLog(String fileName) throws IllegalArgumentException{
        Path path = Paths.get(fileName);
        if(!Files.exists(path)){
            try{
                Files.createFile(path);
            }catch (IOException e){
                throw new IllegalArgumentException("Couldn't create log file " + fileName);
            }
        }
    }

}
