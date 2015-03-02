package com.nononsenseapps.filepicker.sample.googledrive;

/**
 * Created by Pristalov Pavel on 03.02.2015 for NoNonsense-FilePicker.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Вспомогательный класс для проверки доступности Google Services
 */
public class PlayServicesUtils
{
    /**
     * Проверяет, доступны ли Google Services
     * @param context контекст
     * @return true - доступны, false - нет
     */
    public static boolean checkGooglePlayServicesAvailability(Context context)
    {
        int resultCode = ConnectionResult.SERVICE_MISSING;
        if (context != null)
        {
            resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
            if (resultCode != ConnectionResult.SUCCESS) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, 69);
                dialog.setCancelable(true);
                dialog.show();
            }
        }
        return resultCode == ConnectionResult.SUCCESS;
    }
}

