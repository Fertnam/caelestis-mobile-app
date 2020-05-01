package ru.caelestis.restapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Класс, описывающий AsyncTask для загрузки изображений по ссылке
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public abstract class Image extends AsyncTask<String, Void, Bitmap> {
    /**
     * Поле, хранящее шаблон адреса для запроса
     */
    protected String url;

    /**
     * Метод, в котором описывается процесс загрузки изображения
     * @param data - массив с данными для загрузки
     * @return Пиксельное представление изображения
     */
    @Override
    protected Bitmap doInBackground(String ... data) {
        Bitmap imageBitmap = null;

        for (String param: data) {
            url = url.replaceFirst("::", param);
        }

        try {
            java.net.URL urlObject = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                imageBitmap = BitmapFactory.decodeStream(connection.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageBitmap;
    }

    /**
     * Абстрактный метод, в котором обрабатывается загруженное изображение
     * @param imageBitmap - пиксельное представление изображения
     */
    @Override
    protected abstract void onPostExecute(Bitmap imageBitmap);
}
