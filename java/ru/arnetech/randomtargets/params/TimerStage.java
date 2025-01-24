package ru.arnetech.randomtargets.params;

/**
 * Перечисление всех стадий таймера
 */
public enum TimerStage {
    MIN_DELAY,      //минимальная задержка
    RAND_DELAY,     //случайная задержка
    MEMORIZATION,   //запоминание
    EXTENDED,       //дополнительное время
    IMPLEMENTATION  //выполнение
}