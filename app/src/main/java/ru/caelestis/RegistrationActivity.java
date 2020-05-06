package ru.caelestis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import ru.caelestis.restapi.POST;

/**
 * Activity с формой регистрации
 * @autor Миколенко Евгений (Fertnam)
 * @version 2
 */
public class RegistrationActivity extends AppCompatActivity {
    /** Поле, хранящее TextView для отображения ошибок */
    /** Поле, хранящее TextView для отображения сообщения о возможности активации (с ссылкой на активити) */
    private TextView registerErrorText, toActivateActivityFromRegister;

    /** Поле, хранящее кнопку авторизации */
    private Button registerButton;

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registerButton = (Button) findViewById(R.id.registerButton);

        registerErrorText = (TextView) findViewById(R.id.registerErrorText);
        toActivateActivityFromRegister = (TextView) findViewById(R.id.toActivateActivityFromRegister);

        fillToActivationActivityFromRegister();
    }

    /**
     * Метод, который вешает ссылку на Activity активации в TextView
     */
    private void fillToActivationActivityFromRegister() {
        SpannableString spannableString = new SpannableString("Уже зарегистрированы? Активируйтесь.");

        ClickableSpan clickableSpan = new ClickableSpan() {
            /**
             * Событие, которое произойдёт при клике по ссылке
             */
            @Override
            public void onClick(@NonNull View widget) {
                RegistrationActivity.this.startActivity(new Intent(RegistrationActivity.this, ActivationActivity.class));
            }

            /**
             * Метод, выполняющий стилизацию ссылки
             */
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.yellow_bg));
            }
        };

        spannableString.setSpan(clickableSpan, 22, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        toActivateActivityFromRegister.setText(spannableString);
        toActivateActivityFromRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Метод, выполняющийся при нажатии на кнопку регистрации
     */
    public void onRegisterButtonClick(View view) {
        EditText usernameInput = (EditText) findViewById(R.id.registerUsername),
                 emailInput = (EditText) findViewById(R.id.registerEmail),
                 passwordInput = (EditText) findViewById(R.id.registerPassword),
                 repeatPasswordInput = (EditText) findViewById(R.id.registerRepeatPassword);

        registerButton.setEnabled(false);

        new RegistrationAsyncTask().execute(usernameInput.getText().toString(),
                                            emailInput.getText().toString(),
                                            passwordInput.getText().toString(),
                                            repeatPasswordInput.getText().toString());
    }

    /**
     * AsyncTask для регистрации
     * @autor Миколенко Евгений (Fertnam)
     * @version 1
     */
    class RegistrationAsyncTask extends POST {
        /**
         * Конструктор, в котором создаётся AsyncTask
         */
        public RegistrationAsyncTask() {
            super();
            url = "https://caelestis.ru/register/mobile";
            params = "username=::&email=::&password=::&repeat-password=::";
        }

        /**
         * Метод, в котором обрабатывается ответ с сервера
         * @param serverAnswer - ответ с сервера в виде JSON-строки
         */
        @Override
        protected void onPostExecute(JSONObject serverAnswer) {
            registerErrorText.setVisibility(View.VISIBLE);

            if (serverAnswer != null) {
                try {
                    String serverComment = serverAnswer.getString("comment");

                    if (serverAnswer.getBoolean("success")) {
                        Toast.makeText(RegistrationActivity.this, serverComment, Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        finish();
                    } else {
                        registerErrorText.setText(serverComment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                registerErrorText.setText("Возникла ошибка при подключении к серверу");
            }

            registerButton.setEnabled(true);
        }
    }
}
