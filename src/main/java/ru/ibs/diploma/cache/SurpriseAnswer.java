package ru.ibs.diploma.cache;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Реализация интерфейса {@link Answer}, предназначенная для хранения и вывода
 * статистики, сгруппированной по строковым ключам (например, почтовым индексам).
 * <p>
 * Каждый ключ ассоциирован с объектом {@link Statistics}, содержащим два значения:
 * среднюю рыночную стоимость и среднее количество штрафов.
 * </p>
 * <p>
 * При выводе с помощью метода {@link #printAnswer()} записи сортируются по возрастанию
 * значения {@code avgMarketValue} из объекта {@link Statistics}.
 * </p>
 * <p>
 * Внутреннее хранилище основано на {@link HashMap}, что обеспечивает быстрый доступ
 * по ключу, но не гарантирует порядок. Порядок определяется только в момент вывода.
 * </p>
 *
 * <p><strong>Пример вывода:</strong></p>
 * <pre>
 * 100000.00 0.50 REGION_A
 * 250000.50 1.20 REGION_B
 * 500000.00 3.00 REGION_C
 * </pre>
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>{@code
 * Map<String, Statistics> statsMap = new HashMap<>();
 * statsMap.put("REGION_A", new Statistics(new BigDecimal("100000.00"), new BigDecimal("0.50")));
 * statsMap.put("REGION_B", new Statistics(new BigDecimal("250000.50"), new BigDecimal("1.20")));
 *
 * SurpriseAnswer answer = new SurpriseAnswer(statsMap);
 * answer.printAnswer(); // Отсортировано по avgMarketValue
 * }</pre>
 *
 * @see Answer
 * @see Statistics
 */
public class SurpriseAnswer implements Answer{

    /**
     * Хранилище статистических данных, где ключ — строковый идентификатор,
     * а значение — объект {@link Statistics}, содержащий агрегированные показатели.
     * <p>
     * Используется {@link HashMap} для эффективного доступа. Порядок элементов
     * не сохраняется — сортировка выполняется только при выводе.
     * </p>
     */
    private Map<String, Statistics> answers = new HashMap<>();

    /**
     * Конструктор, инициализирующий объект с переданной картой статистики.
     *
     * @param answers карта, сопоставляющая строковые ключи с объектами {@link Statistics};
     *                может быть {@code null}, в этом случае будет использована пустая внутренняя карта
     */
    public SurpriseAnswer(Map<String, Statistics> answers) {
        this.answers = answers;
    }

    /**
     * Выводит все записи в отсортированном порядке: по возрастанию значения
     * {@code avgMarketValue} из объекта {@link Statistics}.
     * <p>
     * Для сравнения используется {@link Comparator#nullsLast(Comparator)},
     * поэтому записи с {@code null} в {@code avgMarketValue} выводятся в конце.
     * </p>
     * <p>
     * Формат вывода: <br>
     * {@code <avgMarketValue> <avgNumOfFines> <ключ>}
     * </p>
     * <p>
     * Если карта пуста, ничего не выводится.
     * </p>
     * <p>
     * Реализует метод из интерфейса {@link Answer}.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void printAnswer() {
        answers.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(
                        Comparator.comparing(
                                Statistics::avgMarketValue,
                                Comparator.nullsLast(BigDecimal::compareTo)
                        )
                ))
                .forEach(entry -> {
                    Statistics stats = entry.getValue();
                    String key = entry.getKey();
                    System.out.println(stats.avgMarketValue() + " " +
                            stats.avgNumOfFines() + " " + key);
                });
    }

    @Override
    public String getAnswer() {
        StringBuilder sb = new StringBuilder();

        answers.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(
                Comparator.comparing(
                    Statistics::avgMarketValue,
                    Comparator.nullsLast(BigDecimal::compareTo)
                )
            ))
            .forEach(entry -> {
                Statistics stats = entry.getValue();
                String key = entry.getKey();
                sb.append(stats.avgMarketValue()).append(" ")
                    .append(stats.avgNumOfFines()).append(" ").append(key).append("\n");
            });

        return sb.toString();
    }

    /**
     * Сравнивает текущий объект с указанным на равенство.
     * <p>
     * Два объекта {@code SurpriseAnswer} считаются равными, если их внутренние карты
     * {@code answers} равны по содержимому (включая ключи и значения).
     * Сравнение выполняется через {@link Objects#equals(Object, Object)}.
     * </p>
     *
     * @param o объект, с которым сравнивается текущий
     * @return {@code true}, если объекты равны, иначе {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurpriseAnswer that)) return false;
        return Objects.equals(answers, that.answers);
    }

    /**
     * Возвращает хеш-код, основанный на содержимом внутренней карты {@code answers}.
     *
     * @return хеш-код, вычисленный с помощью {@link Objects#hashCode(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(answers);
    }
}
