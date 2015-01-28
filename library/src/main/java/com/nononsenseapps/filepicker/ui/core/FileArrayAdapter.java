package com.nononsenseapps.filepicker.ui.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nononsenseapps.filepicker.R;
import com.nononsenseapps.filepicker.fs.FileSystemObjectInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pristalov Pavel on 15.01.2015 for NoNonsense-FilePicker.
 */
public class FileArrayAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private List<FileSystemObjectInterface> data;
    protected DefaultHashMap<Integer, Boolean> checkedItems;

    protected AbstractFilePickerFragment.SelectionMode selectionMode;
    private static final int WRONG_CODE = -1;
    private static final int FILE_CODE = 0;
    private static final int DIRECTORY_CODE = 1;

    public FileArrayAdapter(Context context, List<FileSystemObjectInterface> data, AbstractFilePickerFragment.SelectionMode mode)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        checkedItems = new DefaultHashMap<Integer, Boolean>(false);
        selectionMode = mode;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount()
    {
        return data.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public FileSystemObjectInterface getItem(int position)
    {
        return data.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        FileSystemObjectInterface obj = getItem(position);

        if (obj.isDir()) return DIRECTORY_CODE;
        else if (obj.isFile()) return FILE_CODE;

        return WRONG_CODE;
    }

    @Override
    public int getViewTypeCount()
    {
        // Unknown object, Files and dirs, so 3
        return 3;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = inflater.inflate(getLayout(position), parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        FileSystemObjectInterface obj = getItem(position);

        holder.image.setVisibility(obj.isDir() ? View.VISIBLE : View.GONE);
        holder.title.setText(obj.getName());
        if (holder.title instanceof CheckedTextView)
        {
            ((CheckedTextView) holder.title).setChecked(checkedItems.get(position));
        }

        return convertView;
    }

    protected List<FileSystemObjectInterface> getCheckedItems()
    {
        final ArrayList<FileSystemObjectInterface> files = new ArrayList<FileSystemObjectInterface>();
        for (int pos : checkedItems.keySet())
        {
            if (checkedItems.get(pos))
            {
                files.add(getItem(pos));
            }
        }
        return files;
    }

    public boolean isChecked(int position)
    {
        return checkedItems.get(position);
    }

    public void setChecked(int position, boolean value)
    {
        checkedItems.put(position, value);
    }

    public void clearChecked()
    {
        checkedItems.clear();
    }

    public static class ViewHolder
    {
        private ImageView image;
        private TextView title;

        public ViewHolder(View view)
        {
            image = (ImageView) view.findViewById(R.id.item_icon);
            title = (TextView) view.findViewById(android.R.id.text1);
        }
    }

   private int getLayout(int position)
    {
        if(getItemViewType(position) == DIRECTORY_CODE &&
                selectionMode == AbstractFilePickerFragment.SelectionMode.MODE_FILE)
        {
            return R.layout.filepicker_listitem_dir;
        }
        else
        {
            return R.layout.filepicker_listitem_checkable;
        }
    }

    public class DefaultHashMap<K, V> extends HashMap<K, V>
    {
        protected final V defaultValue;

        public DefaultHashMap(final V defaultValue)
        {
            this.defaultValue = defaultValue;
        }

        @Override
        public V get(Object k)
        {
            return containsKey(k) ? super.get(k) : defaultValue;
        }

    }
}
