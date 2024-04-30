/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.star.field.categories;

import android.content.Context;
import android.content.ContentResolver;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;

import android.text.format.DateFormat;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Surface;
import android.view.View;
import com.pixelstar.support.preferences.SecureSettingListPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";
    private static final String KEY_CLOCK_POSITION = "status_bar_clock_position";
    private static final String KEY_CLOCK_AM_PM = "status_bar_am_pm";

    private SecureSettingListPreference mClockPositionPref;
    private SecureSettingListPreference mAmPmPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        ContentResolver resolver = getActivity().getContentResolver();
         mClockPositionPref = (SecureSettingListPreference) findPreference(KEY_CLOCK_POSITION);
        mAmPmPref = (SecureSettingListPreference) findPreference(KEY_CLOCK_AM_PM);

        boolean hasNotch = hasCenteredCutout(getActivity());
        boolean isRtl = getResources().getConfiguration().getLayoutDirection()
                == View.LAYOUT_DIRECTION_RTL;

        // Adjust clock position pref for RTL and center notch
        int entries = hasNotch ? R.array.status_bar_clock_position_entries_notch
                : R.array.status_bar_clock_position_entries;
        int values = hasNotch ? (isRtl ? R.array.status_bar_clock_position_values_notch_rtl
                : R.array.status_bar_clock_position_values_notch)
                : (isRtl ? R.array.status_bar_clock_position_values_rtl
                : R.array.status_bar_clock_position_values);
        mClockPositionPref.setEntries(entries);
        mClockPositionPref.setEntryValues(values);

        // Disable AM/PM for 24-hour format
        if (DateFormat.is24HourFormat(getActivity())) {
            mAmPmPref.setEnabled(false);
            mAmPmPref.setSummary(R.string.status_bar_am_pm_disabled);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.STARFIELD;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    
    /* returns whether the device has a centered display cutout or not. */
    private static boolean hasCenteredCutout(Context context) {
        Display display = context.getDisplay();
        DisplayCutout cutout = display.getCutout();
        if (cutout != null) {
            Point realSize = new Point();
            display.getRealSize(realSize);

            switch (display.getRotation()) {
                case Surface.ROTATION_0: {
                    Rect rect = cutout.getBoundingRectTop();
                    return !(rect.left <= 0 || rect.right >= realSize.x);
                }
                case Surface.ROTATION_90: {
                    Rect rect = cutout.getBoundingRectLeft();
                    return !(rect.top <= 0 || rect.bottom >= realSize.y);
                }
                case Surface.ROTATION_180: {
                    Rect rect = cutout.getBoundingRectBottom();
                    return !(rect.left <= 0 || rect.right >= realSize.x);
                }
                case Surface.ROTATION_270: {
                    Rect rect = cutout.getBoundingRectRight();
                    return !(rect.top <= 0 || rect.bottom >= realSize.y);
                }
            }
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }

}

