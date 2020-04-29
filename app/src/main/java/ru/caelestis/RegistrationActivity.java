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
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity с формой регистрации
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class RegistrationActivity extends AppCompatActivity {
    /** Поле, хранящее TextView для отображения сообщения о возможности активации (с ссылкой на активити) */
    private TextView toActivateActivityFromRegister;

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

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
}
