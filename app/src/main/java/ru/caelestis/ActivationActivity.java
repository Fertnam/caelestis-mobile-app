package ru.caelestis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

import ru.caelestis.entities.User;

/**
 * Activity с формой активации
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class ActivationActivity extends AppCompatActivity {
    /** Поле, хранящее кнопку активации */
    private Button activateButton;

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        activateButton = (Button) findViewById(R.id.activateButton);
    }

    /**
     * Метод, выполняющийся при нажатии на кнопку активации
     */
    public void onActivateButtonClick(View view) {
        EditText activateCodeInput = (EditText) findViewById(R.id.activateCode);

        activateButton.setEnabled(false);

        new ActivatationAsyncTask().execute(activateCodeInput.getText().toString());
    }

    /**
     * AsyncTask для активации
     * @autor Миколенко Евгений (Fertnam)
     * @version 1
     */
    class ActivatationAsyncTask extends AsyncTask<String, Void, JSONObject> {
        /**
         * Константа, хранящая адрес для запроса
         */
        private final String URL = "https://caelestis.ru/activation/:activation_code";

        /**
         * Метод, в котором описывается процесс активации
         * @param data - массив с данными для авторизации
         * @return Ответ с сервера в виде строки JSON
         */
        @Override
        protected JSONObject doInBackground(String... data) {
            JSONObject result = null;

            String queryWithParams = URL.replace(":activation_code", data[0]);

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

            String toastMessage = null;

            if (answer != null) {
                try {
                    toastMessage = answer.getString("comment");

                    if (answer.getBoolean("success")) {
                        startActivity(new Intent(ActivationActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                toastMessage = "Возникла ошибка при подключении к серверу";
            }

            Toast.makeText(ActivationActivity.this, toastMessage, Toast.LENGTH_SHORT).show();

            activateButton.setEnabled(true);
        }
    }
}
