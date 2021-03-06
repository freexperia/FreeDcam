package com.troop.theme.material.shutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;


import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.shutter.CameraSwitchHandler;
import com.troop.theme.material.R;

/**
 * Created by George on 3/17/2015.
 */
public class MaterialCameraSwitchHandler extends CameraSwitchHandler {
    public MaterialCameraSwitchHandler(View activity, AppSettingsManager appSettingsManager)
    {
        super(activity,appSettingsManager);

    }

    @Override
    protected void initBitmaps()
    {
        bitmaps = new Bitmap[3];
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_switch_camera_white_48dp);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_switch_camera_white_48dp);
        bitmaps[1] = front;
        Bitmap back3d = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_switch_camera_white_48dp);
        bitmaps[2] = back3d;
    }
}
