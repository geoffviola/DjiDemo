package com.ecolumbia.djidemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.Battery.DJIBattery;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.SDKManager.DJISDKManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class BatteryFragment extends Fragment {

    final public int MSG_BATTERY = 1;
    final public int MSG_BATTERY_ERROR = 2;
    final public int MSG_BATTERY_NO_CONNECTION = 3;
    TextView m_tv_BatteryStatus;
    public BatteryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_battery, container, false);
        initUI(v);
        return v;
    }

    @Override
    public void onResume() {
        runBatteryStatus();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the battery state callback
        try {
            DjiApplication.getProductInstance().getBattery()
                    .setBatteryStateUpdateCallback(null);
        } catch (Exception exception) {

        }

    }

    private void initUI(View v){
        m_tv_BatteryStatus = (TextView) v.findViewById(R.id.tvBatteryStatus);
    }

    private void runBatteryStatus() {
        try {
            DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();
            if (djiAircraft != null) {
                djiAircraft.getBattery().setBatteryStateUpdateCallback(new DJIBattery.DJIBatteryStateUpdateCallback() {
                    @Override
                    public void onResult(DJIBattery.DJIBatteryState djiBatteryState) {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("BatteryRemainingPercent", djiBatteryState.getBatteryEnergyRemainingPercent());
                        bundle.putInt("BatteryTemperature", djiBatteryState.getBatteryTemperature());
                        bundle.putInt("BatteryCurrentCurrent", djiBatteryState.getCurrentCurrent());
                        bundle.putInt("BatteryCurrentEnergy", djiBatteryState.getCurrentEnergy());
                        bundle.putInt("BatteryCurrentVoltage", djiBatteryState.getCurrentVoltage());
                        bundle.putInt("BatteryFullChargeEnergy", djiBatteryState.getFullChargeEnergy());
                        bundle.putInt("BatteryLifetimeRemainingPercent", djiBatteryState.getLifetimeRemainingPercent());
                        bundle.putInt("BatteryNumberOfDischarge", djiBatteryState.getNumberOfDischarge());
                        msg.what = MSG_BATTERY;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                });

            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("BatteryNoConnection", "Battery Status - no connection available: ");
                msg.what = MSG_BATTERY_NO_CONNECTION;
                mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("BatteryError", "Battery Error: " + e.getMessage() );
            msg.what = MSG_BATTERY_ERROR;
            mHandler.sendMessage(msg);
        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String batteryState = "";
            switch (msg.what) {
                case MSG_BATTERY:
                    batteryState = "Battery State Section: " + "\n";
                    batteryState += "Battery Update time: " + dateFormat.format(new Date()) + "\n";
                    batteryState += "Battery Remaining Percent: " + bundle.getInt("BatteryRemainingPercent") + "\n";
                    batteryState += "Battery Temperature: " + bundle.getInt("BatteryTemperature") + "\n";
                    batteryState += "Battery Current Current: " + bundle.getInt("BatteryCurrentCurrent") + "\n";
                    batteryState += "Battery Current Energy: " + bundle.getInt("BatteryCurrentEnergy") + "\n";
                    batteryState += "Battery Current Voltage: " + bundle.getInt("BatteryCurrentVoltage") + "\n";
                    batteryState += "Battery Full Charge Energy: " + bundle.getInt("BatteryFullChargeEnergy") + "\n";
                    batteryState += "Battery Lifetime Remaining Percent: " + bundle.getInt("BatteryLifetimeRemainingPercent") + "\n";
                    batteryState += "Battery Number of Discharge: " + bundle.getInt("BatteryNumberOfDischarge") + "\n";
                    break;
                case MSG_BATTERY_ERROR:
                    batteryState += "Battery Error: " + bundle.getString("BatteryError") + "\n";
                    break;
                case MSG_BATTERY_NO_CONNECTION:
                    batteryState += bundle.getString("BatteryNoConnection") + "\n";
                    break;
            }
            m_tv_BatteryStatus.setText(batteryState);
            return false;
        }

    });

}
