package com.aymard.victor.mqtt_bis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private Button btn1;
    private Button btn2;


    private String userName = "fwrpsmyo";
    private String password = "TFyMjVunjGCM";

    String clientId = MqttClient.generateClientId();
    final  MqttAndroidClient client =
            new MqttAndroidClient(MainActivity.this, "tcp://m23.cloudmqtt.com:10980",
                    clientId);

    String topic = "foo/bar";
    int qos = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);






        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // CONNECTED PART
                // TUTO 1

                MqttConnectOptions options = new MqttConnectOptions();
                options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
                try {
                    IMqttToken token = client.connect(options);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                options.setUserName(userName);
                options.setPassword(password.toCharArray());



                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Toast.makeText(MainActivity.this, "We are connected", Toast.LENGTH_SHORT).show();
                            subscribe();
                            Toast.makeText(MainActivity.this, "SUBCRIBE", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Toast.makeText(MainActivity.this, "We are not connected", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }


                startActivity(new Intent(MainActivity.this, Manage_Lamps.class));

                // TUTO 2
/*
                */


            }
        });




        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast.makeText(MainActivity.this, new String(message.getPayload()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "foo/bar";
                String payload = "the payload";
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void subscribe(){
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(MainActivity.this, , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
