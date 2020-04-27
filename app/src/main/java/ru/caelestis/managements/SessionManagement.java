package ru.caelestis.managements;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Map;
import ru.caelestis.entities.User;

/**
 * Менеджер для работы с сессией
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class SessionManagement {
    /** Поле, хранящее объект SharedPreferences для запоминания данных */
    private SharedPreferences sharedPreferences;

    /** Поле, хранящее объект SharedPreferences.Editor для записи данных */
    private SharedPreferences.Editor editor;

    /** Константа, хранящая имя файла, в котором будет сохранена сессия */
    private final String SESSION_NAME = "cs_session";

    /**
     * Конструктор, в котором создаётся менеджер сессии
     * @param context - контекст Activity
     */
    public SessionManagement(Context context) {
        sharedPreferences = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Метод для сохранения пользователя в сессии
     * @param user - пользователь
     */
    public void saveSession(User user){
        editor.putBoolean("is_auth", true);

        editor.putInt("id", user.getId());
        editor.putInt("balance", user.getBalance());
        editor.putInt("xf_user_id", user.getXfUserId());
        editor.putInt("group_id", user.getGroupId());

        editor.putString("username", user.getUsername());
        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());
        editor.putString("activation_code", user.getActivationCode());
        editor.putString("registration_time", user.getRegistrationTime());
        editor.putString("ban_reason", user.getBanReason());
        editor.putString("group_name", user.getGroupName());

        editor.apply();
    }

    /**
     * Метод для получения всех данных сессии
     * @return Данные сессии в формате "ключ -> значение"
     */
    public Map<String, ?> getAllSession() {
        return sharedPreferences.getAll();
    }

    /**
     * Метод для проверки активности текущей сессии
     * @return Результат проверки
     */
    public boolean isSessionActive() {
        return sharedPreferences.getBoolean("is_auth", false);
    }

    /**
     * Метод для уничтожения текущей сессии
     */
    public void removeSession(){
        editor.clear();
        editor.apply();
    }
}
