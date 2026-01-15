package ru.ibs.diploma.cache;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * Реализация интерфейса {@link Answer}, представляющая коллекцию ответов,
 * сгруппированных по строковым ключам (например, почтовым индексам).
 * <p>
 * Каждый ключ в коллекции ассоциирован с объектом {@link FirstAnswer},
 * содержащим числовое значение (например, среднее значение).
 * </p>
 *
 * <p><strong>Пример:</strong>
 * <pre>
 * {@code
 * BigDecimal value = new BigDecimal("123.45");
 * Map<String, FirstAnswer> map = new HashMap<>();
 * map.put("19102", value);
 * Answer answer = new PropertyAnswer(map);
 * }
 * </pre>
 *
 * @see Answer
 * @see FirstAnswer
 */
public class PropertyAnswer implements Answer{

    /**
     * Хранилище ответов, где ключ — строковый идентификатор (например, почтовый индекс),
     * а значение — объект {@link FirstAnswer}, содержащий числовое значение.
     * <p>
     * Используется {@link TreeMap} для автоматической сортировки ключей.
     * </p>
     */
    private Map<String, FirstAnswer> answers = new TreeMap<>();

    /**
     * Конструктор по умолчанию.
     * Создаёт пустой объект {@code PropertyAnswer} с пустой внутренней картой.
     */
    public PropertyAnswer() {
    }

    /**
     * Конструктор, инициализирующий объект с переданной картой ответов.
     * <p>
     *
     * @param answers карта, сопоставляющая строковые ключи с объектами {@link FirstAnswer};
     *                может быть {@code null}, в этом случае будет использована пустая карта
     */
    public PropertyAnswer(Map<String, FirstAnswer> answers) {
        this.answers = answers;
    }

    /**
     * Выводит все сохранённые ответы в формате "ключ = значение" на стандартный поток вывода.
     * <p>
     * Если карта пуста, выводится соответствующее сообщение.
     * Пример вывода:
     * <pre>
     * 10001 = 250.50
     * 10002 = 300.00
     * </pre>
     * </p>
     * <p>
     * Реализует метод из интерфейса {@link Answer}.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void printAnswer() {
        if (answers.isEmpty()) {
            System.out.println("No data.");
        } else {
            for (Map.Entry<String, FirstAnswer> entry : answers.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue().getResult());
            }
        }
    }

    /**
     * Добавляет или обновляет запись в коллекции по заданному коду.
     *
     * @param code строковой ключ (например, почтовый индекс); не должен быть {@code null}
     * @param avg  среднее значение, связанное с кодом; может быть {@code null}
     * @return текущий экземпляр {@code PropertyAnswer} для поддержки цепочки вызовов (fluent API)
     * @throws NullPointerException если {@code code} равен {@code null}
     */
    public PropertyAnswer addZipCode(String code, BigDecimal avg){
        answers.put(code, new FirstAnswer(avg));
        return this;
    }

    /**
     * Возвращает объект {@link FirstAnswer}, связанный с указанным кодом.
     *
     * @param code строковой ключ, по которому выполняется поиск
     * @return объект {@link FirstAnswer}, если найден; иначе {@code null}
     * @throws NullPointerException если {@code code} равен {@code null}
     */
    public FirstAnswer getAvgByCode(String code){
        FirstAnswer ans = answers.get(code);
        return answers.get(code);
    }
}
