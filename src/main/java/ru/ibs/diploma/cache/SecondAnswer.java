package ru.ibs.diploma.cache;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Реализация интерфейса {@link Answer}, предназначенная для хранения и вывода
 * ассоциативной коллекции данных, где строковый ключ (например, почтовый индекс)
 * сопоставляется с числовым значением типа {@link BigDecimal}.
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>{@code
 * Map<String, BigDecimal> data = new HashMap<>();
 * data.put("Region1", new BigDecimal("150.75"));
 * data.put("Region2", new BigDecimal("200.00"));
 *
 * SecondAnswer answer = new SecondAnswer(data);
 * answer.printAnswer();
 * // Вывод:
 * // Region1 150.75
 * // Region2 200.00
 * }</pre>
 *
 * @see Answer
 */
@Component
public class SecondAnswer implements Answer{

    /**
     * Карта, хранящая данные в виде пар "ключ — числовое значение".
     * <p>
     * Ключ — строковый идентификатор (например, почтовый индекс).
     * Значение — точное десятичное число, представленное через {@link BigDecimal}.
     * </p>
     */
    private Map<String, BigDecimal> result;

    /**
     * Конструктор, инициализирующий объект с заданной картой результатов.
     *
     * @param result карта, сопоставляющая строковые ключи с объектами {@link BigDecimal};
     */
    public SecondAnswer(Map<String, BigDecimal> result) {
        this.result = result;
    }

    /**
     * Выводит все пары "ключ значение" в консоль.
     * <p>
     * Каждая строка содержит ключ и соответствующее ему значение, разделённые пробелом.
     * </p>
     * <p>
     * Реализует метод из интерфейса {@link Answer}.
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    public void printAnswer() {
        for(String key : result.keySet()){
            System.out.println(key + " " + result.get(key));
        }
    }

    @Override
    public String getAnswer() {
        StringBuilder sb = new StringBuilder();

        for(String key : result.keySet()){
            sb.append(key).append(" ").append(result.get(key) ).append("\n");
        }

        return sb.toString();
    }

    /**
     * Сравнивает текущий объект с указанным на равенство.
     * <p>
     * Два объекта {@code SecondAnswer} считаются равными, если их поля {@code result}
     * равны согласно {@link Objects#equals(Object, Object)}. Сравнение учитывает
     * {@code null} и структурное равенство карт.
     * </p>
     *
     * @param o объект, с которым сравнивается текущий
     * @return {@code true}, если объекты равны, иначе {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecondAnswer that)) return false;
        return Objects.equals(result, that.result);
    }

    /**
     * Возвращает хеш-код для данного объекта на основе содержимого поля {@code result}.
     *
     * @return хеш-код, вычисленный через {@link Objects#hashCode(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(result);
    }
}
