package com.nononsenseapps.filepicker.sample.googledrive;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.nononsenseapps.filepicker.ui.core.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.ui.core.AbstractFilePickerFragment;

/**
 * Created by Pristalov Pavel on 03.02.2015 for NoNonsense-FilePicker.
 */
public class DriveFilePickerActivity extends AbstractFilePickerActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 0;
    public GoogleApiClient googleApiClient;
    public String rootPath = "";
    DriveFilePickerFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(!googleApiClient.isConnected()) googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if (connectionResult.hasResolution())
        {
            try
            {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            }
            catch (IntentSender.SendIntentException e)
            {
                // Unable to resolve, message user appropriately
            }
        } else
        {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        switch (requestCode)
        {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK)
                {
                    googleApiClient.connect();
                }
                break;
        }
    }

    @Override
    protected AbstractFilePickerFragment getFragment(String startPath, AbstractFilePickerFragment.SelectionMode mode, boolean allowMultiple, boolean allowCreateDir)
    {
        fragment = new DriveFilePickerFragment();
        fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir);
        return fragment;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        DriveFolder root = Drive.DriveApi.getRootFolder(googleApiClient);

        root.getMetadata(googleApiClient).setResultCallback(new ResultCallback<DriveResource.MetadataResult>()
        {
            @Override
            public void onResult(DriveResource.MetadataResult metadataResult)
            {
                if (!metadataResult.getStatus().isSuccess())
                {
                    Toast.makeText(DriveFilePickerActivity.this,
                            "Problem while trying to fetch metadata",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Metadata metadata = metadataResult.getMetadata();
                rootPath = metadata.getTitle();
                DataSource.getInstance().addDataToMemoryCache(rootPath, metadata);

                Toast.makeText(DriveFilePickerActivity.this,
                        "Metadata succesfully fetched. Title: " + rootPath,
                        Toast.LENGTH_SHORT).show();

                if(fragment != null) fragment.refreshAdapter();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }
}
