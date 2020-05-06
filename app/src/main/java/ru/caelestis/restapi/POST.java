package ru.caelestis.restapi;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

/**
 * Класс, описывающий AsyncTask на уровне POST-запросов
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public abstract class POST extends AsyncTask<String, Void, JSONObject> {
    /**
     * Поле, хранящее шаблон адреса для POST-запроса
     * Поле, хранящее шаблон аргументов для POST-запроса
     */
    protected String url, params;

    /**
     * Метод, в котором описывается процесс выполнения POST-запроса
     * @param data - массив с данными для запроса
     * @return Ответ с сервера в виде строки JSON
     */
    @Override
    protected JSONObject doInBackground(String ... data) {
        JSONObject serverAnswer = null;

        for (String param: data) {
            params = params.replaceFirst("::", param);
        }

        try {
            java.net.URL urlObject = new java.net.URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            connection.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.getOutputStream().write(params.getBytes("UTF-8"));

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
