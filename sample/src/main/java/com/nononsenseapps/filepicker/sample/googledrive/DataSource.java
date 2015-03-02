package com.nononsenseapps.filepicker.sample.googledrive;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.google.android.gms.drive.Metadata;

/**
 * Created by Pristalov Pavel on 05.02.2015 for NoNonsense-FilePicker.
 */
public class DataSource
{
    private static DataSource ourInstance = new DataSource();
    private static LruCache<String, Metadata> driveCache;
    public static String currentPath;

    private DataSource()
    {
        if(driveCache == null)
        {
            // Get max available VM memory, exceeding this amount will throw an
            // OutOfMemory exception. Stored in kilobytes as LruCache takes an
            // int in its constructor.
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory());

            // Use 1/32th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 128;

            driveCache = new LruCache<String, Metadata>(cacheSize);

        }
        if(currentPath == null) currentPath = "";
    }

    public void addDataToMemoryCache(String key, Metadata data)
    {
        driveCache.put(key, data);
    }

    public Metadata getDataFromMemCache(String key)
    {
        return driveCache.get(key);
    }
    public synchronized static DataSource getInstance(){return ourInstance;}

}
