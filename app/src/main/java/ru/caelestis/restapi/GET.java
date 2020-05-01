package ru.caelestis.restapi;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

/**
 * Класс, описывающий AsyncTask на уровне GET-запросов
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public abstract class GET extends AsyncTask<String, Void, JSONObject> {
    /**
     * Поле, хранящее шаблон адреса для GET-запроса
     */
    protected String url;

    /**
     * Метод, в котором описывается процесс выполнения GET-запроса
     * @param data - массив с данными для запроса
     * @return Ответ с сервера в виде строки JSON
     */
    @Override
    protected JSONObject doInBackground(String ... data) {
        JSONObject serverAnswer = null;

        for (String param: data) {
            url = url.replaceFirst("::", param);
        }

        try {
            java.net.URL urlObject = new java.net.URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            connection.setDoInput(true);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Scanner Scanner = new Scanner(connection.getInputStream());

                StringBuilder answer = new StringBuilder();

                while (Scanner.hasNextLine()) {
                    answer.append(Scanner.nextLine());
                }

                serverAnswer = new JSONObject(answer.toString());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return serverAnswer;
    }

    /**
     * Абстрактный метод, в котором обрабатывается ответ с сервера
     * @param serverAnswer - ответ с сервера в виде JSON-строки
     */
    @Override
    protected abstract void onPostExecute(JSONObject serverAnswer);
}
