package ru.caelestis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

/**
 * Activity с формой активации
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class ActivationActivity extends AppCompatActivity {
    /** Поле, хранящее ImageView для отображения логотипа */
    private ImageView logoImage;

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        logoImage = (ImageView) findViewById(R.id.logoImageInActivate);
        logoImage.setImageResource(R.drawable.logo);
    }
}
