package ru.ibs.diploma.data;

import java.math.BigDecimal;

/**
 * DTO для данных об объекте недвижимости.
 *
 * @param MarketValue       рыночная стоимость; может быть null
 * @param TotalLivableArea  общая жилая площадь в кв. метрах; может быть null
 * @param ZipCode           почтовый индекс; может быть null
 */
public record Properties(
    BigDecimal MarketValue,
    BigDecimal TotalLivableArea,
    String ZipCode
) {}
