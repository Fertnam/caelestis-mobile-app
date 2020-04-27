package ru.caelestis.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Класс для описания сущности пользователя
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class User {
    /**
     * Поле, хранящее идентификатор пользователя
     * Поле, хранящее баланс пользователя
     * Поля, хранящее идентификатор форумного аккаунта пользователя
     * Поле, хранящее идентификатор группы пользователя
     */
    private int id, balance, xfUserId, groupId;

    /**
     * Поле, хранящее идентификатор логин пользователя
     * Поле, хранящее email пользователя
     * Поля, хранящее хеш пароля пользователя
     * Поле, хранящее код активации пользователя
     * Поле, хранящее время регистрации пользователя
     * Поле, хранящее причину блокировки аккаунта пользователя (если таковая имеется)
     * Поля, хранящее имя группы пользователя
     */
    private String username, email, password, activationCode, registrationTime, banReason, groupName;

    /** Поле, хранящее список ip, привязанных к пользователю */
    private String[] ipList;

    /**
     * Конструктор, в котором создаётся пользователь на основании JSON-строки
     * @param data - данные пользователя в формате JSON
     * @throws JSONException Ошибка парсинга JSON
     */
    public User(JSONObject data) throws JSONException {
        id = data.getInt("id");
        balance = data.getInt("balance");
        xfUserId = data.getInt("xf_user_id");
        groupId = data.getInt("cs_group_id");

        username = data.getString("username");
        email = data.getString("email");
        password = data.getString("password");
        activationCode = data.getString("activation_code");
        registrationTime = data.getString("registration_time");
        banReason = data.getString("ban_reason");
        groupName = data.getString("group_name");

        JSONArray ipListJson = data.getJSONArray("ip_list");

        ipList = new String[ipListJson.length()];

        for (int i = 0; i < ipListJson.length(); i++) {
            ipList[i] = ipListJson.getString(i);
        }
    }

    /**
     * Геттеры
     */
    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public int getXfUserId() {
        return xfUserId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public String getBanReason() {
        return banReason;
    }

    public String getGroupName() {
        return groupName;
    }

    public String[] getIpList() {
        return ipList;
    }
}
