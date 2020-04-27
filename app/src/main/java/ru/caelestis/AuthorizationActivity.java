package ru.caelestis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;
import ru.caelestis.entities.User;
import ru.caelestis.managements.SessionManagement;

/**
 * Activity с формой авторизации
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class AuthorizationActivity extends AppCompatActivity {
    /** Поле, хранящее ImageView для отображения логотипа */
    private ImageView logoImage;

    /** Поле, хранящее TextView для отображения ошибок */
    private TextView errorText;

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

        logoImage = (ImageView) findViewById(R.id.logoImage);
        logoImage.setImageResource(R.drawable.logo);
    }

    /**
     * Метод, выполняющийся при нажатии на кнопку авторизации
     */
    public void onAuthButtonClick(View view) {
        EditText UsernameInput = (EditText) findViewById(R.id.authUsername),
                 PasswordInput = (EditText) findViewById(R.id.authPassword);

        new AuthorizationAsyncTask().execute(UsernameInput.getText().toString(), PasswordInput.getText().toString());
    }

    /**
     * AsyncTask для авторизации
     * @autor Миколенко Евгений (Fertnam)
     * @version 1
     */
    class AuthorizationAsyncTask extends AsyncTask<String, Void, JSONObject> {
        /**
         * Константа, хранящая адрес для запроса
         */
        private final String URL = "https://caelestis.ru/authorization/mobile/:username/:password";

        /**
         * Метод, в котором описывается процесс авторизации
         * @param data - массив с данными для авторизации
         * @return Ответ с сервера в виде строки JSON
         */
        @Override
        protected JSONObject doInBackground(String ... data) {
            JSONObject result = null;

            String queryWithParams = URL.replace(":username", data[0]).replace(":password", data[1]);

            try {
                java.net.URL urlObject = new java.net.URL(queryWithParams);
                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

                connection.setDoInput(true);

                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Scanner Scanner = new Scanner(connection.getInputStream());

                    StringBuilder answer = new StringBuilder();

                    while (Scanner.hasNextLine()) {
                        answer.append(Scanner.nextLine());
                    }

                    result = new JSONObject(answer.toString());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        /**
         * Метод, в котором обрабатывается ответ с сервера
         * @param answer - ответ с сервера в виде JSON-строки
         */
        @Override
        protected void onPostExecute(JSONObject answer) {
            super.onPostExecute(answer);

            if (answer != null) {
                try {
                    if (answer.getBoolean("success")) {
                        user = new User(new JSONObject(answer.getString("comment")));
                        sessionManagement.saveSession(user);

                        if (sessionManagement.isSessionActive()) {
                            Intent intent = new Intent(AuthorizationActivity.this, ProfileActivity.class);

                            String toastMessage = sessionManagement.getAllSession().get("username").toString() + ", вы успешно авторизовались";
                            Toast toast = Toast.makeText(AuthorizationActivity.this, toastMessage, Toast.LENGTH_SHORT);
                            toast.show();

                            startActivity(intent);
                            finish();
                        }
                    } else {
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText(answer.getString("comment"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("Возникла ошибка при подключении к серверу");
            }
        }
    }
}
