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

import android.annotation.SuppressLint;

import com.nononsenseapps.filepicker.ui.core.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.ui.core.AbstractFilePickerFragment;

import java.io.File;

@SuppressLint("Registered")
public class LocalFilePickerActivity extends AbstractFilePickerActivity<File>
{
    public LocalFilePickerActivity()
    {
        super();
    }

    @Override
    protected AbstractFilePickerFragment getFragment(
            final String startPath, final AbstractFilePickerFragment.SelectionMode mode, final boolean allowMultiple,
            final boolean allowCreateDir)
    {
        AbstractFilePickerFragment fragment = new LocalFilePickerFragment();
        fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir);
        return fragment;
    }
}
