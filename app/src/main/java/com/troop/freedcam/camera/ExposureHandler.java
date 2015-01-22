package com.troop.freedcam.camera;

import android.hardware.Camera;

import com.troop.freedcam.camera.modules.CameraFocusEvent;
import com.troop.freedcam.camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.AbstractFocusHandler;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.List;

/**
 * Created by George on 1/21/2015.
 */
public class ExposureHandler extends AbstractFocusHandler {
    private final BaseCameraHolder cameraHolder;
    private final CameraUiWrapper cameraUiWrapper;
    private final AbstractParameterHandler parametersHandler;

    int count;
    List<Camera.Area> areas;
    boolean isFocusing = false;

    public ExposureHandler(CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = cameraUiWrapper.cameraHolder;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
    }



    public void StartFocus()
    {
    }

    public void StartTouchToExposure(FocusRect rect, int width, int height)
    {
        cameraHolder.CancelFocus();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cameraUiWrapper.camParametersHandler.ExposureLock.GetValue().equals("true")) {
            cameraUiWrapper.camParametersHandler.ExposureLock.SetValue("false", true);
            cameraUiWrapper.camParametersHandler.ExposureLock.BackgroundValueHasChanged("false");
        }
        String focusmode = parametersHandler.FocusMode.GetValue();
        if (focusmode.equals("auto") || focusmode.equals("macro"))
        {
            final FocusRect targetFocusRect = new FocusRect(
                    rect.left * 2000 / width - 1000,
                    rect.right * 2000 / width - 1000,
                    rect.top * 2000 / height - 1000,
                    rect.bottom * 2000 / height - 1000);
            //check if stuff is to big or to small and set it to min max value
            if (targetFocusRect.left < -1000)
            {
                int dif = targetFocusRect.left + 1000;
                targetFocusRect.left = -1000;
                targetFocusRect.right += dif;
            }
            if (targetFocusRect.right > 1000)
            {
                int dif = targetFocusRect.right - 1000;
                targetFocusRect.right = 1000;
                targetFocusRect.left -= dif;
            }
            if (targetFocusRect.top < -1000)
            {
                int dif = targetFocusRect.top + 1000;
                targetFocusRect.top = -1000;
                targetFocusRect.bottom += dif;
            }
            if (targetFocusRect.bottom > 1000)
            {
                int dif = targetFocusRect.bottom -1000;
                targetFocusRect.bottom = 1000;
                targetFocusRect.top -=dif;
            }


            if (targetFocusRect.left >= -1000
                    && targetFocusRect.top >= -1000
                    && targetFocusRect.bottom <= 1000
                    && targetFocusRect.right <= 1000)
            {

                parametersHandler.SetFocusAREA(targetFocusRect, 300);


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                    //Ex.FocusStarted(rect);
            }
        }

    }
}