package com.ecolumbia.djidemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;
import dji.sdk.Products.DJIAircraft;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseProduct;

public class ShowConnectionActivity extends AppCompatActivity implements DJIBaseProduct.DJIVersionCallback {

    public static final String TAG = ShowConnectionActivity.class.getName();
    private static final int MSG_VERSION = 1;
    private TextView mTvConnectionStatus;
    private TextView mTvProduct;
    private TextView mTvModelAvailable;
    private Button mBtnOpen;

    private TextView mTvINIT_DJI_SDK;
    private TextView mTvON_PRODUCT_CHANGED_DJI_SDK;
    private TextView mON_COMPONENT_CHANGED_DJI_SDK;
    private TextView mON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK;
    private TextView mON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK;

    private DJIBaseProduct mProduct;
    private Intent mAoaIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_connection);
        initUI();

        /**
         * each time the USB from the RC is connected/disconnected,
         * the phone will prompt the user to select the app they want
         * to connect. If you want to launch main activity whenever you
         * provide the permission to the application, please uncomment
         * the coming codes. Please uncomment the code of main
         * activity in AndroidManifest. Also, comment the codes of the
         * DJIAoaControllerActivity in the AndroidManifest.
         */
        mAoaIntent = getIntent();
        if (mAoaIntent!=null) {
            String action = mAoaIntent.getAction();
            if (action== UsbManager.ACTION_USB_ACCESSORY_ATTACHED) {
                Intent attachedIntent=new Intent();

                attachedIntent.setAction(DJISDKManager.USB_ACCESSORY_ATTACHED);
                sendBroadcast(attachedIntent);
            }
        }

        // Register the receiver when first creating the activity.
        IntentFilter filter = new IntentFilter();
        filter.addAction(DjiApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);


    }


    @Override
    protected void onDestroy() {
        // Remove the receiver when exiting.
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAoaIntent = getIntent();
        if (mAoaIntent!=null) {
            String action = mAoaIntent.getAction();
            if (action== UsbManager.ACTION_USB_ACCESSORY_ATTACHED) {
                Intent attachedIntent=new Intent();

                attachedIntent.setAction(DJISDKManager.USB_ACCESSORY_ATTACHED);
                sendBroadcast(attachedIntent);
            }
        }

    }

    private void initUI() {
        mTvConnectionStatus = (TextView) findViewById(R.id.tv_connection_status);
        mTvModelAvailable = (TextView) findViewById(R.id.tv_model_available);
        mTvProduct = (TextView) findViewById(R.id.tv_product_info);
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mTvINIT_DJI_SDK = (TextView) findViewById(R.id.tv_INIT_DJI_SDK);
        mTvON_PRODUCT_CHANGED_DJI_SDK = (TextView) findViewById(R.id.tv_ON_PRODUCT_CHANGED_DJI_SDK);
        mON_COMPONENT_CHANGED_DJI_SDK = (TextView) findViewById(R.id.tv_ON_COMPONENT_CHANGED_DJI_SDK);
        mON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK = (TextView) findViewById(R.id.tv_ON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK);
        mON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK = (TextView) findViewById(R.id.tv_ON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK);
    }


    private void updateVersion() {
        String version = "";
        if (mProduct != null) {
            version = mProduct.getFirmwarePackageVersion();
        }
        Message msg = new Message();
        Bundle bundle = new Bundle();
        String stVersionMsg = "";
        if (version.equalsIgnoreCase("null")) {
            stVersionMsg = "Unable to retrieve firmware"; // Show a message if firmware is unavailable.
        } else {
            stVersionMsg = "Firmware: " + version; //"Firmware version: " +
        }
        bundle.putString("Version", stVersionMsg );
        msg.what = MSG_VERSION;
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onProductVersionChange(String oldVersion, String newVersion) {
        updateVersion();
    }

    // When the event is broadcast - update the UI.
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSDKRelativeUI();
            showConnectionPreferences();
        }
    };

    private void refreshSDKRelativeUI() {
        try {
            // Show if the application is connected to the 1) drone, 2) only RC, or 3) not connected.
            mProduct = DjiApplication.getProductInstance(); // Obtain the product.
            if (null != mProduct && mProduct.isConnected()) { // Show the case where the drone, rc, and application successfully connects
                Log.v(TAG, "refreshSDK: True");
                mBtnOpen.setEnabled(true);
                String str = mProduct instanceof DJIAircraft ? "DJIAircraft" : "DJIHandHeld";
                mTvConnectionStatus.setText("Status: " + str + " connected");
                mProduct.setDJIVersionCallback(this);
                updateVersion();
                if (null != mProduct.getModel()) {
                    mTvProduct.setText("" + mProduct.getModel().getDisplayName());
                } else {
                    mTvProduct.setText("Product Information");
                }
            } else if (null != mProduct && (!mProduct.isConnected())) { // Show the case where there is a product, no successful connection, but the rc is connected.
                if (mProduct instanceof DJIAircraft) {
                    DJIAircraft aircraft = (DJIAircraft) mProduct;
                    if (aircraft.getRemoteController() != null && aircraft.getRemoteController().isConnected()) {
                        mBtnOpen.setEnabled(false);
                        if (null != mProduct.getModel()) {
                            mTvProduct.setText(mProduct.getModel().getDisplayName());
                        } else {
                            mTvProduct.setText("Product Information");
                        }
                        mTvConnectionStatus.setText("Only RC Connected");
                    } else {
                        mBtnOpen.setEnabled(false);
                        mTvProduct.setText("Product Information");
                        mTvConnectionStatus.setText("Not connected");
                    }
                }
            } else { // Show the case where there is no connection to either the drone and/or rc.
                Log.v(TAG, "refreshSDK: False");
                mBtnOpen.setEnabled(false);
                mTvProduct.setText("Product Information");
                mTvConnectionStatus.setText("Not connected");
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error updating refreshSDKRelativeUI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showConnectionPreferences() {
        // Show the values stored in shared preferences from DjiApplication which are set when the application is first connected to the drone.
        // These values also update when the drone is turned off
        String stINIT_DJI_SDK = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("INIT_DJI_SDK", "No value stored");
        String stON_PRODUCT_CHANGED_DJI_SDK = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ON_PRODUCT_CHANGED_DJI_SDK", "No value stored");
        String stON_COMPONENT_CHANGED_DJI_SDK = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ON_COMPONENT_CHANGED_DJI_SDK", "No value stored");
        String stON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK", "No value stored");
        String stON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK", "No value stored");

        mTvINIT_DJI_SDK.setText("INIT_DJI_SDK: " + stINIT_DJI_SDK);
        mTvON_PRODUCT_CHANGED_DJI_SDK.setText("ON_PRODUCT_CHANGED_DJI_SDK: " + stON_PRODUCT_CHANGED_DJI_SDK);
        mON_COMPONENT_CHANGED_DJI_SDK.setText("ON_COMPONENT_CHANGED_DJI_SDK: " + stON_COMPONENT_CHANGED_DJI_SDK);
        mON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK.setText("ON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK: " + stON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK);
        mON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK.setText("ON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK: " + stON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK);

    }

    public void btnOpen_onClick(View v) {
        // Show the SwitchboardActivty and add the activity to the backstack.
        int id = 1;
        Intent switchboardIntent = new Intent(this, SwitchboardActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(SwitchboardActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(switchboardIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, builder.build());
        startActivity(switchboardIntent);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String version = "";
            switch (msg.what) {
                case MSG_VERSION:
                    version = bundle.getString("Version");
                    break;
            }
            if (mTvModelAvailable != null) {
                mTvModelAvailable.setText(version);
            }
            return false;
        }

    });

}
