package com.example.mania25.droneapp.Utils;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by Abdurrahman on 09-May-17.
 */

public class AccessMqtt {
    private static MqttAndroidClient mqttClient = null;

    public boolean init(Context context, String mqttBrokerURL, String mqttClientId, String mqttAccountName, String mqttAccountPassword) {

        if ( null == mqttClient) {
            try {
                mqttClient = new MqttAndroidClient(context.getApplicationContext(), mqttBrokerURL, mqttClientId);

                if ( mqttClient != null)  {

                    MqttConnectOptions options = new MqttConnectOptions();

                    options.setUserName(mqttAccountName);
                    options.setPassword(mqttAccountPassword.toCharArray());
                    options.setCleanSession(true);

                    IMqttToken iMqttToken = mqttClient.connect(options);

                    iMqttToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d("onSuccess: ", "SUCCESS");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            exception.printStackTrace();
                            Log.d("onFailure: ", exception.getMessage());
                        }
                    });
                }
            }
            catch ( MqttException ex) {
                // log exception
                ex.printStackTrace();
                reset();
            }
        }

        return null != mqttClient;
    }

    public static void write( String mqttTopic, String mqttMessage ) {
        byte[] encodedPayload = new byte[0];

        if ( null != mqttClient ) {
            try {
                MqttMessage message = new MqttMessage(encodedPayload);
                mqttClient.publish(mqttTopic, message);
            }
            catch ( Exception ex ) {
                // log exception
            }
        }
    }

    public void reset( ) {

        if ( null != mqttClient ) {

            try {
                if ( mqttClient.isConnected() ) {
                    mqttClient.disconnect(0);
                }
            }
            catch ( MqttException ex) {
                // log exception
                ex.printStackTrace();
            }

            mqttClient = null;
        }
    }
}