package com.troop.freedcam.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lge.hardware.LGCamera;
import com.sec.android.seccamera.SecCamera;
import com.troop.freedcam.i_camera.modules.CameraFocusEvent;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by troop on 15.08.2014.
 */
public class BaseCameraHolder extends AbstractCameraHolder
{
    Camera mCamera;
    LGCamera lgCamera;
    LGCamera.LGParameters lgParameters;
    private static String TAG = BaseCameraHolder.class.getSimpleName();
    public I_error errorHandler;
    SecCamera samsungCamera;
    I_Callbacks.PictureCallback pictureCallback;
    I_Callbacks.PictureCallback rawCallback;
    I_Callbacks.ShutterCallback shutterCallback;
    I_Callbacks.PreviewCallback previewCallback;
    SurfaceHolder surfaceHolder;

    public boolean hasLGFrameWork = false;
    public boolean hasSamsungFrameWork = false;
    public Location gpsLocation;
    public int Orientation;


    public int CurrentCamera;

    public BaseCameraHolder(I_CameraChangedListner cameraChangedListner, Handler UIHandler)
    {
        super(cameraChangedListner, UIHandler);
        hasSamsungFramework();
        hasLGFramework();
    }

    private void hasSamsungFramework()
    {
        try {
            Class c = Class.forName("com.sec.android.seccamera.SecCamera");
            Log.d(TAG, "Has Samsung Framework");
            hasSamsungFrameWork = true;

            try {
                Class r = Class.forName("com.sec.android.secmediarecorder.SecMediaRecorder");
                Log.d(TAG, "Has SamsungMediaRecorder Framework");
                hasSamsungFrameWork = true;

            } catch (Exception e) {

                hasSamsungFrameWork = false;
                Log.d(TAG, "No SamsungMediaRecorder Framework");
            }

        } catch (Exception e) {

            hasSamsungFrameWork = false;
            Log.d(TAG, "No Samsung Framework");
        }
        catch (UnsatisfiedLinkError er)
        {
            hasSamsungFrameWork = false;
            Log.d(TAG, "No Samsung Framework");
        }


    }

    private void hasLGFramework()
    {
        try {
            Class c = Class.forName("com.lge.hardware.LGCamera");
            Log.d(TAG, "Has Lg Framework");
            hasLGFrameWork = true;

        } catch (Exception e) {

            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }
        catch (UnsatisfiedLinkError er)
        {
            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
        }

    }

    /**
     * Opens the Camera
     * @param camera the camera to open
     * @return false if camera open fails, return true when open
     */
    @Override
    public boolean OpenCamera(final int camera)
    {

        try
        {
            if (hasSamsungFrameWork)
                samsungCamera = SecCamera.open(camera);
            else if (hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/) {
                lgCamera = new LGCamera(camera);
                mCamera = lgCamera.getCamera();
                lgParameters = lgCamera.getLGParameters();
            } else {
                mCamera = Camera.open(camera);
            }

            isRdy = true;
            cameraChangedListner.onCameraOpen("");

        } catch (Exception ex) {
            isRdy = false;
            ex.printStackTrace();
        }

        return isRdy;
    }

    @Override
    public void CloseCamera()
    {
        Log.d(TAG, "Try to close Camera");
        if (samsungCamera != null)
        {
            try {
                samsungCamera.release();
                samsungCamera = null;
                Log.d(TAG, "Samsung Camera closed");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e(TAG, "Error on Samsung Camera close");
            }
        }
        else if (mCamera != null)
        {
            try
            {
                mCamera.release();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e(TAG, "Error on Camera close");
            }
            finally {
                mCamera = null;
                Log.d(TAG, "Camera closed");
            }
        }
        isRdy = false;
        cameraChangedListner.onCameraClose("");
    }



    @Override
    public int CameraCout() {
        return Camera.getNumberOfCameras();
    }

    @Override
    public boolean IsRdy() {
        return isRdy;
    }

