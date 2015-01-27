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

package com.nononsenseapps.filepicker;


import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import com.nononsenseapps.filepicker.com.nononsenseapps.filepicker.core.Extras;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base activity that handles all the fluff you don't care about.
 * <p/>
 * Usage: To start a child activity you could either use an intent starting the
 * activity directly, or you could use an implicit intent with GET_CONTENT, if
 * it
 * is also defined in your manifest. It is defined to be handled here in case
 * you
 * want the user to be able to use other file pickers on the system.
 * <p/>
 * That means using an intent with action GET_CONTENT
 * If you want to be able to select multiple items, include EXTRA_ALLOW_MULTIPLE
 * (default false).
 * <p/>
 * Two non-standard extra arguments are supported as well: EXTRA_ONLY_DIRS
 * (defaults to false)
 * allows only directories to be selected.
 * And EXTRA_START_PATH (default null), which should specify the starting path.
 * <p/>
 * The result of the user's action is returned in onActivityResult intent,
 * access it using getUri.
 * In case of multiple choices, these can be accessed with getClipData
 * containing Uri objects.
 * If running earlier than JellyBean you can access them with
 * getStringArrayListExtra(EXTRA_PATHS)
 *
 * @param <T>
 */
public abstract class AbstractFilePickerActivity<T> extends ActionBarActivity
        implements AbstractFilePickerFragment.OnFilePickedListener {
    //public static final String EXTRA_START_PATH =
    //        "nononsense.intent" + ".START_PATH";
    //public static final String EXTRA_MODE = "nononsense.intent.MODE";
//    public static final String EXTRA_ALLOW_CREATE_DIR =
//            "nononsense.intent" + ".ALLOW_CREATE_DIR";
    // For compatibility
//    public static final String EXTRA_ALLOW_MULTIPLE =
//            "android.intent.extra" + ".ALLOW_MULTIPLE";
    //public static final String EXTRA_PATHS = "nononsense.intent.PATHS";
    protected static final String TAG = "filepicker_fragment";
    protected String startPath = null;
    protected AbstractFilePickerFragment.SelectionMode mode = AbstractFilePickerFragment.SelectionMode.MODE_FILE;
    protected boolean allowCreateDir = false;
    protected boolean allowMultiple = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setupFauxDialog();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filepicker);

        Intent intent = getIntent();
        if (intent != null) {
            startPath = intent.getStringExtra(Extras.EXTRA_START_PATH);
            mode = AbstractFilePickerFragment.SelectionMode.values()
                    [intent.getIntExtra(Extras.EXTRA_MODE,
                    AbstractFilePickerFragment.SelectionMode.MODE_FILE.ordinal())];
            allowCreateDir = intent.getBooleanExtra(Extras.EXTRA_ALLOW_CREATE_DIR, allowCreateDir);
            allowMultiple = intent.getBooleanExtra(Extras.EXTRA_ALLOW_MULTIPLE, allowMultiple);
        }

        setupActionBar();

        FragmentManager fm = getSupportFragmentManager();
        AbstractFilePickerFragment fragment =
                (AbstractFilePickerFragment) fm.findFragmentByTag(TAG);

        if (fragment == null) {
            fragment =
                    getFragment(startPath, mode, allowMultiple, allowCreateDir);
        }

        if (fragment != null) {
            fm.beginTransaction().replace(R.id.fragment, fragment, TAG)
                    .commit();
        }

        // Default to cancelled
        setResult(Activity.RESULT_CANCELED);
    }

    protected void setupFauxDialog()
    {
        // Check if this should be a dialog
        TypedValue tv = new TypedValue();
        if (!getTheme().resolveAttribute(R.attr.isDialog, tv, true) ||
            tv.data == 0) {
            return;
        }

        // Should be a dialog; set up the window parameters.
        DisplayMetrics dm = getResources().getDisplayMetrics();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources()
                .getDimensionPixelSize(R.dimen.configure_dialog_width);
        params.height = Math.min(getResources()
                .getDimensionPixelSize(R.dimen.configure_dialog_max_height),
                dm.heightPixels * 3 / 4);
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        getWindow().setAttributes(params);
    }

    protected void setupActionBar()
    {
        getSupportActionBar().setTitle(getWindowTitle());
    }

    protected abstract AbstractFilePickerFragment getFragment(
            final String startPath, final AbstractFilePickerFragment.SelectionMode mode, final boolean allowMultiple,
            final boolean allowCreateDir);

    /**
     * @return the title to apply to the window
     */
    protected String getWindowTitle()
    {
        final int res;
        switch (mode)
        {
            case MODE_DIR:
                res = R.plurals.select_dir;
                break;
            case MODE_FILE_AND_DIR:
                res = R.plurals.select_dir_or_file;
                break;
            case MODE_FILE:
            default:
                res = R.plurals.select_file;
                break;
        }

        final int count;
        if (allowMultiple)
        {
            count = 99;
        }
        else
        {
            count = 1;
        }

        return getResources().getQuantityString(res, count);
    }

    @Override
    public void onSaveInstanceState(Bundle b)
    {
        super.onSaveInstanceState(b);
    }

    @Override
    public void onFilePicked(final Uri file)
    {
        Intent i = new Intent();
        i.setData(file);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    @Override
    public void onFilesPicked(final List<Uri> files)
    {
        Intent i = new Intent();
        i.putExtra(Extras.EXTRA_ALLOW_MULTIPLE, true);

        ArrayList<String> paths = new ArrayList<String>();
        for (Uri file : files) {paths.add(file.toString());}
        i.putStringArrayListExtra(Extras.EXTRA_PATHS, paths);

        setResult(Activity.RESULT_OK, i);
        finish();
    }

    @Override
    public void onCancelled()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
