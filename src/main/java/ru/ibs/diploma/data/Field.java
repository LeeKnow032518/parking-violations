package ru.ibs.diploma.data;

import java.math.BigDecimal;

/**
 * Перечисление, представляющее определённые поля объекта недвижимости,
 * которые могут быть использованы в расчётах или вопросах системы.
 * <p>
 * Каждая константа enum соответствует конкретному измеримому параметру
 * и ассоциирована с:
 * <ul>
 *   <li>номером вопроса в анкете или системе запросов ({@link #getQuestionNumber()}),</li>
 *   <li>методом получения значения из объекта {@link Properties} ({@link #getValue(Properties)}).</li>
 * </ul>
 * </p>
 *
 * <p><strong>Поддерживаемые поля:</strong></p>
 * <ul>
 *   <li>{@link #MARKET_VALUE} — рыночная стоимость, вопрос №3</li>
 *   <li>{@link #LIVABLE_AREA} — жилая площадь, вопрос №4</li>
 * </ul>
 *
 * @see Properties
 * @see BigDecimal
 */
public enum Field {

    /**
     * Представляет поле "Рыночная стоимость" объекта недвижимости.
     * <p>
     * Ассоциировано с вопросом №3 в системе сбора данных.
     * Значение извлекается из метода {@link Properties#MarketValue()}.
     * </p>
     */
    MARKET_VALUE{
        @Override
        public int getQuestionNumber(){
            return 3;
        }

        @Override
        public BigDecimal getValue(Properties p){
            return p.MarketValue();
        }
    },

    /**
     * Представляет поле "Общая жилая площадь" объекта недвижимости.
     * <p>
     * Ассоциировано с вопросом №4 в системе сбора данных.
     * Значение извлекается из метода {@link Properties#TotalLivableArea()}.
     * </p>
     */
    LIVABLE_AREA{
        @Override
        public int getQuestionNumber(){
            return 4;
        }

        @Override
        public BigDecimal getValue(Properties p){return p.TotalLivableArea();}
    };

    /**
     * Возвращает порядковый номер вопроса, связанного с данным полем.
     *
     * @return номер вопроса (целое число)
     */
    public abstract int getQuestionNumber();

    /**
     * Извлекает числовое значение данного поля из переданного объекта {@link Properties}.
     *
     * @param p объект свойств недвижимости;
     * @return значение поля в виде {@link BigDecimal}; может быть {@code null};
     */
    public abstract BigDecimal getValue(Properties p);
}
