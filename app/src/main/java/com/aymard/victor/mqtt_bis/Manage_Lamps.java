package com.aymard.victor.mqtt_bis;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Manage_Lamps extends AppCompatActivity {

    int progressH1, progressH2, progressH3, progressHG = 360;
    int progressS1, progressS2, progressS3, progressSG = 100;
    int progressB1, progressB2, progressB3, progressBG = 100;

    // LAMP 1
    LinearLayout l1;
    SeekBar sbH1, sbS1, sbB1;
    TextView hue1, sat1, bri1;
    Button btn1;

    // http://colorizer.org/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__lamps);

        initialize();


    }

    /**
     * Initialisation
     */
    private void initialize() {


        //========
        // LAMP 1
        //========

        l1 = (LinearLayout) findViewById(R.id.l1);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btn1 = (Button) findViewById(R.id.btn1);
        /**
         * ADD ACTION
         */

        sbH1 = (SeekBar) findViewById(R.id.sbH1);
        sbH1.setMax(360);
        sbH1.setProgress(progressH1);

        hue1 = (TextView) findViewById(R.id.hue1);
        hue1.setText("H: " + progressH1);

        sbH1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressH1 = i;
                hue1.setText("H: " + progressH1);
                // change couleur
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        l1 = (LinearLayout) findViewById(R.id.l1);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btn1 = (Button) findViewById(R.id.btn1);
        /**
         * ADD ACTION
         */



        sbB1 = (SeekBar) findViewById(R.id.sbB1);
        sbB1.setMax(360);
        sbB1.setProgress(progressH1);

        bri1 = (TextView) findViewById(R.id.hue1);
        bri1.setText("B: " + progressH1);

        sbB1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressB1 = i;
                bri1.setText("B: " + progressB1);
                // change couleur
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbS1 = (SeekBar) findViewById(R.id.sbS1);
        sbS1.setMax(100);
        sbS1.setProgress(progressS1);

        sat1 = (TextView) findViewById(R.id.sat1);
        sat1.setText("S: " + progressS1);

        sbS1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressS1 = i;
                sat1.setText("S: " + progressS1);
                // change couleur
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //========
        // LAMP 2
        //========


        //========
        // LAMP 3
        //========


        //========
        // LAMP G
        //========

    }
}
