package ru.ibs.diploma.data;

import java.time.Instant;

/**
 * Запись о парковочном нарушении.
 * Содержит данные о штрафе: сумму, место (postIndex, state), время, причину, авто и идентификаторы.
 * Неизменяемый DTO.
 *
 * @param timestamp     время нарушения (может быть null)
 * @param moneyAmount   сумма штрафа
 * @param reason        причина штрафа
 * @param carId         ID автомобиля
 * @param state         штат/регион
 * @param violationId   ID нарушения
 * @param postIndex     почтовый индекс места нарушения
 */
public record Parking (
    Instant timestamp,
    int moneyAmount,
    String reason,
    long carId,
    String state,
    long violationId,
    String postIndex
){
    /**
     * Упрощённый конструктор: только postIndex, state и moneyAmount.
     * Остальные поля устанавливаются в null или 0.
     *
     * @param postIndex     почтовый индекс; не null
     * @param state         штат; не null
     * @param moneyAmount   сумма штрафа; >= 0
     * @throws NullPointerException     если postIndex или state — null
     * @throws IllegalArgumentException если moneyAmount < 0
     */
    public Parking(String postIndex, String state, int moneyAmount) {
        this(null, moneyAmount, "", 0, state, 0, postIndex);
    }
}
