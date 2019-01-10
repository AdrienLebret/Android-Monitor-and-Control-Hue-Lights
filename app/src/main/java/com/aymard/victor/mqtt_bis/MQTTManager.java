package com.aymard.victor.mqtt_bis;

import android.content.Context;
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

/**
 * Created by Victor AYMARD on 10.01.2019.
 */
public class MQTTManager extends MqttAndroidClient {

    //MqttAndroidClient client;

    private String userName = "fwrpsmyo";
    private String password = "TFyMjVunjGCM";

    private String topic1 = "lamp/lamp1";
    private String topic2 = "lamp/lamp2";
    private String topic3 = "lamp/lamp3";
    private String topicGeneral = "lamp/#";

    Context content;

    int qos = 1;

    public MQTTManager(Context content, String serverURI, String clientId) {
        super(content, serverURI, clientId);
        content = content;
        this.subscribeInstance(content);
    }

    private void subscribeInstance(final Context content) {
        // CONNECTED PART
        // TUTO 1

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        try {
            IMqttToken token = connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        options.setUserName(userName);
        options.setPassword(password.toCharArray());



        try {
            IMqttToken token = connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(content, "We are connected", Toast.LENGTH_SHORT).show();
                    subscribeTopic(topic1);
                    subscribeTopic(topic2);
                    subscribeTopic(topic3);
                    Toast.makeText(content, "SUBCRIBE", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(content, "We are not connected", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    protected void subscribeTopic(String named){
        try {
            IMqttToken subToken = subscribe(named, qos);
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

    void publishWithinTopic(String topicName, String dataStr) {

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = dataStr.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            publish(topicName, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    void setQos(int qos) {
        qos = qos;
    }
}
