package ru.caelestis.enums;

/**
 * Перечесление для работы с режимами загрузки скинов
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public enum SkinMode {
    /**
     * Поле, хранящее режим загрузки спереди
     * Поле, хранящее режим загрузки сзади
     * Поле, хранящее режим загрузки головы
     */
    FRONT(1), BACK(2), HEAD(3);

    /**
     * Поле, хранящее код режима загрузки
     */
    private int code;

    /**
     * Конструктор, в котором создаётся экземпляр режима загрузки
     * @param code - код режима загрузки
     */
    SkinMode(final int code) {
        this.code = code;
    }

    /**
     * Метод для возврата кода режима загрузки
     * @return Код режима загрузки
     */
    public int getCode() {
        return code;
    }
}