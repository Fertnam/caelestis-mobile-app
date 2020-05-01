package ru.caelestis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;
import ru.caelestis.enums.SkinMode;
import ru.caelestis.managements.SessionManagement;
import ru.caelestis.restapi.Image;

/**
 * Activity с профилем пользователя
 * @autor Миколенко Евгений (Fertnam)
 * @version 2
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

        new SkinDownloadAsyncTask(SkinMode.FRONT).execute(username, "205");
        new SkinDownloadAsyncTask(SkinMode.BACK).execute(username, "205");
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
     * @version 2
     */
    class SkinDownloadAsyncTask extends Image {
        /**
         * Поле, хранящее режим загрузки скина
         */
        private SkinMode mode;

        /**
         * Конструктор, в котором создаётся AsyncTask
         * @param mode - режим загрузки скина
         */
        public SkinDownloadAsyncTask(final SkinMode mode) {
            super();

            this.mode = mode;
            url = "https://caelestis.ru/application/components/skin.php?user=::&mode=" + mode.getCode() + "&size=::";
        }

        /**
         * Метод, в котором подставляются скины в ImageView
         * @param imageBitmap - пиксельное представления скина
         */
        @Override
        protected void onPostExecute(Bitmap imageBitmap) {
            switch (mode) {
                case BACK: backSkinImage.setImageBitmap(imageBitmap); break;
                case FRONT:
                default: frontSkinImage.setImageBitmap(imageBitmap); break;
            }
        }
    }
}
