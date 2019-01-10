package com.aymard.victor.mqtt_bis;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/**
 * Created by Adrien LEBRET on 29.12.2018.
 * activité permettant les interactions bluetooth
 * activation / désactivation / scan des appareils disponnibles
 */

public class Manage_Lamps extends AppCompatActivity {


    private Handler mHandler = new Handler();

    //=========================================
    // INFORMATIONS THAT WE NEED FOR THE LAMPS
    //=========================================

    int progressH1, progressH2, progressH3, progressHG = 360;
    int progressS1, progressS2, progressS3, progressSG = 100;
    int progressB1, progressB2, progressB3, progressBG = 100;
    boolean switch1, switch2, switch3 = true;
    boolean isConnected1, isConnected2, isConnected3 = false;

    MQTTManager cloudManager = null;

    String clientId = MqttClient.generateClientId();
    private String urlServer = "tcp://m23.cloudmqtt.com:10980";

    // LAMP 1
    LinearLayout l1;
    SeekBar sbH1, sbS1, sbB1;
    TextView hue1, sat1, bri1;
    Button btn1;

    // LAMP 2
    LinearLayout l2;
    SeekBar sbH2, sbS2, sbB2;
    TextView hue2, sat2, bri2;
    Button btn2;

    // LAMP 3
    LinearLayout l3;
    SeekBar sbH3, sbS3, sbB3;
    TextView hue3, sat3, bri3;
    Button btn3;

    // LAMP G
    LinearLayout lG;
    SeekBar sbHG, sbSG, sbBG;
    TextView hueG, satG, briG;
    Button btnG;

    // http://colorizer.org/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__lamps);
        mToastRunnable.run();
        Log.d("JONATHAN", "RUN APPELé");
        initialize();

