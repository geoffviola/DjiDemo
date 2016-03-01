package com.ecolumbia.djidemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShootVideoFragment extends Fragment implements View.OnClickListener  {

    private TextView m_tv_ResultShootVideo;
    private Button m_btn_StartVideo;
    private Button m_btn_StopVideo;
    private Button m_btn_SetCameraModeToVideo;
    private final int MSG_SHOOT_VIDEO_SUCCESS = 1;
    private final int MSG_SHOOT_VIDEO_ERROR = 2;
    private final int MSG_SET_CAMERA_MODE_VIDEO_SUCCESS = 3;
    private final int MSG_SET_CAMERA_MODE_VIDEO_ERROR = 4;

    public ShootVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shoot_video, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        m_tv_ResultShootVideo = (TextView) view.findViewById(R.id.tvResultShootVideo);
        m_btn_StartVideo = (Button) view.findViewById(R.id.btnStartVideo);
        m_btn_StartVideo.setOnClickListener(this);
        m_btn_StopVideo = (Button) view.findViewById(R.id.btnStopVideo);
        m_btn_StopVideo.setOnClickListener(this);
        m_btn_SetCameraModeToVideo = (Button) view.findViewById(R.id.btnSetCameraModeToVideo);
        m_btn_SetCameraModeToVideo.setOnClickListener(this);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            switch (msg.what) {
                case MSG_SHOOT_VIDEO_SUCCESS:
                    String videoSuccess = bundle.getString("ShootVideo") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootVideo.setText(videoSuccess);
                    break;
                case MSG_SHOOT_VIDEO_ERROR:
                    String photoError = bundle.getString("ShootVideoError") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootVideo.setText(photoError);
                    break;
                case MSG_SET_CAMERA_MODE_VIDEO_SUCCESS:
                    String cameraModeSuccess = bundle.getString("CameraMode") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootVideo.setText(cameraModeSuccess);
                    break;
                case MSG_SET_CAMERA_MODE_VIDEO_ERROR:
                    String cameraModeError = bundle.getString("CameraModeError") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootVideo.setText(cameraModeError);
                    break;
            }

            return false;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartVideo:
                btn_StartVideo_onClick();
                break;
            case R.id.btnStopVideo:
                btn_StopVideo_onClick();
                break;
            case R.id.btnSetCameraModeToVideo:
                btn_SetCameraModeToVideo_onClick();
        }

    }

    private void btn_StartVideo_onClick() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.startRecordVideo(new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null == djiError) {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("ShootVideo", "Video started successfully.");
                            msg.what = MSG_SHOOT_VIDEO_SUCCESS;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("ShootVideoError", "Video starting error: " + djiError.getDescription());
                            msg.what = MSG_SHOOT_VIDEO_ERROR;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("ShootVideoError", "Camera is not available: ");
                msg.what = MSG_SHOOT_VIDEO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("ShootVideoError", "Drone is not available: ");
            msg.what = MSG_SHOOT_VIDEO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void btn_StopVideo_onClick() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.stopRecordVideo(new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null == djiError) {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("ShootVideo", "Video successfully stopped");
                            msg.what = MSG_SHOOT_VIDEO_SUCCESS;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("ShootVideo", "Video not stopped there was an error: " + djiError.getDescription());
                            msg.what = MSG_SHOOT_VIDEO_ERROR;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("ShootVideoError", "Camera is not available: ");
                msg.what = MSG_SHOOT_VIDEO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("ShootVideoError", "Drone is not available: ");
            msg.what = MSG_SHOOT_VIDEO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void btn_SetCameraModeToVideo_onClick() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.setCameraMode(
                        DJICameraSettingsDef.CameraMode.RecordVideo,
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError) {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("CameraMode", "Camera Mode set to video: success");
                                    msg.what = MSG_SET_CAMERA_MODE_VIDEO_SUCCESS;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("CameraModeError", "Camera mode set to video error: " + djiError.getDescription());
                                    msg.what = MSG_SET_CAMERA_MODE_VIDEO_ERROR;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                }
                            }
                        });
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("CameraModeError", "Camera is not available: ");
                msg.what = MSG_SET_CAMERA_MODE_VIDEO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("CameraModeError", "Drone is not available: ");
            msg.what = MSG_SET_CAMERA_MODE_VIDEO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

}
