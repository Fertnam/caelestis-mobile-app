package ru.caelestis.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ru.caelestis.R;

/**
 * Fragment с логотипом
 * @autor Миколенко Евгений (Fertnam)
 * @version 1
 */
public class LogoFragment extends Fragment {
    /** Поле, хранящее ImageView для отображения логотипа*/
    private ImageView logoImage;

    /**
     * Метод, выполняющийся при создании fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logo, container, false);

        logoImage = (ImageView) view.findViewById(R.id.logoImage);
        logoImage.setImageResource(R.drawable.logo);

        return view;
    }
}
