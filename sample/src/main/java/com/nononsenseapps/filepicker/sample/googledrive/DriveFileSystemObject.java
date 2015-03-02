package com.nononsenseapps.filepicker.sample.googledrive;

import android.net.Uri;

import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.nononsenseapps.filepicker.fs.FileSystemObjectInterface;

/**
 * Created by Pristalov Pavel on 04.02.2015 for NoNonsense-FilePicker.
 */
public class DriveFileSystemObject implements FileSystemObjectInterface
{
    private String path;

    DriveFileSystemObject(String path)
    {
        this.path = path;
    }

    /**
     * Returns the file or folder name
     *
     * @return name of file or folder
     */
    @Override
    public String getName()
    {
        Metadata data = DataSource.getInstance().getDataFromMemCache(path);
        return (data != null) ? data.getTitle() : null;
    }

    /**
     * Returns String-path to current object (with file name)
     *
     * @return full path to current object
     */
    @Override
    public String getFullPath()
    {
        return this.path;
    }

    /**
     * Returns true if the object is folder
     *
     * @return true - folder, false otherwise
     */
    @Override
    public boolean isDir()
    {
        Metadata data = DataSource.getInstance().getDataFromMemCache(path);
        return (data != null) ? data.isFolder() : false;
    }

    /**
     * Returns Uri representation of file path
     *
     * @return Uri with path
     */
    @Override
    public Uri toUri()
    {
        return new Uri.Builder().scheme("drive").path(getFullPath()).build();
    }

    /**
     * Return the path to the parent directory. Should return the root if
     * from is root.     *
     */
    @Override
    public DriveFileSystemObject getParent()
    {
        String newPath = path;

        if (newPath.length() > 1 && newPath.endsWith("/"))
        {
            newPath = newPath.substring(0, newPath.length() - 1);
        }

        newPath = newPath.substring(0, newPath.lastIndexOf("/"));

        return newPath.length() != 0 ? new DriveFileSystemObject(newPath) : this;
    }

    /**
     * Inits inner file object with the specific path
     *
     * @param newPath String with path in current file system
     */
    @Override
    public void setPath(String newPath)
    {
        if (newPath.length() > 1 && newPath.endsWith("/"))
        {
            newPath = newPath.substring(0, newPath.length() - 1);
        }

        this.path = newPath;
    }
}