        cloudManager = new MQTTManager(this, urlServer, clientId);
        cloudManager.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast.makeText(Manage_Lamps.this, new String(message.getPayload()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    /**
     * Initialisation of all the components of the IHM
     *
     * TO DO : onProgressChanged ADD THE POSSIBILITY TO SEND THE INFORMATION TO THE MQTT
     *
     * Normally, if we send new H S B and if the lamp is OFF, the color will not change
     * But if we press the button, everything will be fine
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

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn1.getText().equals("LAMP1: ON")){
                    btn1.setText("LAMP1: OFF");
                    switch1 = false;
                 }else{
                    btn1.setText("LAMP1: ON");
                    switch1 = true;
                    changeBackgroundColor(1, progressH1, progressS1, progressB1);
                }
            }
        });

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
                changeBackgroundColor( 1 ,progressH1, progressS1, progressB1);

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
                changeBackgroundColor( 1 ,progressH1, progressS1, progressB1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sbB1 = (SeekBar) findViewById(R.id.sbB1);
        sbB1.setMax(100);
        sbB1.setProgress(progressB1);

        bri1 = (TextView) findViewById(R.id.bri1);
        bri1.setText("B: " + progressB1);

        sbB1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressB1 = i;
                bri1.setText("B: " + progressB1);
                changeBackgroundColor( 1 ,progressH1, progressS1, progressB1);
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
        l2 = (LinearLayout) findViewById(R.id.l2);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btn2 = (Button) findViewById(R.id.btn2);
        /**
         * ADD ACTION
         */

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn2.getText().equals("LAMP2: ON")){
                    btn2.setText("LAMP2: OFF");
                    switch2 = false;
                }else{
                    btn2.setText("LAMP2: ON");
                    switch2 = true;
                    changeBackgroundColor(2 ,progressH2, progressS2, progressB2);
                }
            }
        });

        sbH2 = (SeekBar) findViewById(R.id.sbH2);
        sbH2.setMax(360);
        sbH2.setProgress(progressH2);

        hue2 = (TextView) findViewById(R.id.hue2);
        hue2.setText("H: " + progressH2);

        sbH2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressH2 = i;
                hue2.setText("H: " + progressH2);
                changeBackgroundColor(2 ,progressH2, progressS2, progressB2);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbS2 = (SeekBar) findViewById(R.id.sbS2);
        sbS2.setMax(100);
        sbS2.setProgress(progressS2);

        sat2 = (TextView) findViewById(R.id.sat2);
        sat2.setText("S: " + progressS2);

        sbS2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressS2 = i;
                sat2.setText("S: " + progressS2);
                changeBackgroundColor(2 ,progressH2, progressS2, progressB2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sbB2 = (SeekBar) findViewById(R.id.sbB2);
        sbB2.setMax(100);
        sbB2.setProgress(progressB2);

        bri2 = (TextView) findViewById(R.id.bri2);
        bri2.setText("B: " + progressB2);

        sbB2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressB2 = i;
                bri2.setText("B: " + progressB2);
                changeBackgroundColor(2 ,progressH2, progressS2, progressB2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //========
        // LAMP 3
        //========

        l3 = (LinearLayout) findViewById(R.id.l3);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btn3 = (Button) findViewById(R.id.btn3);
        /**
         * ADD ACTION
         */

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn3.getText().equals("LAMP3: ON")){
                    btn3.setText("LAMP3: OFF");
                    switch3 = false;
                }else{
                    btn3.setText("LAMP3: ON");
                    switch3 = true;
                    changeBackgroundColor(3 ,progressH3, progressS3, progressB3);
                }
            }
        });

        sbH3 = (SeekBar) findViewById(R.id.sbH3);
        sbH3.setMax(360);
        sbH3.setProgress(progressH3);

        hue3 = (TextView) findViewById(R.id.hue3);
        hue3.setText("H: " + progressH3);

        sbH3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressH3 = i;
                hue3.setText("H: " + progressH3);
                changeBackgroundColor(3 ,progressH3, progressS3, progressB3);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbS3 = (SeekBar) findViewById(R.id.sbS3);
        sbS3.setMax(100);
        sbS3.setProgress(progressS3);

        sat3 = (TextView) findViewById(R.id.sat3);
        sat3.setText("S: " + progressS3);

        sbS3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressS3 = i;
                sat3.setText("S: " + progressS3);
                changeBackgroundColor(3 ,progressH3, progressS3, progressB3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sbB3 = (SeekBar) findViewById(R.id.sbB3);
        sbB3.setMax(100);
        sbB3.setProgress(progressB3);

        bri3 = (TextView) findViewById(R.id.bri3);
        bri3.setText("B: " + progressB3);

        sbB3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressB3 = i;
                bri3.setText("B: " + progressB3);
                changeBackgroundColor(3 ,progressH3, progressS3, progressB3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //========
        // LAMP G
        //========

        lG = (LinearLayout) findViewById(R.id.lG);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btnG = (Button) findViewById(R.id.btnG);
        /**
         * ADD ACTION
         */

        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBackgroundColor(4 ,progressHG, progressSG, progressBG);
                if (btnG.getText().equals("LAMPS: ON")){
                    btnG.setText("LAMPS: OFF");
                    btn1.setText("LAMP1: OFF");
                    btn2.setText("LAMP2: OFF");
                    btn3.setText("LAMP3: OFF");
                    switch1 = false;
                    switch2 = false;
                    switch3 = false;
                }else{
                    btnG.setText("LAMPS: ON");
                    btn1.setText("LAMP1: ON");
                    btn2.setText("LAMP2: ON");
                    btn3.setText("LAMP3: ON");
                    switch1 = true;
                    switch2 = true;
                    switch3 = true;

                }


            }
        });

        sbHG = (SeekBar) findViewById(R.id.sbHG);
        sbHG.setMax(360);
        sbHG.setProgress(progressHG);

        hueG = (TextView) findViewById(R.id.hueG);
        hueG.setText("H: " + progressHG);

        sbHG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressHG = i;
                hueG.setText("H: " + progressHG);
                changeBackgroundColor(4 ,progressHG, progressSG, progressBG);
                sbH1.setProgress(progressHG);
                sbH2.setProgress(progressHG);
                sbH3.setProgress(progressHG);

                sbS1.setProgress(progressSG);
                sbS2.setProgress(progressSG);
                sbS3.setProgress(progressSG);

                sbB1.setProgress(progressBG);
                sbB2.setProgress(progressBG);
                sbB3.setProgress(progressBG);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbSG = (SeekBar) findViewById(R.id.sbSG);
        sbSG.setMax(100);
        sbSG.setProgress(progressSG);

        satG = (TextView) findViewById(R.id.satG);
        satG.setText("S: " + progressSG);

        sbSG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressSG = i;
                satG.setText("S: " + progressSG);
                changeBackgroundColor(4 ,progressHG, progressSG, progressBG);
                sbH1.setProgress(progressHG);
                sbH2.setProgress(progressHG);
                sbH3.setProgress(progressHG);

                sbS1.setProgress(progressSG);
                sbS2.setProgress(progressSG);
                sbS3.setProgress(progressSG);

                sbB1.setProgress(progressBG);
                sbB2.setProgress(progressBG);
                sbB3.setProgress(progressBG);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sbBG = (SeekBar) findViewById(R.id.sbBG);
        sbBG.setMax(100);
        sbBG.setProgress(progressBG);

        briG = (TextView) findViewById(R.id.briG);
        briG.setText("B: " + progressH1);

        sbBG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressBG = i;
                briG.setText("B: " + progressBG);
                changeBackgroundColor(4 ,progressHG, progressSG, progressBG);

                sbH1.setProgress(progressHG);
                sbH2.setProgress(progressHG);
                sbH3.setProgress(progressHG);

                sbS1.setProgress(progressSG);
                sbS2.setProgress(progressSG);
                sbS3.setProgress(progressSG);

                sbB1.setProgress(progressBG);
                sbB2.setProgress(progressBG);
                sbB3.setProgress(progressBG);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void changeBackgroundColor(int lamp,int hue, int saturation, int brightness) {

        String hexResutlt = hsvToRGB(hue,saturation,brightness);
        //String cutString = hexResutlt.substring(0, 7);


        Log.d("JONATHAN", "H : " + hue + " S : " + saturation + " B : " + brightness);

        Log.d("JONATHAN", "hsvToRGB : " + hexResutlt);

        //Log.d("JONATHAN", "CutString : " + cutString);


        switch (lamp){
            case 1:
                l1.setBackgroundColor(Color.parseColor(hexResutlt));
                break;
            case 2:
                l2.setBackgroundColor(Color.parseColor(hexResutlt));
                break;
            case 3:
                l3.setBackgroundColor(Color.parseColor(hexResutlt));
                break;
            case 4:
                l1.setBackgroundColor(Color.parseColor(hexResutlt));
                l2.setBackgroundColor(Color.parseColor(hexResutlt));
                l3.setBackgroundColor(Color.parseColor(hexResutlt));
                lG.setBackgroundColor(Color.parseColor(hexResutlt));
                break;

        }
    }



    /**
     * @param H : 0-360
     * @param S : 0-100
     * @param V : 0-100
     * @return color in hex string
     */
    public static String hsvToRGB(float H, float S, float V) {

        float R, G, B;

        H /= 360f;
        S /= 100f;
        V /= 100f;

        if (S == 0)
        {
            R = V * 255;
            G = V * 255;
            B = V * 255;
        } else {
            float var_h = H * 6;
            if (var_h == 6)
                var_h = 0; // H must be < 1
            int var_i = (int) Math.floor((double) var_h); // Or ... var_i =
            // floor( var_h )
            float var_1 = V * (1 - S);
            float var_2 = V * (1 - S * (var_h - var_i));
            float var_3 = V * (1 - S * (1 - (var_h - var_i)));

            float var_r;
            float var_g;
            float var_b;
            if (var_i == 0) {
                var_r = V;
                var_g = var_3;
                var_b = var_1;
            } else if (var_i == 1) {
                var_r = var_2;
                var_g = V;
                var_b = var_1;
            } else if (var_i == 2) {
                var_r = var_1;
                var_g = V;
                var_b = var_3;
            } else if (var_i == 3) {
                var_r = var_1;
                var_g = var_2;
                var_b = V;
            } else if (var_i == 4) {
                var_r = var_3;
                var_g = var_1;
                var_b = V;
            } else {
                var_r = V;
                var_g = var_1;
                var_b = var_2;
            }

            R = var_r * 255; // RGB results from 0 to 255
            G = var_g * 255;
            B = var_b * 255;
        }

        String rs = Integer.toHexString((int) (R));
        String gs = Integer.toHexString((int) (G));
        String bs = Integer.toHexString((int) (B));

        if (rs.length() == 1)
            rs = "0" + rs;
        if (gs.length() == 1)
            gs = "0" + gs;
        if (bs.length() == 1)
            bs = "0" + bs;
        return "#" + rs + gs + bs;
    }


    /**
     * Method that will send every second a message to the CLOUD
     */

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("JONATHAN", "ON EST DANS LE RUN");
            sendMessage();
            mHandler.postDelayed(this, 1000);
        }
    };

    public void sendMessage(){


        if(isConnected1) {


        }
        if (isConnected2){

        }
        if (isConnected3){

        }
    }

    /**
     * Method of creation the JSON / String
     */
    public String createMessage(){
        String msg ="";

        return msg;

    }



}
