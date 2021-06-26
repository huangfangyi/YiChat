/*
 * Kontalk Android client
 * Copyright (C) 2016 Kontalk Devteam <devteam@kontalk.org>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.htmessage.sdk.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;


/**
 * System-related utilities.
 * @author Daniele Ricci
 */
public final class SystemUtils {

    private static final Pattern VERSION_CODE_MATCH = Pattern
        .compile("\\(([0-9]+)\\)$");

    private static Uri sProfileUri;

    private SystemUtils() {
    }




    @SuppressWarnings("deprecation")
    public static Point getDisplaySize(Context context) {
        Point displaySize = null;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        if (display != null) {
            displaySize = new Point();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
                displaySize.set(display.getWidth(), display.getHeight());
            }
            else {
                display.getSize(displaySize);
            }
        }

        return displaySize;
    }

    public static int getDisplayRotation(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getRotation();
    }

    /**
     * Returns the correct screen orientation based on the supposedly preferred
     * position of the device.
     * http://stackoverflow.com/a/16585072/1045199
     */
    public static int getScreenOrientation(Activity activity) {
        WindowManager windowManager =  (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Configuration configuration = activity.getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        // Search for the natural position of the device
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
            (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ||
            configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270))
        {
            // Natural position is Landscape
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                case Surface.ROTATION_180:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        }
        else {
            // Natural position is Portrait
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_180:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        }

        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public static void acquireScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager
            .LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void releaseScreenOn(Activity activity) {
        activity.getWindow().clearFlags(WindowManager
            .LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /** Returns the type name of the current network, or null. */
    public static String getCurrentNetworkName(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connMgr.getActiveNetworkInfo();
        return info != null ? info.getTypeName() : null;
    }

    public static int getCurrentNetworkType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connMgr.getActiveNetworkInfo();
        return info != null ? info.getType() : -1;
    }

    public static boolean isOnWifi(Context context) {
        return getCurrentNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }

    /** Checks for network availability. */
    public static boolean isNetworkConnectionAvailable(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getBackgroundDataSetting()) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.getState() == NetworkInfo.State.CONNECTED)
                return true;
        }

        return false;
    }

    public static Bitmap getProfilePhoto(Context context) {
        // profile photo is available only since API level 14
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ContentResolver cr = context.getContentResolver();
            InputStream input = ContactsContract.Contacts
                .openContactPhotoInputStream(cr, ContactsContract.Profile.CONTENT_URI);
            if (input != null) {
                try {
                    return BitmapFactory.decodeStream(input);
                }
                finally {
                    try {
                        input.close();
                    }
                    catch (IOException ignore) {
                    }
                }
            }
        }

        return null;
    }



    public static Uri lookupPhoneNumber(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber));
        Cursor cur = context.getContentResolver().query(uri,
            new String[] { ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.LOOKUP_KEY },
            null, null, null);
        if (cur != null) {
            try {
                if (cur.moveToNext()) {
                    long id = cur.getLong(0);
                    String lookupKey = cur.getString(1);
                    return ContactsContract.Contacts.getLookupUri(id, lookupKey);
                }
            }
            finally {
                cur.close();
            }
        }

        return null;
    }

    /**
     * Provides clone functionality for the {@link SparseBooleanArray}.
     * See https://code.google.com/p/android/issues/detail?id=39242
     */
    public static SparseBooleanArray cloneSparseBooleanArray(SparseBooleanArray array) {
        final SparseBooleanArray clone = new SparseBooleanArray();

        synchronized (array) {
            final int size = array.size();
            for (int i = 0; i < size; i++) {
                int key = array.keyAt(i);
                clone.put(key, array.get(key));
            }
        }

        return clone;
    }

    public static <T> T[] concatenate (T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static <T> T[] concatenate (T[] a, T b) {
        int aLen = a.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + 1);
        System.arraycopy(a, 0, c, 0, aLen);
        c[aLen] = b;

        return c;
    }

    public static <T> boolean contains(final T[] array, final T v) {
        for (final T e : array)
            if (e == v || v != null && v.equals(e))
                return true;

        return false;
    }

    /** Instead of importing the whole commons-io :) */
    public static long copy(final InputStream input, final OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /** Closes the given stream, ignoring any errors. */
    public static void closeStream(Closeable stream) {
        try {
            stream.close();
        }
        catch (Exception ignored) {
        }
    }



    public static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String getUserSerial(Context context) {
        //noinspection ResourceType
        Object userManager = context.getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        }
        catch (Exception ignored) {
        }
        return "";
    }

    public static CharacterStyle getColoredSpan(Context context, @ColorRes int colorResId) {
        return new ForegroundColorSpan(ContextCompat.getColor(context, colorResId));
    }

    public static CharacterStyle getTypefaceSpan(int typeface) {
        return new StyleSpan(typeface);
    }

    public static int getThemedResource(Context context, @AttrRes int attrResId) {
        TypedValue value = new TypedValue();
        if (!context.getTheme().resolveAttribute(attrResId, value, true))
            throw new Resources.NotFoundException();
        return value.resourceId;
    }

}
