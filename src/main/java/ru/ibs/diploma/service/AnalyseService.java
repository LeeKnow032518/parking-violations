package ru.ibs.diploma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ibs.diploma.cache.*;
import ru.ibs.diploma.data.Field;
import ru.ibs.diploma.data.Parking;
import ru.ibs.diploma.data.Properties;
import ru.ibs.diploma.datamanagement.ReadParking;
import ru.ibs.diploma.datamanagement.ReadPopulation;
import ru.ibs.diploma.datamanagement.ReadProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Сервис-анализатор, выполняющий расчёты по данным о населении, парковочных штрафах и недвижимости.
 *
 * <p>Реализует следующие типы отчётов:
 * <ul>
 *   <li>общая численность населения по ZIP-кодам;</li>
 *   <li>суммарные штрафы за парковку на душу населения;</li>
 *   <li>средняя стоимость объекта недвижимости по произвольному полю;</li>
 *   <li>рыночная стоимость всей недвижимости на одного жителя;</li>
 *   <li>«сюрприз-опция» – сводная статистика по каждому ZIP-коду.</li>
 * </ul>
 *
 * <p>Для ускорения повторных вызовов результаты кэшируются с помощью реализации
 * {@link CachedAnswerInterface}. Ключ кэша совпадает с номером вопроса (1–6).
 *
 * <p>Данные читаются один раз при первом обращении и хранятся в памяти.
 * Синхронизация при многопоточном доступе не предусмотрена.
 *
 * @see Answer
 * @see FirstAnswer
 * @see SecondAnswer
 * @see PropertyAnswer
 * @see SurpriseAnswer
 * @see Statistics
 */
@Service
public class AnalyseService {

    /** Кэш результатов расчётов. */
    @Autowired
    private CachedAnswerInterface answers;

    /** Карта «ZIP-код → численность населения». */
    private Map<String, BigDecimal> population = new TreeMap<>();

    /** Список всех записей о парковочных штрафах. */
    private List<Parking> parking = new ArrayList<>();

    /** Список всех записей об объектах недвижимости. */
    private List<Properties> properties = new ArrayList<>();

    /** Сервис чтения файла с данными о населении. */
    private final ReadPopulation readPopulation;

    /** Сервис чтения файла с данными о парковочных штрафах. */
    private final ReadParking readParking;

    /** Сервис чтения файла с данными о недвижимости. */
    private final ReadProperties readProperties;

    /**
     * Конструирует экземпляр, внедряя необходимые сервисы чтения данных и кэш.
     *
     * @param readPopulation  сервис чтения населения
     * @param readParking     сервис чтения штрафов
     * @param readProperties  сервис чтения недвижимости
     * @param answers         кэш результатов
     */
    public AnalyseService(ReadPopulation readPopulation, ReadParking readParking, ReadProperties readProperties, CachedAnswerInterface answers) {
        this.readPopulation = readPopulation;
        this.readParking = readParking;
        this.readProperties = readProperties;
        this.answers = answers;
    }

    /**
     * Возвращает суммарную численность населения по всем ZIP-кодам.
     *
     * <p>При первом вызове читает данные о населении, если они ещё не загружены.
     * Результат кэшируется под ключом {@code 1}.
     *
     * @return объект {@link FirstAnswer}, содержащий итоговое значение
     * @throws IOException при ошибке чтения файлов
     */
    public Answer totalPopulation() throws IOException{
        if (answers.searchCache(1) != null) {
            return answers.searchCache(1);
        }

        checkPopulation();

        Optional<BigDecimal> result = population.keySet().stream().map(key -> population.get(key))
                .reduce(BigDecimal::add);

        FirstAnswer total = new FirstAnswer(BigDecimal.ZERO);
        if(result.isPresent()) {
            total = new FirstAnswer(result.get());
        }

        answers.cacheAnswer(1, total);

        return total;
    }

    /**
     * Рассчитывает суммарные штрафы за парковку (state = "PA") на душу населения
     * для каждого ZIP-кода.
     *
     * <p>Результат кэшируется под ключом {@code 2}.
     *
     * @return объект {@link SecondAnswer} с картой «ZIP-код → средняя сумма штрафа»
     * @throws IOException при ошибке чтения файлов
     */
    public Answer totalParkingFinesPerCapita() throws IOException{
        if (answers.searchCache(2) != null) {
            return answers.searchCache(2);
        }

        checkParking();
        checkPopulation();

        Map<String, BigDecimal> result = new TreeMap<>();

        for (String code : population.keySet()) {
            if(population.get(code).equals(BigDecimal.ZERO)) continue;
            List<Parking> temporaryCalculations = parking.stream()
                    .filter(p -> (p.postIndex() != null) && (p.postIndex().equals(code) && (p.state().equals("PA"))))
                    .toList();
            if (temporaryCalculations.isEmpty()) {
                continue;
            }
            int total = temporaryCalculations.stream()
                    .map(Parking::moneyAmount).reduce(Integer::sum).get();
            if(total == 0) continue;
            BigDecimal avg = BigDecimal.valueOf(total)
                    .divide(population.get(code), 4, RoundingMode.DOWN);

            result.put(code, avg);
        }


        SecondAnswer answer = new SecondAnswer(result);
        answers.cacheAnswer(2, answer);

        return answer;
    }

