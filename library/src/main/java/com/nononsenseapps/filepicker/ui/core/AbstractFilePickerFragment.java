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

package com.nononsenseapps.filepicker.ui.core;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.ui.NewFolderFragment;
import com.nononsenseapps.filepicker.ui.NewItemFragment;
import com.nononsenseapps.filepicker.R;
import com.nononsenseapps.filepicker.fs.FileSystemObjectInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Files.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link
 * OnFilePickedListener}
 * interface.
 */
public abstract class AbstractFilePickerFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<FileSystemObjectInterface>>, NewItemFragment.OnNewFolderListener,
        AdapterView.OnItemLongClickListener
{
    public enum SelectionMode
    {
        MODE_FILE,
        MODE_DIR,
        MODE_FILE_AND_DIR
    }

    // The different preset modes of operation. This impacts the behaviour
    // and possible actions in the UI.
    protected SelectionMode mode = SelectionMode.MODE_FILE;
    // Used for saving state.
    protected static final String KEY_CURRENT_PATH = "KEY_CURRENT PATH";
    //protected final DefaultHashMap<Integer, Boolean> checkedItems;
    protected FileSystemObjectInterface currentPath = null;
    protected boolean allowCreateDir = false;
    protected boolean allowMultiple = false;
    protected Comparator<FileSystemObjectInterface> comparator = null;
    private OnFilePickedListener listener;
    private FileArrayAdapter adapter;
    private TextView currentDirView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AbstractFilePickerFragment()
    {}

    /**
     * Set before making the fragment visible.
     *
     * @param startPath
     * @param mode
     * @param allowMultiple
     * @param allowDirCreate
     */
    public void setArgs(final String startPath, final SelectionMode mode,
            final boolean allowMultiple, final boolean allowDirCreate)
    {
        Bundle b = new Bundle();
        if (startPath != null)
        {
            b.putString(Extras.EXTRA_START_PATH, startPath);
        }

        b.putBoolean(Extras.EXTRA_ALLOW_CREATE_DIR, allowDirCreate);
        b.putBoolean(Extras.EXTRA_ALLOW_MULTIPLE, allowMultiple);
        b.putInt(Extras.EXTRA_MODE, mode.ordinal());

        setArguments(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_filepicker, null);

        ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setOnItemLongClickListener(this);

        view.findViewById(R.id.button_cancel)
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        if (listener != null)
                        {
                            listener.onCancelled();
                        }
                    }
                });

        view.findViewById(R.id.button_ok)
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        if (listener == null)
                        {
                            return;
                        }

                        List<FileSystemObjectInterface> checkedItems = adapter.getCheckedItems();
                        // Some invalid cases first
                        if (allowMultiple && checkedItems.isEmpty())
                        {
                            Toast.makeText(getActivity(),
                                    R.string.select_something_first,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mode == SelectionMode.MODE_FILE && currentPath.isDir())
                        {
                            Toast.makeText(getActivity(),
                                    R.string.select_something_first,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (allowMultiple)
                        {
                            listener.onFilesPicked(toUri(checkedItems));
                        }
                        else
                        {
                            listener.onFilePicked(currentPath.toUri());
                        }
                    }
                });

        view.findViewById(R.id.button_go_parent)
                .setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        // Go to parent
                        currentPath = currentPath.getParent();
                        refresh();
                    }
                });

        final View createDirView = view.findViewById(R.id.button_create_dir);
        // Only show the create dir button if configured to
        createDirView.setVisibility((allowCreateDir && (mode == SelectionMode.MODE_DIR)) ?
                                    View.VISIBLE :
                                    View.INVISIBLE);
        createDirView.setEnabled((allowCreateDir && (mode == SelectionMode.MODE_DIR)));
        createDirView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                Activity activity = getActivity();
                if(activity instanceof FragmentActivity)
                {
                    NewFolderFragment.showDialog(
                            ((FragmentActivity) activity).getSupportFragmentManager(),
                            AbstractFilePickerFragment.this);
                }
            }
        });

        currentDirView = (TextView) view.findViewById(R.id.current_dir);
        // Restore state
        if (currentPath != null)
        {
            currentDirView.setText(currentPath.getFullPath());
        }

        return view;
    }

    protected List<Uri> toUri(List<FileSystemObjectInterface> files)
    {
        ArrayList<Uri> uris = new ArrayList<Uri>();
        for (FileSystemObjectInterface file : files)
        {
            uris.add(file.toUri());
        }
        return uris;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        currentPath = (FileSystemObjectInterface) getListAdapter().getItem(position);
        if (currentPath.isDir())
        {
            refresh();
        }
        else if (isCheckable(currentPath))
        {
            toggleItemCheck(
                    (CheckedTextView) v.findViewById(android.R.id.text1),
                    position, currentPath);
        }
    }

    protected boolean isCheckable(final FileSystemObjectInterface data)
    {
        final boolean checkable;
        if (data.isDir())
        {
            checkable = ((mode == SelectionMode.MODE_DIR && allowMultiple) ||
                         (mode == SelectionMode.MODE_FILE_AND_DIR && allowMultiple));
        }
        else
        {
            // File
            checkable = (mode != SelectionMode.MODE_DIR);
        }
        return checkable;
    }

    protected void toggleItemCheck(final CheckedTextView view,
            final int position, final FileSystemObjectInterface data)
    {
        if (!isCheckable(data))
        {
            return;
        }

        final boolean oldVal = adapter.isChecked(position);

        if (!allowMultiple)
        {
            adapter.clearChecked();
        }

        adapter.setChecked(position, !oldVal);
        // Redraw the items
        getListView().invalidateViews();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            listener = (OnFilePickedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() +
                                         " must implement OnFilePickedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Only if we have no state
        if (currentPath == null)
        {
            if (savedInstanceState != null)
            {
                mode = SelectionMode.values()
                        [savedInstanceState.getInt(Extras.EXTRA_MODE, SelectionMode.MODE_FILE.ordinal())];
                allowCreateDir = savedInstanceState
                        .getBoolean(Extras.EXTRA_ALLOW_CREATE_DIR, allowCreateDir);
                allowMultiple = savedInstanceState
                        .getBoolean(Extras.EXTRA_ALLOW_MULTIPLE, allowMultiple);
                setCurrentPath(savedInstanceState.getString(KEY_CURRENT_PATH));
            }
            else if (getArguments() != null)
            {
                mode = SelectionMode.values()
                        [getArguments().getInt(Extras.EXTRA_MODE, SelectionMode.MODE_FILE.ordinal())];
                allowCreateDir = getArguments()
                        .getBoolean(Extras.EXTRA_ALLOW_CREATE_DIR, allowCreateDir);
                allowMultiple = getArguments()
                        .getBoolean(Extras.EXTRA_ALLOW_MULTIPLE, allowMultiple);
                if (getArguments().containsKey(Extras.EXTRA_START_PATH))
                {
                    setCurrentPath(getArguments().getString(KEY_CURRENT_PATH));
                }
            }

            // If still null
            if (currentPath == null) {
                currentPath = getRoot();
            }
        }

        refresh();

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle b)
    {
        super.onSaveInstanceState(b);
        b.putString(KEY_CURRENT_PATH, currentPath.getFullPath());
        b.putBoolean(Extras.EXTRA_ALLOW_MULTIPLE, allowMultiple);
        b.putBoolean(Extras.EXTRA_ALLOW_CREATE_DIR, allowCreateDir);
        b.putInt(Extras.EXTRA_MODE, mode.ordinal());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Get the root path (lowest allowed).
     */
    protected abstract FileSystemObjectInterface getRoot();

    protected void setCurrentPath(String newPath)
    {
        currentPath.setPath(newPath);
    }

    /**
     * Refreshes the list. Call this when current path changes.
     */
    protected void refresh() {
        getLoaderManager()
                .restartLoader(0, null, AbstractFilePickerFragment.this);
    }

    /**
     * Callback method to be invoked when an item in this view has been
     * clicked and held.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need to access
     * the data associated with the selected item.
     *
     * @param parent   The AbsListView where the click happened
     * @param view     The view within the AbsListView that was clicked
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view,
            final int position, final long id)
    {
        if(!(getListAdapter() instanceof FileArrayAdapter)) return false;

        final FileSystemObjectInterface data =
                (FileSystemObjectInterface) getListAdapter().getItem(position);

        if (!isCheckable(data))
        {
            return false;
        }
        // Special case for single choice to handle directories
        if (!allowMultiple)
        {
            return false;
        }

        toggleItemCheck((CheckedTextView) view.findViewById(android.R.id.text1),
                position, data);
        return true;
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<FileSystemObjectInterface>> onCreateLoader(final int id, final Bundle args) {
        return getLoader();
    }

    /**
     * Get a loader that lists the files in the current path,
     * and monitors changes.
     */
    protected abstract Loader<List<FileSystemObjectInterface>> getLoader();

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(final Loader<List<FileSystemObjectInterface>> loader,
            final List<FileSystemObjectInterface> data) {
        /*if (adapter == null) {
            // View type not really used, overridden in ViewBinder
            adapter = new BindableArrayAdapter<T>(getActivity(),
                    R.layout.filepicker_listitem_checkable);
            adapter.setViewBinder(getViewBinder());
        } else {
            adapter.clear();
        }*/
        Collections.sort(data, getComparator());

        adapter = new FileArrayAdapter(getActivity(), data, mode);

        /*if (comparator == null)
        {
            comparator = getComparator();
        }*/

        //adapter.clearChecked();

        //for(T item : data) adapter.add(item);
        //adapter.sort(comparator);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        currentDirView.setText(currentPath.getFullPath());
    }

    /**
     * @return a comparator that can sort the items alphabetically
     */
    protected Comparator<FileSystemObjectInterface> getComparator()
    {
        return new Comparator<FileSystemObjectInterface>() {
            @Override
            public int compare(final FileSystemObjectInterface lhs,
                               final FileSystemObjectInterface rhs)
            {
                if (lhs.isDir() && !rhs.isDir()) {
                    return -1;
                } else if (rhs.isDir() && !lhs.isDir()) {
                    return 1;
                } else {
                    return lhs.getName().toLowerCase().compareTo(rhs.getName()
                            .toLowerCase());
                }
            }
        };
    }

    /**
     * @param path
     * @return the name of this file/folder
     */
    //protected abstract String getName(final T path);

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(final Loader<List<FileSystemObjectInterface>> loader) {
        setListAdapter(null);
        adapter = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating
     * .html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFilePickedListener {
        public void onFilePicked(Uri file);

        public void onFilesPicked(List<Uri> files);

        public void onCancelled();
    }

//    public class DefaultHashMap<K, V> extends HashMap<K, V> {
//        protected final V defaultValue;
//
//        public DefaultHashMap(final V defaultValue) {
//            this.defaultValue = defaultValue;
//        }
//
//        @Override
//        public V get(Object k) {
//            return containsKey(k) ? super.get(k) : defaultValue;
//        }
//    }
}
