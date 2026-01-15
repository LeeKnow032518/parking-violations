package ru.ibs.diploma.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Реализация интерфейса {@link CachedAnswerInterface} для кэширования результатов анализа.
 * <p>
 * Хранит объекты {@link Answer} в памяти с ключом-идентификатором типа {@code int}.
 * Предназначен для оптимизации производительности — избегает повторных вычислений и чтения файлов.
 * <p>
 * Класс помечен аннотацией {@link Component}, поэтому автоматически регистрируется Spring как бин.
 * <p>
 * <b>Потокобезопасность:</b> не гарантируется. При работе в многопоточной среде требуется внешняя синхронизация.
 *
 * @author Твое Имя  // ← Замени на своё имя
 * @version 1.0
 * @since 2025-04-05
 *
 * @see CachedAnswerInterface
 * @see Answer
 */
@Component
public class CachedAnswers implements CachedAnswerInterface{

    private Map<Integer, Answer> answers = new HashMap<>();

    /**
     * Сохраняет результат анализа в кэше под указанным номером.
     * <p>
     * Если элемент с таким ключом уже существует, он будет заменён.
     *
     * @param number уникальный идентификатор запроса:
     *               <ul>
     *                 <li>{@code 1} — общее население</li>
     *                 <li>{@code 2} — средние штрафы на душу</li>
     *                 <li>{@code 3} — средняя рыночная стоимость</li>
     *                 <li>{@code 4} — средняя жилая площадь</li>
     *                 <li>{@code 5} — общая рыночная стоимость на душу</li>
     *                 <li>{@code 6} — surprise option</li>
     *               </ul>
     * @param answer объект результата, не должен быть {@code null}
     *
     * <p><strong>Пример использования:</strong>
     * <pre>{@code
     * Answer total = new FirstAnswer(new BigDecimal("1000000"));
     * cachedAnswers.cacheAnswer(1, total);
     * }</pre>
     */
    public void cacheAnswer(int number, Answer answer){
        answers.put(number, answer);
    }

    /**
     * Возвращает закэшированный ответ по его номеру.
     *
     * @param number идентификатор запроса (см. {@link #cacheAnswer(int, Answer)})
     * @return объект {@link Answer}, если найден; иначе — {@code null}
     *
     * <p><strong>Пример проверки:</strong>
     * <pre>{@code
     * Answer cached = cachedAnswers.searchCache(1);
     * if (cached != null) {
     *     System.out.println("Результат в кэше: " + cached.getAvgMarketValue());
     * }
     * }</pre>
     */
    public Answer searchCache(int number){
        return answers.get(number);
    }
}
