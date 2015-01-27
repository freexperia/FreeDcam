package com.troop.freedcam.camera.parameters;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.manual.BrightnessManualParameter;
import com.troop.freedcam.camera.parameters.manual.BurstManualParam;
import com.troop.freedcam.camera.parameters.manual.CCTManualParameter;
import com.troop.freedcam.camera.parameters.manual.ContrastManualParameter;
import com.troop.freedcam.camera.parameters.manual.ConvergenceManualParameter;
import com.troop.freedcam.camera.parameters.manual.ExposureManualParameter;
import com.troop.freedcam.camera.parameters.manual.FXManualParameter;
import com.troop.freedcam.camera.parameters.manual.FocusManualParameter;
import com.troop.freedcam.camera.parameters.manual.ISOManualParameter;
import com.troop.freedcam.camera.parameters.manual.SaturationManualParameter;
import com.troop.freedcam.camera.parameters.manual.SharpnessManualParameter;
import com.troop.freedcam.camera.parameters.manual.ShutterManualParameter;
import com.troop.freedcam.camera.parameters.manual.ZoomManualParameter;
import com.troop.freedcam.camera.parameters.modes.AE_Bracket_HdrModeParameter;
import com.troop.freedcam.camera.parameters.modes.AntiBandingModeParameter;
import com.troop.freedcam.camera.parameters.modes.BaseModeParameter;
import com.troop.freedcam.camera.parameters.modes.ColorModeParameter;
import com.troop.freedcam.camera.parameters.modes.DigitalImageStabilizationParameter;
import com.troop.freedcam.camera.parameters.modes.ExposureLockParameter;
import com.troop.freedcam.camera.parameters.modes.ExposureModeParameter;
import com.troop.freedcam.camera.parameters.modes.FlashModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusModeParameter;
import com.troop.freedcam.i_camera.parameters.GuideList;
import com.troop.freedcam.camera.parameters.modes.ImagePostProcessingParameter;
import com.troop.freedcam.camera.parameters.modes.IsoModeParameter;
import com.troop.freedcam.camera.parameters.modes.JpegQualityParameter;
import com.troop.freedcam.camera.parameters.modes.NightModeParameter;
import com.troop.freedcam.camera.parameters.modes.NonZslManualModeParameter;
import com.troop.freedcam.camera.parameters.modes.PictureFormatParameter;
import com.troop.freedcam.camera.parameters.modes.PictureSizeParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewFormatParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewSizeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoHDRModeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.camera.parameters.modes.VideoSizeParameter;
import com.troop.freedcam.camera.parameters.modes.WhiteBalanceModeParameter;
import com.troop.freedcam.camera.parameters.modes.ZeroShutterLagParameter;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler extends AbstractParameterHandler implements I_ParameterChanged
{

    private static String TAG = "freedcam.CameraParametersHandler";

    HashMap<String, String> cameraParameters;
    public HashMap<String, String> getParameters(){return cameraParameters;}

    boolean moreParametersToSet = false;
    BaseCameraHolder baseCameraHolder;
    public BaseModeParameter DualMode;

    SetParameterRunner setParameterRunner;

    public CamParametersHandler(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager,Handler backGroundHandler, Handler uiHandler)
    {
        super(cameraHolder,appSettingsManager, backGroundHandler, uiHandler);
        ParametersEventHandler = new CameraParametersEventHandler();
        baseCameraHolder = (BaseCameraHolder) cameraHolder;
    }

    public void GetParametersFromCamera()
    {
        cameraParameters = baseCameraHolder.GetCameraParameters();
    }

    public void SetParametersToCamera()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = baseCameraHolder.GetCameraParameters();
        setParameterRunner = new SetParameterRunner();
        initParameters();
    }

    private void logParameters(HashMap<String, String> parameters)
    {
        Log.d(TAG, "Manufactur:" + Build.MANUFACTURER);
        Log.d(TAG, "Model:" + Build.MODEL);
        Log.d(TAG, "Product:" + Build.PRODUCT);
        for(Map.Entry e : parameters.entrySet())
        {
            Log.d(TAG, e.getKey() + "=" + e.getValue());
        }
    }

    private void initParameters()
    {
        logParameters(cameraParameters);
        ManualBrightness = new BrightnessManualParameter(cameraParameters, "","","", this);
        ManualContrast = new ContrastManualParameter(cameraParameters, "", "", "",this);
        ManualConvergence = new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", this);
        ManualExposure = new ExposureManualParameter(cameraParameters,"exposure-compensation","max-exposure-compensation","min-exposure-compensation", this);
        ManualFocus = new FocusManualParameter(cameraParameters,"","","", cameraHolder, this);
        ManualSaturation = new SaturationManualParameter(cameraParameters,"","","", this);
        ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "", this);
        ManualShutter = new ShutterManualParameter(cameraParameters,"","","", cameraHolder, this);
        CCT = new CCTManualParameter(cameraParameters,"","","", this);

        FX = new FXManualParameter(cameraParameters,"","","", this);

        Burst = new BurstManualParam(cameraParameters,"","","",this);
       /* if (DeviceUtils.isSonyADV()) {
            ISOManual = new ISOManualParameter(cameraParameters, "", "", "", this);
        }*/
        ISOManual = new ISOManualParameter(cameraParameters,"","","", this);

        Zoom = new ZoomManualParameter(cameraParameters,"", "", "", this);


        ColorMode = new ColorModeParameter(cameraParameters,this, "effect", "effect-values");
        ExposureMode = new ExposureModeParameter(cameraParameters,this,"","");
        FlashMode = new FlashModeParameter(cameraParameters,this,"flash-mode","flash-mode-values");
        IsoMode = new IsoModeParameter(cameraParameters,this,"","", cameraHolder);
        AntiBandingMode = new AntiBandingModeParameter(cameraParameters,this, "antibanding", "antibanding-values");
        WhiteBalanceMode = new WhiteBalanceModeParameter(cameraParameters, this, "whitebalance", "whitebalance-values");
        PictureSize = new PictureSizeParameter(cameraParameters,this, "picture-size", "picture-size-values");
        if(DeviceUtils.isSonyADV())
            PictureFormat = new PictureFormatParameter(cameraParameters, this, "sony-postview-format", "sony-postview-format-values", this, appSettingsManager);
        else
            PictureFormat = new PictureFormatParameter(cameraParameters, this, "picture-format", "picture-format-values", this, appSettingsManager);


        JpegQuality = new JpegQualityParameter(cameraParameters, this, "jpeg-quality", "");
        //defcomg was here


        AE_Bracket = new AE_Bracket_HdrModeParameter(cameraParameters,this, "ae-bracket-hdr", "ae-bracket-hdr-values");
        ImagePostProcessing = new ImagePostProcessingParameter(cameraParameters,this, "ipp", "ipp-values");
        PreviewSize = new PreviewSizeParameter(cameraParameters, this, "preview-size", "preview-size-values", cameraHolder);
        /*PreviewFPS = new PreviewFpsParameter(cameraParameters, this, "preview-frame-rate", "preview-frame-rate-values", cameraHolder);*/
        PreviewFormat = new PreviewFormatParameter(cameraParameters, this, "preview-format", "preview-format-values", cameraHolder);

        SceneMode =  new BaseModeParameter(cameraParameters, this, "scene-mode","scene-mode-values");
        FocusMode = new FocusModeParameter(cameraParameters, this,"focus-mode","focus-mode-values");
        RedEye = new BaseModeParameter(cameraParameters, this, "redeye-reduction", "redeye-reduction-values");
        LensShade = new BaseModeParameter(cameraParameters, this, "lensshade", "lensshade-values");
        ZSL = new ZeroShutterLagParameter(cameraParameters, this, "", "", cameraHolder);
        SceneDetect = new BaseModeParameter(cameraParameters, this, "scene-detect", "scene-detect-values");
        Denoise = new BaseModeParameter(cameraParameters, this, "denoise", "denoise-values");
