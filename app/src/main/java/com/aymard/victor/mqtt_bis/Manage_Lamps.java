package com.aymard.victor.mqtt_bis;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Adrien LEBRET, Daniel IKKA and Victor AYMARD on 29.12.2018.
 * activité permettant les interactions bluetooth
 * activation / désactivation / scan des appareils disponnibles
 */

//https://github.com/AltBeacon/android-beacon-library-reference/blob/master/app/src/main/java/org/altbeacon/beaconreference/RangingActivity.java

public class Manage_Lamps extends AppCompatActivity implements MqttCallback, BeaconConsumer {

    //=========================================
    // INFORMATIONS THAT WE NEED FOR THE LAMPS
    //=========================================

    protected static final String TAG = "MonitoringActivity";

    public static final int REQUEST_ACCES_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;

    int progressH1, progressH2, progressH3, progressHG;
    int progressS1, progressS2, progressS3, progressSG;
    int progressB1, progressB2, progressB3, progressBG;
    boolean lamp1_isOn, lamp2_isOn, lamp3_isOn;
    boolean isConnected1, isConnected2, isConnected3;

    String UUIDLamp1 = "75a88c0e-997b-4bf5-9a5e-97f5ffe33688";
    String UUIDLamp2 = "73737490-78d9-4645-a51c-1db8490b9090";
    String UUIDLamp3 = "8c6d4409-0372-4b47-be9a-687c73574ab4";


    MQTTManager cloudManager;
    //private BluetoothAdapter bluetoothAdapter;
    //private ArrayAdapter<String> listAdapter;

    // scan btn
    Button scanningBtn;

    // LAMP 1
    LinearLayout l1;
    SeekBar sbH1, sbS1, sbB1;
    TextView hue1, sat1, bri1;
    Button btn1;
    ImageView rect1A, rect1B;

    // LAMP 2
    LinearLayout l2;
    SeekBar sbH2, sbS2, sbB2;
    TextView hue2, sat2, bri2;
    Button btn2;
    ImageView rect2A, rect2B;

    // LAMP 3
    LinearLayout l3;
    SeekBar sbH3, sbS3, sbB3;
    TextView hue3, sat3, bri3;
    Button btn3;
    ImageView rect3A, rect3B;

    // LAMP G
    LinearLayout lG;
    SeekBar sbHG, sbSG, sbBG;
    TextView hueG, satG, briG;
    Button btnG;
    private BeaconManager beaconManager;
    // http://colorizer.org/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__lamps);

        initialize();

        cloudManager = new MQTTManager(this);
        cloudManager.setCallback(this);

        setupBLE();
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    private void setupBLE() {
       // bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // we create a simple array adapter to display devices detected
        //listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);

        // Put all the LED detector red !
        rect1A.setVisibility(View.INVISIBLE);
        rect1B.setVisibility(View.VISIBLE);

        rect2A.setVisibility(View.INVISIBLE);
        rect2B.setVisibility(View.VISIBLE);

        rect3A.setVisibility(View.INVISIBLE);
        rect3B.setVisibility(View.VISIBLE);

        // Put all the LED detector red !
        rect1A.setVisibility(View.INVISIBLE);
        rect1B.setVisibility(View.VISIBLE);

        rect2A.setVisibility(View.INVISIBLE);
        rect2B.setVisibility(View.VISIBLE);

        rect3A.setVisibility(View.INVISIBLE);
        rect3B.setVisibility(View.VISIBLE);

        //we check bluetooth state
        checkBluetoothState();

        // Turn on BLE
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
//            // mBlueIv.setImageResource(R.drawable.ic_action_on);
//        } else {
//            showToast("Bluetooth is already on");
//        }

//        //Start scanning
//        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//            // we check if coarse location must be asked
//            if (checkCoarseLocationPermission()) {
//                Log.d("Salope", "Je passe sans le btn");
//                listAdapter.clear();
//                bluetoothAdapter.startDiscovery();
//            }
//        } else {
//            checkBluetoothState();
//        }


        scanningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//                    // we check if coarse location must be asked
//                    if (checkCoarseLocationPermission()) {
//                        Log.d("Salope", "Je passe avec le btn");
//                        //listAdapter.clear();
//                        // Put all the LED detector red !
//                        rect1A.setVisibility(View.INVISIBLE);
//                        rect1B.setVisibility(View.VISIBLE);
//
//                        rect2A.setVisibility(View.INVISIBLE);
//                        rect2B.setVisibility(View.VISIBLE);
//
//                        rect3A.setVisibility(View.INVISIBLE);
//                        rect3B.setVisibility(View.VISIBLE);
//                        //bluetoothAdapter.startDiscovery();
//
//                        isConnected1 = false;
//                        isConnected2 = false;
//                        isConnected3 = false;
//                    }
//                } else {
//                    checkBluetoothState();
//                }
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
        // SCAN BTN
        //========

        scanningBtn = findViewById(R.id.scanningBtn);

        //========
        // LAMP 1
        //========

        rect1A = findViewById(R.id.rect1A);
        rect1B = findViewById(R.id.rect1B);



        l1 = findViewById(R.id.l1);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btn1 = findViewById(R.id.btn1);
        btn1.setText("LAMP1: OFF");
        /**
         * ADD ACTION
         */
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lamp1_isOn){
                    btn1.setText("LAMP1: OFF");
                    lamp1_isOn = false;
                } else {
                    btn1.setText("LAMP1: ON");
                    lamp1_isOn = true;
                    changeBackgroundColor(1, progressH1, progressS1, progressB1);
                }

