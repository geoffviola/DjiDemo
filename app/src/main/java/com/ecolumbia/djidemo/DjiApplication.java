package com.ecolumbia.djidemo;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import dji.sdk.Products.DJIAircraft;
import dji.sdk.Products.DJIHandHeld;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;
import dji.sdk.base.DJISDKError;

/**
 * Created by DaveJacob on 2/6/2016.
 */
public class DjiApplication extends Application {

    private static final String TAG = DjiApplication.class.getName();

    public static final String FLAG_CONNECTION_CHANGE = "com_ecolumbia_djidemo_connection_change";

    private static DJIBaseProduct mProduct;

    private Handler mHandler;

    /**
     * Gets instance of the specific product connected after the
     * API KEY is successfully validated. Please make sure the
     * API_KEY has been added in the Manifest
     */
    public static synchronized DJIBaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getDJIProduct();
        }
        return mProduct;
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof DJIAircraft;
    }

    public static boolean isHandHeldConnected() {
        return getProductInstance() != null && getProductInstance() instanceof DJIHandHeld;
    }

    public static synchronized DJIAircraft getAircraftInstance() {
        if (!isAircraftConnected()) return null;
        return (DJIAircraft) getProductInstance();
    }

    public static synchronized DJIHandHeld getHandHeldInstance() {
        if (!isHandHeldConnected()) return null;
        return (DJIHandHeld) getProductInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler(Looper.getMainLooper());

        /**
         * handles SDK Registration using the API_KEY
         */
        DJISDKManager.getInstance().initSDKManager(this, mDJISDKManagerCallback);
    }

    private DJISDKManager.DJISDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.DJISDKManagerCallback() {

        @Override
        public void onGetRegisteredResult(DJIError error) {
            if(error == DJISDKError.REGISTRATION_SUCCESS) {
                DJISDKManager.getInstance().startConnectionToProduct();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("INIT_DJI_SDK", "Init DJI SDK succeeded").commit();
            } else {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("INIT_DJI_SDK", "Init DJI SDK failed" + error.getDescription()).commit();
            }
            Log.v(TAG, error.getDescription());
        }

        @Override
        public void onProductChanged(DJIBaseProduct oldProduct, DJIBaseProduct newProduct) {

            Log.v(TAG, String.format("onProductChanged oldProduct:%s, newProduct:%s", oldProduct, newProduct));
            mProduct = newProduct;
            if(mProduct != null ) {
                mProduct.setDJIBaseProductListener(mDJIBaseProductListener);
            }
            if (newProduct != null) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ON_PRODUCT_CHANGED_DJI_SDK", "New Product: " + newProduct.toString()).commit();
            } else {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ON_PRODUCT_CHANGED_DJI_SDK", "New Product: " + "no product found").commit();
            }
            notifyStatusChange();
        }

        private DJIBaseProduct.DJIBaseProductListener mDJIBaseProductListener = new DJIBaseProduct.DJIBaseProductListener() {

            @Override
            public void onComponentChange(DJIBaseProduct.DJIComponentKey key, DJIBaseComponent oldComponent, DJIBaseComponent newComponent) {

                if(newComponent != null) {
                    newComponent.setDJIComponentListener(mDJIComponentListener);
                }
                Log.v(TAG, String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s", key, oldComponent, newComponent));
                if (newComponent != null) {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ON_COMPONENT_CHANGED_DJI_SDK", "New Product: " + newComponent.toString()).commit();
                }else {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ON_COMPONENT_CHANGED_DJI_SDK", "New Product: " + "no component found").commit();
                }
                notifyStatusChange();
            }

            @Override
            public void onProductConnectivityChanged(boolean isConnected) {

                Log.v(TAG, "onProductConnectivityChanged: " + isConnected);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ON_PRODUCT_CONNECTIVITY_CHANGED_DJI_SDK", "Product Connectivity Result: " + isConnected).commit();
                notifyStatusChange();
            }

        };

        private DJIBaseComponent.DJIComponentListener mDJIComponentListener = new DJIBaseComponent.DJIComponentListener() {

            @Override
            public void onComponentConnectivityChanged(boolean isConnected) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ON_COMPONENT_CONNECTIVITY_CHANGED_DJI_SDK", "Component Connectivity Result: " + isConnected).commit();
                notifyStatusChange();
            }

        };

        private void notifyStatusChange() {
            mHandler.removeCallbacks(updateRunnable);
            mHandler.postDelayed(updateRunnable, 500);
        }

        private Runnable updateRunnable = new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
                sendBroadcast(intent);
            }
        };
    };

}
