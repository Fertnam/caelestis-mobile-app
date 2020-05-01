package ru.caelestis;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import ru.caelestis.restapi.GET;

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
     * @version 2
     */
    class ActivatationAsyncTask extends GET {
        /**
         * Конструктор, в котором создаётся AsyncTask
         */
        public ActivatationAsyncTask() {
            super();
            url = "https://caelestis.ru/activation/::";
        }

        /**
         * Метод, в котором обрабатывается ответ с сервера
         * @param serverAnswer - ответ с сервера в виде JSON-строки
         */
        @Override
        protected void onPostExecute(JSONObject serverAnswer) {
            String toastMessage = null;

            if (serverAnswer != null) {
                try {
                    toastMessage = serverAnswer.getString("comment");

                    if (serverAnswer.getBoolean("success")) {
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
