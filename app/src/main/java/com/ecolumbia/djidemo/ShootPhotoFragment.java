package com.ecolumbia.djidemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShootPhotoFragment extends Fragment implements View.OnClickListener {

    private TextView m_tv_ResultShootPhoto;
    private Button m_btn_ShootPhoto;
    private Button m_btn_SetCameraModeToPhoto;
    private Spinner m_spinner_PhotoChoices;
    private Button m_btn_StartIntervalPhoto;
    private Button m_btn_EndIntervalPhoto;
    private Button m_btn_UpdateInterval;
    private EditText m_edittext_IntervalAmount;
    private TextView m_tv_IntervalPhotoResults;
    private final int MSG_SHOOT_PHOTO_SUCCESS = 1;
    private final int MSG_SHOOT_PHOTO_ERROR = 2;
    private final int MSG_INTERVAL_PHOTO_SUCCESS = 3;
    private final int MSG_INTERVAL_PHOTO_ERROR = 4;
    private final int MSG_INTERVAL_PHOTO_UPDATED = 5;
    private final int MSG_INTERVAL_PHOTO_ENDED = 6;
    private final int MSG_SET_CAMERA_MODE_TO_PHOTO = 7;
    private final int MSG_SET_CAMERA_MODE_TO_PHOTO_ERROR = 8;

    public ShootPhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shoot_photo, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m_tv_ResultShootPhoto = (TextView) view.findViewById(R.id.tvResultShootPhoto);
        m_btn_ShootPhoto = (Button) view.findViewById(R.id.btnShootPhoto);
        m_btn_ShootPhoto.setOnClickListener(this);
        m_btn_SetCameraModeToPhoto = (Button) view.findViewById(R.id.btnSetCameraModeToPhoto);
        m_btn_SetCameraModeToPhoto.setOnClickListener(this);
        // Set up the spinner with the photo choices.
        String array_spinner[] = new String[4];
        array_spinner[0] = "Single";
        array_spinner[1] = "HDR";
        array_spinner[2] = "Burst";
        array_spinner[3] = "Auto bracketing";
        m_spinner_PhotoChoices = (Spinner) view.findViewById(R.id.spinnerPhotoChoices);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, array_spinner);
        m_spinner_PhotoChoices.setAdapter(adapter);
        // Set up the interval variables
        m_btn_StartIntervalPhoto = (Button) view.findViewById((R.id.btnStartIntervalPhoto));
        m_btn_StartIntervalPhoto.setOnClickListener(this);
        m_btn_EndIntervalPhoto = (Button) view.findViewById((R.id.btnEndIntervalPhoto));
        m_btn_EndIntervalPhoto.setOnClickListener(this);
        m_tv_IntervalPhotoResults = (TextView) view.findViewById(R.id.tvIntervalPhotoResults);
        m_edittext_IntervalAmount = (EditText) view.findViewById((R.id.edittext_IntervalAmount));
        m_btn_UpdateInterval = (Button) view.findViewById(R.id.btnUpdateInterval);
        m_btn_UpdateInterval.setOnClickListener(this);
        getPhotoInterval(); // Retrieves the photo interval
    }

    private void btn_ShootSinglePhoto_onClick(final DJICameraSettingsDef.CameraShootPhotoMode currentCameraShootPhotoMode) {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.startShootPhoto(
                        currentCameraShootPhotoMode,
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError) {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    switch (currentCameraShootPhotoMode) {
                                        case Single:
                                            bundle.putString("ShootPhoto", "Single Photo was successful");
                                            break;
                                        case HDR:
                                            bundle.putString("ShootPhoto", "HDR Photos were successful");
                                            break;
                                        case Burst:
                                            bundle.putString("ShootPhoto", "Burst Photos were successful");
                                            break;
                                        case AEBCapture:
                                            bundle.putString("ShootPhoto", "Auto Bracketing Photos were successful");
                                            break;
                                    }
                                    msg.what = MSG_SHOOT_PHOTO_SUCCESS;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("ShootPhotoError", "Single Photo was not successful: " + djiError.getDescription());
                                    msg.what = MSG_SHOOT_PHOTO_ERROR;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                }

                            }
                        }
                );
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("ShootPhotoError", "Camera is not available: ");
                msg.what = MSG_SHOOT_PHOTO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("ShootPhotoError", "Drone is not available: ");
            msg.what = MSG_SHOOT_PHOTO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void btn_StartIntervalPhoto_onClick() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.startShootPhoto(
                        DJICameraSettingsDef.CameraShootPhotoMode.Interval,
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError) {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("IntervalPhoto", "Interval Photo was successfully started");
                                    msg.what = MSG_INTERVAL_PHOTO_SUCCESS;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("IntervalPhotoError", "Interval Photo was not successfully started: " + djiError.getDescription());
                                    msg.what = MSG_INTERVAL_PHOTO_ERROR;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                }

                            }
                        }
                );
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("IntervalPhotoError", "Camera is not available: ");
                msg.what = MSG_INTERVAL_PHOTO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("IntervalPhotoError", "Drone is not available: ");
            msg.what = MSG_INTERVAL_PHOTO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void btn_EndIntervalPhoto_onClick() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.stopShootPhoto(
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError) {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("IntervalPhotoEnded", "Interval Photo was successfully stopped");
                                    msg.what = MSG_INTERVAL_PHOTO_ENDED;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("IntervalPhotoError", "Interval Photo was not successfully stopped: " + djiError.getDescription());
                                    msg.what = MSG_INTERVAL_PHOTO_ERROR;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                }

                            }
                        }
                );
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("IntervalPhotoError", "Camera is not available: ");
                msg.what = MSG_INTERVAL_PHOTO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("IntervalPhotoError", "Drone is not available: ");
            msg.what = MSG_INTERVAL_PHOTO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void btn_UpdateInterval_onClick() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                DJICameraSettingsDef.CameraPhotoIntervalParam currentCameraPhotoIntervalParam = new DJICameraSettingsDef.CameraPhotoIntervalParam();
                try {
                    currentCameraPhotoIntervalParam.timeIntervalInSeconds = Integer.parseInt(m_edittext_IntervalAmount.getText().toString());
                } catch (Exception e) {
                    currentCameraPhotoIntervalParam.timeIntervalInSeconds = 10;
                }
                if (currentCameraPhotoIntervalParam.timeIntervalInSeconds <= 10 || currentCameraPhotoIntervalParam.timeIntervalInSeconds >= 100) {
                    currentCameraPhotoIntervalParam.timeIntervalInSeconds = 10;
                }
                currentCameraPhotoIntervalParam.captureCount = 50;
                camera.setPhotoIntervalParam(
                        currentCameraPhotoIntervalParam,
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError) {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("IntervalPhoto", "Interval time in seconds was updated successfully");
                                    msg.what = MSG_INTERVAL_PHOTO_SUCCESS;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("IntervalPhotoError", "Interval time in seconds was not updated: " + djiError.getDescription());
                                    msg.what = MSG_INTERVAL_PHOTO_ERROR;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                }

                            }
                        }
                );
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("IntervalPhotoError", "Camera is not available: ");
                msg.what = MSG_INTERVAL_PHOTO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("IntervalPhotoError", "Drone is not available: ");
            msg.what = MSG_INTERVAL_PHOTO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void getPhotoInterval() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.getPhotoIntervalParam(
                        new DJIBaseComponent.DJICompletionCallbackWith<DJICameraSettingsDef.CameraPhotoIntervalParam>() {


                            @Override
                            public void onSuccess(DJICameraSettingsDef.CameraPhotoIntervalParam currentCameraPhotoIntervalParam) {
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("IntervalPhotoUpdated", "Interval time in seconds was retrieved successfully");
                                bundle.putInt("IntervalPhotoInSeconds", currentCameraPhotoIntervalParam.timeIntervalInSeconds);
                                msg.what = MSG_INTERVAL_PHOTO_UPDATED;
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            }

                            @Override
                            public void onFailure(DJIError djiError) {
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("IntervalPhotoError", "Unable to retrieve the camera photo interval: " + djiError.getDescription());
                                msg.what = MSG_INTERVAL_PHOTO_ERROR;
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            }
                        }
                );
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("IntervalPhotoError", "Camera is not available: ");
                msg.what = MSG_INTERVAL_PHOTO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("IntervalPhotoError", "Drone is not available: ");
            msg.what = MSG_INTERVAL_PHOTO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void btn_SetCameraModeToPhoto_onClick() {
        DJIAircraft aircraft = DjiApplication.getAircraftInstance();
        DJICamera camera;
        if (DjiApplication.getAircraftInstance() != null) {
            camera = aircraft.getCamera();
            if (camera != null) {
                camera.setCameraMode(
                        DJICameraSettingsDef.CameraMode.ShootPhoto,
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError) {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("CameraMode", "Camera Mode set to photo: success");
                                    msg.what = MSG_SET_CAMERA_MODE_TO_PHOTO;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("CameraModeError", "Camera mode set to photo error: " + djiError.getDescription());
                                    msg.what = MSG_SET_CAMERA_MODE_TO_PHOTO_ERROR;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                }
                            }
                        });
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("CameraModeError", "Camera is not available: ");
                msg.what = MSG_SET_CAMERA_MODE_TO_PHOTO_ERROR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("CameraModeError", "Drone is not available: ");
            msg.what = MSG_SET_CAMERA_MODE_TO_PHOTO_ERROR;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShootPhoto:
                DJICameraSettingsDef.CameraShootPhotoMode currentCameraShootPhotoMode;
                switch (m_spinner_PhotoChoices.getSelectedItem().toString()) {
                    case "Single":
                        currentCameraShootPhotoMode = DJICameraSettingsDef.CameraShootPhotoMode.Single;
                        break;
                    case "HDR":
                        currentCameraShootPhotoMode = DJICameraSettingsDef.CameraShootPhotoMode.HDR;
                        break;
                    case "Burst":
                        currentCameraShootPhotoMode = DJICameraSettingsDef.CameraShootPhotoMode.Burst;
                        break;
                    case "Auto bracketing":
                        currentCameraShootPhotoMode = DJICameraSettingsDef.CameraShootPhotoMode.AEBCapture;
                        break;
                    default:
                        currentCameraShootPhotoMode = DJICameraSettingsDef.CameraShootPhotoMode.Single;
                        break;
                }
                btn_ShootSinglePhoto_onClick(currentCameraShootPhotoMode);
                break;
            case R.id.btnStartIntervalPhoto:
                btn_StartIntervalPhoto_onClick();
                break;
            case R.id.btnEndIntervalPhoto:
                btn_EndIntervalPhoto_onClick();
                break;
            case R.id.btnUpdateInterval:
                btn_UpdateInterval_onClick();
                break;
            case R.id.btnSetCameraModeToPhoto:
                btn_SetCameraModeToPhoto_onClick();
                break;

        }

    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            switch (msg.what) {
                case MSG_SHOOT_PHOTO_SUCCESS:
                    String photoSuccess = bundle.getString("ShootPhoto") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootPhoto.setText(photoSuccess);
                    break;
                case MSG_SHOOT_PHOTO_ERROR:
                    String photoError = bundle.getString("ShootPhotoError") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootPhoto.setText(photoError);
                    break;
                case MSG_INTERVAL_PHOTO_SUCCESS:
                    String intervalSuccess = bundle.getString("IntervalPhoto") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_IntervalPhotoResults.setText(intervalSuccess);
                    break;
                case MSG_INTERVAL_PHOTO_ERROR:
                    String intervalError = bundle.getString("IntervalPhotoError") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_IntervalPhotoResults.setText(intervalError);
                    break;
                case MSG_INTERVAL_PHOTO_ENDED:
                    String intervalEnded = bundle.getString("IntervalPhotoEnded") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_IntervalPhotoResults.setText(intervalEnded);
                    break;
                case MSG_INTERVAL_PHOTO_UPDATED:
                    String intervalUpdated = bundle.getString("IntervalPhotoUpdated") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_IntervalPhotoResults.setText(intervalUpdated);
                    int intervalInSeconds = bundle.getInt("IntervalPhotoInSeconds", 10);
                    m_edittext_IntervalAmount.setText(Integer.toString(intervalInSeconds));
                    break;
                case MSG_SET_CAMERA_MODE_TO_PHOTO:
                    String cameraMode = bundle.getString("CameraMode") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootPhoto.setText(cameraMode);
                    break;
                case MSG_SET_CAMERA_MODE_TO_PHOTO_ERROR:
                    String cameraModeError = bundle.getString("camerModeError") + " " + dateFormat.format(new Date()) + "\n";
                    m_tv_ResultShootPhoto.setText(cameraModeError);
                    break;
            }

            return false;
        }
    });


}
