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

import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.Products.DJIAircraft;


/**
 * A simple {@link Fragment} subclass.
 */
public class GimbalPositionFragment extends Fragment {

    private TextView m_tvGimbalCurrentPitch;
    private final int MSG_GIMBAL_CURRENT_PITCH = 1;
    private final int MSG_GIMBAL_CURRENT_PITCH_ERROR = 2;

    public GimbalPositionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gimbal_position, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InitUi(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the update gimbal callback
        try {
            DJIAircraft aircraft = DjiApplication.getAircraftInstance();
            if (aircraft != null) {
                DJIGimbal gimbal = aircraft.getGimbal();
                if (gimbal != null ) {
                    gimbal.setGimbalStateUpdateCallback(null);
                }
            }
        } catch (Exception e) {

        }
    }

    private void InitUi(View v){
        m_tvGimbalCurrentPitch = (TextView) v.findViewById(R.id.tvGimbalCurrentPitch);

        // Run the callback to retrieve the current gimbal angle.
        // This is just for the phantom 3 pro - which only has the pitch. The phantom 3 pro does not have the roll or yaw values.
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        if (aircraft != null ) {
            DJIGimbal gimbal = aircraft.getGimbal();
            if (gimbal != null ) {
                gimbal.setGimbalStateUpdateCallback(
                        new DJIGimbal.GimbalStateUpdateCallback() {
                            @Override
                            public void onGimbalStateUpdate(DJIGimbal djiGimbal,
                                                            DJIGimbal.DJIGimbalState djiGimbalState) {
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("GimbalCurrentPitch", "Gimbal Current Pitch in degrees: " + djiGimbalState.getAttitudeInDegrees().pitch);
                                msg.what = MSG_GIMBAL_CURRENT_PITCH;
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            }
                        }
                );
            } else {
                // gimbal not found
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("GimbalCurrentPitchError", "Gimbal Current Pitch: unable to retrieve. Gimbal not found " );
                msg.what = MSG_GIMBAL_CURRENT_PITCH_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            // aircraft is not found
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("GimbalCurrentPitchError", "Gimbal Current Pitch: unable to retrieve. Aircraft not found " );
            msg.what = MSG_GIMBAL_CURRENT_PITCH_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            switch (msg.what) {
                case MSG_GIMBAL_CURRENT_PITCH:
                    String gimbalCurrentPitch = bundle.getString("GimbalCurrentPitch") + " " + dateFormat.format(new Date()) + "\n";
                    m_tvGimbalCurrentPitch.setText(gimbalCurrentPitch);
                    break;
                case MSG_GIMBAL_CURRENT_PITCH_ERROR:
                    String gimbalCurrentPitchError = bundle.getString("GimbalCurrentPitchError") + " " + dateFormat.format(new Date()) + "\n";
                    m_tvGimbalCurrentPitch.setText(gimbalCurrentPitchError);
                    break;
            }

            return false;
        }
    });



}
