package com.aymard.victor.mqtt_bis;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/**
 * Created by Daniel IKKA on 10.01.2019.
 */
public class MQTTManager extends MqttAndroidClient {

    private static final String SERVER_URI = "tcp://m20.cloudmqtt.com:19003";
    private static final String USERNAME = "bdnsrfca";
    private static final String PASSWORD = "cfL0e0aBHBx8";
    private static final String CLIENT_ID = MqttClient.generateClientId();

//    private static final String SERVER_URI = "tcp://m15.cloudmqtt.com:11260";
//    private static final String USERNAME = "reeixoch";
//    private static final String PASSWORD = "qO0Llhkf4KVK";

    private String topic1 = "lamp/lamp1";
    private String topic2 = "lamp/lamp2";
    private String topic3 = "lamp/lamp3";
    private String topicInfos = "lamp/out";
    private String allTopics = "lamp/#";

    int qos = 1;

    /* CONSTRUCTOR */

    public MQTTManager(Context content) {
        super(content, SERVER_URI, CLIENT_ID);

        this.subscribeInstance(content);
    }

    /* PUBLICS FUNCTIONS */

    /** Function to send a message
     *
     * @Param : on the topic named: topicName
     * @Param : the string to send
     */
    public void publishWithinTopic(String topicName, String dataStr) {

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = dataStr.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            publish(topicName, message);

            // DEBUG CODE
            if (topicName == topic1) {
                Log.d("Send Message topic1", dataStr);
            } else if ( topicName == topic2) {
                Log.d("Send Message topic2", dataStr);
            } else if ( topicName == topic3) {
                Log.d("Send Message topic3", dataStr);
            }

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void setQos(int qos) {
        qos = qos;
    }


    /* PRIVATE FUNCTIONS */
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

        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(content, "We are connected", Toast.LENGTH_SHORT).show();
                    subscribeTopic(getAllTopic());
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

    private void subscribeTopic(String named){
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

    public String getTopic1() { return topic1; }

    public String getTopic2() { return topic2; }

    public String getTopic3() { return topic3; }

    public String getAllTopic() { return allTopics; }

    public String getTopicInfos() { return topicInfos; }
}
