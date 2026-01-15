package ru.ibs.diploma.cache;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Реализация интерфейса {@link Answer}, предназначенная для хранения числового результата
 * в виде объекта {@link BigDecimal} и вывода его на консоль.
 *
 * <p><strong>Пример:</strong>
 * <pre>
 * {@code
 * BigDecimal value = new BigDecimal("123.45");
 * Answer answer = new FirstAnswer(value);
 * answer.printAnswer(); // Выведет: 123.45
 * }
 * </pre>
 *
 * @see Answer
 */
public class FirstAnswer implements Answer{
    /**
     * Результат вычисления, представленный с высокой точностью.
     * Поле объявлено как final, что обеспечивает неизменяемость объекта.
     *
     * @see BigDecimal
     */
    private final BigDecimal result;

    /**
     * Конструктор, инициализирующий объект значением результата.
     *
     * @param result числовое значение результата; может быть {@code null},
     *               но в этом случае при выводе будет напечатано "null"
     */
    public FirstAnswer(BigDecimal result) {
        this.result = result;
    }

    /**
     * Выводит значение результата в консоль.
     * <p>
     * Реализует метод из интерфейса {@link Answer}.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void printAnswer() {
        System.out.println(result);
    }

    /**
     * Возвращает результат вычисления.
     *
     * @return значение результата в виде {@link BigDecimal}; может быть {@code null}
     */
    public BigDecimal getResult() {
        return result;
    }

    /**
     * Сравнивает текущий объект с указанным на равенство.
     * <p>
     * Два объекта {@code FirstAnswer} считаются равными, если их поля {@code result}
     * равны согласно сравнению через {@link Objects#equals(Object, Object)}.
     * </p>
     *
     * @param o объект, с которым сравнивается текущий
     * @return {@code true}, если объекты равны, иначе {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirstAnswer that = (FirstAnswer) o;
        return Objects.equals(result, that.result);
    }

    /**
     * Возвращает хеш-код для данного объекта.
     * <p>
     * Хеш-код вычисляется на основе значения поля {@code result} с использованием
     * {@link Objects#hashCode(Object)}.
     * </p>
     *
     * @return хеш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(result);
    }
}
