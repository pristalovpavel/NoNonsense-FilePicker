package com.nononsenseapps.filepicker.fs;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Pristalov Pavel on 15.01.2015 for NoNonsense-FilePicker.
 */
public class LocalFileSystemObject
        implements FileSystemObjectInterface
{
    private File file;

    public void setFile(File file)
    {
        this.file = file;
    }

    public File getFile()
    {
        return file;
    }

    public LocalFileSystemObject(File file)
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
        return file != null ? file.getName() : "" ;
    }

    /**
     * Returns the full path to current file or directory
     *
     * @return path to object
     */
    @Override
    public LocalFileSystemObject getDir(String path)
    {
        return !TextUtils.isEmpty(path) ?
                new LocalFileSystemObject(new File(path)) :
                null;
    }

    /**
     * Returns String-path to current object (with file name)
     *
     * @return full path to current object
     */
    @Override
    public String getFullPath()
    {
        return file != null ? file.getPath() : "";
    }

    /**
     * Returns true if the object is folder
     *
     * @return true - folder, false otherwise
     */
    @Override
    public boolean isDir()
    {
        return file != null ? file.isDirectory() : false;
    }

    /**
     * Returns true if the object is file
     *
     * @return true - file, false otherwise
     */
    @Override
    public boolean isFile()
    {
        return file != null ? file.isFile() : false ;
    }

    /**
     * Returns Uri representation of file path
     *
     * @return Uri with path
     */
    @Override
    public Uri toUri()
    {
        return file != null ? Uri.fromFile(file) : null;
    }

    /**
     * Return the path to the parent directory. Should return the root if
     * from is root.     *
     */
    @Override
    public FileSystemObjectInterface getParent()
    {
        if(file == null) return null;

        if (file.getParentFile() != null)
        {
            if (file.isFile())
                file = file.getParentFile();

            return new LocalFileSystemObject(file.getParentFile());
        }
        else
        {
            return this;
        }
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
            file = new File(path);
    }
}
