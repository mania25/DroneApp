package com.example.mania25.droneapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private ImageView imgZoomOutMap, imgZoomInMap, imgForward, imgBackward,
            imgLeft, imgRight, imgPowerOffDroneSystem;

    private TextView lblVehicleAltitude, lblVehicleMode, lblVehicleArmStatus,
            lblVehicleRollValue, lblVehiclePitchValue, lblVehicleYawValue;

    private RelativeLayout layoutGamepad;
    private LinearLayout layoutAltitudeSet;

    private EditText txtAltitude;
    private JoystickView joystickYawDirection;
    private Button btnDecrementSpeed, btnTakeOfforLand, btnIncrementSpeed;

    private GoogleMap mMap;
    private MarkerOptions gMapMarkerOptions;
    private Marker quadcopterMarker;

    private String mqttBrokerUrl = "tcp://172.31.0.220:1883";
    private MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        imgZoomInMap = (ImageView)findViewById(R.id.imgZoomInMap);
        imgZoomOutMap = (ImageView)findViewById(R.id.imgZoomOutMap);

        layoutAltitudeSet = (LinearLayout)findViewById(R.id.layoutAltitudeSet);
        layoutGamepad = (RelativeLayout)findViewById(R.id.layoutGamepad);

        imgForward = (ImageView)findViewById(R.id.imgForward);
        imgBackward = (ImageView)findViewById(R.id.imgBackward);
        imgRight = (ImageView)findViewById(R.id.imgRight);
        imgLeft = (ImageView)findViewById(R.id.imgLeft);
        imgPowerOffDroneSystem = (ImageView)findViewById(R.id.imgPowerOffDroneSystem);

        txtAltitude = (EditText) findViewById(R.id.txtAltitude);
        joystickYawDirection = (JoystickView)findViewById(R.id.joystickYawDirection);

        btnDecrementSpeed = (Button)findViewById(R.id.btnDecrementSpeed);
        btnTakeOfforLand = (Button)findViewById(R.id.btnTakeOfforLand);
        btnIncrementSpeed = (Button)findViewById(R.id.btnIncrementSpeed);


        lblVehicleAltitude = (TextView)findViewById(R.id.lblVehicleAltitude);
        lblVehicleMode = (TextView)findViewById(R.id.lblVehicleMode);
        lblVehicleArmStatus = (TextView)findViewById(R.id.lblVehicleArmStatus);

        lblVehicleRollValue = (TextView)findViewById(R.id.lblVehicleRollValue);
        lblVehiclePitchValue = (TextView)findViewById(R.id.lblVehiclePitchValue);
        lblVehicleYawValue = (TextView)findViewById(R.id.lblVehicleYawValue);

        imgZoomInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }
        });

        imgZoomOutMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
            }
        });

        imgForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                long currentTimeMillis = System.currentTimeMillis();

                if (action == MotionEvent.ACTION_UP) {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|BRAKE|" + String.valueOf(currentTimeMillis));
//                    publishMessage("/controlling-drone", "BRAKE");
//                } else {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|FORWARD|" + String.valueOf(currentTimeMillis));
//                    publishMessage("/controlling-drone", "FORWARD");
                }

                return true;
            }
        });

        imgBackward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                long currentTimeMillis = System.currentTimeMillis();

                if (action == MotionEvent.ACTION_UP) {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|BRAKE|" + String.valueOf(currentTimeMillis));
                } else {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|BACKWARD|" + String.valueOf(currentTimeMillis));
//                    publishMessage("/controlling-drone", "BACKWARD");
                }

                return true;
            }
        });

        imgLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                long currentTimeMillis = System.currentTimeMillis();

                if (action == MotionEvent.ACTION_UP) {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|BRAKE|" + String.valueOf(currentTimeMillis));
                } else {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|LEFT|" + String.valueOf(currentTimeMillis));
//                    publishMessage("/controlling-drone", "LEFT");
                }

                return true;
            }
        });

        imgRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                long currentTimeMillis = System.currentTimeMillis();

                if (action == MotionEvent.ACTION_UP) {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|BRAKE|" + String.valueOf(currentTimeMillis));
                } else {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|RIGHT|" + String.valueOf(currentTimeMillis));
//                    publishMessage("/controlling-drone", "RIGHT");
                }

                return true;
            }
        });

        imgPowerOffDroneSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Are You Sure ?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long currentTimeMillis = System.currentTimeMillis();
                                publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|LANDANDSHUTDOWN|" + String.valueOf(currentTimeMillis));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        joystickYawDirection.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                long currentTimeMillis = System.currentTimeMillis();

                if (strength >= 75) {
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|YAW:" + String.valueOf(angle) + "|" + String.valueOf(currentTimeMillis));
                }

                Log.d("onMove: ", String.valueOf(angle) + " °");
            }
        });

        btnTakeOfforLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.this.lblVehicleArmStatus.getText().toString().toUpperCase().equals("TRUE")) {
                    long currentTimeMillis = System.currentTimeMillis();
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|LAND|" + String.valueOf(currentTimeMillis));
//                    publishMessage("/controlling-drone", "LAND");
                } else {
                    long currentTimeMillis = System.currentTimeMillis();
                    publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|TAKEOFF|" + String.valueOf(currentTimeMillis));
//                    publishMessage("/controlling-drone", "TAKEOFF");
                }
            }
        });

        txtAltitude.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                long currentTimeMillis = System.currentTimeMillis();

                                publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|ALT:" + v.getText().toString() + "|" + String.valueOf(currentTimeMillis));

                                return false; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        btnIncrementSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimeMillis = System.currentTimeMillis();
                publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|INCR|" + String.valueOf(currentTimeMillis));
            }
        });

        btnDecrementSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimeMillis = System.currentTimeMillis();
                publishMessage("/controlling-drone", "ce28fd41-2e21-479b-b78e-77a88f759c0b|DECR" + "|" + String.valueOf(currentTimeMillis));
            }
        });

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), mqttBrokerUrl, "device-android-user-1");
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect){
                    Log.d("Reconnected to:", serverURI);
                } else {
                    Log.d("Connected To:", serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("Connected Lost:", cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                final String messageData = new String(message.getPayload());

                if (messageData.contains("|")) {
                    String[] vehicleAttitudeData = messageData.split("\\|");

                    String pitch = vehicleAttitudeData[0].split(":")[1];
                    String roll = vehicleAttitudeData[1].split(":")[1];
                    String yaw = vehicleAttitudeData[2].split(":")[1];

                    lblVehiclePitchValue.setText(pitch + " °");
                    lblVehicleRollValue.setText(roll + " °");
                    lblVehicleYawValue.setText(yaw + " °");
                }

                if (messageData.contains(";")) {
                    String[] vehicleLocationGlobalFrameData = messageData.split(";");

                    String latitude = vehicleLocationGlobalFrameData[0].split(":")[1];
                    String longitude = vehicleLocationGlobalFrameData[1].split(":")[1];
                    String altitude = vehicleLocationGlobalFrameData[2].split(":")[1];

                    LatLng newLatLngPosition = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    lblVehicleAltitude.setText(altitude + " m");
                    quadcopterMarker.setPosition(newLatLngPosition);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLngPosition));
                }

                if (messageData.contains("&")) {
                    String[] vehicleModeAndArmedStatus = messageData.split("&");

                    String vehicleMode = vehicleModeAndArmedStatus[0].split(":")[1];
                    String vehicleArmedStatus = vehicleModeAndArmedStatus[1].split(":")[1];

                    lblVehicleMode.setText(vehicleMode.toUpperCase());
                    lblVehicleArmStatus.setText(vehicleArmedStatus.toUpperCase());

                    if (vehicleArmedStatus.toUpperCase().equals("TRUE")) {
                        joystickYawDirection.setVisibility(View.VISIBLE);
                        layoutGamepad.setVisibility(View.VISIBLE);
                        btnDecrementSpeed.setVisibility(View.VISIBLE);
                        btnIncrementSpeed.setVisibility(View.VISIBLE);
                        layoutAltitudeSet.setVisibility(View.VISIBLE);
                        btnTakeOfforLand.setText("Land");
                    } else {
                        joystickYawDirection.setVisibility(View.GONE);
                        layoutGamepad.setVisibility(View.GONE);
                        btnDecrementSpeed.setVisibility(View.GONE);
                        btnIncrementSpeed.setVisibility(View.GONE);
                        layoutAltitudeSet.setVisibility(View.GONE);
                        btnTakeOfforLand.setText("Take Off");
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        mqttConnectOptions.setUserName("bbff39d0d3066758ffe55666762b3c8b150295b848cb6c871b79f2fff36c79fb");
        mqttConnectOptions.setPassword("50acea3098359517297e08040dc6bfc371d044190be6527c1ac29e078cbe8313".toCharArray());
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic("/drone-status");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
                    Log.d("Failed to connect to: ", mqttBrokerUrl);
                }
            });
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currLocation = new LatLng(-6.9761261, 107.6294003);
        gMapMarkerOptions = new MarkerOptions().position(currLocation)
                .title("Drone Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.quadcopter));
        quadcopterMarker = mMap.addMarker(gMapMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 18.0f));
        mMap.getUiSettings().setZoomGesturesEnabled(false);
    }

    public void subscribeToTopic(String mqttTopic){
        try {
            mqttAndroidClient.subscribe(mqttTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("onSuccess: ", "SUBSCRIBED");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Failed to subscribe", exception.getMessage());
                }
            });
        } catch (Exception ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessage(String mqttTopic, String command){

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(command.getBytes());
            mqttAndroidClient.publish(mqttTopic, message);
            Log.d("publishMessage: ", "Message Published");
            if(!mqttAndroidClient.isConnected()){
                Log.d("publishMessage: ", mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");;
            }
        } catch (Exception e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
