package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.LandscapeSeekbarControl;
import com.troop.freecam.enums.E_ManualSeekbar;

/**
 * Created by troop on 27.08.13.
 */
public class ZoomSeekbar extends LandscapeSeekbarControl implements Camera.OnZoomChangeListener
{
    boolean zoomaktiv = false;
    String TAG = "freecam.ZoomManager";

    public ZoomSeekbar(Context context) {
        super(context);
    }

    public ZoomSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public  void setZoom(int zoom)
    {
        if(!zoomaktiv)
        {
        int temp = current + zoom;
            if ( temp >= 0 && temp <= cameraManager.parametersManager.getParameters().getMaxZoom() )
            {
                current = temp;
                Log.d(TAG, "SmoothZoomSupported:" + cameraManager.parametersManager.getParameters().isSmoothZoomSupported());
                cameraManager.parametersManager.getParameters().setZoom(current);
                if (cameraManager.parametersManager.getParameters().isSmoothZoomSupported())
                {
                    cameraManager.mCamera.startSmoothZoom(current);
                    zoomaktiv = true;
                }
                else
                {
                    cameraManager.ReloadCameraParameters(false);
                    zoomaktiv = false;
                }


            }
        }
    }

    @Override
    public void onZoomChange(int zoomValue, boolean stopped, Camera camera)
    {
        if (stopped)
            zoomaktiv = false;
        //cameraManager.mCamera.stopSmoothZoom();
    }

    public  void ResetZoom()
    {
        current = 0;
    }

    public int getMaxZoomValue()
    {
        return cameraManager.parametersManager.getParameters().getMaxZoom();
    }

    @Override
    public void SetCameraManager(CameraManager cameraManager) {
        super.SetCameraManager(cameraManager);
        e_manualSeekbar = E_ManualSeekbar.Zoom;
        cameraManager.SetOnZoomChangedListner(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        setZoom(progress);
        textView_currentValue.setText("Zoom: " + progress);

    }

    @Override
    public void SetCurrentValue(int current) {
        if ( current >= 0 && current <= cameraManager.parametersManager.getParameters().getMaxZoom() )
        {
            this.current = current;
            Log.d(TAG, "SmoothZoomSupported:" + cameraManager.parametersManager.getParameters().isSmoothZoomSupported());
            cameraManager.parametersManager.getParameters().setZoom(current);
            if (cameraManager.parametersManager.getParameters().isSmoothZoomSupported())
            {
                cameraManager.mCamera.startSmoothZoom(current);
                zoomaktiv = true;
            }
            else
            {
                cameraManager.ReloadCameraParameters(false);
                zoomaktiv = false;
            }


        }
        textView_currentValue.setText("Zoom: " + current);
    }


}