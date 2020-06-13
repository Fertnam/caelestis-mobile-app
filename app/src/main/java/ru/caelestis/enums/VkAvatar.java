package ru.caelestis.enums;

/**
 * Перечесление для работы с режимами загрузки аватарок ВК
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public enum VkAvatar {
    /**
     * Поле, хранящее режим загрузки аватарки текущего пользователя
     * Поле, хранящее режим загрузки аватарки друга
     */
    CURRENT(1), FRIEND(2);

    private int mode;

    VkAvatar(final int mode) {
        this.mode = mode;
    }
}
