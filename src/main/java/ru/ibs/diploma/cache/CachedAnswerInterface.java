package ru.ibs.diploma.cache;

/**
 * Интерфейсб определяющий контракт для объектов, кэширующих ответы
 * <p>
 * Любой класс, реализующий этот интерфейс должен предоставить собственную
 * реализацию методов {@link #cacheAnswer(int, Answer)}, который отвечает за
 * сохранение ответа в кэш и {@link #searchCache(int)}, который отвечает за
 * поиск нужного закэшированного ответа.
 * </p>
 */
public interface CachedAnswerInterface {

    public void cacheAnswer(int number, Answer answer);

    public Answer searchCache(int number);
}
