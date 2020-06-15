package ru.caelestis;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.caelestis.entities.User;
import ru.caelestis.managements.SessionManagement;
import ru.caelestis.restapi.GET;

/**
 * Activity с формой авторизации
 * @autor Миколенко Евгений (Fertnam)
 * @version 2
 */
public class AuthorizationActivity extends AppCompatActivity implements SensorEventListener {
    /** Поле, хранящее время последнего обновления датчика */
    private long lastUpdate = 0;

    /** Поле, хранящее последнюю координату x */
    /** Поле, хранящее последнюю координату y */
    /** Поле, хранящее последнюю координату z */
    private float lastX, lastY, lastZ;

    /** Поле, хранящее порог чувствительности акселерометра */
    private static final int SHAKE_THRESHOLD = 600;

    /** Поле, хранящее объект для доступа к сенсорам устройства */
    private SensorManager sensorManager;

    /** Поле, хранящее объект для доступа к акслерометру */
    private Sensor accelerometer;

    /** Поле, хранящее TextView для отображения ошибок */
    /** Поле, хранящее TextView для отображения сообщения о возможности регистрации (с ссылкой на активити) */
    private TextView errorText, toRegisterActivityFromAuth;

    /** Поле, хранящее EditText для ввода логина */
    /** Поле, хранящее EditText для ввода пароля */
    private EditText usernameInput, passwordInput;

    /** Поле, хранящее кнопку авторизации */
    private Button authButton;

    /** Поле, хранящее авторизованного пользователя */
    private User user;

    /** Поле, хранящее менеджер для работы с сессиией */
    private SessionManagement sessionManagement;

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        sessionManagement = new SessionManagement(this);

        errorText = (TextView) findViewById(R.id.errorText);
        toRegisterActivityFromAuth = (TextView) findViewById(R.id.toRegisterActivityFromAuth);

        usernameInput = (EditText) findViewById(R.id.authUsername);
        passwordInput = (EditText) findViewById(R.id.authPassword);

        authButton = (Button) findViewById(R.id.authButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        fillToRegisterActivityFromAuth();
    }

    /**
     * Метод, выполняющийся при паузе activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Метод, выполняющийся при возобновлении работы activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Метод, который вешает ссылку на Activity регистрации в TextView
     */
    private void fillToRegisterActivityFromAuth() {
        SpannableString spannableString = new SpannableString("Нет аккаунта? Зарегистрироваться.");

        ClickableSpan clickableSpan = new ClickableSpan() {
            /**
             * Событие, которое произойдёт при клике по ссылке
             */
            @Override
            public void onClick(@NonNull View widget) {
                AuthorizationActivity.this.startActivity(new Intent(AuthorizationActivity.this, RegistrationActivity.class));
            }

            /**
             * Метод, выполняющий стилизацию ссылки
             */
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.yellow_bg));
            }
        };

        spannableString.setSpan(clickableSpan, 14, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        toRegisterActivityFromAuth.setText(spannableString);
        toRegisterActivityFromAuth.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Метод, выполняющийся при нажатии на кнопку авторизации
     */
    public void onAuthButtonClick(View view) {
        authButton.setEnabled(false);

        new AuthorizationAsyncTask().execute(usernameInput.getText().toString(), passwordInput.getText().toString());
    }

    /**
     * Метод, выполняющийся при нажатии на кнопку авторизации через vk
     */
    public void onVkAuthButtonClick(View view) {
        startActivity(new Intent(this, VKActivity.class));
    }

    /**
     * Метод, выполняющийся при нажатии на кнопку голосового ввода
     */
    public void onSpeechButtonClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        String defaultLaunguage = "en-US";

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, defaultLaunguage);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, defaultLaunguage);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, defaultLaunguage);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, defaultLaunguage);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Произнесите ваш логин");

        startActivityForResult(intent, 1);
    }

    /**
     * Метод для обработки результата с RecognizerIntent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            usernameInput.setText(result.get(0));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Метод, выполняющийся при изменении значений акселерометра
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x, y, z;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            long currentTime = System.currentTimeMillis(),
                 timeDifference = (currentTime - lastUpdate);

            if (timeDifference > 100) {
                lastUpdate = currentTime;

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDifference * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    onAuthButtonClick(null);
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    /**
     * Метод, выполняющийся при изменении точности показаний
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //НИЧЕГО
    }

    /**
     * AsyncTask для авторизации
     * @autor Миколенко Евгений (Fertnam)
     * @version 2
     */
    class AuthorizationAsyncTask extends GET {
        /**
         * Конструктор, в котором создаётся AsyncTask
         */
        public AuthorizationAsyncTask() {
            super();
            url = "https://caelestis.ru/auth/mobile/::/::";
        }

        /**
         * Метод, в котором обрабатывается ответ с сервера
         * @param serverAnswer - ответ с сервера в виде JSON-строки
         */
        @Override
        protected void onPostExecute(JSONObject serverAnswer) {
            errorText.setVisibility(View.VISIBLE);

            if (serverAnswer != null) {
                try {
                    if (serverAnswer.getBoolean("success")) {
                        user = new User(new JSONObject(serverAnswer.getString("comment")));
                        sessionManagement.saveSession(user);

                        if (sessionManagement.isSessionActive()) {
                            String toastMessage = sessionManagement.getAllSession().get("username").toString() + ", вы успешно авторизовались";

                            Toast toast = Toast.makeText(AuthorizationActivity.this, toastMessage, Toast.LENGTH_SHORT);
                            toast.show();

                            startActivity(new Intent(AuthorizationActivity.this, ProfileActivity.class));
                            finish();
                        }
                    } else {
                        errorText.setText(serverAnswer.getString("comment"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                errorText.setText("Возникла ошибка при подключении к серверу");
            }

            authButton.setEnabled(true);
        }
    }
}
