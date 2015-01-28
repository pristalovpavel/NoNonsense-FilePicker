package com.nononsenseapps.filepicker.fs;

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
    public FileSystemObjectInterface getDir(String path);

    /**
     * Returns String-path to current object (with file name)
     * @return full path to current object
     */
    public String getFullPath();

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

    /**
     * Inits inner file object with the specific path
     * @param path String with path in current file system
     */
    public void setPath(String path);
}
