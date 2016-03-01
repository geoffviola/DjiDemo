package com.ecolumbia.djidemo;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import de.greenrobot.event.util.ErrorDialogManager;
import dji.midware.data.model.P3.DataOsdGetPushCommon;

public class SwitchboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switchboard);

        // Set up a fragment manager to add in ground station capabilities.
        // Each fragment will be one capability: ie. battery, flight controller, video...
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Comment or uncomment which fragment you want to display in the application
        BatteryFragment(ft);
        BasicFlightFragment(ft);
        FlightControllerCurrentStateFragment(ft);
        BasicVideoFragment(ft);
        CameraSystemUpdateFragment(ft);
        ShootPhotoFragment(ft);
        ShootVideoFragment(ft);
        GimbalPositionFragment(ft);
        ft.commit();

    }

    private void BatteryFragment(FragmentTransaction ft) {
        BatteryFragment batteryFragment = new BatteryFragment();
        ft.add(R.id.fragmentContainer, batteryFragment, "BatteryFragment");
    }

    private void FlightControllerCurrentStateFragment(FragmentTransaction ft) {
        FlightControllerCurrentStateFragment flightControllerCurrentStateFragment = new FlightControllerCurrentStateFragment();
        ft.add(R.id.fragmentContainer, flightControllerCurrentStateFragment, "FlightControllerCurrentStateFragment");
    }

    private void BasicFlightFragment(FragmentTransaction ft) {
        BasicFlightFragment basicFlightFragment = new BasicFlightFragment();
        ft.add(R.id.fragmentContainer, basicFlightFragment, "BasicFlightFragment");
    }

    private void BasicVideoFragment(FragmentTransaction ft) {
        BasicVideoFragment basicVideoFragment = new BasicVideoFragment();
        ft.add(R.id.fragmentContainer, basicVideoFragment, "BasicVideoFragment");
    }

    private void CameraSystemUpdateFragment(FragmentTransaction ft) {
        CameraSystemUpdateFragment cameraSystemUpdateFragment = new CameraSystemUpdateFragment();
        ft.add(R.id.fragmentContainer, cameraSystemUpdateFragment, "CameraSystemUpdateFragment");
    }

    private void ShootPhotoFragment(FragmentTransaction ft) {
        ShootPhotoFragment shootPhotoFragment = new ShootPhotoFragment();
        ft.add(R.id.fragmentContainer, shootPhotoFragment, "ShootPhotoFragment");
    }

    private void ShootVideoFragment(FragmentTransaction ft) {
        ShootVideoFragment shootVideoFragment = new ShootVideoFragment();
        ft.add(R.id.fragmentContainer, shootVideoFragment, "ShootVideoFragment");
    }

    private void GimbalPositionFragment(FragmentTransaction ft) {
        GimbalPositionFragment gimbalPositionFragment = new GimbalPositionFragment();
        ft.add(R.id.fragmentContainer, gimbalPositionFragment, "GimbalPositionFragment");
    }
}

