package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 05.09.2014.
 */
public class DigitalImageStabilizationParameter extends  BaseModeParameter {
    I_CameraHolder baseCameraHolder;

    public DigitalImageStabilizationParameter(HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public DigitalImageStabilizationParameter(HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);

    }
}
