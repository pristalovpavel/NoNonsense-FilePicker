package com.nononsenseapps.filepicker.com.nononsenseapps.filepicker.core;

import android.net.Uri;

/**
 * Created by Pristalov Pavel on 15.01.2015 for NoNonsense-FilePicker.
 */
public interface FileSystemObjectInterface
{
    /**
     * Returns the file or folder name
     * @return name of file or folder
     */
    public String getName();

    /**
     * Returns the full path to current file or directory
     * @return path to object
     */
    public String getPath();

    /**
     * Returns true if the object is folder
     * @return true - folder, false otherwise
     */
    public boolean isDir();

    /**
     * Returns true if the object is file
     * @return true - file, false otherwise
     */
    public boolean isFile();

    /**
     * Returns Uri representation of file path
     * @return Uri with path
     */
    public Uri toUri();

    /**
     * Return the path to the parent directory. Should return the root if
     * from is root.     *
     */
    public FileSystemObjectInterface getParent();
}
