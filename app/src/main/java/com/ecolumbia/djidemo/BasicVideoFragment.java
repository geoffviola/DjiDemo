package com.ecolumbia.djidemo;


import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import dji.sdk.AirLink.DJILBAirLink;
import dji.sdk.Camera.DJICamera;
import dji.sdk.Codec.DJICodecManager;
import dji.sdk.base.DJIBaseProduct;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicVideoFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private TextureView mVideoSurface = null;
    private DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallback = null;
    private DJILBAirLink.DJIOnReceivedVideoCallback mOnReceivedVideoCallback = null;
    private DJICodecManager mCodecManager = null;
    private DJIBaseProduct mProduct = null;
    private TextView mTvError;
    private LinearLayout mLayoutVideoContainer;

    public BasicVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;
        v = inflater.inflate(R.layout.fragment_basic_video, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVideoSurface = (TextureView) getActivity().findViewById(R.id.textureview_Video);
        mTvError = (TextView) getActivity().findViewById(R.id.tvError);
        mLayoutVideoContainer = (LinearLayout) getActivity().findViewById(R.id.llVideoContainer);
        ViewGroup.LayoutParams params = mLayoutVideoContainer.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;

        mLayoutVideoContainer.setLayoutParams(params);

        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);

            // This callback is for
            mOnReceivedVideoCallback = new DJILBAirLink.DJIOnReceivedVideoCallback() {
                @Override
                public void onResult(byte[] videoBuffer, int size) {
                    if (mCodecManager != null) {
                        mCodecManager.sendDataToDecoder(videoBuffer, size);
                    }
                }
            };

            mReceivedVideoDataCallback = new DJICamera.CameraReceivedVideoDataCallback() {
                @Override
                public void onResult(byte[] videoBuffer, int size) {
                    if (null != mCodecManager) {
                        mCodecManager.sendDataToDecoder(videoBuffer, size);
                    }
                }
            };
        } else {
            mTvError.setText("Video Surface is null");
        }


        initSDKCallback();
    }

    private void initSDKCallback() {
        try {
            mProduct = DjiApplication.getProductInstance();

            if (mProduct.getModel() != DJIBaseProduct.Model.UnknownAircraft) {
                mProduct.getCamera().setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallback);
            } else {
                mProduct.getAirLink().getLBAirLink().setDJIOnReceivedVideoCallback(mOnReceivedVideoCallback);
            }
        } catch (Exception exception) {
            mTvError.setText("Exception on initSDKCallback: " + exception.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(getContext(), surface, width, height);
        } else {
            mTvError.setText("mCodecManger is null ");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mCodecManager = null;
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(getContext(), surface, width, height);
        } else {
            mTvError.setText("mCodecManger is null ");
        }

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
