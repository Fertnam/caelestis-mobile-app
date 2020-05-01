package ru.caelestis;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import ru.caelestis.managements.SessionManagement;

/**
 * Главный activity, с которого идёт перенаправление на другие activity в зависимости от существования сессии
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class MainActivity extends AppCompatActivity {
    /** Поле, хранящее менеджер для работы с сессиией */
    private SessionManagement sessionManagement;

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManagement = new SessionManagement(this);

        Intent intent = sessionManagement.isSessionActive() ? new Intent(this, ProfileActivity.class) : new Intent(this, AuthorizationActivity.class);

        startActivity(intent);
        finish();
    }
}
