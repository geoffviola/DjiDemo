package com.ecolumbia.djidemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.FlightController.DJIFlightControllerDelegate;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.SDKManager.DJISDKManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FlightControllerCurrentStateFragment extends Fragment {

    final public int MSG_FLIGHT_CONTROLLER_CURRENT_STATE = 1;
    final public int MSG_FLIGHT_CONTROLLER_CURRENT_STATE_ERROR = 2;
    final public int MSG_FLIGHT_CONTROLLER_CURRENT_STATE_NO_CONNECTION = 3;
    TextView m_tv_FlightControllerCurrentState;

    public FlightControllerCurrentStateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_flight_controller_current_state, container, false);
        initUI(v);
        runFlightControllerCurrentState();
        return v;
    }


    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the flight controller state callback
        try {
            DJIAircraft djiAircraft = DjiApplication.getAircraftInstance();
            if (djiAircraft != null) {
                DJIFlightController flightController = djiAircraft.getFlightController();
                flightController.setUpdateSystemStateCallback(null);
            }
        } catch (Exception exception) {

        }

    }

    private void initUI(View v){
        m_tv_FlightControllerCurrentState = (TextView) v.findViewById(R.id.tvFlightControllerCurrentState);
    }

    private void runFlightControllerCurrentState() {
        try {
            DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();
            if (djiAircraft != null) {

                DJIFlightController flightController = djiAircraft.getFlightController();

                flightController.setUpdateSystemStateCallback(
                        new DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback() {

                            @Override
                            public void onResult(DJIFlightControllerDataType.DJIFlightControllerCurrentState
                                                         djiFlightControllerCurrentState) {
                                DJIFlightControllerDataType.DJILocationCoordinate3D threedee = djiFlightControllerCurrentState.getAircraftLocation();
                                Boolean blMotorsOn = djiFlightControllerCurrentState.areMotorsOn();
                                DJIFlightControllerDataType.DJIAircraftRemainingBatteryState djiBatteryState = djiFlightControllerCurrentState.getRemainingBattery();
                                Double dbSatelliteCount = djiFlightControllerCurrentState.getSatelliteCount();
                                DJIFlightControllerDataType.DJIAttitude djiAttitude = djiFlightControllerCurrentState.getAttitude();
                                Boolean blIsFlying = djiFlightControllerCurrentState.isFlying();
                                Boolean blIsFailsafe = djiFlightControllerCurrentState.isFailsafe();
                                Boolean blIsGoingHome = djiFlightControllerCurrentState.isGoingHome();
                                Boolean blIsHomePointSet = djiFlightControllerCurrentState.isHomePointSet();
                                Boolean blIsImuPreheating = djiFlightControllerCurrentState.isIMUPreheating();
                                Boolean blIsMultipModeOpen = djiFlightControllerCurrentState.isMultipModeOpen();
                                Boolean blIsReachLimitedHeight = djiFlightControllerCurrentState.isReachLimitedHeight();
                                Boolean blIsReachLimitedRadius = djiFlightControllerCurrentState.isReachLimitedRadius();
                                Boolean blIsUltrasonicBeingUsed = djiFlightControllerCurrentState.isUltrasonicBeingUsed();
                                Boolean blIsVisionSensorBeingUsed = djiFlightControllerCurrentState.isVisionSensorBeingUsed();
                                Boolean blIsUltrasonicError = djiFlightControllerCurrentState.isUltrasonicError();

                                Integer intGetAircraftHeadDirection = djiFlightControllerCurrentState.getAircraftHeadDirection();
                                String stFlightModeString = djiFlightControllerCurrentState.getFlightModeString();
                                Float flFlyTime = djiFlightControllerCurrentState.getFlyTime();
                                Integer intGoHomeHeight = djiFlightControllerCurrentState.getGoHomeHeight();
                                Integer intGoHomeMode = djiFlightControllerCurrentState.getGoHomeMode();
                                Float flHomePointAltitude = djiFlightControllerCurrentState.getHomePointAltitude();
                                double dbNoFlyZoneRadius = djiFlightControllerCurrentState.getNoFlyZoneRadius();
                                Float flUltrasonicHeight = djiFlightControllerCurrentState.getUltrasonicHeight();
                                Float flVelocityX = djiFlightControllerCurrentState.getVelocityX();
                                Float flVelocityY = djiFlightControllerCurrentState.getVelocityY();
                                Float flVelocityZ = djiFlightControllerCurrentState.getVelocityZ();

                                DJIFlightControllerDataType.DJIFlightControllerFlightMode flightMode = djiFlightControllerCurrentState.getFlightMode();
                                DJIFlightControllerDataType.DJIGoHomeStatus goHomeStatus = djiFlightControllerCurrentState.getGoHomeStatus();
                                DJIFlightControllerDataType.DJIGPSSignalStatus gpsSignalStatus = djiFlightControllerCurrentState.getGpsSignalStatus();
                                DJIFlightControllerDataType.DJILocationCoordinate2D homeLocation = djiFlightControllerCurrentState.getHomeLocation();
                                DJIFlightControllerDataType.DJIFlightOrientationMode orientationMode = djiFlightControllerCurrentState.getOrientaionMode();
                                DJIFlightControllerDataType.DJIFlightControllerSmartGoHomeStatus smartGoHomeStatus = djiFlightControllerCurrentState.getSmartGoHomeStatus();

                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putDouble("Latitude", threedee.getLatitude());
                                bundle.putDouble("Longitude", threedee.getLongitude());
                                bundle.putFloat("Altitude", threedee.getAltitude());
                                bundle.putBoolean("MotorsOn", blMotorsOn);
                                bundle.putInt("RemainingBatteryState", djiBatteryState.value());
                                bundle.putDouble("SatelliteCount", dbSatelliteCount);
                                bundle.putDouble("Pitch", djiAttitude.pitch);
                                bundle.putDouble("Roll", djiAttitude.roll);
                                bundle.putDouble("Yaw", djiAttitude.yaw);

                                bundle.putBoolean("IsFlying", blIsFlying);
                                bundle.putBoolean("IsFailsafe", blIsFailsafe);
                                bundle.putBoolean("IsGoingHome", blIsGoingHome);
                                bundle.putBoolean("IsHomePointSet", blIsHomePointSet);
                                bundle.putBoolean("IsImuPreheating", blIsImuPreheating);
                                bundle.putBoolean("IsMultipModeOpen", blIsMultipModeOpen);
                                bundle.putBoolean("IsReachLimitedHeight", blIsReachLimitedHeight);
                                bundle.putBoolean("IsReachLimitedRadius", blIsReachLimitedRadius);
                                bundle.putBoolean("IsUltrasonicBeingUsed", blIsUltrasonicBeingUsed);
                                bundle.putBoolean("IsVisionSensorBeingUsed", blIsVisionSensorBeingUsed);
                                bundle.putBoolean("IsUltrasonicError", blIsUltrasonicError);

                                bundle.putInt("GetAircraftHeadDirection", intGetAircraftHeadDirection);
                                bundle.putString("FlightModeString", stFlightModeString);
                                bundle.putFloat("FlyTime", flFlyTime);
                                bundle.putInt("GoHomeHeight", intGoHomeHeight);
                                bundle.putInt("GoHomeMode", intGoHomeMode);
                                bundle.putFloat("HomePointAltitude", flHomePointAltitude);
                                bundle.putDouble("NoFlyZoneRadius", dbNoFlyZoneRadius);
                                bundle.putFloat("UltrasonicHeight", flUltrasonicHeight);
                                bundle.putFloat("VelocityX", flVelocityX);
                                bundle.putFloat("VelocityY", flVelocityY);
                                bundle.putFloat("VelocityZ", flVelocityZ);

                                // Complete the more complex returned values from flight controller.
                                bundle.putString("FlightMode", flightMode.name());
                                bundle.putString("GoHomeStatus", goHomeStatus.name());
                                bundle.putString("GpsSignalStatus", gpsSignalStatus.name());
                                bundle.putDouble("HomeLatitude", homeLocation.getLatitude());
                                bundle.putDouble("HomeLongitude", homeLocation.getLongitude());
                                bundle.putString("OrientationMode", orientationMode.name());
                                bundle.putInt("SmartGoHomeStatus_BatteryPercentageToGoHome", smartGoHomeStatus.getBatteryPercentageNeededToGoHome());
                                bundle.putInt("SmartGoHomeStatus_RemainingFlightTime", smartGoHomeStatus.getRemainingFlightTime());
                                bundle.putInt("SmartGoHomeStatus_TimeNeededToGoHome", smartGoHomeStatus.getTimeNeededToGoHome());
                                bundle.putInt("SmartGoHomeStatus_TimeNeededToLandFromCurrentHeight", smartGoHomeStatus.getTimeNeededToLandFromCurrentHeight());
                                bundle.putFloat("SmartGoHomeStatus_MaxRadiusAircraftCanFlyAndGoHome", smartGoHomeStatus.getMaxRadiusAircraftCanFlyAndGoHome());
                                bundle.putBoolean("SmartGoHomeStatus_IsAircraftShouldGoHome", smartGoHomeStatus.isAircraftShouldGoHome());

                                msg.what = MSG_FLIGHT_CONTROLLER_CURRENT_STATE;
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            }
                        });

            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("FlightControllerCurrentStateNoConnection", "Flight Controller Current State - no connection available: ");
                msg.what = MSG_FLIGHT_CONTROLLER_CURRENT_STATE_NO_CONNECTION;
                mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("FlightControllerCurrentState", "Flight Controller Current State Error: " + e.getMessage() );
            msg.what = MSG_FLIGHT_CONTROLLER_CURRENT_STATE_ERROR;
            mHandler.sendMessage(msg);
        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String flightControllerCurrentState = "";
            switch (msg.what) {
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE:

                    Double dbLatitude = bundle.getDouble("Latitude");
                    Double dbLongitude = bundle.getDouble("Longitude");
                    Float flAltitude = bundle.getFloat("Altitude");
                    Boolean motorsOn = bundle.getBoolean("MotorsOn");
                    Integer iRemainingBatteryState = bundle.getInt("RemainingBatteryState");
                    String stRemainingBatteryState = "";
                    Double dbSatelliteCount = bundle.getDouble("SatelliteCount");
                    Double dbPitch = bundle.getDouble("Pitch");
                    Double dbRoll = bundle.getDouble("Roll");
                    Double dbYaw = bundle.getDouble("Yaw");
                    Boolean blIsFlying = bundle.getBoolean("IsFlying");
                    Boolean blIsFailsafe = bundle.getBoolean("IsFailsafe");
                    Boolean blIsGoingHome = bundle.getBoolean("IsGoingHome");
                    Boolean blIsHomePointSet = bundle.getBoolean("IsHomePointSet");
                    Boolean blIsImuPreheating = bundle.getBoolean("IsImuPreheating");
                    Boolean blIsMultipModeOpen = bundle.getBoolean("IsMultipModeOpen");
                    Boolean blIsReachLimitedHeight = bundle.getBoolean("IsReachLmitedHeight");
                    Boolean blIsReachLimitedRadius = bundle.getBoolean("IsReachLimitedRadius");
                    Boolean blIsUltrasonicBeingUsed = bundle.getBoolean("IsUltrasonicBeingUsed");
                    Boolean blIsVisionSensorBeingUsed = bundle.getBoolean("IsVisionSensorBeingUsed");
                    Boolean blIsUltrasonicError = bundle.getBoolean("IsUltrasonicError");

                    Integer intGetAircraftHeadDirection = bundle.getInt("GetAircraftHeadDirection");
                    String stFlightModeString = bundle.getString("FlightModeString");
                    Float flFlyTime = bundle.getFloat("FlyTime");
                    Integer intGoHomeHeight = bundle.getInt("GoHomeHeight");
                    Integer intGoHomeMode = bundle.getInt("GoHomeMode");
                    Float flHomePointAltitude = bundle.getFloat("HomePointAltitude");
                    Double dbNoFlyZoneRadius = bundle.getDouble("NoFlyZoneRadius");
                    Float flUltrasonicHeight = bundle.getFloat("UltrasonicHeight");
                    Float flVelocityX = bundle.getFloat("VelocityX");
                    Float flVelocityY = bundle.getFloat("VelocityY");
                    Float flVelocityZ = bundle.getFloat("VelocityZ");

                    String stFlightMode = bundle.getString("FlightMode");
                    String stGoHomeStatue = bundle.getString("GoHomeStatus");
                    String stGpsSignalStatus = bundle.getString("GpsSignalStatus");
                    Double dbHomeLatitude = bundle.getDouble("HomeLatitude");
                    Double dbHomeLongitude = bundle.getDouble("HomeLongitude");
                    String stOrientationMode = bundle.getString("OrientationMode");
                    Integer intSmartGoHomeStatus_BatteryPercentageToGoHome = bundle.getInt("SmartGoHomeStatus_BatteryPercentageToGoHome");
                    Integer intSmartGoHomeStatus_RemainingFlightTime = bundle.getInt("SmartGoHomeStatus_RemainingFlightTime");
                    Integer intSmartGoHomeStatus_TimeNeededToGoHome = bundle.getInt("SmartGoHomeStatus_TimeNeededToGoHome");
                    Integer intSmartGoHomeStatus_TimeNeededToLandFromCurrentHeight = bundle.getInt("SmartGoHomeStatus_TimeNeededToLandFromCurrentHeight");
                    Float flSmartGoHomeStatus_MaxRadiusAircraftCanFlyAndGoHome = bundle.getFloat("SmartGoHomeStatus_MaxRadiusAircraftCanFlyAndGoHome");
                    Boolean blSmartGoHomeStatus_IsAircraftShouldGoHome = bundle.getBoolean("SmartGoHomeStatus_IsAircraftShouldGoHome");


                    flightControllerCurrentState = "Update time: " + dateFormat.format(new Date()) + "\n";
                    flightControllerCurrentState += "Latitude: " + dbLatitude + "\n";
                    flightControllerCurrentState += "Longitude: " + dbLongitude + "\n";
                    flightControllerCurrentState += "Altitude: " + flAltitude + "\n";
                    flightControllerCurrentState += "Motors On: " + motorsOn + "\n";
                    if (iRemainingBatteryState == DJIFlightControllerDataType.DJIAircraftRemainingBatteryState.Normal.value()) {
                        stRemainingBatteryState = "Normal";
                    } else if (iRemainingBatteryState == DJIFlightControllerDataType.DJIAircraftRemainingBatteryState.Low.value()) {
                        stRemainingBatteryState = "Low";
                    } else if (iRemainingBatteryState == DJIFlightControllerDataType.DJIAircraftRemainingBatteryState.VeryLow.value()) {
                        stRemainingBatteryState = "Very Low";
                    } else if (iRemainingBatteryState == DJIFlightControllerDataType.DJIAircraftRemainingBatteryState.Reserved.value()) {
                        stRemainingBatteryState = "Reserved Battery State";
                    }

                    flightControllerCurrentState += "Remaining Battery: " + stRemainingBatteryState + "\n";
                    flightControllerCurrentState += "Satellite Count: " + dbSatelliteCount + "\n";
                    flightControllerCurrentState += "Pitch: " + dbPitch + "\n";
                    flightControllerCurrentState += "Roll: " + dbRoll + "\n";
                    flightControllerCurrentState += "Yaw: " + dbYaw + "\n";

                    flightControllerCurrentState += "is Flying: " + blIsFlying + "\n";
                    flightControllerCurrentState += "is Failsafe: " + blIsFailsafe + "\n";
                    flightControllerCurrentState += "is Going Home: " + blIsGoingHome + "\n";
                    flightControllerCurrentState += "is Home Point Set: " + blIsHomePointSet + "\n";
                    flightControllerCurrentState += "is IMU Preheating: " + blIsImuPreheating + "\n";
                    flightControllerCurrentState += "is Multi P Mode Open: " + blIsMultipModeOpen + "\n";
                    flightControllerCurrentState += "is Reach Limited Height: " + blIsReachLimitedHeight + "\n";
                    flightControllerCurrentState += "is Reach Limited Radius: " + blIsReachLimitedRadius + "\n";
                    flightControllerCurrentState += "is Ultrasonic Being Used: " + blIsUltrasonicBeingUsed + "\n";
                    flightControllerCurrentState += "is Vision Senso Being Used: " + blIsVisionSensorBeingUsed + "\n";
                    flightControllerCurrentState += "is Ultrasonic Error: " + blIsUltrasonicError + "\n";

                    flightControllerCurrentState += "Get Aircraft Head Direction: " + intGetAircraftHeadDirection + "\n";
                    flightControllerCurrentState += "Flight Mode String: " + stFlightModeString + "\n";
                    flightControllerCurrentState += "Fly Time: " + flFlyTime + "\n";
                    flightControllerCurrentState += "Go Home Height: " + intGoHomeHeight + "\n";
                    flightControllerCurrentState += "Go Home Mode: " + intGoHomeMode + "\n";
                    flightControllerCurrentState += "Home Point Altitude: " + flHomePointAltitude + "\n";
                    flightControllerCurrentState += "No Fly Zone Radius: " + dbNoFlyZoneRadius + "\n";
                    flightControllerCurrentState += "Ultrasonic Height: " + flUltrasonicHeight + "\n";
                    flightControllerCurrentState += "Velocity X: " + flVelocityX + "\n";
                    flightControllerCurrentState += "Velocity Y: " + flVelocityY + "\n";
                    flightControllerCurrentState += "Velocity Z: " + flVelocityZ + "\n";

                    flightControllerCurrentState += "Flight Mode: " + stFlightMode + "\n";
                    flightControllerCurrentState += "Go Home Status: " + stGoHomeStatue  + "\n";
                    flightControllerCurrentState += "GPS Signal Status: " + stGpsSignalStatus  + "\n";
                    flightControllerCurrentState += "Home Latitude: " + dbHomeLatitude + "\n";
                    flightControllerCurrentState += "Home Longitude: " + dbHomeLongitude + "\n";
                    flightControllerCurrentState += "Orientation Mode: " + stOrientationMode + "\n";
                    flightControllerCurrentState += "Smart Battery  - Percentage To Go Home: " + intSmartGoHomeStatus_BatteryPercentageToGoHome  + "\n";
                    flightControllerCurrentState += "Smart Battery  - Remaining Flight Time: " + intSmartGoHomeStatus_RemainingFlightTime  + "\n";
                    flightControllerCurrentState += "Smart Battery  - Time Needed to go Home: " + intSmartGoHomeStatus_TimeNeededToGoHome  + "\n";
                    flightControllerCurrentState += "Smart Battery  - Tm Needed to land from Cur Hght: " + intSmartGoHomeStatus_TimeNeededToLandFromCurrentHeight  + "\n";
                    flightControllerCurrentState += "Smart Battery  - Max Radius Can Fly and Go Home: " + flSmartGoHomeStatus_MaxRadiusAircraftCanFlyAndGoHome + "\n";
                    flightControllerCurrentState += "Smart Battery  - Is Aircraft should go home: " + blSmartGoHomeStatus_IsAircraftShouldGoHome  + "\n";
                    break;
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE_ERROR:
                    flightControllerCurrentState += "Flight Controller Current State Error: " + bundle.getString("FlightControllerCurrentStateError") + "\n";
                    break;
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE_NO_CONNECTION:
                    flightControllerCurrentState += bundle.getString("FlightControllerCurrentStateNoConnection") + "\n";
                    break;
            }
            m_tv_FlightControllerCurrentState.setText(flightControllerCurrentState);
            return false;
        }

    });

}