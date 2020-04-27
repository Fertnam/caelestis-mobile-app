package ru.caelestis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import ru.caelestis.managements.SessionManagement;

/**
 * Activity с профилем пользователя
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class ProfileActivity extends AppCompatActivity {
    /**
     * Поле, хранящее объект класса TextView для вывода логина пользователя
     * Поле, хранящее объект класса TextView для вывода email пользователя
     * Поле, хранящее объект класса TextView для вывода группы пользователя
     * Поле, хранящее объект класса TextView для вывода времени регистрации пользователя
     * Поле, хранящее объект класса TextView для вывода баланса пользователя
     */
    private TextView usernameView, emailView, groupView, registrationTimeView, balanceView;

    /**
     * Поле, хранящее объект класса ImageView для вывода передней части скина пользователя
     * Поле, хранящее объект класса ImageView для вывода задней части скина пользователя
     */
    private ImageView frontSkinImage, backSkinImage;

    /** Поле, хранящее менеджер для работы с сессиией */
    private SessionManagement sessionManagement;

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManagement = new SessionManagement(this);

        frontSkinImage = (ImageView) findViewById(R.id.frontSkinImage);
        backSkinImage = (ImageView) findViewById(R.id.backSkinImage);

        fillSkins();

        usernameView = (TextView) findViewById(R.id.usernameView);
        emailView = (TextView) findViewById(R.id.emailView);
        groupView = (TextView) findViewById(R.id.groupView);
        registrationTimeView = (TextView) findViewById(R.id.registrationTimeView);
        balanceView = (TextView) findViewById(R.id.balanceView);

        fillProfile();
    }

    /**
     * Метод, подгружающий скины пользователя
     */
    private void fillSkins() {
        String username = sessionManagement.getAllSession().get("username").toString();

        new SkinDownloadAsyncTask().execute(username, "1", "205");
        new SkinDownloadAsyncTask().execute(username, "2", "205");
    }

    /**
     * Метод, заполняющий профиль пользователя
     */
    private void fillProfile() {
        Map<String, ?> sessionData = sessionManagement.getAllSession();

        usernameView.setText(sessionData.get("username").toString());
        emailView.setText(sessionData.get("email").toString());
        groupView.setText(sessionData.get("group_name").toString());
        registrationTimeView.setText(sessionData.get("registration_time").toString());
        balanceView.setText(sessionData.get("balance").toString() + " руб");
    }

    /**
     * Метод, выполняющийся при нажатии на кпоку выхода
     */
    public void onLogOutButtonClick(View view) {
        sessionManagement.removeSession();

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    /**
     * AsyncTask для загрузки скинов с сервера
     * @autor Миколенко Евгений (Fertnam)
     * @version 1
     */
    class SkinDownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * Поле, хранящее режим загрузки скина
         */
        private String mode;

        /**
         * Константа, хранящая адрес для запроса
         */
        private final String URL = "https://caelestis.ru/application/components/skin.php?user=:username&mode=:mode&size=:size";

        /**
         * Метод, в котором описывается процесс загрузки
         * @param data - массив с данными для загрузки
         * @return Пиксельное представление скина
         */
        @Override
        protected Bitmap doInBackground(String ... data) {
            Bitmap result = null;

            mode = data[1];

            String queryWithParams = URL.replace(":username", data[0]).replace(":mode", data[1]).replace(":size", data[2]);

            try {
                java.net.URL urlObject = new URL(queryWithParams);

                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        /**
         * Метод, в котором подставляются скины в ImageView
         * @param result - пиксельное представления изображения
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            switch (mode) {
                case "2": backSkinImage.setImageBitmap(result); break;
                case "1":
                default: frontSkinImage.setImageBitmap(result); break;
            }
        }
    }
}
