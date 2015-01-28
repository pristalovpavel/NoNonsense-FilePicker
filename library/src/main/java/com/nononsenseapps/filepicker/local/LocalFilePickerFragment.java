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

package com.nononsenseapps.filepicker.local;

import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Environment;
import android.os.FileObserver;
import android.widget.Toast;

import com.nononsenseapps.filepicker.R;
import com.nononsenseapps.filepicker.fs.FileSystemObjectInterface;
import com.nononsenseapps.filepicker.fs.LocalFileSystemObject;
import com.nononsenseapps.filepicker.ui.core.AbstractFilePickerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalFilePickerFragment extends AbstractFilePickerFragment
{
    public LocalFilePickerFragment()
    {}

    /**
     * Get the root path (lowest allowed).
     */
    @Override
    protected FileSystemObjectInterface getRoot()
    {
        return new LocalFileSystemObject(Environment.getExternalStorageDirectory());
    }

    @Override
    protected void setCurrentPath(String path)
    {
        ((LocalFileSystemObject)currentPath).setPath(path);
    }

    /**
     * Get a loader that lists the Files in the current path,
     * and monitors changes.
     */
    @Override
    protected Loader<List<FileSystemObjectInterface>> getLoader()
    {
        return new AsyncTaskLoader<List<FileSystemObjectInterface>>(getActivity())
        {
            FileObserver fileObserver;

            @Override
            public List<FileSystemObjectInterface> loadInBackground()
            {
                ArrayList<FileSystemObjectInterface> files = new ArrayList<FileSystemObjectInterface>();
                File[] listFiles = ((LocalFileSystemObject)currentPath).getFile().listFiles();

                if(listFiles != null)
                {
                    for (java.io.File f : listFiles)
                    {
                        if ((mode == SelectionMode.MODE_FILE || mode == SelectionMode.MODE_FILE_AND_DIR)
                                || f.isDirectory())
                        {
                            LocalFileSystemObject obj = new LocalFileSystemObject(f);
                            files.add(obj);
                        }
                    }
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

                // handle if directory does not exist. Fall back to root.
                if (currentPath == null || !currentPath.isDir())
                {
                    currentPath = getRoot();
                }

                // Start watching for changes
                fileObserver = new FileObserver(currentPath.getFullPath(),
                        FileObserver.CREATE |
                                FileObserver.DELETE
                                | FileObserver.MOVED_FROM | FileObserver.MOVED_TO)
                {
                    @Override
                    public void onEvent(int event, String path)
                    {
                        // Reload
                        onContentChanged();
                    }
                };
                fileObserver.startWatching();

                forceLoad();
            }

            /**
             * Handles a request to completely reset the Loader.
             */
            @Override
            protected void onReset()
            {
                super.onReset();

                // Stop watching
                if (fileObserver != null) {
                    fileObserver.stopWatching();
                    fileObserver = null;
                }
            }
        };
    }

    /**
     * Name is validated to be non-null, non-empty and not containing any
     * slashes.
     *
     * @param name The name of the folder the user wishes to create.
     */
    @Override
    public void onNewFolder(final String name)
    {
        File folder = new File(((LocalFileSystemObject)currentPath).getFile(), name);

        if (folder.mkdir())
        {
            ((LocalFileSystemObject)currentPath).setFile(folder);
            refresh();
        } else {
            Toast.makeText(getActivity(), R.string.create_folder_error,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