    /**
     * Среднее значение указанного поля недвижимости для заданного ZIP-кода.
     *
     * <p>Использует номер вопроса из {@link Field#getQuestionNumber()} в качестве ключа кэша.
     *
     * @param code  ZIP-код
     * @param field поле, по которому производится усреднение
     * @return объект {@link FirstAnswer} со средним значением
     * @throws IOException при ошибке чтения файлов
     */
    public Answer averageProperties(String code, Field field) throws IOException{
        PropertyAnswer result = (PropertyAnswer) answers.searchCache(field.getQuestionNumber());
        if((result != null) && (result.getAvgByCode(code) != null)){
            FirstAnswer ans = result.getAvgByCode(code);
            return result.getAvgByCode(code);
        }
        if(result == null){
            result = new PropertyAnswer();
        }

        checkProperties();

        long numOfProperties = properties.stream()
                .filter(p -> (p.ZipCode() != null) && (p.ZipCode().equals(code)) && (field.getValue(p) != null)).count();

        if(numOfProperties == 0){
            answers.cacheAnswer(field.getQuestionNumber(), result.addZipCode(code, BigDecimal.ZERO));
            return result.getAvgByCode(code);
        }

        Optional<BigDecimal> sum = properties.stream()
                .filter(p -> (p.ZipCode() != null) && (p.ZipCode().equals(code)) &&(field.getValue(p) != null))
                .map(field::getValue)
                        .reduce(BigDecimal::add);

        BigDecimal answer = BigDecimal.ZERO;
        if(sum.isPresent()) answer = sum.get();

        result.addZipCode(code, answer.divide(BigDecimal.valueOf(numOfProperties),0,  RoundingMode.DOWN));

        answers.cacheAnswer(field.getQuestionNumber(), result);
        return result.getAvgByCode(code);
    }

    /**
     * Рыночная стоимость всей недвижимости ZIP-кода, делённая на численность населения.
     *
     * <p>Результат кэшируется под ключом {@code 5}.
     *
     * @param code ZIP-код
     * @return объект {@link FirstAnswer} со значением на одного жителя
     * @throws IOException при ошибке чтения файлов
     */
    public Answer totalMarketValuePerCapita(String code) throws IOException{
        PropertyAnswer result = (PropertyAnswer) answers.searchCache(5);
        if((result != null) && (result.getAvgByCode(code) != null)){
            return result.getAvgByCode(code);
        }
        if(result == null){
            result = new PropertyAnswer();
        }

        checkPopulation();
        checkProperties();

        BigDecimal numOfPeople = population.get(code);
        if((numOfPeople == null) || numOfPeople.equals(BigDecimal.ZERO)){
            answers.cacheAnswer(5, result.addZipCode(code, BigDecimal.ZERO));
            return result.getAvgByCode(code);
        }

        Optional<BigDecimal> sum = properties.stream()
                .filter(p -> (p.ZipCode() != null) && (p.ZipCode().equals(code)))
                .map(Properties::MarketValue).reduce(BigDecimal::add);

        BigDecimal answer = BigDecimal.ZERO;
        if(sum.isPresent()) answer = sum.get();

        result.addZipCode(code, answer.divide(numOfPeople,0,  RoundingMode.DOWN));

        answers.cacheAnswer(5, result);
        return result.getAvgByCode(code);
    }

    /**
     * Для каждого ZIP-кода возвращает пару значений:
     * средняя рыночная стоимость недвижимости и среднее число штрафов на жителя.
     *
     * <p>Результат кэшируется под ключом {@code 6}.
     *
     * @return объект {@link SurpriseAnswer} с картой «ZIP-код → {@link Statistics}»
     * @throws IOException при ошибке чтения файлов
     */
    public Answer surpriseOption()throws IOException{
        if(answers.searchCache(6) != null){
            return answers.searchCache(6);
        }

        checkPopulation();
        checkParking();
        checkProperties();

        Map<String, Statistics> result = new HashMap<>();

        for(String code : population.keySet()){
            long numOfFines = parking.stream()
                    .filter(p -> (p.postIndex() != null) && (p.postIndex().equalsIgnoreCase(code))).count();

            BigDecimal avgFines = BigDecimal.ZERO;
            if(numOfFines != 0 && population.get(code) != null){
                avgFines = BigDecimal.valueOf(numOfFines).divide(population.get(code), 4, RoundingMode.DOWN);
            }

            FirstAnswer avgProperties = (FirstAnswer) averageProperties(code, Field.MARKET_VALUE);
            result.put(code, new Statistics(avgProperties.getResult(), avgFines));
        }

        Answer answer = new SurpriseAnswer(result);
        answers.cacheAnswer(6, answer);
        return answer;
    }

    /* ----------- служебные методы загрузки данных ----------- */

    /**
     * Загружает данные о населении, если они ещё не загружены.
     *
     * @throws IOException при ошибке чтения файла
     */
    public void checkPopulation() throws IOException{
        if (population.isEmpty()) {
            population = readPopulation.readFile();
        }
    }

    /**
     * Загружает данные о парковочных штрафах, если они ещё не загружены.
     *
     * @throws IOException при ошибке чтения файла
     */
    public void checkParking() throws IOException{
        if (parking.isEmpty()) {
            parking = readParking.readFile(Parking.class);
        }
    }

    /**
     * Загружает данные об объектах недвижимости, если они ещё не загружены.
     *
     * @throws IOException при ошибке чтения файла
     */
    public void checkProperties() throws IOException{
        if (properties.isEmpty()) {
            properties = readProperties.readFile(Properties.class);
        }
    }
}
