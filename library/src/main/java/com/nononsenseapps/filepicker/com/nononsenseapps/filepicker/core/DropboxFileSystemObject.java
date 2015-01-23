package com.nononsenseapps.filepicker.com.nononsenseapps.filepicker.core;

import android.net.Uri;
import android.text.TextUtils;

import com.dropbox.client2.DropboxAPI;

import java.io.File;

/**
 * Created by Pristalov Pavel on 21.01.2015 for NoNonsense-FilePicker.
 */
public class DropboxFileSystemObject implements FileSystemObjectInterface
{
    private DropboxAPI.Entry file;

    public DropboxAPI.Entry getFile()
    {
        return file;
    }

    public void setFile(DropboxAPI.Entry file)
    {
        this.file = file;
    }

    public DropboxFileSystemObject(DropboxAPI.Entry file)
    {
        this.file = file;
    }

    /**
     * Returns the file or folder name
     *
     * @return name of file or folder
     */
    @Override
    public String getName()
    {
        return file != null ? file.fileName() : "";
    }

    /**
     * Returns the full path to current file or directory
     *
     * @return path to object
     */
    @Override
    public DropboxFileSystemObject getDir(String path)
    {
        final DropboxAPI.Entry entry = new DropboxAPI.Entry();
        entry.path = path;
        entry.isDir = true;
        return new DropboxFileSystemObject(entry);
    }

    /**
     * Returns String-path to current object (with file name)
     *
     * @return full path to current object
     */
    @Override
    public String getFullPath()
    {
        return file != null ? file.path : "/";
    }

    /**
     * Returns true if the object is folder
     *
     * @return true - folder, false otherwise
     */
    @Override
    public boolean isDir()
    {
        return file != null ? file.isDir : false;
    }

    /**
     * Returns true if the object is file
     *
     * @return true - file, false otherwise
     */
    @Override
    public boolean isFile()
    {
        return file != null ? !file.isDir : false;
    }

    /**
     * Returns Uri representation of file path
     *
     * @return Uri with path
     */
    @Override
    public Uri toUri()
    {
        return new Uri.Builder().scheme("dropbox").path(getFullPath()).build();
    }

    /**
     * Return the path to the parent directory. Should return the root if
     * from is root.     *
     */
    @Override
    public FileSystemObjectInterface getParent()
    {
        if(file == null) return null;

        // Take care of a slight limitation in Dropbox code:
        if (getFullPath().length() > 1 && getFullPath().endsWith("/"))
        {
            file.path = getFullPath().substring(0, getFullPath().length() - 1);
        }

        String parent = file.parentPath();
        if (TextUtils.isEmpty(parent))
        {
            parent = "/";
        }

        return getDir(parent);
    }

    /**
     * Inits inner file object with the specific path
     *
     * @param path String with path in current file system
     */
    @Override
    public void setPath(String path)
    {
        if(!TextUtils.isEmpty(path))
        {
            file = new DropboxAPI.Entry();
            file.path = path;
        }
    }
}
