package ru.ibs.diploma.cache;

import java.math.BigDecimal;

/**
 * Немутабельный контейнер данных для хранения статистических показателей.
 * <p>
 * Этот record содержит агрегированные значения, полученные в результате анализа данных,
        * такие как средняя рыночная стоимость и среднее количество штрафов.
        * </p>
        * <p>
 * Поскольку {@link Statistics} является {@code record}, он автоматически:
        * <ul>
 *   <li>имеет приватные финальные поля для каждого компонента,</li>
        *   <li>генерирует конструктор, геттеры, методы {@code equals}, {@code hashCode} и {@code toString},</li>
        *   <li>является неизменяемым (immutable) и потокобезопасным при передаче неизменяемых значений.</li>
        * </ul>
        * </p>
        *
        * <p><strong>Пример использования:</strong></p>
        * <pre>{@code
 * BigDecimal avgValue = new BigDecimal("2_500_000.00");
 * BigDecimal avgFines = new BigDecimal("1.75");
 *
         * Statistics stats = new Statistics(avgValue, avgFines);
 *
         * System.out.println(stats.avgMarketValue()); // 2500000.00
        * System.out.println(stats.avgNumOfFines());  // 1.75
        * }</pre>
        *
        * @param avgMarketValue  средняя рыночная стоимость объектов;
 *                        может быть {@code null} в случае отсутствия данных
 * @param avgNumOfFines   среднее количество штрафов на объект;
 *                        может быть {@code null} в случае отсутствия данных
 *
 */
public record Statistics(
        BigDecimal avgMarketValue,
        BigDecimal avgNumOfFines
) {
}
