package ru.ibs.diploma.datamanagement;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ibs.diploma.data.FileNames;
import ru.ibs.diploma.data.Parking;
import ru.ibs.diploma.data.SafeCast;
import ru.ibs.diploma.logging.WriteLogService;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Сервис чтения файлов с данными о парковочных штрафах.
 *
 * <p>Реализует интерфейс {@link ReadFile} и умеет работать с двумя форматами:
 * <ul>
 *   <li>CSV – через {@link CSVReader} (OpenCSV);</li>
 *   <li>JSON – через библиотеку {@code json-simple}.</li>
 * </ul>
 *
 * <p>Выбор конкретного способа чтения определяется значением
 * {@link FileNames#getParkingType()}: {@code "csv"} или {@code "json"}
 * (регистр не важен).
 *
 * <p>Каждая строка/объект должна содержать ровно 7 полей в следующем порядке:
 * <ol>
 *   <li>date – дата (строка)</li>
 *   <li>fine – сумма штрафа (целое число)</li>
 *   <li>violation – описание нарушения (строка)</li>
 *   <li>plate_id – номерной знак (число)</li>
 *   <li>state – штат (строка)</li>
 *   <li>ticket_number – номер талона (число)</li>
 *   <li>zip_code – ZIP-код (строка)</li>
 * </ol>
 *
 * <p>Все операции чтения логируются через {@link WriteLogService}.
 *
 */
@Service
public class ReadParking implements ReadFile{

    /**
     * Хранилище имён файлов и текущего формата данных.
     */
    private final FileNames fileNames;

    /**
     * Сервис логирования; может быть {@code null} при использовании
     * упрощённого конструктора.
     */
    @Autowired
    private WriteLogService writeLogService;

    /**
     * Утилита безопасного приведения типов; может быть {@code null}
     * при использовании упрощённого конструктора.
     */
    @Autowired
    private SafeCast safeCast;

    /**
     * Полный конструктор с внедрением всех зависимостей.
     *
     * @param fileNames       хранилище имён файлов
     * @param writeLogService сервис логирования
     * @param safeCast        утилита приведения типов
     */
    @Autowired
    public ReadParking(FileNames fileNames, WriteLogService writeLogService, SafeCast safeCast) {
        this.fileNames = fileNames;
        this.writeLogService = writeLogService;
        this.safeCast = safeCast;
    }

    /**
     * Упрощённый конструктор (без автоматического внедрения зависимостей).
     *
     * @param fileNames хранилище имён файлов
     */
    public ReadParking(FileNames fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * Точка входа интерфейса {@link ReadFile}.
     *
     * <p>Выбирает нужный способ чтения (CSV/JSON) на основании
     * {@link FileNames#getParkingType()}. При несоответствии типа выбрасывает
     * {@link ClassCastException}.
     *
     * @param type класс, в который должен быть преобразован файл
     * @param <T>  тип элементов результирующего списка
     * @return список объектов {@link Parking}
     * @throws ClassCastException если {@code type} не является {@link Parking}
     *                            или его наследником
     * @throws IOException        при ошибке чтения или парсинга файла
     */
    @Override
    public <T> List<T> readFile(Class<T> type) throws ClassCastException, IOException{
        if(Parking.class.isAssignableFrom(type)) {
            if (fileNames.getParkingType().equalsIgnoreCase("json")) {
                return (List<T>) readJsonParking();
            } else {
                return (List<T>) readCsvParking();
            }
        }else{
            throw new ClassCastException("Type is not for parking file");
        }
    }

    /**
     * Читает JSON-массив штрафов и преобразует его в список объектов
     * {@link Parking}.
     *
     * @return список штрафов; никогда не {@code null}
     * @throws IOException если файл не найден, повреждён или имеет
     *                     неправильную структуру
     */
    public List<Parking> readJsonParking()throws IOException{
        List<Parking> result = new ArrayList<>();

        JSONArray jarray = null;
        try{
            writeLogService.logFileEntry(fileNames.getParkingFile());
            Object obj = new JSONParser().parse(new FileReader(fileNames.getParkingFile()));
            jarray = (JSONArray) obj;
        }catch(IOException | ParseException e){
            throw new IOException("Couldn't read file: " + e.getMessage());
        }

        if(jarray != null){
            for(Object o : jarray) {
                JSONObject jobj = (JSONObject) o;

                String[] line = {jobj.get("date").toString(),
                                jobj.get("fine").toString(),
                                jobj.get("violation").toString(),
                                jobj.get("plate_id").toString(),
                                jobj.get("state").toString(),
                                jobj.get("ticket_number").toString(),
                                jobj.get("zip_code").toString()};

                addParking(line, result);
            }
        }

        return result;
    }

    /**
     * Читает CSV-файл штрафов и преобразует его в список объектов
     * {@link Parking}.
     *
     * @return список штрафов; никогда не {@code null}
     * @throws IOException при ошибке чтения или нарушении CSV-формата
     */
    public List<Parking> readCsvParking() throws IOException {
        List<Parking> result = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(fileNames.getParkingFile()));
            CSVReader cr = new CSVReader(br)){
            writeLogService.logFileEntry(fileNames.getParkingFile());

            String[] line;

            while((line = cr.readNext()) != null){
                addParking(line, result);
            }

        }catch (IOException | CsvValidationException ie){
            throw new IOException("Couldn't finish work with file: " + ie);
        }

        return result;
    }

    /**
     * Создаёт объект {@link Parking} из массива строк и добавляет его
     * в коллекцию.
     *
     * @param line   массив из 7 строк-полей
     * @param result коллекция, в которую будет добавлен новый объект
     * @throws IOException если длина массива отлична от 7
     */
    public void addParking(String[] line, List<Parking> result)throws IOException{
        if( line == null || line.length !=7){
            throw new IOException("Couldn't create new parking from line " + Arrays.toString(line));
        }
        Parking parking = new Parking(safeCast.safeGetInstance(line, 0),
                safeCast.safeGetInt(line, 1),
                safeCast.safeGetString(line, 2),
                safeCast.safeGetLong(line, 3),
                safeCast.safeGetString(line, 4),
                safeCast.safeGetLong(line, 5),
                safeCast.safeGetString(line, 6));

        result.add(parking);
    }

}
