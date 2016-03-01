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

import dji.sdk.Camera.DJICamera;
import dji.sdk.Products.DJIAircraft;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraSystemUpdateFragment extends Fragment {

    private TextView m_tv_CameraSystemUpdate;
    private final int MSG_RESULT_CAMERA_SYSTEM_UPDATE = 1;
    private final int MSG_CAMERA_SYSTEM_ERROR = 2;

    public CameraSystemUpdateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_camera_system_update, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        m_tv_CameraSystemUpdate = (TextView) getActivity().findViewById(R.id.tvCameraSystemUpdate);

        try {
            DJIAircraft aircraft = DjiApplication.getAircraftInstance();
            if (aircraft != null) {
                DJICamera djiCamera =  aircraft.getCamera();
                if (djiCamera != null) {
                    djiCamera.setDJICameraUpdatedSystemStateCallback(new DJICamera.CameraUpdatedSystemStateCallback(){

                        @Override
                        public void onResult(DJICamera.CameraSystemState cameraSystemState) {
                            String cameraResult = "Camera mode: " + cameraSystemState.getCameraMode().name() + "\n";
                            cameraResult += "Is Recording: " + cameraSystemState.isRecording() + "\n";
                            cameraResult += "Is storing photo: " + cameraSystemState.isStoringPhoto() + "\n";
                            cameraResult += "Is Camera overheated: " + cameraSystemState.isCameraOverHeated() + "\n";
                            cameraResult += "Is there a camera error: " + cameraSystemState.isCameraError() + "\n";
                            cameraResult += "Is storing photo: " + cameraSystemState.isStoringPhoto() + "\n";
                            cameraResult += "Is shooting burst photo: " + cameraSystemState.isShootingBurstPhoto() + "\n";
                            cameraResult += "Is shooting interval photo: " + cameraSystemState.isShootingIntervalPhoto() + "\n";
                            cameraResult += "Is shooting single photo: " + cameraSystemState.isShootingSinglePhoto() + "\n";
                            cameraResult += "Is shooting single photo in raw format: " + cameraSystemState.isShootingSinglePhotoInRAWFormat() + "\n";
                            cameraResult += "Is Record: " + cameraSystemState.isUSBMode() + "\n";
                            cameraResult += "Current Video Recording time in seconds: " + cameraSystemState.getCurrentVideoRecordingTimeInSeconds() + "\n";
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("CameraSystemUpdate", cameraResult);
                            msg.what = MSG_RESULT_CAMERA_SYSTEM_UPDATE;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    });

                } else {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("CameraSystemError", "No valid camera found: ");
                    msg.what = MSG_CAMERA_SYSTEM_ERROR;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("CameraSystemError", "No valid aircraft found: ");
                msg.what = MSG_CAMERA_SYSTEM_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } catch (Exception exception) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("CameraSystemError", "Camera System Error: " + exception.getMessage());
            msg.what = MSG_CAMERA_SYSTEM_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }



    }


    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String cameraSystemUpdate = "Camera System Update" + "\n";
            switch (msg.what) {
                case MSG_RESULT_CAMERA_SYSTEM_UPDATE:
                    cameraSystemUpdate += bundle.getString("CameraSystemUpdate") + "\n";
                    m_tv_CameraSystemUpdate.setText(cameraSystemUpdate);
                    break;
                case MSG_CAMERA_SYSTEM_ERROR:
                    cameraSystemUpdate += bundle.getString("CameraSystemError") + "\n";
                    m_tv_CameraSystemUpdate.setText(cameraSystemUpdate);
                    break;
            }

            return false;
        }
    });



}
