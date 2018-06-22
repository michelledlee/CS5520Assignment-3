package edu.neu.madcourse.michellelee.numad18s_michellelee;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SubmisisonButtons extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.submission_buttons, container, false);
        Button B1 = (Button) rootView.findViewById(R.id.B1);
        B1.setOnClickListener(new View.OnClickListener()      {
            @Override
            public void onClick(View v) {
                String toSubmit = "";
//                for (char c : GameFragment.tile1) {
//                    if (c != 0) {
//                        toSubmit += c;
//                    }
//                }
//                checkWord(toSubmit, TextView view);
            }
        });
        return rootView;
    }
}
