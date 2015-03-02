package com.nononsenseapps.filepicker.sample.googledrive;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.nononsenseapps.filepicker.fs.DropboxFileSystemObject;
import com.nononsenseapps.filepicker.fs.FileSystemObjectInterface;
import com.nononsenseapps.filepicker.sample.R;
import com.nononsenseapps.filepicker.ui.core.AbstractFilePickerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pristalov Pavel on 03.02.2015 for NoNonsense-FilePicker.
 */
public class DriveFilePickerFragment extends AbstractFilePickerFragment
{
    //private final GoogleApiClient googleApiClient;
    private FolderCreator folderCreator;
    private MetadataBuffer fileBuffer;

    public DriveFilePickerFragment()
    {
        super();
        /*this.googleApiClient = ((DriveFilePickerActivity)getActivity()).googleApiClient;
        if (this.googleApiClient == null)
        {
            throw new NullPointerException("FileSystem may not be null");
        }
        else if (!this.googleApiClient.isConnected())
        {
            throw new IllegalArgumentException("Must be linked with Drive!");
        }*/
    }

    /**
     * Name is validated to be non-null, non-empty and not containing any
     * slashes.
     *
     * @param name The name of the folder the user wishes to create.
     */
    public void onNewFolder(final String name)
    {
        File folder = new File(currentPath.getFullPath(), name);

        if (folderCreator == null)
        {
            folderCreator = new FolderCreator();
        }

        folderCreator.execute(folder.getPath());
    }

    /*@Override
    public void onConnected(Bundle bundle)
    {
        Query query = new Query.Builder().build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(metadataCallback);

    }

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback
            = new ResultCallback<DriveApi.MetadataBufferResult>()
    {
        @Override
        public void onResult(DriveApi.MetadataBufferResult result)
        {
            if (!result.getStatus().isSuccess())
            {
                Toast.makeText(getActivity(), "Problem while retrieving results", Toast.LENGTH_SHORT);
                return;
            }
            fileBuffer = result.getMetadataBuffer();
        }
    };*/

    private class FolderCreator extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(final String... paths)
        {
            for (String path : paths)
            {
                /*try
                {
                    //dbApi.createFolder(path);
                    //currentPath = dbApi.metadata(path, 1, null, false, null);
                    refresh();
                } catch (DropboxException e)
                {
                    Toast.makeText(getActivity(), R.string.create_folder_error, Toast.LENGTH_SHORT).show();
                }*/
            }
            return null;
        }
    }

    public void refreshAdapter()
    {

    }

    /**
     * Get the root path (lowest allowed).
     */
    @Override
    protected FileSystemObjectInterface getRoot()
    {
        return new DriveFileSystemObject(((DriveFilePickerActivity)getActivity()).rootPath);
    }

    /**
     * Get a loader that lists the files in the current path,
     * and monitors changes.
     */
    @Override
    protected Loader<List<FileSystemObjectInterface>> getLoader()
    {
        return null;
    }
}