//sony-is for images sony-vs for video
        if(DeviceUtils.isSonyADV())
            DigitalImageStabilization = new BaseModeParameter(cameraParameters, this, "sony-vs", "sony-vs-values");
        else
            DigitalImageStabilization = new BaseModeParameter(cameraParameters, this, "dis", "dis-values");

        MemoryColorEnhancement = new BaseModeParameter(cameraParameters, this, "mce", "mce-values");
        SkinToneEnhancment = new DigitalImageStabilizationParameter(cameraParameters, this, "skinToneEnhancement", "skinToneEnhancement-values", cameraHolder);
        NightMode = new NightModeParameter(cameraParameters, this,"","");
        NonZslManualMode = new NonZslManualModeParameter(cameraParameters, this, "non-zsl-manual-mode", "", cameraHolder);
        Histogram = new BaseModeParameter(cameraParameters,this, "histogram", "histogram-values");
        CameraMode = new BaseModeParameter(cameraParameters,this, "camera-mode", "camera-mode-values");
        DualMode = new BaseModeParameter(cameraParameters,this, "dual_mode", "");

        ExposureLock = new ExposureLockParameter(cameraParameters, this, "","");


        VideoSize = new VideoSizeParameter(cameraParameters,this,"video-size","video-size");
        if(DeviceUtils.isSonyADV())
            VideoHDR = new VideoHDRModeParameter(cameraParameters, this, "sony-video-hdr", "sony-video-hdr-values", cameraHolder);
        else
            VideoHDR = new VideoHDRModeParameter(cameraParameters, this, "video-hdr", "video-hdr-values", cameraHolder);

        if (baseCameraHolder.hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/)
            VideoProfilesG3 = new VideoProfilesG3Parameter(cameraParameters,this,"","", cameraHolder);
        else
            VideoProfiles = new VideoProfilesParameter(cameraParameters,this,"","", cameraHolder);

        appSettingsManager.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ParametersEventHandler.ParametersHasLoaded();
            }
        });
    }

    @Override
    public void ParameterChanged()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    class SetParameterRunner implements Runnable
    {
        private boolean isRunning = false;

        @Override
        public void run()
        {
            isRunning = true;
            cameraHolder.SetCameraParameters(cameraParameters);
            try {
                //maybe need to incrase the sleeptime if a device crash when setting the manual parameters like manual exposure or manual saturation
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRunning = false;
            //logParameters(cameraHolder.GetCamera().getParameters());
            if (moreParametersToSet)
            {
                moreParametersToSet = false;
                run();
            }
        }
    }

    //focus-areas=(0, 0, 0, 0, 0)
    public void SetFocusAREA(FocusRect focusAreas, int weight)
    {
        ((BaseCameraHolder)cameraHolder).SetFocusAreas(focusAreas);

    }

    public void SetPictureOrientation(int orientation)
    {
        /*try {
            cameraParameters.setRotation(orientation);
            cameraHolder.SetCameraParameters(cameraParameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }*/
    }



    public void LockExposureAndWhiteBalance(boolean value)
    {
        isExposureAndWBLocked = value;
        if (ExposureLock.IsSupported())
            ExposureLock.SetValue(value+"", false);
        SetParametersToCamera();
    }

    public String GetRawSize()
    {
        return cameraParameters.get("raw-size");
    }

    //rawsave-mode=2
    public void setTHL5000Raw(boolean raw)
    {
        Log.d(TAG, "THL5000 try to set mode");
        if (!raw) {
            cameraParameters.put("rawsave-mode", 0+"");
            cameraParameters.put("isp-mode", 0+"");
            Log.d(TAG, "THL5000 set mode to jpeg");
        }
        else
        {
            cameraParameters.put("rawsave-mode", 2+"");
            cameraParameters.put("isp-mode", 1+"");
            Log.d(TAG, "THL5000 set mode to RAW");
        }
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    //rawfname=/storage/sdcard0/DCIM/CameraEM/Capture20141230-160133ISOAuto.raw;
    public void setTHL5000rawFilename(String filename)
    {
        cameraParameters.put("rawfname", filename);
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void setString(String param, String value)
    {
        cameraParameters.put(param, value);
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void setRawSize(String size)
    {
        cameraParameters.put("raw-size", size);
        cameraHolder.SetCameraParameters(cameraParameters);
    }

}