    @Override
    public boolean SetCameraParameters(final HashMap<String, String> parameters)
    {
        String ret = "";
        for (Map.Entry s : parameters.entrySet())
        {
            ret += s.getKey() + "=" + s.getValue()+";";
        }

        if (hasSamsungFrameWork)
        {
            Log.d(TAG, "Set Samsung Parameters");
            SecCamera.Parameters p = samsungCamera.getParameters();
            p.unflatten(ret);
            samsungCamera.setParameters(p);
        }
        else if (hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/)
        {
            Log.d(TAG, "Set lg Parameters");
            Camera.Parameters p = lgParameters.getParameters();
            p.unflatten(ret);
            lgParameters.setParameters(p);
        }
        else
        {
            Log.d(TAG, "Set Parameters");
            Camera.Parameters p = mCamera.getParameters();
            p.unflatten(ret);
            mCamera.setParameters(p);
        }





        return true;
    }

    @Override
    public boolean SetSurface(SurfaceHolder surfaceHolder)
    {
        this.surfaceHolder = surfaceHolder;
        try
        {
            if(hasSamsungFrameWork)
                samsungCamera.setPreviewDisplay(surfaceHolder);
            else
                mCamera.setPreviewDisplay(surfaceHolder);
            this.surfaceHolder = surfaceHolder;
            return  true;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return false;
    }

    public SurfaceHolder getSurfaceHolder()
    {
        return  surfaceHolder;
    }

    @Override
    public void StartPreview()
    {
        if (hasSamsungFrameWork)
            samsungCamera.startPreview();
        else
            mCamera.startPreview();
        isPreviewRunning = true;
        Log.d(TAG, "PreviewStarted");
        cameraChangedListner.onPreviewOpen("");
    }

    @Override
    public void StopPreview()
    {
        if (mCamera == null && samsungCamera == null)
            return;
        try {
            if (hasSamsungFrameWork)
                samsungCamera.stopPreview();
            else
                mCamera.stopPreview();
            isPreviewRunning = false;
            Log.d(TAG, "Preview Stopped");
            cameraChangedListner.onPreviewClose("");

        } catch (Exception ex) {
            isPreviewRunning = false;
            Log.d(TAG, "Camera was released");
            ex.printStackTrace();
        }
    }

    public HashMap<String, String> GetCameraParameters()
    {
        String[] split = null;
        if (hasSamsungFrameWork)
            split = samsungCamera.getParameters().flatten().split(";");
        else if (hasLGFrameWork)
            split = lgCamera.getLGParameters().getParameters().flatten().split(";");
        else
            split = mCamera.getParameters().flatten().split(";");
        HashMap<String, String> map = new HashMap<>();
        for (String s: split)
        {
            String[] valSplit = s.split("=");
            boolean sucess = false;
            try
            {
                map.put(valSplit[0], valSplit[1]);
            }
            catch (Exception ex)
            {
                map.put(valSplit[0], "");
            }

        }

        return map;
    }

    public void TakePicture(final I_Callbacks.ShutterCallback shutter, final I_Callbacks.PictureCallback raw, final I_Callbacks.PictureCallback picture)
    {
        this.pictureCallback = picture;
        this.shutterCallback = shutter;
        this.rawCallback = raw;

        if (hasSamsungFrameWork)
        {
            takeSamsungPicture();
        }
        else
        {
            takePicture();
        }
    }

    private void takePicture()
    {
        Camera.ShutterCallback sh = null;
        if (shutterCallback != null)
        {
            sh = new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    shutterCallback.onShutter();
                }
            };
        }
        Camera.PictureCallback r = null;
        if (rawCallback != null)
        {
            r = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera secCamera)
                {
                    if (rawCallback != null)
                        rawCallback.onPictureTaken(bytes);
                }
            };
        }
        if (pictureCallback == null)
            return;
        Camera.PictureCallback pic = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera secCamera) {
                pictureCallback.onPictureTaken(bytes);
                
            }
        };
        this.mCamera.takePicture(sh, r, pic);
    }

    private void takeSamsungPicture() {
        SecCamera.ShutterCallback sh = null;
        if (shutterCallback != null)
        {
            sh = new SecCamera.ShutterCallback() {
                @Override
                public void onShutter() {
                    shutterCallback.onShutter();
                }
            };
        }
        SecCamera.PictureCallback r = null;
        if (rawCallback != null)
        {
            r = new SecCamera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, SecCamera secCamera) {
                    rawCallback.onPictureTaken(bytes);
                }
            };
        }
        if (pictureCallback == null)
            return;
        SecCamera.PictureCallback pic = new SecCamera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, SecCamera secCamera) {
                pictureCallback.onPictureTaken(bytes);
                rawCallback = null;
                shutterCallback = null;
                pictureCallback = null;
            }
        };
        samsungCamera.takePicture(sh,r,pic);
    }

    public void SetPreviewCallback(final I_Callbacks.PreviewCallback previewCallback)
    {
        this.previewCallback = previewCallback;
        if (hasSamsungFrameWork)
        {
            if (previewCallback == null)
                samsungCamera.setPreviewCallback(null);
            else
                samsungCamera.setPreviewCallback(new SecCamera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, SecCamera secCamera) {
                        previewCallback.onPreviewFrame(bytes);
                    }
                });
        }
        else
        {
            if (previewCallback == null)
                mCamera.setPreviewCallback(null);
            else
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        previewCallback.onPreviewFrame(data);
                    }
                });
        }
    }

    public void SetErrorCallback(final I_Callbacks.ErrorCallback errorCallback)
    {
        if (hasSamsungFrameWork)
        {
            samsungCamera.setErrorCallback(new SecCamera.ErrorCallback() {
                @Override
                public void onError(int i, SecCamera secCamera) {
                    errorCallback.onError(i);
                }
            });
        }
        else
        {
            mCamera.setErrorCallback(new Camera.ErrorCallback() {
                @Override
                public void onError(int error, Camera camera) {
                    errorCallback.onError(error);
                }
            });
        }
    }

    public void StartFocus(final I_Callbacks.AutoFocusCallback autoFocusCallback)
    {

        try {
            if (hasSamsungFrameWork)
            {
                samsungCamera.autoFocus(new SecCamera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(int i, SecCamera secCamera)
                    {
                        CameraFocusEvent focusEvent = new CameraFocusEvent();
                        focusEvent.samsungCamera = secCamera;
                        if (i == 1) //no idea if this correct
                            focusEvent.success = true;
                        else
                            focusEvent.success = false;
                        autoFocusCallback.onAutoFocus(focusEvent);
                    }
                });
            }
            else
            {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera)
                    {
                        CameraFocusEvent focusEvent = new CameraFocusEvent();
                        focusEvent.camera = camera;
                        focusEvent.success = success;
                        autoFocusCallback.onAutoFocus(focusEvent);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void SetFocusAreas(FocusRect focusRect, FocusRect meteringRect)
    {

        try {
            if (hasSamsungFrameWork) {
                Log.d(TAG, "Set Samsung Focus");
                List<SecCamera.Area> areaList = new ArrayList<>();
                areaList.add(new SecCamera.Area(new Rect(focusRect.left, focusRect.top, focusRect.right, focusRect.bottom), 1));
                //List<SecCamera.Area> meteringList = new ArrayList<>();
                //if (meteringRect != null)
                //    meteringList.add(new SecCamera.Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 1));
                SecCamera.Parameters p = samsungCamera.getParameters();
                if (p.getMaxNumFocusAreas() > 0)
                    p.setFocusAreas(areaList);
                /*if (meteringRect != null && p.getMaxNumMeteringAreas() > 0)
                    p.setMeteringAreas(meteringList);
                else if (p.getMaxNumMeteringAreas() > 0)
                    p.setMeteringAreas(areaList);*/
                try {
                    Log.d(TAG, "try Set Samsung Focus");
                    samsungCamera.setParameters(p);
                    Log.d(TAG, "Setted Samsung Focus");
                } catch (Exception ex) {
                    Log.d(TAG, "Set Samsung Focus FAILED!");
                }

            } else {
                List<Camera.Area> areaList = new ArrayList<>();
                areaList.add(new Camera.Area(new Rect(focusRect.left, focusRect.top, focusRect.right, focusRect.bottom), 1000));
                //List<Camera.Area> meteringList = new ArrayList<>();
                //if (meteringRect != null)
                //    meteringList.add(new Camera.Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 1000));
                if (mCamera == null)
                    return;
                Camera.Parameters p = mCamera.getParameters();
                if (p.getMaxNumFocusAreas() > 0)
                    p.setFocusAreas(areaList);
                /*if (meteringList.size() > 0 && p.getMaxNumMeteringAreas() > 0)
                    p.setMeteringAreas(meteringList);
                else if (p.getMaxNumMeteringAreas() > 0)
                    p.setMeteringAreas(areaList);*/
                try {
                    Log.d(TAG, "try Set Focus");
                    mCamera.setParameters(p);
                    Log.d(TAG, "Setted Focus");
                } catch (Exception ex) {
                    Log.d(TAG, "Set Focus FAILED!");
                }

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void CancelFocus()
    {
        if (hasSamsungFrameWork)
        {
            samsungCamera.cancelAutoFocus();
        }
        else
        {
            mCamera.cancelAutoFocus();
        }
    }

    public void SetMeteringAreas(FocusRect meteringRect)
    {
        try {
            if (hasSamsungFrameWork)
            {
                List<SecCamera.Area> meteringList = new ArrayList<>();
                if (meteringRect != null)
                    meteringList.add(new SecCamera.Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 1000));
                SecCamera.Parameters p = samsungCamera.getParameters();
                if(p.getMaxNumMeteringAreas() > 0);
                    p.setMeteringAreas(meteringList);

                try {
                    Log.d(TAG, "try Set Metering");
                    samsungCamera.setParameters(p);
                    Log.d(TAG, "Setted Metering");
                } catch (Exception ex) {
                    Log.d(TAG, "Set Metering FAILED!");
                }
            }
            else {
                List<Camera.Area> meteringList = new ArrayList<>();
                if (meteringRect != null)
                    meteringList.add(new Camera.Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 1000));
                Camera.Parameters p = mCamera.getParameters();
                if(p.getMaxNumMeteringAreas() > 0);
                    p.setMeteringAreas(meteringList);

                try {
                    Log.d(TAG, "try Set Metering");
                    mCamera.setParameters(p);
                    Log.d(TAG, "Setted Metering");
                } catch (Exception ex) {
                    Log.d(TAG, "Set Metering FAILED!");
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void SetLocation(Location loc)
    {
        this.gpsLocation = loc;
        if(hasSamsungFrameWork && samsungCamera != null)
        {
            SecCamera.Parameters paras = samsungCamera.getParameters();
            paras.setGpsAltitude(loc.getAltitude());
            paras.setGpsLatitude(loc.getLatitude());
            paras.setGpsLongitude(loc.getLongitude());
            paras.setGpsProcessingMethod(loc.getProvider());
            paras.setGpsTimestamp(loc.getTime());
            samsungCamera.setParameters(paras);
        }
        else
        {
            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
                paras.setGpsAltitude(loc.getAltitude());
                paras.setGpsLatitude(loc.getLatitude());
                paras.setGpsLongitude(loc.getLongitude());
                paras.setGpsProcessingMethod(loc.getProvider());
                paras.setGpsTimestamp(loc.getTime());
                mCamera.setParameters(paras);
            }
        }
    }

    public void SetOrientation(int or)
    {
        this.Orientation = or;
        if(hasSamsungFrameWork && samsungCamera != null)
        {
            SecCamera.Parameters paras = samsungCamera.getParameters();
            paras.setRotation(or);
            samsungCamera.setParameters(paras);
        }
        else
        {
            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
                paras.setRotation(or);
                mCamera.setParameters(paras);
            }
        }
    }

    public void SetCameraRotation(int rotation)
    {
        if (samsungCamera == null && mCamera == null)
            return;
        if (hasSamsungFrameWork)
            samsungCamera.setDisplayOrientation(rotation);
        else
            mCamera.setDisplayOrientation(rotation);
    }

    public Camera GetCamera() {
        return mCamera;
    }

    public String getLgParameters()
    {
        return lgParameters.getParameters().flatten();
    }

    public SecCamera GetSamsungCamera() {
        return samsungCamera;
    }

    public void setShootmode(int shootmode)
    {
        samsungCamera.setShootingMode(shootmode);
    }


}
