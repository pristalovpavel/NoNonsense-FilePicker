package com.nononsenseapps.filepicker.com.nononsenseapps.filepicker.core;

import java.io.File;

/**
 * Created by Pristalov Pavel on 15.01.2015 for NoNonsense-FilePicker.
 */
public class LocalFileSystemObject
        implements FileSystemObjectInterface
{
    private File file;

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
    public String getPath()
    {
        return file != null ? file.getPath() : "" ;
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


}
