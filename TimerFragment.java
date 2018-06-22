package edu.neu.madcourse.michellelee.numad18s_michellelee;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerFragment extends Fragment {

    private AlertDialog mDialog;     // paused dialog
    public boolean isPaused = false; // paused status
    private long timeRemaining = 0;  // CountDownTimer remaining time

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.countdown_timer, container, false);

        // get reference of the XML layout's widgets
        final TextView tView = (TextView) rootView.findViewById(R.id.timer);
//        final Button btnStart = (Button) rootView.findViewById(R.id.btn_start);
        final Button btnPause = (Button) rootView.findViewById(R.id.btn_pause);

        // befeore start, pause and resume are disabled
        btnPause.setEnabled(false);

        // initial start; tell the user it is phase 1
        Toast.makeText(getActivity(), "PHASE 1", Toast.LENGTH_LONG).show();

        // once started, pause button is active
//        btnStart.setEnabled(false);
        btnPause.setEnabled(true);

        CountDownTimer timer;
        long millisInFuture = 182000; //30 seconds
        long countDownInterval = 1000; //1 second

        // Initialize a new CountDownTimer instance
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;

                // notifications for time
                if (millis >= 90000 && millis <= 91000) {                                       // if time is 1.5min
                    Toast.makeText(getActivity(), "PHASE 2", Toast.LENGTH_LONG).show();    // entering phase 2
                }
                if (millis >= 10000 && millis <= 11000) {                                       // if time is 10 seconds
                    Toast.makeText(getActivity(), "FINAL COUNTDOWN", Toast.LENGTH_LONG).show();    // entering final countdown
                }

                // display time in minutes and seconds
                String text = String.format(Locale.getDefault(),"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                if (isPaused) {   // cancel current instance if paused
                    cancel();
                } else {
                    tView.setText(text);        // display current time set above
                    timeRemaining = millisUntilFinished;    // store remaining time
                }

            }

            public void onFinish() {
                tView.setText("Times up!");     // alert user when finished

                //Enable the start button
//                btnStart.setEnabled(true);
                //Disable the pause, resume and cancel button
                btnPause.setEnabled(false);     // on finish, pause does not work
            }
        }.start();

        // Set a Click Listener for pause button
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = true;

//                btnStart.setEnabled(false);
                btnPause.setEnabled(false); // disable the pause button

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.paused_screen, null);     // create view for custom dialog
                builder.setCancelable(false);
                builder.setView(dialogView);    // set view to paused screen layout
                Button resume = (Button) dialogView.findViewById(R.id.btn_resume);
                resume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        //                btnStart.setEnabled(false);
                        btnPause.setEnabled(true);   // enable the pause button

                        // specify the current state is not paused
                        isPaused = false;

                        // initialize a new CountDownTimer instance
                        long millisInFuture = timeRemaining;
                        long countDownInterval = 1000;
                        new CountDownTimer(millisInFuture, countDownInterval) {
                            public void onTick(long millisUntilFinished) {
                                long millis = millisUntilFinished;

                                // notifications for time
                                if (millis >= 90000 && millis <= 91000) {                                       // if time is 1.5min
                                    Toast.makeText(getActivity(), "PHASE 2", Toast.LENGTH_LONG).show();    // entering phase 2
                                }
                                if (millis >= 10000 && millis <= 11000) {                                       // if time is 10 seconds
                                    Toast.makeText(getActivity(), "FINAL COUNTDOWN", Toast.LENGTH_LONG).show();    // entering final countdown
                                }

                                // display time in minutes and seconds
                                String text = String.format(Locale.getDefault(),"%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(millis),
                                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                                if (isPaused) { // pause requested
                                    cancel();
                                } else {
                                    tView.setText(text);
                                    timeRemaining = millisUntilFinished;    // remember time remaining
                                }
                            }

                            public void onFinish() {
                                tView.setText("Times up!");     // alert user when finished

                                // disable all buttons
                                btnPause.setEnabled(false);
                                //Enable the start button
//                        btnStart.setEnabled(true);
                            }
                        }.start();

                    }
                });
                mDialog = builder.show();
            }
        });

        return rootView;
    }

}