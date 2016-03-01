package com.ecolumbia.djidemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicFlightFragment extends Fragment implements View.OnClickListener {

    protected TextView m_tv_TakeOffResult;
    protected TextView m_tv_LandingResult;
    protected TextView m_tv_HomeResult;
    protected TextView m_tv_MotorsOnResult;
    protected TextView m_tv_MotorsOffResult;
    protected TextView m_tv_VirtualStickEnable;
    protected TextView m_tv_VirtualStickDisable;
    protected TextView m_tv_VirtualStickRun;
    protected TextView m_tv_VirtualStickVerticalThrottle;
    protected SeekBar m_sb_ThrottleUp_Altitude;
    protected TextView m_tv_ThrottleUpAltitudeValue;
    protected SeekBar m_sb_ThrottleUp_Velocity;
    protected TextView m_tv_ThrottleUpVelocityValue;
    protected TextView m_tv_RollPitchMode;
    protected TextView m_tv_Yaw_AngularVelocity_Symbol;
    protected TextView m_tv_YawMode;
    protected TextView m_tv_Roll_Angle_Value;
    protected TextView m_tv_Pitch_Angle_Value;
    protected TextView m_tv_Yaw_Angle_Value;
    protected TextView m_tv_Roll_Velocity_Value;
    protected TextView m_tv_Pitch_Velocity_Value;
    protected TextView m_tv_Yaw_AngularVelocity_Value;
    protected TextView m_tv_SetOrientationMode;

    protected SeekBar m_sb_Roll_Angle;
    protected SeekBar m_sb_Roll_Velocity;
    protected SeekBar m_sb_Pitch_Angle;
    protected SeekBar m_sb_Pitch_Velocity;
    protected SeekBar m_sb_Yaw_Angle;
    protected SeekBar m_sb_Yaw_AngularVelocity;

    protected Button m_btn_Takeoff;
    protected Button m_btn_Land;
    protected Button m_btn_Home;
    protected Button m_btn_MotorsOn;
    protected Button m_btn_MotorsOff;
    protected Button m_btn_EnableVirtualStick;
    protected Button m_btn_DisableVirtualStick;
    protected Button m_btn_ToggleThrottleVirtualStick;
    protected Button m_btn_ToggleRollPitchVirtualStick;
    protected Button m_btn_ToggleYawVirtualStick;
    protected Button m_btn_RunVirtualStick;
    protected Button m_btn_SetOrientationMode_CourseLock;
    protected Button m_btn_SetOrientationMode_Home;
    protected Button m_btn_SetOrientationMode_DefaultAircraftHeading;

    private final int MSG_RESULT_TAKEOFF = 1;
    private final int MSG_RESULT_LANDING = 2;
    private final int MSG_RESULT_Home = 3;
    private final int MSG_RESULT_MOTOR_ON = 4;
    private final int MSG_RESULT_MOTOR_OFF = 5;
    private final int MSG_VIRTUAL_STICK_ENABLE = 6;
    private final int MSG_VIRTUAL_STICK_DISABLE = 7;
    private final int MSG_VIRTUAL_STICK_RUN = 8;
    private final int MSG_VIRTUAL_STICK_VERTICAL_THROTTLE = 9;
    private final int MSG_VIRTUAL_STICK_ROLL_PITCH_MODE = 10;
    private final int MSG_VIRTUAL_STICK_YAW_MODE = 11;
    private final int MSG_VIRTUAL_STICK_ORIENTATION_MODE = 12;

    DJIBaseProduct mProduct = null;

    private DJIFlightController mFlightController;

    public BasicFlightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_basic_flight, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v) {

        // initialize all the module level variables.
        m_tv_LandingResult = (TextView) v.findViewById(R.id.tvLandingResult);
        m_tv_TakeOffResult = (TextView) v.findViewById(R.id.tvTakeOffResult);
        m_tv_HomeResult = (TextView) v.findViewById(R.id.tvHomeResult);
        m_tv_MotorsOnResult = (TextView) v.findViewById(R.id.tvMotorsOnResult);
        m_tv_MotorsOffResult = (TextView) v.findViewById(R.id.tvMotorsOffResult);
        m_tv_VirtualStickEnable = (TextView) v.findViewById(R.id.tvVirtualStickStatus_Enable);
        m_tv_VirtualStickDisable = (TextView) v.findViewById(R.id.tvVirtualStickStatus_Disable);
        m_tv_VirtualStickRun = (TextView) v.findViewById(R.id.tvRunThrottle);
        m_tv_VirtualStickVerticalThrottle = (TextView) v.findViewById(R.id.tvToggleThrottle);

        m_sb_ThrottleUp_Altitude = (SeekBar) v.findViewById(R.id.sbThrottleUp_Altitude);
        m_sb_ThrottleUp_Altitude.setOnSeekBarChangeListener(customThrottleUpAltitudeListener);
        m_tv_ThrottleUpAltitudeValue = (TextView) v.findViewById(R.id.tvThrottleUpAltitudeValue);
        m_sb_ThrottleUp_Velocity = (SeekBar) v.findViewById(R.id.sbThrottleUp_Velocity);
        m_sb_ThrottleUp_Velocity.setProgress(400); // Set the progress at the mid-point from -4 to +4.
        m_sb_ThrottleUp_Velocity.setOnSeekBarChangeListener(customThrottleUpVelocityListener);
        m_tv_ThrottleUpVelocityValue = (TextView) v.findViewById(R.id.tvThrottleUpVelocityValue);
        m_tv_RollPitchMode = (TextView) v.findViewById(R.id.tvRollPitchMode);
        m_tv_Yaw_AngularVelocity_Symbol = (TextView) v.findViewById(R.id.tv_Yaw_AngularVelocity_Symbol);
        m_tv_Yaw_AngularVelocity_Symbol.setText("\u00B0" + "/s");
        m_tv_YawMode = (TextView) v.findViewById(R.id.tvYawMode);

        m_tv_Roll_Angle_Value = (TextView) v.findViewById(R.id.tv_Roll_Angle_Value);;
        m_tv_Pitch_Angle_Value = (TextView) v.findViewById(R.id.tv_Pitch_Angle_Value);;
        m_tv_Yaw_Angle_Value = (TextView) v.findViewById(R.id.tv_Yaw_Angle_Value);
        m_tv_Roll_Velocity_Value = (TextView) v.findViewById(R.id.tv_Roll_Velocity_Value);
        m_tv_Pitch_Velocity_Value = (TextView) v.findViewById(R.id.tv_Pitch_Velocity_Value);
        m_tv_Yaw_AngularVelocity_Value = (TextView) v.findViewById(R.id.tv_Yaw_AngularVelocity_Value);

        m_sb_Roll_Angle = (SeekBar) v.findViewById(R.id.sb_Roll_Angle);
        m_sb_Roll_Angle.setOnSeekBarChangeListener(customRollAngleListener);
        m_sb_Roll_Angle.setProgress(300); // Values -300 to +300
        m_tv_Roll_Angle_Value = (TextView) v.findViewById(R.id.tv_Roll_Angle_Value);


        m_sb_Roll_Velocity = (SeekBar) v.findViewById(R.id.sb_Roll_Velocity);
        m_sb_Roll_Velocity.setOnSeekBarChangeListener(customRollVelocityListener);
        m_sb_Roll_Velocity.setProgress(150); // Values -150 to +150
        m_tv_Roll_Velocity_Value = (TextView) v.findViewById(R.id.tv_Roll_Velocity_Value);

        m_sb_Pitch_Angle = (SeekBar) v.findViewById(R.id.sb_Pitch_Angle);
        m_sb_Pitch_Angle.setOnSeekBarChangeListener(customPitchAngleListener);
        m_sb_Pitch_Angle.setProgress(300); // Values -300 to +300
        m_tv_Pitch_Angle_Value = (TextView) v.findViewById(R.id.tv_Pitch_Angle_Value);

        m_sb_Pitch_Velocity = (SeekBar) v.findViewById(R.id.sb_Pitch_Velocity);
        m_sb_Pitch_Velocity.setOnSeekBarChangeListener(customPitchVelocityListener);
        m_sb_Pitch_Velocity.setProgress(150); // Values -150 to +150
        m_tv_Pitch_Velocity_Value = (TextView) v.findViewById(R.id.tv_Pitch_Velocity_Value);

        m_sb_Yaw_Angle = (SeekBar) v.findViewById(R.id.sb_Yaw_Angle);
        m_sb_Yaw_Angle.setOnSeekBarChangeListener(customYawAngleListener);
        m_sb_Yaw_Angle.setProgress(180); // Values -180 to +180
        m_tv_Yaw_Angle_Value = (TextView) v.findViewById(R.id.tv_Yaw_Angle_Value);

        m_sb_Yaw_AngularVelocity = (SeekBar) v.findViewById(R.id.sb_Yaw_AngularVelocity);
        m_sb_Yaw_AngularVelocity.setOnSeekBarChangeListener(customYawAngularVelocityListener);
        m_sb_Yaw_AngularVelocity.setProgress(100); // Values -100 to +100
        m_tv_Yaw_AngularVelocity_Value = (TextView) v.findViewById(R.id.tv_Yaw_AngularVelocity_Value);

        m_btn_Takeoff = (Button) v.findViewById(R.id.btnTakeoff);
        m_btn_Land = (Button) v.findViewById(R.id.btnLand);
        m_btn_Home = (Button) v.findViewById(R.id.btnHome);
        m_btn_MotorsOn = (Button) v.findViewById(R.id.btnMotorsOn);
        m_btn_MotorsOff = (Button) v.findViewById(R.id.btnMotorsOff);
        m_btn_EnableVirtualStick = (Button) v.findViewById(R.id.btnEnableVirtualStick);
        m_btn_DisableVirtualStick = (Button) v.findViewById(R.id.btnDisableVirtualStick);
        m_btn_ToggleThrottleVirtualStick = (Button) v.findViewById(R.id.btnToggleThrottle);
        m_btn_ToggleRollPitchVirtualStick = (Button) v.findViewById(R.id.btnRollPitch);
        m_btn_ToggleYawVirtualStick = (Button) v.findViewById(R.id.btnYaw);
        m_btn_RunVirtualStick = (Button) v.findViewById(R.id.btnRunVirtualStick);
        m_btn_SetOrientationMode_CourseLock = (Button) v.findViewById(R.id.btnSetOrientationMode_CourseLock);
        m_btn_SetOrientationMode_Home = (Button) v.findViewById(R.id.btnSetOrientationMode_Home);
        m_btn_SetOrientationMode_DefaultAircraftHeading = (Button) v.findViewById(R.id.btnSetOrientationMode_DefaultAircraftHeading);
        m_tv_SetOrientationMode = (TextView) v.findViewById(R.id.tvSet_OrientationMode);

        m_btn_Takeoff.setOnClickListener(this);
        m_btn_Land.setOnClickListener(this);
        m_btn_Home.setOnClickListener(this);
        m_btn_MotorsOn.setOnClickListener(this);
        m_btn_MotorsOff.setOnClickListener(this);
        m_btn_EnableVirtualStick.setOnClickListener(this);
        m_btn_DisableVirtualStick.setOnClickListener(this);
        m_btn_ToggleThrottleVirtualStick.setOnClickListener(this);
        m_btn_ToggleRollPitchVirtualStick.setOnClickListener(this);
        m_btn_ToggleYawVirtualStick.setOnClickListener(this);
        m_btn_RunVirtualStick.setOnClickListener(this);
        m_btn_SetOrientationMode_CourseLock.setOnClickListener(this);
        m_btn_SetOrientationMode_Home.setOnClickListener(this);
        m_btn_SetOrientationMode_DefaultAircraftHeading.setOnClickListener(this);

        // Initialize the textviews with the current values:
        DJIAircraft djiAircraft = DjiApplication.getAircraftInstance();

        // Initialize the text fields to their current values
        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            m_tv_VirtualStickVerticalThrottle.setText("Vertical Throttle Mode: " + flightController.getVerticalControlMode().name());
            m_tv_RollPitchMode.setText("Roll/Pitch Mode: " + flightController.getRollPitchControlMode().name());
            m_tv_YawMode.setText("Yaw Mode: " + flightController.getYawControlMode().name());
            m_tv_SetOrientationMode.setText(flightController.getCurrentState().getOrientaionMode().name());
        }

    }

    private SeekBar.OnSeekBarChangeListener customThrottleUpAltitudeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                m_tv_ThrottleUpAltitudeValue.setText(String.valueOf(progress));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            try {
                m_tv_ThrottleUpAltitudeValue.setText(String.valueOf(seekBar.getProgress()));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener customThrottleUpVelocityListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) progress - 400)/100));
            try {
                m_tv_ThrottleUpVelocityValue.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) seekBar.getProgress() - 400)/100));
            try {
                m_tv_ThrottleUpVelocityValue.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private SeekBar.OnSeekBarChangeListener customYawAngleListener = new SeekBar.OnSeekBarChangeListener() {
        Integer intMax = 180;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Integer intAdjustProgress = progress - intMax;
            try {
                m_tv_Yaw_Angle_Value.setText(String.valueOf(intAdjustProgress));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Integer intAdjustProgress = seekBar.getProgress() - intMax;
            try {
                m_tv_Yaw_Angle_Value.setText(String.valueOf(intAdjustProgress));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener customYawAngularVelocityListener = new SeekBar.OnSeekBarChangeListener() {
        Integer intMax = 100;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Integer intAdjustProgress = progress - intMax;
            try {
                m_tv_Yaw_AngularVelocity_Value.setText(String.valueOf(intAdjustProgress));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Integer intAdjustProgress = seekBar.getProgress() - intMax;
            try {
                m_tv_Yaw_AngularVelocity_Value.setText(String.valueOf(intAdjustProgress));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private SeekBar.OnSeekBarChangeListener customRollAngleListener = new SeekBar.OnSeekBarChangeListener() {
        int intMax = 300;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) progress - intMax)/10));
            try {
                m_tv_Roll_Angle_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) seekBar.getProgress() - intMax)/10));
            try {
                m_tv_Roll_Angle_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener customRollVelocityListener = new SeekBar.OnSeekBarChangeListener() {
        int intMax = 150;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) progress - intMax)/10));
            try {
                m_tv_Roll_Velocity_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) seekBar.getProgress() - intMax)/10));
            try {
                m_tv_Roll_Velocity_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener customPitchAngleListener = new SeekBar.OnSeekBarChangeListener() {
        int intMax = 300;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) progress - intMax)/10));
            try {
                m_tv_Pitch_Angle_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) seekBar.getProgress() - intMax)/10));
            try {
                m_tv_Pitch_Angle_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener customPitchVelocityListener = new SeekBar.OnSeekBarChangeListener() {
        int intMax = 150;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) progress - intMax)/10));
            try {
                m_tv_Pitch_Velocity_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            DecimalFormat oneDForm = new DecimalFormat("#.#");
            Float flOneDForm = Float.valueOf(oneDForm.format(( (float) seekBar.getProgress() - intMax)/10));
            try {
                m_tv_Pitch_Velocity_Value.setText(String.valueOf(flOneDForm));
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            switch (msg.what) {
                case MSG_RESULT_MOTOR_ON:
                    String motorsOnResult = "Motor On Result: " + bundle.getString("DjiError") + "\n";
                    m_tv_MotorsOnResult.setText(motorsOnResult);
                    break;
                case MSG_RESULT_MOTOR_OFF:
                    String motorsOffResult = "Motor Off Result: " + bundle.getString("DjiError") + "\n";
                    m_tv_MotorsOffResult.setText(motorsOffResult);
                    break;
                case MSG_RESULT_TAKEOFF:
                    String takeOffResult = "Takeoff Result: " + bundle.getString("DjiError") + "\n";
                    m_tv_TakeOffResult.setText(takeOffResult);
                    break;
                case MSG_RESULT_LANDING:
                    String landingResult = "Landing Result: " + bundle.getString("DjiError") + "\n";
                    m_tv_LandingResult.setText(landingResult);
                    break;
                case MSG_VIRTUAL_STICK_ENABLE:
                    String virtualStickEnable = bundle.getString("EnableVirtualStick") + "\n";
                    m_tv_VirtualStickEnable.setText(virtualStickEnable);
                    m_tv_VirtualStickDisable.setText("");
                    break;
                case MSG_VIRTUAL_STICK_DISABLE:
                    String virtualStickDisable = bundle.getString("DisableVirtualStick") + "\n";
                    m_tv_VirtualStickDisable.setText(virtualStickDisable);
                    m_tv_VirtualStickEnable.setText("");
                    break;
                case MSG_VIRTUAL_STICK_RUN:
                    String virtualStickRun = bundle.getString("VirtualStickRun") + "\n";
                    m_tv_VirtualStickRun.setText(virtualStickRun);
                    break;
                case MSG_VIRTUAL_STICK_VERTICAL_THROTTLE:
                    String virtualStickVerticalThrottle = bundle.getString("VirtualStickVerticalThrottle") + "\n";
                    m_tv_VirtualStickVerticalThrottle.setText(virtualStickVerticalThrottle);
                    break;
                case MSG_VIRTUAL_STICK_ROLL_PITCH_MODE:
                    String virtualStickRollPitchMode = bundle.getString("VirtualStickRollPitchMode") + "\n";
                    m_tv_RollPitchMode.setText(virtualStickRollPitchMode);
                    break;
                case MSG_VIRTUAL_STICK_YAW_MODE:
                    String virtualStickYawMode = bundle.getString("VirtualStickYawMode") + "\n";
                    m_tv_YawMode.setText(virtualStickYawMode);
                    break;
                case MSG_VIRTUAL_STICK_ORIENTATION_MODE:
                    String virtualStickOrientationMode = bundle.getString("VirtualStickOrientationMode") + "\n";
                    m_tv_SetOrientationMode.setText(virtualStickOrientationMode);
                    break;
            }

            return false;
        }
    });

    public void btnTakeOff_onClick(View v) {

        mProduct = DJISDKManager.getInstance().getDJIProduct();

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();
            mFlightController.takeOff(new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DjiError", djiError.getDescription());
                    } else {
                        bundle.putString("DjiError", "DjiError returned no value and takeoff was successful");
                    }
                    msg.what = MSG_RESULT_TAKEOFF;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });


        }
    }

    public void btnLand_onClick(View v) {
        mProduct = DJISDKManager.getInstance().getDJIProduct();

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();
            mFlightController.autoLanding(new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DjiError", djiError.getDescription());
                    } else {
                        bundle.putString("DjiError", "DjiError returned no value and the landing was successful");
                    }
                    msg.what = MSG_RESULT_LANDING;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });


        }
    }

    public void btnHome_onClick(View v) {
        mProduct = DJISDKManager.getInstance().getDJIProduct();

        DJIAircraft djiAircraft = DjiApplication.getAircraftInstance();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();
            mFlightController.goHome(new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DjiError", djiError.getDescription());
                    } else {
                        bundle.putString("DjiError", "DjiError returned no value and the home commmand was successful");
                    }
                    msg.what = MSG_RESULT_Home;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });


        }
    }

    public void btnMotorsOn_onClick(View v) {

        DJIAircraft djiAircraft = DjiApplication.getAircraftInstance();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();
            mFlightController.turnOnMotors(new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("DjiError", djiError.getDescription());
                    msg.what = MSG_RESULT_MOTOR_ON;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });


        }
    }

    public void btnMotorsOff_onClick(View v) {

        DJIAircraft djiAircraft = DjiApplication.getAircraftInstance();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();
            mFlightController.turnOffMotors(new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DjiError", djiError.getDescription());
                    } else {
                        bundle.putString("DjiError", "djiError was null. No response");
                    }
                    msg.what = MSG_RESULT_MOTOR_OFF;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });


        }
    }


    public void btnEnableVirtualStick_onClick(View v) {

        mProduct = DJISDKManager.getInstance().getDJIProduct();

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();

            if (mFlightController.isVirtualStickControlModeAvailable()) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("EnableVirtualStick", "Virtual Stick control mode is available");
                msg.what = MSG_VIRTUAL_STICK_ENABLE;
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                mFlightController.enableVirtualStickControlMode(new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("EnableVirtualStick", "Virtual Stick has been enabled successfully.");
                            msg.what = MSG_VIRTUAL_STICK_ENABLE;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("EnableVirtualStick", "Virtual Stick not enabled: " + djiError.getDescription());
                            msg.what = MSG_VIRTUAL_STICK_ENABLE;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                });


            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("EnableVirtualStick", "Virtual Stick control mode not available");
                msg.what = MSG_VIRTUAL_STICK_ENABLE;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }


        }
    }

    public void btnDisableVirtualStick_onClick(View v) {

        DJIAircraft djiAircraft = DjiApplication.getAircraftInstance();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();

            if (mFlightController.isVirtualStickControlModeAvailable()) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("DisableVirtualStick", "Virtual Stick control mode is not available");
                msg.what = MSG_VIRTUAL_STICK_DISABLE;
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                mFlightController.disableVirtualStickControlMode(new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("DisableVirtualStick", "Virtual Stick has been disabled successfully.");
                            msg.what = MSG_VIRTUAL_STICK_DISABLE;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("DisableVirtualStick", "Virtual Stick was not disabled: " + djiError.getDescription());
                            msg.what = MSG_VIRTUAL_STICK_DISABLE;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                });


            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("DisableVirtualStick", "Virtual Stick control mode not available");
                msg.what = MSG_VIRTUAL_STICK_DISABLE;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }


        }
    }


    public void btnRunVirtualStick_onClick(View v) {

        DJIAircraft djiAircraft = DjiApplication.getAircraftInstance();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();

            if (mFlightController.isVirtualStickControlModeAvailable()) {
                final DJIFlightControllerDataType.DJIVirtualStickFlightControlData controlData;
                Float flThrottle;
                Float flYaw;
                Float flPitch;
                Float flRoll;
                String stThrottle = "";
                String stYaw = "";
                String stPitch = "";
                String stRoll = "";
                if (mFlightController.getVerticalControlMode() == DJIFlightControllerDataType.DJIVirtualStickVerticalControlMode.Position) {
                    flThrottle = (Float) Float.parseFloat(m_tv_ThrottleUpAltitudeValue.getText().toString()); // This is the throttle position (altitude) value
                    stThrottle = "Throttle Altitude: " + flThrottle;
                } else {
                    flThrottle = (Float) Float.parseFloat(m_tv_ThrottleUpVelocityValue.getText().toString()); // This is the throttle velocity value
                    stThrottle = "Throttle Velocity: " + flThrottle;
                }
                if (mFlightController.getYawControlMode() == DJIFlightControllerDataType.DJIVirtualStickYawControlMode.Angle) {
                    flYaw = (Float) Float.parseFloat(m_tv_Yaw_Angle_Value.getText().toString()); // This is the yaw angle value
                    stYaw = "Yaw Angle: " + flYaw;
                } else {
                    flYaw = (Float) Float.parseFloat(m_tv_Yaw_AngularVelocity_Value.getText().toString()); // This is the yaw angular velocity value
                    stYaw = "Yaw Angular Velocity: " + flYaw;
                }
                if (mFlightController.getRollPitchControlMode() == DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMode.Angle) {
                    flPitch = (Float) Float.parseFloat(m_tv_Pitch_Angle_Value.getText().toString());  // This is the pitch angle value
                    flRoll = (Float) Float.parseFloat(m_tv_Roll_Angle_Value.getText().toString()); // This is the roll angle value
                    stPitch = "Pitch Angle: " + flPitch;
                    stRoll = "Roll Angle: " + flRoll;
                } else {
                    flPitch = (Float) Float.parseFloat(m_tv_Pitch_Velocity_Value.getText().toString()); // This is the pitch velocity value
                    flRoll = (Float) Float.parseFloat(m_tv_Roll_Velocity_Value.getText().toString()); // This is the roll velocity value
                    stPitch = "Pitch Velocity: " + flPitch;
                    stRoll = "Roll Velocity: " + flRoll;
                }

                controlData = new DJIFlightControllerDataType.DJIVirtualStickFlightControlData(flPitch, flRoll, flYaw, flThrottle);
                // Using the final string for Throttle, Pitch, Roll, and Yaw is a hack and not appropriate coding. Need to use a proper class instead.
                final String stFinalThrottle = stThrottle;
                final String stFinalPitch = stPitch;
                final String stFinalRoll = stRoll;
                final String stFinalYaw = stYaw;

                mFlightController.sendVirtualStickFlightControlData(controlData, new DJIBaseComponent.DJICompletionCallback() { // Send the control data command to the drone.
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("VirtualStickRun", "Virtual String Success: " + "\n" + stFinalThrottle + "\n" + stFinalPitch + "\n" + stFinalRoll + "\n" + stFinalYaw + "\n" );
                            msg.what = MSG_VIRTUAL_STICK_RUN;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("RunVirtualStickRun", "Throttle Up not successful: " + djiError.getDescription());
                            msg.what = MSG_VIRTUAL_STICK_RUN;
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("RunVirtualStickRun", "Set Throttle could not be run because virtual stick control mode is not available ");
                msg.what = MSG_VIRTUAL_STICK_RUN;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickRun", "Connection is not set.");
            msg.what = MSG_VIRTUAL_STICK_RUN;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    public void btnToggleThrottleVirtualStick_onClick(View v) {

        mProduct = DJISDKManager.getInstance().getDJIProduct();

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();

            DJIFlightControllerDataType.DJIVirtualStickVerticalControlMode newVerticalControlMode;
            DJIFlightControllerDataType.DJIVirtualStickVerticalControlMode currentVerticalControlMode = mFlightController.getVerticalControlMode();
            if (currentVerticalControlMode == DJIFlightControllerDataType.DJIVirtualStickVerticalControlMode.Position) {
                newVerticalControlMode = DJIFlightControllerDataType.DJIVirtualStickVerticalControlMode.Velocity; // Switch to velocity
            } else {
                newVerticalControlMode = DJIFlightControllerDataType.DJIVirtualStickVerticalControlMode.Position; // Switch to position
            }
            mFlightController.setVerticalControlMode(newVerticalControlMode); // Set the new Vertical Control mode.
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickVerticalThrottle", "Vertical Throttle Mode: " + newVerticalControlMode.name());
            msg.what = MSG_VIRTUAL_STICK_VERTICAL_THROTTLE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickVerticalThrottle", " Vertical Throttle mode could not be changed. ");
            msg.what = MSG_VIRTUAL_STICK_RUN;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    public void btnToggleRollPitchVirtualStick_onClick(View v) {

        mProduct = DJISDKManager.getInstance().getDJIProduct();

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();

            DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMode newRollPitchControlMode; // Create a new roll pitch contol mode to send to the drone.
            DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMode currentRollPitchControlMode = mFlightController.getRollPitchControlMode();
            if (currentRollPitchControlMode == DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMode.Angle) {
                newRollPitchControlMode = DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMode.Velocity; // Set to velocity
            } else {
                newRollPitchControlMode = DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMode.Angle; // set to angle
            }
            mFlightController.setRollPitchControlMode(newRollPitchControlMode); // Set the new RollPitchControlMode
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickRollPitchMode", "Roll/Pitch Mode: " + newRollPitchControlMode.name());
            msg.what = MSG_VIRTUAL_STICK_ROLL_PITCH_MODE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickRollPitchMode", "Roll/Pitch mode could not be changed. ");
            msg.what = MSG_VIRTUAL_STICK_ROLL_PITCH_MODE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    public void btnToggleYawVirtualStick_onClick(View v) {

        mProduct = DJISDKManager.getInstance().getDJIProduct();

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();

            DJIFlightControllerDataType.DJIVirtualStickYawControlMode newYawControlMode;
            DJIFlightControllerDataType.DJIVirtualStickYawControlMode currentYawControlMode = mFlightController.getYawControlMode();
            if (currentYawControlMode == DJIFlightControllerDataType.DJIVirtualStickYawControlMode.Angle) {
                newYawControlMode = DJIFlightControllerDataType.DJIVirtualStickYawControlMode.AngularVelocity;
            } else {
                newYawControlMode = DJIFlightControllerDataType.DJIVirtualStickYawControlMode.Angle;
            }
            mFlightController.setYawControlMode(newYawControlMode);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickYawMode", "Yaw Mode: " + newYawControlMode.name());
            msg.what = MSG_VIRTUAL_STICK_YAW_MODE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickYawMode", "Yaw mode could not be changed. ");
            msg.what = MSG_VIRTUAL_STICK_YAW_MODE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    public void btnSetOrientationMode_CourseLock_onClick(View v, final DJIFlightControllerDataType.DJIFlightOrientationMode newOrientationMode) {
        // Set the orientation mode to the newOrientationMode: CourseLock, HomeLock, or Default Aircraft Heading.

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        if ((DjiApplication.getProductInstance() instanceof DJIAircraft) && null != DjiApplication.getProductInstance()) {
            mFlightController = djiAircraft.getFlightController();

            mFlightController.setFlightOrientationMode(newOrientationMode, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("VirtualStickOrientationMode", "Orientation Mode: " + newOrientationMode.name());
                        msg.what = MSG_VIRTUAL_STICK_ORIENTATION_MODE;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("VirtualStickOrientationMode", "Error changing orientation Mode: " + djiError.getDescription());
                        msg.what = MSG_VIRTUAL_STICK_ORIENTATION_MODE;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }

                }
            });

        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("VirtualStickOrientationMode", "Orientation mode could not be changed. ");
            msg.what = MSG_VIRTUAL_STICK_ORIENTATION_MODE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakeoff:
                btnTakeOff_onClick(v);
                break;
            case R.id.btnLand:
                btnLand_onClick(v);
                break;
            case R.id.btnHome:
                btnHome_onClick(v);
                break;
            case R.id.btnMotorsOn:
                btnMotorsOn_onClick(v);
                break;
            case R.id.btnMotorsOff:
                btnMotorsOff_onClick(v);
                break;
            case R.id.btnEnableVirtualStick:
                btnEnableVirtualStick_onClick(v);
                break;
            case R.id.btnDisableVirtualStick:
                btnDisableVirtualStick_onClick(v);
                break;
            case R.id.btnToggleThrottle:
                btnToggleThrottleVirtualStick_onClick(v);
                break;
            case R.id.btnRollPitch:
                 btnToggleRollPitchVirtualStick_onClick(v);
                break;
            case R.id.btnYaw:
                btnToggleYawVirtualStick_onClick(v);
                break;
            case R.id.btnSetOrientationMode_CourseLock:
                btnSetOrientationMode_CourseLock_onClick(v, DJIFlightControllerDataType.DJIFlightOrientationMode.CourseLock);
                break;
            case R.id.btnSetOrientationMode_Home:
                btnSetOrientationMode_CourseLock_onClick(v, DJIFlightControllerDataType.DJIFlightOrientationMode.HomeLock);
                break;
            case R.id.btnSetOrientationMode_DefaultAircraftHeading:
                btnSetOrientationMode_CourseLock_onClick(v, DJIFlightControllerDataType.DJIFlightOrientationMode.DefaultAircraftHeading);
                break;
            case R.id.btnRunVirtualStick:
                btnRunVirtualStick_onClick(v);
                break;
        }

    }
}