                if (isConnected1) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
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
                if(isConnected1){
                    cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
                }
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
                if (isConnected1) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
                }

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
                if (isConnected1) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
                }
            }
        });

        changeBackgroundColor( 1 ,progressH1, progressS1, progressB1);

        //========
        // LAMP 2
        //========

        rect2A = (ImageView) findViewById(R.id.rect2A);
        rect2B = (ImageView) findViewById(R.id.rect2B);

        l2 = (LinearLayout) findViewById(R.id.l2);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setText("LAMP2: OFF");
        /**
         * ADD ACTION
         */

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lamp2_isOn){
                    btn2.setText("LAMP2: OFF");
                    lamp2_isOn = false;
                } else {
                    btn2.setText("LAMP2: ON");
                    lamp2_isOn = true;
                    changeBackgroundColor(2 ,progressH2, progressS2, progressB2);
                }
                if (isConnected2) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
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
                if (isConnected2) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
                }
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
                if (isConnected2) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
                }
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
                if (isConnected2) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
                }
            }
        });

        changeBackgroundColor(2 ,progressH2, progressS2, progressB2);

        //========
        // LAMP 3
        //========

        rect3A = (ImageView) findViewById(R.id.rect3A);
        rect3B = (ImageView) findViewById(R.id.rect3B);

        l3 = (LinearLayout) findViewById(R.id.l3);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setText("LAMP3: OFF");
        /**
         * ADD ACTION
         */

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lamp3_isOn){
                    btn3.setText("LAMP3: OFF");
                    lamp3_isOn = false;
                } else {
                    btn3.setText("LAMP3: ON");
                    lamp3_isOn = true;
                    changeBackgroundColor(3 ,progressH3, progressS3, progressB3);
                }

                if (isConnected3) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic3(), createMessage(lamp3_isOn,progressH3, progressS3, progressB3));
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
                if (isConnected3) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic3(), createMessage(lamp3_isOn,progressH3, progressS3, progressB3));
                }
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
                if (isConnected3) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic3(), createMessage(lamp3_isOn,progressH3, progressS3, progressB3));
                }
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
                if (isConnected3) {
                    cloudManager.publishWithinTopic(cloudManager.getTopic3(), createMessage(lamp3_isOn,progressH3, progressS3, progressB3));
                }
            }
        });

        changeBackgroundColor(3 ,progressH3, progressS3, progressB3);

        //========
        // LAMP G
        //========

        lG = (LinearLayout) findViewById(R.id.lG);
        //l1.setBackgroundColor(Color.parseColor("#b71a51")); //

        btnG = (Button) findViewById(R.id.btnG);
        btnG.setText("LAMPS CO: OFF");
        /**
         * ADD ACTION
         */

        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBackgroundColor(4 ,progressHG, progressSG, progressBG);
                if (btnG.getText().equals("LAMPS CO: ON")){
                    btnG.setText("LAMPS CO: OFF");
                    btn1.setText("LAMP1: OFF");
                    btn2.setText("LAMP2: OFF");
                    btn3.setText("LAMP3: OFF");
                    lamp1_isOn = false;
                    lamp2_isOn = false;
                    lamp3_isOn = false;
                } else {
                    btnG.setText("LAMPS CO: ON");
                    btn1.setText("LAMP1: ON");
                    btn2.setText("LAMP2: ON");
                    btn3.setText("LAMP3: ON");
                    lamp1_isOn = true;
                    lamp2_isOn = true;
                    lamp3_isOn = true;
                }

                sendAllMessages();
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
                sendAllMessages();
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
                sendAllMessages();
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
                sendAllMessages();
            }
        });
        changeBackgroundColor(4 ,progressHG, progressSG, progressBG);
    }
    private void changeBackgroundColor(int lamp,int hue, int saturation, int brightness) {

        String hexResutlt = hsvToRGB(hue,saturation,brightness);
        //String cutString = hexResutlt.substring(0, 7);

        /*
            DEBUG
        */
        //Log.d("JONATHAN", "H : " + hue + " S : " + saturation + " B : " + brightness);
        //Log.d("JONATHAN", "hsvToRGB : " + hexResutlt);
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

    public void sendAllMessages(){

        if(isConnected1){
            String dataToSend = createMessage(lamp1_isOn,progressH1, progressS1, progressB1);
            cloudManager.publishWithinTopic(cloudManager.getTopic1(), dataToSend);
        } if (isConnected2){
            String dataToSend = createMessage(lamp2_isOn,progressH2, progressS2, progressB2);
            cloudManager.publishWithinTopic(cloudManager.getTopic2(), dataToSend);
        } if (isConnected3){
            String dataToSend = createMessage(lamp3_isOn,progressH3, progressS3, progressB3);
            cloudManager.publishWithinTopic(cloudManager.getTopic3(), dataToSend);
        }
    }

    /**
     * Method of creation the JSON / String
     * Ex : {"on":true, "sat":254, "bri":254,"hue":10000}
     * int H, S, B
     * boolean connected
     */
    public String createMessage(boolean isConnected, int h, int s, int b ){
        h = h * 65535 / 360; // hue runs from 0 to 65535
        s = s * 255/100;
        b = b * 255/100;

        String connectedToString;
        if (isConnected){
            connectedToString = "true";
        } else {
            connectedToString = "false";
        }
        // Le message doit contenir les guillemets Attention !
        String msg ="{\"on\":" + connectedToString + ", \"sat\":" + s + ", \"bri\":" + b + ", \"hue\":" + h + "}";
        return msg;
    }



    /**
     -------------
     MqttCallBack
     -------------
     **/

    @Override
    public void connectionLost(Throwable cause) {
        Toast.makeText(this,"Connection Lost", Toast.LENGTH_LONG);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {


        Log.d("mes : ", "From: " + topic + " " + message.toString());

        if (topic == "lamp/out") {

            Log.d("mes : ", "yes");
            Log.d("mes : ", message.toString());

            JSONArray jsonData = new JSONArray(message.toString());

            updateInterface(jsonData);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Toast.makeText(this,"delivery Complete", Toast.LENGTH_LONG);
    }

    private void updateInterface(JSONArray data) throws JSONException {
        if (isConnected1) {
            JSONObject lampData = data.getJSONObject(0);

            Log.d("message", lampData.toString());

            // Button1 update
            if (lampData.getBoolean("on")) {
                btn1.setText("LAMP1: ON");
                lamp1_isOn = true;
                changeBackgroundColor(1, progressH1, progressS1, progressB1);
            } else {
                btn1.setText("LAMP1: OFF");
                lamp1_isOn = false;
            }

            // Sliders update
            progressS1 = lampData.getInt("sat");
            progressB1 = lampData.getInt("bri");
            progressH1 = lampData.getInt("hue");

            sbB1.setProgress(progressB1);
            sbH1.setProgress(progressH1);
            sbS1.setProgress(progressS1);

        } if (isConnected2) {
            JSONObject lampData = data.getJSONObject(1);

            // Button2 update
            if (lampData.getBoolean("on")) {
                btn2.setText("LAMP1: ON");
                lamp2_isOn = true;
                changeBackgroundColor(1, progressH1, progressS1, progressB1);
            } else {
                btn2.setText("LAMP1: OFF");
                lamp2_isOn = false;
            }

            // Sliders update
            progressS2 = lampData.getInt("sat");
            progressB2 = lampData.getInt("bri");
            progressH2 = lampData.getInt("hue");

            sbB2.setProgress(progressB2);
            sbH2.setProgress(progressH2);
            sbS2.setProgress(progressS2);
        } if (isConnected3) {
            JSONObject lampData = data.getJSONObject(2);

            // Button3 update
            if (lampData.getBoolean("on")) {
                btn3.setText("LAMP1: ON");
                lamp3_isOn = true;
                changeBackgroundColor(1, progressH1, progressS1, progressB1);
            } else {
                btn3.setText("LAMP1: OFF");
                lamp3_isOn = false;
            }

            // Sliders update
            progressS3 = lampData.getInt("sat");
            progressB3 = lampData.getInt("bri");
            progressH3 = lampData.getInt("hue");

            sbB3.setProgress(progressB3);
            sbH3.setProgress(progressH3);
            sbS3.setProgress(progressS3);
        }
    }

    /** ----------------
     *    Function BLE
     ---------------- **/

    @Override
    protected void onResume() {
        super.onResume();
        //we register a dedicated receiver for some Bluetooth actions
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(devicesFoundReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            checkBluetoothState();
        }
    }

    public String PCVictorAdresse = "44:85:00:19:CE:5D";
    public String IphoneDAdresse = "F0:98:9D:12:46:64";
    public String troisiemeDeviceAdresse = "34:12:F9:AF:2B:3E"; // Adrien LEBRET device



    // we need to implement our revceiver to get devices detected       // config de ce qui est lu ------------------------
    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            String action = intent.getAction();
//
//
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                if (PCVictorAdresse.equals(device.getAddress())) {
//                    showToast("Lamp 1 is detected !");
//                    Log.d("BLE : ", "lamp 1");
//                    isConnected1 = true;
//                    rect1A.setVisibility(View.VISIBLE);
//                    rect1B.setVisibility(View.INVISIBLE);
//                }
//                if (IphoneDAdresse.equals(device.getAddress())) {
//                    showToast("Lamp 2 is detected !");
//                    Log.d("BLE : ", "lamp 2");
//                    isConnected2 = true;
//                    rect2A.setVisibility(View.VISIBLE);
//                    rect2B.setVisibility(View.INVISIBLE);
//                }
//                if (troisiemeDeviceAdresse.equals(device.getAddress())) {
//                    showToast("Lamp 3 is detected !");
//                    Log.d("BLE : ", "lamp 3");
//                    isConnected3 = true;
//                    rect3A.setVisibility(View.VISIBLE);
//                    rect3B.setVisibility(View.INVISIBLE);
//                }
//            }
//            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                scanningBtn.setText("Scanning Bluetooth Devices");
//            }
//            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                scanningBtn.setText("Scanning in progres ...");
//            }
        }
    };

    private void checkBluetoothState() {
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth is not supported on your device !", Toast.LENGTH_SHORT).show();
//        } else {
//            if (bluetoothAdapter.isEnabled()) {
//                if (bluetoothAdapter.isDiscovering()) {
//                    Toast.makeText(this, "Device discovering process ...", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Bluetooth is activated", Toast.LENGTH_SHORT).show();
//                }
//                //  mBlueIv.setImageResource(R.drawable.ic_action_on);
//            } else {
//                Toast.makeText(this, "You need to activate Bluetooth", Toast.LENGTH_SHORT).show();
//                //Intent enableIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE); //on peut mettre ces lignes pour que lorsque l'on clique sur le btn recherche il active automatiquement le bluetooth et lance la recherche
//                //startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
//            }
//        }
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCES_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    //toast message function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    // BeconConsumer
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {


                if (beacons.size() > 0) {
                    Iterator iterator = beacons.iterator();

                    boolean altbeaconIsDetected[] = new boolean[]{false, false, false};
                    Log.d("pute", "10 balle la pipe");
                    while(iterator.hasNext()) {
                        Beacon beacon = (Beacon) iterator.next();

                        if(beacon.getId1().toString().equals(UUIDLamp1)) {
                            altbeaconIsDetected[0] = true;
                        } if (beacon.getId1().toString().equals(UUIDLamp2)) {
                            altbeaconIsDetected[1] = true;
                        } if (beacon.getId1().toString().equals(UUIDLamp3)) {
                            altbeaconIsDetected[2] = true;
                        }
                    }

                    int i = 0;

                    if (altbeaconIsDetected[i]) {

                        isConnected1 = true;
                        rect1A.setVisibility(View.VISIBLE);
                        rect1B.setVisibility(View.INVISIBLE);

                    } else {

                        isConnected1 = false;
                        rect1A.setVisibility(View.INVISIBLE);
                        rect1B.setVisibility(View.VISIBLE);
                    }

                    i = 1;

                    if (altbeaconIsDetected[i]) {

                        isConnected2 = true;
                        rect2A.setVisibility(View.VISIBLE);
                        rect2B.setVisibility(View.INVISIBLE);

                    } else {

                        isConnected2 = false;
                        rect2A.setVisibility(View.INVISIBLE);
                        rect2B.setVisibility(View.VISIBLE);
                    }

                    i = 2;

                    if (altbeaconIsDetected[i]) {

                        isConnected3 = true;
                        rect3A.setVisibility(View.VISIBLE);
                        rect3B.setVisibility(View.INVISIBLE);

                    } else {

                        isConnected3 = false;
                        rect3A.setVisibility(View.INVISIBLE);
                        rect3B.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }

    }
}