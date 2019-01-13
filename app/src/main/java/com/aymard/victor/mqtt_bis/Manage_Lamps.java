package com.aymard.victor.mqtt_bis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aymard.victor.mqtt_bis.BLT.BluetoothController;
import com.aymard.victor.mqtt_bis.BLT.BluetoothDiscoveryDeviceListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adrien LEBRET on 29.12.2018.
 * activité permettant les interactions bluetooth
 * activation / désactivation / scan des appareils disponnibles
 */

public class Manage_Lamps extends AppCompatActivity implements MqttCallback, BluetoothDiscoveryDeviceListener {

    //=========================================
    // INFORMATIONS THAT WE NEED FOR THE LAMPS
    //=========================================

    int progressH1, progressH2, progressH3, progressHG;
    int progressS1, progressS2, progressS3, progressSG;
    int progressB1, progressB2, progressB3, progressBG;
    boolean lamp1_isOn, lamp2_isOn, lamp3_isOn;
    boolean isConnected1, isConnected2, isConnected3;

    MQTTManager cloudManager;
    BluetoothController bleController;

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
    // http://colorizer.org/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage__lamps);

        initialize();

        cloudManager = new MQTTManager(this);
        cloudManager.setCallback(this);

        BLEsetup();
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

                cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic1(), createMessage(lamp1_isOn,progressH1, progressS1, progressB1));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
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

                cloudManager.publishWithinTopic(cloudManager.getTopic2(), createMessage(lamp2_isOn,progressH2, progressS2, progressB2));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic3(), createMessage(lamp3_isOn,progressH3, progressS3, progressB3));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic3(), createMessage(lamp3_isOn,progressH3, progressS3, progressB3));
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
                cloudManager.publishWithinTopic(cloudManager.getTopic3(), createMessage(lamp3_isOn,progressH3, progressS3, progressB3));
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

    private void BLEsetup() {
        // [#11] Ensures that the Bluetooth is available on this device before proceeding.
        boolean hasBluetooth = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if(!hasBluetooth) {
            AlertDialog dialog = new AlertDialog.Builder(Manage_Lamps.this).create();
            dialog.setTitle("Problem with BLE");
            dialog.setMessage("BLE is not available");
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Closes the dialog and terminates the activity.
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            // Sets up the bluetooth controller.
            this.bleController = new BluetoothController(this, BluetoothAdapter.getDefaultAdapter(), this);
        }

        // If the bluetooth is not enabled, turns it on.
        if (!bleController.isBluetoothEnabled()) {
            Toast.makeText(this, "BLE is enabling", Toast.LENGTH_SHORT).show();
            bleController.turnOnBluetoothAndScheduleDiscovery();
        } else {
            //Prevents the user from spamming the button and thus glitching the UI.
            if (!bleController.isDiscovering()) {
                // Starts the discovery.
                Toast.makeText(this, "device discovery started", Toast.LENGTH_SHORT).show();
                bleController.startDiscovery();
            } else {
                Toast.makeText(this, "device discovery stopped", Toast.LENGTH_SHORT).show();
                bleController.cancelDiscovery();
            }
        }
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

        isConnected1 = true;
        isConnected2 = true;
        isConnected3 = true;

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


    /**
     --------------------------------
     BluetoothDiscoveryDeviceListener
     --------------------------------
     **/

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        Log.d("BLE DISCOVER :", device.getName() + ": "+ device.getAddress());
    }

    @Override
    public void onDeviceDiscoveryStarted() {
        Log.d("Debug BLE", "onDeviceDiscoveryStarted");
    }

    @Override
    public void setBluetoothController(BluetoothController bluetooth) {

    }

    @Override
    public void onDeviceDiscoveryEnd() {
        Log.d("Debug BLE", "onDeviceDiscoveryEnd");
        bleController.turnOnBluetoothAndScheduleDiscovery();
    }

    @Override
    public void onBluetoothStatusChanged() {

    }

    @Override
    public void onBluetoothTurningOn() {

    }

    @Override
    public void onDevicePairingEnded() {

    }
}

