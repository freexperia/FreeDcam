package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.BaseCameraHolder;
import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewFpsParameter extends  BaseModeParameter
{
    BaseCameraHolder cameraHolder;
    public PreviewFpsParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewFpsParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, BaseCameraHolder holder) {
        super(parameters, parameterChanged, value, values);
        this.cameraHolder = holder;
    }



    @Override
    public void SetValue(String valueToSet)
    {
        if (cameraHolder.IsPreviewRunning())
            cameraHolder.StopPreview();
        parameters.set(value, valueToSet);
        cameraHolder.SetCameraParameters(parameters);
        if (!cameraHolder.IsPreviewRunning())
            cameraHolder.StartPreview();
        super.SetValue(valueToSet);

    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}