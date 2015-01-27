/*
 * Copyright (c) 2014 Jonas Kalderstam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.annotation.SuppressLint;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.com.nononsenseapps.filepicker.core.DropboxFileSystemObject;
import com.nononsenseapps.filepicker.com.nononsenseapps.filepicker.core.FileSystemObjectInterface;
import com.nononsenseapps.filepicker.sample.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressLint("ValidFragment")
public class DropboxFilePickerFragment
        extends AbstractFilePickerFragment
{
    private final DropboxAPI<AndroidAuthSession> dbApi;
    private FolderCreator folderCreator;

    @SuppressLint("ValidFragment")
    public DropboxFilePickerFragment(final DropboxAPI<AndroidAuthSession> api) {
        super();
        if (api == null) {
            throw new NullPointerException("FileSystem may not be null");
        } else if (!api.getSession().isLinked()) {
            throw new IllegalArgumentException("Must be linked with Dropbox");
        }

        this.dbApi = api;
    }

    public void onNewFolder(final String name) {
        File folder = new File(currentPath.getFullPath(), name);

        if (folderCreator == null) {
            folderCreator = new FolderCreator();
        }

        folderCreator.execute(folder.getPath());
    }

    private class FolderCreator extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... paths) {
            for (String path : paths) {
                try {
                    dbApi.createFolder(path);
                    //currentPath = dbApi.metadata(path, 1, null, false, null);
                    refresh();
                } catch (DropboxException e) {
                    Toast.makeText(getActivity(), R.string.create_folder_error,
                            Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }
    }

    @Override
    protected /*DropboxAPI.Entry*/ DropboxFileSystemObject getRoot()
    {
        final DropboxAPI.Entry entry = new DropboxAPI.Entry();
        entry.path = "/";
        entry.isDir = true;
        return new DropboxFileSystemObject(entry);
    }

    @Override
    protected void initCurrentPath(String path)
    {
        ((DropboxFileSystemObject)currentPath).setPath(path);
    }

    @Override
    protected Loader<List<FileSystemObjectInterface>> getLoader()
    {
        return new AsyncTaskLoader<List<FileSystemObjectInterface>>(getActivity())
        {
            @Override
            public List<FileSystemObjectInterface> loadInBackground()
            {
                ArrayList<FileSystemObjectInterface> files =
                        new ArrayList<FileSystemObjectInterface>();
                try
                {
                    if (!dbApi.metadata(currentPath.getFullPath(), 1, null, false,
                            null).isDir) {
                        currentPath = getRoot();
                    }

                    DropboxAPI.Entry dirEntry =
                            dbApi.metadata(currentPath.getFullPath(), 0, null, true,
                                    null);
                    for (DropboxAPI.Entry entry : dirEntry.contents)
                    {
                        if ((mode == SelectionMode.MODE_FILE || mode == SelectionMode.MODE_FILE_AND_DIR) ||
                            entry.isDir)
                        {
                            files.add(new DropboxFileSystemObject(entry));
                        }
                    }
                } catch (DropboxException e) {
                }

                return files;
            }

            /**
             * Handles a request to start the Loader.
             */
            @Override
            protected void onStartLoading()
            {
                super.onStartLoading();

                if (currentPath == null /*|| !currentPath.isDir*/)
                {
                    currentPath = getRoot();
                }

                forceLoad();
            }

            /**
             * Handles a request to completely reset the Loader.
             */
            @Override
            protected void onReset()
            {
                super.onReset();
            }
        };
    }


}